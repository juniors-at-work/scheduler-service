package com.example.scheduler.domain.model;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

public record Profile(
        UUID userId,
        String fullName,
        ZoneId timezone,
        String description,
        boolean isActive,
        String logo,
        Instant createdAt,
        Instant updatedAt
) {
}
