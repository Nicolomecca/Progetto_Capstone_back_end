package Nicolo_Mecca.Progetto_Capstone.dto;

import Nicolo_Mecca.Progetto_Capstone.enums.QuizDifficulty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuizRequestDTO(

        @NotNull(message = "Programming language name is required")
        @NotBlank(message = "Programming language name cannot be blank")
        String languageName,

        @NotNull(message = "Difficulty level is required")
        QuizDifficulty difficulty,
        @NotNull(message = "Score is required")
        @Min(value = 0, message = "Score cannot be negative")
        @Max(value = 100, message = "Score cannot be more than 100")
        Integer score
) {
}