package com.example.scheduler.adapters.dto;

import java.util.UUID;

public record EventResponse(
        UUID id,
        String shareLink
) {
}
