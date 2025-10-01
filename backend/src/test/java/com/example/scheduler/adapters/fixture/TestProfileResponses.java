package com.example.scheduler.adapters.fixture;

import com.example.scheduler.adapters.dto.ProfileResponse;

import java.time.ZoneId;
import java.util.UUID;

public final class TestProfileResponses {

    private TestProfileResponses() {
        throw new AssertionError();
    }

    public static ProfileResponse alice() {
        return new ProfileResponse(
                UUID.fromString("d3e68c3b-2d6d-48a1-a037-99a390e9433e"),
                "alice",
                "Alice Arno",
                ZoneId.of("Europe/Paris"),
                "Test description",
                true,
                "Logo"
        );
    }
}
