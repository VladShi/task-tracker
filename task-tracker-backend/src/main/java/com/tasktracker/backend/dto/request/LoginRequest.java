package com.tasktracker.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotNull(message = "Login must not be null")
        @Email(message = "Login must be a well-formed email address (your_address@mail_service.com)")
        @Size(min = 4, max = 40, message = "Login must be between 4 and 40 characters")
        String login,

        @NotNull(message = "Password must not be null")
        @Size(min = 4, max = 20, message = "Password must be between 4 and 20 characters")
        @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*_-]*$",
                message = "Password can only contain English letters, numbers, and special characters !@#$%^&*_-")
        String password
) {}
