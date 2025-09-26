package com.example.scheduler.adapters.dto;

import com.example.scheduler.domain.model.EventType;

import java.util.UUID;

public record EventShortDto(
        UUID id,
        String title,
        boolean isActive,
        String slug,
        EventType eventType
) {
}
