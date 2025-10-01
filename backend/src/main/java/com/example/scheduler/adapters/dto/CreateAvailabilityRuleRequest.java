package com.example.scheduler.adapters.dto;

import com.example.scheduler.adapters.annotation.TimeRange;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

@TimeRange(startField = "startTime", endField = "endTime")
public record CreateAvailabilityRuleRequest(
        @NotNull
        DayOfWeek weekday,
        @NotNull
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,
        @NotNull
        @JsonFormat(pattern = "HH:mm")
        LocalTime endTime
) {
}
