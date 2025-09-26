package com.example.scheduler.domain.fixture;

import com.example.scheduler.domain.model.Slot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public final class TestTimeSlots {

    private TestTimeSlots() {
        throw new AssertionError();
    }

    public static List<Slot> getSlots() {
        Slot slot1 = new Slot(UUID.randomUUID(), UUID.fromString("8840ddd5-e176-46d8-8f1b-babb00d989cd"),
                Instant.now().plusSeconds(1800), Instant.now().plusSeconds(3600),
                true);
        Slot slot2 = new Slot(UUID.randomUUID(), UUID.fromString("8840ddd5-e176-46d8-8f1b-babb00d989cd"),
                Instant.now().plusSeconds(5400), Instant.now().plusSeconds(7200),
                true);
        Slot slot3 = new Slot(UUID.randomUUID(), UUID.fromString("8840ddd5-e176-46d8-8f1b-babb00d989cd"),
                Instant.now().plusSeconds(9000), Instant.now().plusSeconds(10800),
                true);
        return new ArrayList<>(Arrays.asList(slot1, slot2, slot3));
    }
}
