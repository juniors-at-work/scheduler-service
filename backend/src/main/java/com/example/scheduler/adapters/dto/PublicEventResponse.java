package com.example.scheduler.adapters.dto;

import com.example.scheduler.domain.model.EventType;

import java.time.ZoneId;

public record PublicEventResponse(
        String title,
        int duration,
        EventType groupEvent,
        ZoneId timeZone
) {
}
