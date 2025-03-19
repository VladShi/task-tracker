package com.tasktracker.backend.dto.response;

import java.time.Instant;

public record UserTaskResponse(
        long id,
        String title,
        String description,
        Instant createdAt,
        boolean completed,
        Instant completedAt) {
}
