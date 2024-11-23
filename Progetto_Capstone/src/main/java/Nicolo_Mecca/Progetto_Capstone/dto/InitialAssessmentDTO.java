package Nicolo_Mecca.Progetto_Capstone.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InitialAssessmentDTO(
        @NotNull(message = "User ID is required")
        UUID userId,

        @NotNull(message = "Programming language name is required")
        @NotBlank(message = "Programming language name cannot be blank")
        String programmingLanguageName,

        @NotNull(message = "Score is required")
        @Min(value = 0, message = "Score cannot be negative")
        @Max(value = 100, message = "Score cannot be more than 100")
        Integer score


) {
}