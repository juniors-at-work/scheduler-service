package com.example.scheduler.domain.fixture;

import com.example.scheduler.domain.model.Credential;

import java.util.UUID;

public final class TestCredentials {

    private TestCredentials() {
        throw new AssertionError();
    }

    public static Credential alice() {
        return new Credential(
                UUID.fromString("d3e68c3b-2d6d-48a1-a037-99a390e9433e"),
                "alice",
                "{noop}12345",
                "USER",
                true
        );
    }

    public static Credential bob() {
        return new Credential(
                UUID.fromString("9e7f7e33-4574-43b6-83d8-ded7f169c03f"),
                "bob",
                "{noop}54321",
                "USER",
                true
        );
    }

    public static Credential charlie() {
        return new Credential(
                UUID.fromString("f089b61d-26e9-419f-9481-df5854a5312c"),
                "charlie",
                "{noop}12345",
                "USER",
                true
        );
    }
}
