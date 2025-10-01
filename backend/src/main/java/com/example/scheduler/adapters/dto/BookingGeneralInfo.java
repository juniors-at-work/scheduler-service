package com.example.scheduler.adapters.dto;

import java.time.Instant;

public record BookingGeneralInfo(
        String eventName,
        String inviteeName,
        String inviteeEmail,
        Instant startTime,
        Instant endTime,
        boolean isCanceled,
        Instant createdAt
) {
  }
