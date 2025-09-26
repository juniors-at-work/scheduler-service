package com.example.scheduler.application.service;

import com.example.scheduler.adapters.dto.AvailabilityRuleResponse;
import com.example.scheduler.adapters.dto.CreateAvailabilityRuleRequest;
import com.example.scheduler.domain.model.Credential;

import java.util.List;
import java.util.UUID;

public interface AvailabilityRuleService {
    AvailabilityRuleResponse createRule(CreateAvailabilityRuleRequest request, Credential credential);

    List<AvailabilityRuleResponse> getAllRulesByUser(UUID userId);
}
