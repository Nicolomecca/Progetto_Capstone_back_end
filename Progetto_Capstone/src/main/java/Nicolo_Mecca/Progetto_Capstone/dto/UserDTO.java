package Nicolo_Mecca.Progetto_Capstone.dto;

import jakarta.validation.constraints.*;

public record UserDTO(
        @NotEmpty(message = "Name is required")
        @Size(min = 3, max = 30, message = "Name must be between 3 and 30 characters")
        String name,

        @NotEmpty(message = "Surname is required")
        @Size(min = 3, max = 30, message = "Surname must be between 3 and 30 characters")
        String surname,

        @NotEmpty(message = "Username is required")
        @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
        String userName,

        @NotEmpty(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotEmpty(message = "Password is required")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
                message = "Password must contain at least 8 characters, one uppercase, one lowercase, one number and one special character"
        )
        String password,

        @NotNull(message = "Total score is required")
        Integer totalScore
) {
}