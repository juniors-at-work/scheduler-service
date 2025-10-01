package com.example.scheduler.adapters.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;

public record CreateProfileRequest(
        @NotBlank String fullName,
        @NotNull ZoneId timezone,
        String description,
        String logo
) {
}
