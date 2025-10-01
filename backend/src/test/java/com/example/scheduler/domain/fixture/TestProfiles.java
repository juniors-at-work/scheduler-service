package com.example.scheduler.domain.fixture;

import com.example.scheduler.domain.model.Profile;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

public final class TestProfiles {

    private TestProfiles() {
        throw new AssertionError();
    }

    public static Profile alice() {
        return new Profile(
                UUID.fromString("d3e68c3b-2d6d-48a1-a037-99a390e9433e"),
                "Alice Arno",
                ZoneId.of("Europe/Paris"),
                "Test description",
                true,
                "Logo",
                Instant.parse("2001-02-03T04:05:06.789012Z"),
                Instant.parse("2001-02-03T04:05:06.789012Z")
        );
    }

    public static Profile aliceUpdated() {
        return new Profile(
                UUID.fromString("d3e68c3b-2d6d-48a1-a037-99a390e9433e"),
                "Alice Arnold",
                ZoneId.of("UTC"),
                "Test description updated",
                false,
                "Logo updated",
                Instant.parse("2002-03-04T05:06:07.890123Z"),
                Instant.parse("2003-04-05T06:07:08.901234Z")
        );
    }

    public static Profile bob() {
        return new Profile(
                UUID.fromString("9e7f7e33-4574-43b6-83d8-ded7f169c03f"),
                "Bob Brooks",
                ZoneId.of("Europe/London"),
                "Another test description",
                true,
                "Another logo",
                Instant.parse("2002-03-04T05:06:07.890123Z"),
                Instant.parse("2002-03-04T05:06:07.890123Z")
        );
    }

    public static Profile bobUpdated() {
        return new Profile(
                UUID.fromString("9e7f7e33-4574-43b6-83d8-ded7f169c03f"),
                "Bob Bricks",
                ZoneId.of("UTC"),
                "Another test description updated",
                false,
                "Another logo updated",
                Instant.parse("2002-03-05T06:07:08.901234Z"),
                Instant.parse("2002-03-06T07:08:09.012345Z")
        );
    }
}
