package com.example.scheduler.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Duration;

@Configuration
public class SystemClockConfig {

    @Bean
    public Clock clock() {
        // Adjust application clock precision to that of PostgreSQL (1 microsecond)
        return Clock.tick(Clock.systemDefaultZone(), Duration.ofNanos(1_000));
    }
}
