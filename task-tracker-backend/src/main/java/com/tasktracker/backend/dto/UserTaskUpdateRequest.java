package com.tasktracker.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserTaskUpdateRequest(
        @NotBlank(message = "The title must not be empty")
        @Size(max = 255, message = "The title must be no longer than 255 chars.")
        String title,

        @Size(max = 5000, message = "The title must be no longer than 5000 chars.")
        String description,

        boolean completed) {
}
