package com.example.scheduler.adapters.fixture;

import com.example.scheduler.adapters.dto.user.RegisterRequest;

public final class TestRegisterRequests {

    public static final RegisterRequest ALICE = new RegisterRequest(
            "alice",
            "12345",
            "alice@mail.com"
    );

    public static final RegisterRequest CHARLIE = new RegisterRequest(
            "charlie",
            "12345",
            "charlie@mail.com"
    );

    private TestRegisterRequests() {
        throw new AssertionError();
    }
}
