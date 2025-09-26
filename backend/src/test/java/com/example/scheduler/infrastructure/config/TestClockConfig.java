package com.example.scheduler.infrastructure.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@TestConfiguration
public class TestClockConfig {

    public static final Instant FIXED_TIME = Instant.parse("2001-02-03T04:05:06.789012Z");

    @Bean
    public static Clock fixedClock() {
        return Clock.fixed(FIXED_TIME, ZoneId.of("Z"));
    }
}
