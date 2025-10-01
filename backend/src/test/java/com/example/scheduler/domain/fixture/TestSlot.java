package com.example.scheduler.domain.fixture;

import com.example.scheduler.domain.model.Slot;

import java.time.Instant;
import java.util.UUID;

public class TestSlot {
    private TestSlot() {
        throw new AssertionError();
    }

    public static Slot getTestSlot() {
        return new Slot(UUID.fromString("c3a4441b-c585-41de-bbc3-e1219d0fd67c"),
                UUID.fromString("7b9ba85e-386a-4929-9f45-0167562b69dd"),
                Instant.parse("2025-07-28T16:34:05.946179Z"), Instant.parse("2025-07-28T17:34:05.946179Z"),
                true);
    }
}
