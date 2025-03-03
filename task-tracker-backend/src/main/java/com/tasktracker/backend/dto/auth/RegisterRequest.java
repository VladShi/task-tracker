package com.tasktracker.backend.dto.auth;

import com.tasktracker.backend.validation.PasswordsMatch;
import jakarta.validation.constraints.*;

@PasswordsMatch
public record RegisterRequest(
        @NotNull
        @Email
        @Size(min = 4, max = 40, message = "Login must be between 4 and 40 characters")
        String login,

        @NotNull
        @Size(min = 4, max = 20, message = "Password must be between 4 and 20 characters")
        @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*_-]*$",
                message = "Password can only contain English letters, numbers, and special characters !@#$%^&*_-")
        String password,

        @NotNull
        String confirmPassword
) {}