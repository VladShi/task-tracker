package com.tasktracker.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotNull
        @Email
        @Size(min = 4, max = 40, message = "Login must be between 4 and 40 characters")
        String login,

        @NotNull
        @Size(min = 4, max = 20, message = "Password must be between 4 and 20 characters")
        @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*_-]*$",
                message = "Password can only contain English letters, numbers, and special characters !@#$%^&*_-")
        String password
) {}
