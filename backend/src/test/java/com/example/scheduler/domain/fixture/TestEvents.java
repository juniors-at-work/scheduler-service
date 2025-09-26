package com.example.scheduler.domain.fixture;

import com.example.scheduler.domain.model.Event;
import com.example.scheduler.domain.model.EventType;

import java.time.Instant;
import java.util.UUID;

public final class TestEvents {

    private TestEvents() {
        throw new AssertionError();
    }

    public static Event demo() {
        return new Event(
                UUID.fromString("8840ddd5-e176-46d8-8f1b-babb00d989cd"),
                UUID.fromString("d3e68c3b-2d6d-48a1-a037-99a390e9433e"),
                "Demo",
                "Sprint #42 demo",
                true,
                1,
                60,
                10,
                15,
                EventType.ONE2ONE,
                "b452644a-dba8-427a-8e44-d5c1bc528231",
                Instant.parse("2024-07-01T10:00:00.000000Z"),
                Instant.parse("2024-07-04T17:00:00.000000Z"),
                Instant.parse("2001-02-03T04:05:06.789012Z"),
                Instant.parse("2001-03-04T05:06:07.890123Z")
        );
    }

    public static Event daily() {
        return new Event(
                UUID.fromString("7b9ba85e-386a-4929-9f45-0167562b69dd"),
                UUID.fromString("d3e68c3b-2d6d-48a1-a037-99a390e9433e"),
                "Daily",
                "Backend Daily",
                true,
                10,
                30,
                0,
                0,
                EventType.GROUP,
                "d3653051-6830-456e-a451-4b9c9bf2d64a",
                Instant.parse("2024-07-01T10:00:00.000000Z"),
                null,
                Instant.parse("2001-03-04T05:06:07.890123Z"),
                Instant.parse("2001-04-05T06:07:08.901234Z")
        );
    }

    public static Event retro() {
        return new Event(
                UUID.fromString("4cb7b595-78b9-45a5-b3b0-630998b556d8"),
                UUID.fromString("d3e68c3b-2d6d-48a1-a037-99a390e9433e"),
                "Retro",
                "Sprint #13 Retro",
                false,
                10,
                60,
                0,
                0,
                EventType.GROUP,
                "71c2becb-a8eb-43e5-b2aa-d6525708a142",
                Instant.parse("2024-07-11T12:00:00.000000Z"),
                Instant.parse("2024-07-11T13:00:00.000000Z"),
                Instant.parse("2001-04-05T06:07:08.901234Z"),
                Instant.parse("2001-05-06T07:08:09.012345Z")
        );
    }
}
