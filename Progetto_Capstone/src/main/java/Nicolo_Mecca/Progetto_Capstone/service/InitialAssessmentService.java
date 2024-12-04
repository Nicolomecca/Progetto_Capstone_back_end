package Nicolo_Mecca.Progetto_Capstone.service;

import Nicolo_Mecca.Progetto_Capstone.dto.InitialAssessmentDTO;
import Nicolo_Mecca.Progetto_Capstone.dto.InitialAssessmentResponseDTO;
import Nicolo_Mecca.Progetto_Capstone.entities.InitialAssessment;
import Nicolo_Mecca.Progetto_Capstone.entities.ProgrammingLanguage;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.entities.UserLanguageProgress;
import Nicolo_Mecca.Progetto_Capstone.enums.UserLevel;
import Nicolo_Mecca.Progetto_Capstone.exceptions.BadRequestException;
import Nicolo_Mecca.Progetto_Capstone.exceptions.NotFoundException;
import Nicolo_Mecca.Progetto_Capstone.repository.InitialAssessmentRepository;
import Nicolo_Mecca.Progetto_Capstone.repository.ProgrammingLanguageRepository;
import Nicolo_Mecca.Progetto_Capstone.repository.UserLanguageProgressRepository;
import Nicolo_Mecca.Progetto_Capstone.repository.UserRepository;
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
    @Autowired
    private UserRepository userRepository;


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
            case "code" -> "Code";
            case "laravel" -> "Laravel";
            case "nodejs" -> "Nodejs";
            default -> throw new RuntimeException("Unsupported programming language: " + languageName);
        };
    }


    public InitialAssessmentResponseDTO saveInitialAssessment(User user, InitialAssessmentDTO assessmentDTO) {
        ProgrammingLanguage language = programmingLanguageRepository.findByName(assessmentDTO.programmingLanguageName())
                .orElseThrow(() -> new NotFoundException("Programming language not found: " + assessmentDTO.programmingLanguageName()));

        if (initialAssessmentRepository.existsByUserAndProgrammingLanguage(user, language)) {
            throw new BadRequestException("Initial assessment already exists for this user and language");
        }

        InitialAssessment assessment = new InitialAssessment(
                assessmentDTO.score(),
                LocalDateTime.now(),
                true
        );
        assessment.setUser(user);
        assessment.setProgrammingLanguage(language);

        InitialAssessment savedAssessment = initialAssessmentRepository.save(assessment);

        UserLevel skillLevel = UserLevel.fromScore(assessmentDTO.score());
        UserLanguageProgress progress = new UserLanguageProgress(skillLevel, assessmentDTO.score());
        progress.setUser(user);
        progress.setProgrammingLanguage(language);
        progressRepository.save(progress);

        // Update user's total score
        user.setTotalScore(user.getTotalScore() + assessmentDTO.score());
        userRepository.save(user);

        return new InitialAssessmentResponseDTO(
                savedAssessment.getInitial_assessmentId(),
                user.getUserId(),
                user.getUsername(),
                savedAssessment.getScore(),
                savedAssessment.getDate(),
                savedAssessment.getCompleted(),
                skillLevel,
                language.getName()
        );
    }


    public Optional<InitialAssessment> getUserInitialAssessment(User user, String languageName) {
        ProgrammingLanguage language = programmingLanguageRepository.findByName(languageName)
                .orElseThrow(() -> new RuntimeException("Programming language not found"));

        return initialAssessmentRepository.findByUserAndProgrammingLanguage(user, language);
    }

    public boolean hasUserCompletedAssessmentForLanguage(User user, String languageName) {
        ProgrammingLanguage language = programmingLanguageRepository.findByName(languageName)
                .orElseThrow(() -> new NotFoundException("Programming language not found: " + languageName));

        Optional<InitialAssessment> assessment = initialAssessmentRepository.findByUserAndProgrammingLanguage(user, language);
        return assessment.isPresent() && assessment.get().getCompleted();
    }
}