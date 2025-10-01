package com.example.scheduler.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Booking(
        UUID id,
        UUID eventTemplateId,
        UUID slotId,
        String inviteeName,
        String inviteeEmail,
        boolean isCanceled,
        Instant createdAt,
        Instant updatedAt
) {}
