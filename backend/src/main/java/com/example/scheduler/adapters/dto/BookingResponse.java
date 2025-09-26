package com.example.scheduler.adapters.dto;

import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID eventId,
        UUID slotId,
        Instant startTime,
        Instant endTime,
        boolean isCanceled
) {
}