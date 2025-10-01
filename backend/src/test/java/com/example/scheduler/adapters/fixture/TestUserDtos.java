package com.example.scheduler.adapters.fixture;

import com.example.scheduler.adapters.dto.user.UserDto;

import java.util.UUID;

public final class TestUserDtos {

    public static final UserDto ALICE = new UserDto(
            UUID.fromString("d3e68c3b-2d6d-48a1-a037-99a390e9433e"),
            "alice",
            "alice@mail.com"
    );

    public static final UserDto CHARLIE = new UserDto(
            UUID.fromString("f089b61d-26e9-419f-9481-df5854a5312c"),
            "charlie",
            "charlie@mail.com"
    );

    private TestUserDtos() {
        throw new AssertionError();
    }
}
