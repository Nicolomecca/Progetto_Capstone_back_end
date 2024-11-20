package Nicolo_Mecca.Progetto_Capstone.service;

import Nicolo_Mecca.Progetto_Capstone.entities.ProgrammingLanguage;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.entities.UserLanguageProgress;
import Nicolo_Mecca.Progetto_Capstone.entities.UserQuizResult;
import Nicolo_Mecca.Progetto_Capstone.enums.QuizDifficulty;
import Nicolo_Mecca.Progetto_Capstone.enums.UserLevel;
import Nicolo_Mecca.Progetto_Capstone.repository.ProgrammingLanguageRepository;
import Nicolo_Mecca.Progetto_Capstone.repository.UserLanguageProgressRepository;
import Nicolo_Mecca.Progetto_Capstone.repository.UserQuizResultRepository;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class QuizService {
    @Value("${quiz.api.key}")
    private String apiKey;

    @Autowired
    private UserQuizResultRepository quizResultRepository;
    @Autowired
    private UserLanguageProgressRepository progressRepository;
    @Autowired
    private ProgrammingLanguageRepository programmingLanguageRepository;

    public List<Map<String, Object>> getQuizByLanguage(String languageName, QuizDifficulty difficulty) {
        try {
            String apiCategory = mapLanguageToCategory(languageName);

            HttpResponse<JsonNode> response = Unirest.get("https://quizapi.io/api/v1/questions")
                    .queryString("apiKey", apiKey)
                    .queryString("category", apiCategory)
                    .queryString("difficulty", difficulty.toString().toLowerCase())
                    .queryString("limit", "1")
                    .asJson();

            if (response.getStatus() == 200) {
                JsonNode body = response.getBody();
                List<Map<String, Object>> questions = new ArrayList<>();
                for (int i = 0; i < body.getArray().length(); i++) {
                    questions.add(body.getArray().getJSONObject(i).toMap());
                }
                return questions;
            }
            throw new RuntimeException("Failed to fetch quiz question");
        } catch (Exception e) {
            throw new RuntimeException("Error fetching quiz question: " + e.getMessage());
        }
    }

    private String mapLanguageToCategory(String languageName) {
        return switch (languageName.toLowerCase()) {
            case "react" -> "Frontend";
            case "python" -> "Backend";
            case "postgresql" -> "Database";
            default -> throw new RuntimeException("Unsupported programming language: " + languageName);
        };
    }

    public UserQuizResult saveQuizResult(User user, String languageName, Integer score, QuizDifficulty difficulty) {
        ProgrammingLanguage language = programmingLanguageRepository.findByName(languageName)
                .orElseThrow(() -> new RuntimeException("Programming language not found"));

        UserQuizResult result = new UserQuizResult(
                difficulty,
                score,
                LocalDateTime.now(),
                true
        );
        result.setUser(user);
        result.setProgrammingLanguage(language);

        updateUserProgress(user, language, score);

        return quizResultRepository.save(result);
    }

    private void updateUserProgress(User user, ProgrammingLanguage language, Integer newScore) {
        UserLanguageProgress progress = progressRepository
                .findByUserAndProgrammingLanguage(user, language)
                .orElseThrow(() -> new RuntimeException("User progress not found"));

        progress.setCurrentScore(progress.getCurrentScore() + newScore);
        progress.setSkillLevel(calculateNewLevel(progress.getCurrentScore()));
        progressRepository.save(progress);
    }

    private UserLevel calculateNewLevel(Integer totalScore) {
        if (totalScore < 100) return UserLevel.BEGINNER;
        if (totalScore < 200) return UserLevel.INTERMEDIATE;
        return UserLevel.ADVANCED;
    }

    public List<UserQuizResult> getUserQuizHistory(User user, String languageName) {
        ProgrammingLanguage language = programmingLanguageRepository.findByName(languageName)
                .orElseThrow(() -> new RuntimeException("Programming language not found"));

        return quizResultRepository.findByUserAndProgrammingLanguage(user, language);
    }
}