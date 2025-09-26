package com.example.scheduler.adapters.dto.slot;

import java.time.Instant;
import java.util.UUID;

public record PublicSlotDto(UUID id, Instant startTime, Instant endTime) {
}
