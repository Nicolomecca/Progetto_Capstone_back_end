package Nicolo_Mecca.Progetto_Capstone.service;

import Nicolo_Mecca.Progetto_Capstone.entities.InitialAssessment;
import Nicolo_Mecca.Progetto_Capstone.entities.ProgrammingLanguage;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.entities.UserLanguageProgress;
import Nicolo_Mecca.Progetto_Capstone.enums.UserLevel;
import Nicolo_Mecca.Progetto_Capstone.repository.InitialAssessmentRepository;
import Nicolo_Mecca.Progetto_Capstone.repository.ProgrammingLanguageRepository;
import Nicolo_Mecca.Progetto_Capstone.repository.UserLanguageProgressRepository;
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
import java.util.Optional;

@Service
public class InitialAssessmentService {
    @Value("${quiz.api.key}")
    private String apiKey;

    @Autowired
    private InitialAssessmentRepository initialAssessmentRepository;
    @Autowired
    private ProgrammingLanguageRepository programmingLanguageRepository;
    @Autowired
    private UserLanguageProgressRepository progressRepository;

    public List<Map<String, Object>> getInitialAssessment(String languageName) {
        try {
            String apiCategory = mapLanguageToCategory(languageName);

            HttpResponse<JsonNode> response = Unirest.get("https://quizapi.io/api/v1/questions")
                    .queryString("apiKey", apiKey)
                    .queryString("category", apiCategory)
                    .queryString("limit", "10")
                    .asJson();

            if (response.getStatus() == 200) {
                JsonNode body = response.getBody();
                List<Map<String, Object>> questions = new ArrayList<>();
                for (int i = 0; i < body.getArray().length(); i++) {
                    questions.add(body.getArray().getJSONObject(i).toMap());
                }
                return questions;
            }
            throw new RuntimeException("Failed to fetch assessment questions");
        } catch (Exception e) {
            throw new RuntimeException("Error fetching assessment questions: " + e.getMessage());
        }
    }

    private String mapLanguageToCategory(String languageName) {
        return switch (languageName.toLowerCase()) {
            case "react" -> "React";
            case "next.js" -> "Next.js";
            case "postgresql" -> "SQL";
            default -> throw new RuntimeException("Unsupported programming language: " + languageName);
        };
    }

    public InitialAssessment saveInitialAssessment(User user, String languageName, Integer score) {
        ProgrammingLanguage language = programmingLanguageRepository.findByName(languageName)
                .orElseThrow(() -> new RuntimeException("Programming language not found"));

        if (initialAssessmentRepository.existsByUserAndProgrammingLanguage(user, language)) {
            throw new RuntimeException("Initial assessment already exists for this user and language");
        }

        InitialAssessment assessment = new InitialAssessment(
                score,
                LocalDateTime.now(),
                true
        );
        assessment.setUser(user);
        assessment.setProgrammingLanguage(language);

        initializeUserProgress(user, language, score);

        return initialAssessmentRepository.save(assessment);
    }

    private void initializeUserProgress(User user, ProgrammingLanguage language, Integer score) {
        UserLanguageProgress progress = new UserLanguageProgress(
                calculateInitialLevel(score),
                score
        );
        progress.setUser(user);
        progress.setProgrammingLanguage(language);
        progressRepository.save(progress);
    }

    private UserLevel calculateInitialLevel(Integer score) {
        if (score < 30) return UserLevel.BEGINNER;
        if (score < 60) return UserLevel.INTERMEDIATE;
        return UserLevel.ADVANCED;
    }

    public Optional<InitialAssessment> getUserInitialAssessment(User user, String languageName) {
        ProgrammingLanguage language = programmingLanguageRepository.findByName(languageName)
                .orElseThrow(() -> new RuntimeException("Programming language not found"));

        return initialAssessmentRepository.findByUserAndProgrammingLanguage(user, language);
    }
}