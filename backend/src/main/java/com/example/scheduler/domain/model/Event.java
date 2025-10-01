package com.example.scheduler.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Event(
        UUID id,
        UUID ownerId,
        String title,
        String description,
        boolean isActive,
        int maxParticipants,
        int durationMinutes,
        int bufferBeforeMinutes,
        int bufferAfterMinutes,
        EventType eventType,
        String slug,
        Instant startDate,
        Instant endDate,
        Instant createdAt,
        Instant updatedAt
) {
}