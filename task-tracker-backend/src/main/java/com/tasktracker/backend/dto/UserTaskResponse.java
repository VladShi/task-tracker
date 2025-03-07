package com.tasktracker.backend.dto;

import java.time.Instant;

public record UserTaskResponse(
        long id,
        String title,
        String description,
        Instant createdAt,
        boolean completed,
        Instant completedAt) {
}
