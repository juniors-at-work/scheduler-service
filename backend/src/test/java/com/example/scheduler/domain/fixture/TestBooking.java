package com.example.scheduler.domain.fixture;

import com.example.scheduler.domain.model.Booking;

import java.time.Instant;
import java.util.UUID;


public class TestBooking {
    private TestBooking() {
        throw new AssertionError();
    }

    public static Booking getTestBooking() {
        return new Booking(UUID.fromString("fc763648-4263-48c3-80dd-7e50edd30b22"),
                UUID.fromString("7b9ba85e-386a-4929-9f45-0167562b69dd"),
                UUID.fromString("c3a4441b-c585-41de-bbc3-e1219d0fd67c"),
                "testName",
                "test@mail.com",
                false,
                Instant.now().minusSeconds(3600),
                Instant.now().minusSeconds(3600));
    }
}
