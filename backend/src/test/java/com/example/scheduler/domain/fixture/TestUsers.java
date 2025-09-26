package com.example.scheduler.domain.fixture;

import com.example.scheduler.domain.model.User;

import java.time.Instant;
import java.util.UUID;

public final class TestUsers {

    public static final User ALICE = User.builder()
            .id(UUID.fromString("d3e68c3b-2d6d-48a1-a037-99a390e9433e"))
            .username("alice")
            .email("alice@mail.com")
            .passwordHash("{noop}12345")
            .role("USER")
            .createdAt(Instant.parse("2001-02-03T04:05:06.789012Z"))
            .updatedAt(Instant.parse("2001-02-03T04:05:06.789012Z"))
            .build();

    public static final User BOB = User.builder()
            .id(UUID.fromString("9e7f7e33-4574-43b6-83d8-ded7f169c03f"))
            .username("bob")
            .email("bob@mail.com")
            .passwordHash("{noop}54321")
            .role("USER")
            .createdAt(Instant.parse("2002-03-04T05:06:07.890123Z"))
            .updatedAt(Instant.parse("2002-03-04T05:06:07.890123Z"))
            .build();

    public static final User CHARLIE = User.builder()
            .id(UUID.fromString("f089b61d-26e9-419f-9481-df5854a5312c"))
            .username("charlie")
            .email("charlie@mail.com")
            .passwordHash("{noop}12345")
            .role("USER")
            .createdAt(Instant.parse("2003-04-05T06:07:08.901234Z"))
            .updatedAt(Instant.parse("2003-04-05T06:07:08.901234Z"))
            .build();

    private TestUsers() {
        throw new AssertionError();
    }
}
