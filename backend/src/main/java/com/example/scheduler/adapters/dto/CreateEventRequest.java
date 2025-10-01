package com.example.scheduler.adapters.dto;

import com.example.scheduler.domain.model.EventType;
import com.example.scheduler.infrastructure.util.EntityAction;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateEventRequest(
        @NotNull(groups = EntityAction.OnCreate.class)
        @Size(min = 1, max = 255)
        String title,
        @NotNull(groups = EntityAction.OnCreate.class)
        @Size(min = 1, max = 512)
        String description,
        @NotNull(groups = EntityAction.OnCreate.class)
        EventType eventType,
        @PositiveOrZero
        Integer maxParticipants,
        @NotNull(groups = EntityAction.OnCreate.class)
        @Min(15)
        Integer durationMinutes,
        @PositiveOrZero
        Integer bufferBeforeMinutes,
        @PositiveOrZero
        Integer bufferAfterMinutes,
        @NotNull(groups = EntityAction.OnCreate.class)
        Instant startDate,
        Instant endDate
) {
}
