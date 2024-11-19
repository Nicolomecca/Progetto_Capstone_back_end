package Nicolo_Mecca.Progetto_Capstone.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserLoginDTO(@NotEmpty(message = "Username must be provided")
                           @Size(min = 4, max = 16, message = "Username must be between 4 and 16 characters")
                           String username,
                           @NotEmpty(message = "Password must be provided")
                           @Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#!$%^&+=]).*$",
                                   message = "Password must contain at least 8 characters, at least one number, " +
                                           "at least one lowercase character, " +
                                           "at least one uppercase character and one special character @#!$%^&+=")
                           String password) {
}