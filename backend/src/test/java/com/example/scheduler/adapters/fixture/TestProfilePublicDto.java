package com.example.scheduler.adapters.fixture;

import com.example.scheduler.adapters.dto.ProfilePublicDto;

public final class TestProfilePublicDto {
    private TestProfilePublicDto() {
        throw new AssertionError();
    }

    public static ProfilePublicDto alice() {
        return new ProfilePublicDto("Alice Arno", "logo.jpg");
    }
}
