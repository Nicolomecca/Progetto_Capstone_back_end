package Nicolo_Mecca.Progetto_Capstone.service;

import Nicolo_Mecca.Progetto_Capstone.dto.QuizQuestionDTO;
import Nicolo_Mecca.Progetto_Capstone.dto.QuizRequestDTO;
import Nicolo_Mecca.Progetto_Capstone.dto.QuizResponseDTO;
import Nicolo_Mecca.Progetto_Capstone.entities.*;
import Nicolo_Mecca.Progetto_Capstone.enums.QuizDifficulty;
import Nicolo_Mecca.Progetto_Capstone.enums.UserLevel;
import Nicolo_Mecca.Progetto_Capstone.exceptions.NotFoundException;
import Nicolo_Mecca.Progetto_Capstone.repository.*;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizService {
    @Autowired
    private UserQuizResultRepository quizResultRepository;

    @Autowired
    private UserLanguageProgressRepository progressRepository;

    @Autowired
    private ProgrammingLanguageRepository programmingLanguageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Value("${quiz.api.key}")
    private String apiKey;


    public List<Map<String, Object>> getQuizQuestionsForUser(User user, String languageName, String difficulty) {
        // Convert the difficulty string to QuizDifficulty enum
        QuizDifficulty quizDifficulty = QuizDifficulty.valueOf(difficulty.toUpperCase());

        // Fetch and return quiz questions based on the provided difficulty
        return getQuizQuestions(languageName, quizDifficulty);
    }

    private UserLevel getUserLevelForLanguage(User user, String languageName) {
        UserLanguageProgress progress = progressRepository.findByUserAndProgrammingLanguage(user, findProgrammingLanguage(languageName))
                .orElseThrow(() -> new NotFoundException("User progress not found for language: " + languageName));
        return progress.getSkillLevel();
    }

    @Cacheable("quizQuestions")
    public List<Map<String, Object>> getQuizQuestions(String languageName, QuizDifficulty difficulty) {
        try {
            String apiCategory = mapLanguageToCategory(languageName);
            HttpResponse<JsonNode> response = fetchQuestionsFromApi(apiCategory, difficulty);
            return parseApiResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching quiz questions: " + e.getMessage(), e);
        }
    }


    public QuizResponseDTO saveQuizResult(User user, QuizRequestDTO quizRequest) {
        ProgrammingLanguage language = findProgrammingLanguage(quizRequest.languageName());

        UserQuizResult result = new UserQuizResult(
                quizRequest.difficulty(),
                quizRequest.score(),
                LocalDateTime.now(),
                true
        );
        result.setUser(user);
        result.setProgrammingLanguage(language);

        final UserQuizResult savedResult = quizResultRepository.save(result);

        List<QuizQuestion> questions = quizRequest.questions().stream()
                .map(q -> {
                    QuizQuestion question = new QuizQuestion(
                            q.questionText(),
                            q.answers(),
                            q.userAnswer(),
                            q.correctAnswer()
                    );
                    question.setQuizResult(savedResult);
                    return quizQuestionRepository.save(question);
                })
                .collect(Collectors.toList());

        savedResult.setQuestions(questions);
        UserQuizResult finalResult = quizResultRepository.save(savedResult);

        // Aggiorna il progresso dell'utente
        UserLanguageProgress updatedProgress = updateUserProgress(user, language, quizRequest.score());

        // Aggiorna il punteggio totale dell'utente
        int newTotalScore = user.getTotalScore() + quizRequest.score();
        user.setTotalScore(newTotalScore);
        userRepository.save(user);

        return new QuizResponseDTO(
                finalResult.getUserQuizResultId(),
                user.getUserId(),
                user.getUsername(),
                finalResult.getScore(),
                finalResult.getCompletionDate(),
                finalResult.getCompleted(),
                updatedProgress.getSkillLevel(),
                language.getName(),
                finalResult.getDifficulty(),
                questions.stream()
                        .map(q -> new QuizQuestionDTO(
                                q.getQuestionText(),
                                q.getAnswers(),
                                q.getUserAnswer(),
                                q.getCorrectAnswer()
                        ))
                        .collect(Collectors.toList())
        );
    }

    public Map<String, List<QuizResponseDTO>> getUserQuizHistoryGrouped(User user) {
        List<UserQuizResult> quizResults = quizResultRepository.findByUser(user);

        return quizResults.stream()
                .collect(Collectors.groupingBy(
                        result -> result.getProgrammingLanguage().getName(),
                        Collectors.mapping(
                                result -> new QuizResponseDTO(
                                        result.getUserQuizResultId(),
                                        user.getUserId(),
                                        user.getUsername(),
                                        result.getScore(),
                                        result.getCompletionDate(),
                                        result.getCompleted(),
                                        UserLevel.fromScore(result.getScore()),
                                        result.getProgrammingLanguage().getName(),
                                        result.getDifficulty(),
                                        result.getQuestions().stream()
                                                .map(q -> new QuizQuestionDTO(
                                                        q.getQuestionText(),
                                                        q.getAnswers(),
                                                        q.getUserAnswer(),
                                                        q.getCorrectAnswer()
                                                ))
                                                .collect(Collectors.toList())
                                ),
                                Collectors.toList()
                        )
                ));
    }

    private String mapLanguageToCategory(String languageName) {
        return switch (languageName.toLowerCase()) {
            case "react" -> "React";
            case "next.js" -> "Next.js";
            case "postgresql" -> "SQL";
            case "code" -> "Code";
            case "laravel" -> "Laravel";
            case "nodejs" -> "Nodejs";
            default -> throw new IllegalArgumentException("Unsupported programming language: " + languageName);
        };
    }

    private HttpResponse<JsonNode> fetchQuestionsFromApi(String category, QuizDifficulty difficulty) {
        return Unirest.get("https://quizapi.io/api/v1/questions")
                .queryString("apiKey", apiKey)
                .queryString("category", category)
                .queryString("difficulty", difficulty.toString().toLowerCase())
                .queryString("limit", "10")
                .asJson();
    }

    private List<Map<String, Object>> parseApiResponse(HttpResponse<JsonNode> response) {
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed to fetch quiz questions. Status: " + response.getStatus());
        }
        JsonNode body = response.getBody();
        List<Map<String, Object>> questions = new ArrayList<>();
        for (int i = 0; i < body.getArray().length(); i++) {
            questions.add(body.getArray().getJSONObject(i).toMap());
        }
        return questions;
    }

    private ProgrammingLanguage findProgrammingLanguage(String languageName) {
        return programmingLanguageRepository.findByName(languageName)
                .orElseThrow(() -> new NotFoundException("Programming language not found: " + languageName));
    }

    private UserLanguageProgress updateUserProgress(User user, ProgrammingLanguage language, Integer newScore) {
        UserLanguageProgress progress = progressRepository
                .findByUserAndProgrammingLanguage(user, language)
                .orElseThrow(() -> new NotFoundException("User progress not found"));

        int updatedScore = progress.getCurrentScore() + newScore;
        progress.setCurrentScore(updatedScore);

        UserLevel newLevel = UserLevel.fromScore(updatedScore);
        progress.setSkillLevel(newLevel);

        return progressRepository.save(progress);
    }


}