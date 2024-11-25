package Nicolo_Mecca.Progetto_Capstone.service;

import Nicolo_Mecca.Progetto_Capstone.dto.QuizResponseDTO;
import Nicolo_Mecca.Progetto_Capstone.entities.ProgrammingLanguage;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.entities.UserLanguageProgress;
import Nicolo_Mecca.Progetto_Capstone.entities.UserQuizResult;
import Nicolo_Mecca.Progetto_Capstone.enums.QuizDifficulty;
import Nicolo_Mecca.Progetto_Capstone.enums.UserLevel;
import Nicolo_Mecca.Progetto_Capstone.exceptions.NotFoundException;
import Nicolo_Mecca.Progetto_Capstone.repository.ProgrammingLanguageRepository;
import Nicolo_Mecca.Progetto_Capstone.repository.UserLanguageProgressRepository;
import Nicolo_Mecca.Progetto_Capstone.repository.UserQuizResultRepository;
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

    @Value("${quiz.api.key}")
    private String apiKey;

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

    public QuizResponseDTO saveQuizResult(User user, QuizResponseDTO quizResult) {
        ProgrammingLanguage language = findProgrammingLanguage(quizResult.programmingLanguageName());

        UserQuizResult result = new UserQuizResult(
                quizResult.difficulty(),
                quizResult.score(),
                LocalDateTime.now(),
                true
        );
        result.setUser(user);
        result.setProgrammingLanguage(language);

        updateUserProgress(user, language, quizResult.score());

        UserQuizResult savedResult = quizResultRepository.save(result);

        return new QuizResponseDTO(
                savedResult.getUserQuizResultId(),
                user.getUserId(),
                user.getUsername(),
                savedResult.getScore(),
                savedResult.getCompletionDate(),
                savedResult.getCompleted(),
                UserLevel.fromScore(savedResult.getScore()),
                language.getName(),
                savedResult.getDifficulty()
        );
    }

    public List<QuizResponseDTO> getUserQuizHistory(User user, String languageName) {
        ProgrammingLanguage language = findProgrammingLanguage(languageName);
        List<UserQuizResult> quizResults = quizResultRepository.findByUserAndProgrammingLanguage(user, language);

        return quizResults.stream().map(result -> new QuizResponseDTO(
                result.getUserQuizResultId(),
                user.getUserId(),
                user.getUsername(),
                result.getScore(),
                result.getCompletionDate(),
                result.getCompleted(),
                UserLevel.fromScore(result.getScore()),
                language.getName(),
                result.getDifficulty()
        )).collect(Collectors.toList());
    }

    private String mapLanguageToCategory(String languageName) {
        return switch (languageName.toLowerCase()) {
            case "react" -> "React";
            case "next.js" -> "Next.js";
            case "postgresql" -> "SQL";
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

    private void updateUserProgress(User user, ProgrammingLanguage language, Integer newScore) {
        UserLanguageProgress progress = progressRepository
                .findByUserAndProgrammingLanguage(user, language)
                .orElseThrow(() -> new NotFoundException("User progress not found"));

        // Aggiorna il punteggio corrente
        int updatedScore = progress.getCurrentScore() + newScore;
        progress.setCurrentScore(updatedScore);

        // Calcola e aggiorna il livello dell'utente in base al nuovo punteggio
        UserLevel newLevel = UserLevel.fromScore(updatedScore);
        progress.setSkillLevel(newLevel);

        // Salva le modifiche nel repository
        progressRepository.save(progress);
    }
}