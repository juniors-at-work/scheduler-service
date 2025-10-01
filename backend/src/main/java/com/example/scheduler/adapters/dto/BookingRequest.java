package com.example.scheduler.adapters.dto;

import java.util.UUID;

public record BookingRequest(
        UUID slotId,
        String email,
        String name
) {
}
