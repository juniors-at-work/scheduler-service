package com.example.scheduler.adapters.fixture;

import com.example.scheduler.adapters.dto.CreateProfileRequest;

import java.time.ZoneId;

public final class TestCreateProfileRequests {

    private TestCreateProfileRequests() {
        throw new AssertionError();
    }

    public static CreateProfileRequest alice() {
        return new CreateProfileRequest(
                "Alice Arno",
                ZoneId.of("Europe/Paris"),
                "Test description",
                "Logo"
        );
    }
}
