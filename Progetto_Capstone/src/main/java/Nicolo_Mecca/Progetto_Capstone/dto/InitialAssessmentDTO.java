package Nicolo_Mecca.Progetto_Capstone.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record InitialAssessmentDTO(
        @NotNull(message = "Score is required")
        @Min(value = 0, message = "Score cannot be negative")
        @Max(value = 100, message = "Score cannot be more than 100")
        Integer score,

        @NotNull(message = "Date is required")
        LocalDateTime date,

        @NotNull(message = "Completed status is required")
        Boolean completed
) {
}