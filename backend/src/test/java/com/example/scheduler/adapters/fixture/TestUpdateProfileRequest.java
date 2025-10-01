package com.example.scheduler.adapters.fixture;

import com.example.scheduler.adapters.dto.UpdateProfileRequest;

import java.time.ZoneId;

public class TestUpdateProfileRequest {
    private TestUpdateProfileRequest() {
        throw new AssertionError();
    }

    public static UpdateProfileRequest aliceUpdateEveryField() {
        return new UpdateProfileRequest(
                "Alice Arnold",
                ZoneId.of("UTC"),
                "Test description updated",
                "Logo updated"
        );
    }

    public static UpdateProfileRequest aliceUpdateOnlyName() {
        return new UpdateProfileRequest(
                "Alice Arne",
                null,
                null,
                null
        );
    }
}
