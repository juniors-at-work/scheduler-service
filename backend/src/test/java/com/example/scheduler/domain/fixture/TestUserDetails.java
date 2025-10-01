package com.example.scheduler.domain.fixture;

import com.example.scheduler.domain.model.Credential;

import java.util.UUID;

public class TestUserDetails extends Credential {
    public TestUserDetails() {
        super(UUID.randomUUID(), "username", "password", "USER", true);
    }
}
