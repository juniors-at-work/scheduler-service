package com.example.scheduler.domain.repository;

import com.example.scheduler.domain.model.AvailabilityRule;

import java.util.List;
import java.util.UUID;

public interface AvailabilityRuleRepository {

    AvailabilityRule save(AvailabilityRule rule);

    boolean intersects(AvailabilityRule rule);

    List<AvailabilityRule> getAllRulesByUser(UUID userId);
}