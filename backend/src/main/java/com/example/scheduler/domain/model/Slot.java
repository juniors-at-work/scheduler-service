package com.example.scheduler.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Slot(
        UUID id,
        UUID eventId,
        Instant startTime,
        Instant endTime,
        boolean isAvailable
) {
}