package com.example.scheduler.adapters.dto;

import java.time.ZoneId;

public record UpdateProfileRequest(
        String fullName,
        ZoneId timezone,
        String description,
        String logo
) {
}
