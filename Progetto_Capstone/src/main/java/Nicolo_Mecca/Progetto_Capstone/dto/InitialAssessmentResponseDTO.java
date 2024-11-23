package Nicolo_Mecca.Progetto_Capstone.dto;

import Nicolo_Mecca.Progetto_Capstone.enums.UserLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public record InitialAssessmentResponseDTO(
        UUID assessmentId,
        UUID userId,
        String username,
        Integer score,
        LocalDateTime date,
        Boolean completed,
        UserLevel skillLevel,
        String programmingLanguageName
) {
}