package com.example.scheduler.adapters.dto;

import java.time.ZoneId;
import java.util.UUID;

public record ProfileResponse(
        UUID userId,
        String username,
        String fullName,
        ZoneId timezone,
        String description,
        boolean isActive,
        String logo
) {
}
