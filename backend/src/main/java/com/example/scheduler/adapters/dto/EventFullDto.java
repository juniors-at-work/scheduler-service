package com.example.scheduler.adapters.dto;

import com.example.scheduler.domain.model.EventType;

import java.time.Instant;
import java.util.UUID;

public record EventFullDto(
        UUID id,
        UUID ownerId,
        String title,
        String description,
        int durationMinutes,
        int bufferBeforeMinutes,
        int bufferAfterMinutes,
        int maxParticipants,
        boolean isActive,
        EventType eventType,
        String slug,
        Instant startDate,
        Instant endDate,
        Instant createdAt,
        Instant updatedAt
) {
}
