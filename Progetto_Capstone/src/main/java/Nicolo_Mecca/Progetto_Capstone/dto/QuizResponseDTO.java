package Nicolo_Mecca.Progetto_Capstone.dto;

import Nicolo_Mecca.Progetto_Capstone.enums.QuizDifficulty;
import Nicolo_Mecca.Progetto_Capstone.enums.UserLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuizResponseDTO(
        UUID quizResultId,
        UUID userId,
        String username,
        Integer score,
        LocalDateTime date,
        Boolean completed,
        UserLevel skillLevel,
        String programmingLanguageName,
        QuizDifficulty difficulty
) {
}