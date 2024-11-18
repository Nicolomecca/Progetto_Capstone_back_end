package Nicolo_Mecca.Progetto_Capstone.service;

import Nicolo_Mecca.Progetto_Capstone.dto.InitialAssessmentDTO;
import Nicolo_Mecca.Progetto_Capstone.entities.InitialAssessment;
import Nicolo_Mecca.Progetto_Capstone.entities.User;
import Nicolo_Mecca.Progetto_Capstone.exceptions.BadRequestException;
import Nicolo_Mecca.Progetto_Capstone.exceptions.NotFoundException;
import Nicolo_Mecca.Progetto_Capstone.repository.InitialAssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class InitialAssessmentService {
    @Autowired
    private InitialAssessmentRepository assessmentRepository;
    @Autowired
    private UserService userService;

    public InitialAssessment saveAssessment(UUID userId, InitialAssessmentDTO assessmentDTO) {
        // Verifico esistenza utente
        User user = userService.findById(userId);

        // Verifico se esiste già una valutazione
        if (assessmentRepository.existsByUser(user)) {
            throw new BadRequestException("Assessment already exists for this user");
        }

        InitialAssessment assessment = new InitialAssessment(
                assessmentDTO.score(),
                LocalDateTime.now(),
                true
        );
        assessment.setUser(user);

        return assessmentRepository.save(assessment);
    }

    public InitialAssessment findByUser(UUID userId) {
        User user = userService.findById(userId);
        return assessmentRepository.findByUser(user).orElseThrow(() -> new NotFoundException("Assessment not found for user with id " + userId));
    }

}
