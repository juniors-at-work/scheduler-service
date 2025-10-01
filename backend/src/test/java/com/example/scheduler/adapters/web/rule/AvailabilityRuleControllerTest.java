package com.example.scheduler.adapters.web.rule;

import com.example.scheduler.adapters.dto.AvailabilityRuleResponse;
import com.example.scheduler.application.service.AvailabilityRuleService;
import com.example.scheduler.domain.fixture.TestUsers;
import com.example.scheduler.domain.model.Credential;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityRuleControllerTest {

    @Mock
    private AvailabilityRuleService ruleService;

    @InjectMocks
    private AvailabilityRuleController controller;

    @Test
    void whenGetAllRulesByUser() {
        Credential credential = mock(Credential.class);
        UUID userId = UUID.randomUUID();
        when(credential.getId()).thenReturn(userId);

        List<AvailabilityRuleResponse> rules = List.of(new AvailabilityRuleResponse(UUID.randomUUID(),
                TestUsers.ALICE.id(), DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(12, 0),
                Instant.now()), new AvailabilityRuleResponse(UUID.randomUUID(),
                TestUsers.ALICE.id(), DayOfWeek.TUESDAY, LocalTime.of(12, 0), LocalTime.of(13, 0),
                Instant.now()));
        when(ruleService.getAllRulesByUser(userId)).thenReturn(rules);

        ResponseEntity<List<AvailabilityRuleResponse>> result = controller.getAllRulesByUser(credential);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(rules, result.getBody());
        assertEquals(2, rules.size());

        verify(ruleService).getAllRulesByUser(userId);
    }
}
