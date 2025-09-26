package com.example.scheduler.domain.model;

import java.time.Instant;

public class TimeInterval {
    Instant startTime;
    Instant endTime;

    public TimeInterval(Instant startTime, Instant endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean overlapsWithSlot(Slot slot) {
        return this.startTime.isBefore(slot.endTime()) && slot.startTime().isBefore(this.endTime);
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }
}
