package Nicolo_Mecca.Progetto_Capstone.dto;

import Nicolo_Mecca.Progetto_Capstone.enums.UserLevel;

import java.util.Map;
import java.util.UUID;

public record UserProfileDTO(
        UUID userId,
        String name,
        String surname,
        String userName,
        String email,
        String profileImage,
        Integer totalScore,
        Map<String, UserLevel> languageProgresses
) {
}