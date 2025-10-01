package com.example.scheduler.infrastructure.repository;

import com.example.scheduler.domain.fixture.TestUsers;
import com.example.scheduler.domain.model.AvailabilityRule;
import com.example.scheduler.domain.repository.AvailabilityRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@ContextConfiguration(classes = {AvailabilityRuleRepositoryImpl.class})
class AvailabilityRuleRepositoryImplTest {

    @Autowired
    private AvailabilityRuleRepository ruleRepository;
    AvailabilityRule rule;

    @BeforeEach
    void setUp() {
        rule = new AvailabilityRule(UUID.randomUUID(), TestUsers.ALICE.id(), DayOfWeek.MONDAY,
                LocalTime.of(12, 0), LocalTime.of(13, 0),
                Instant.now(), Instant.now());
        ruleRepository.save(rule);
    }

    @Test
    void whenTimeEqualsSavedTime_thenIntersected() {
        assertTrue(ruleRepository.intersects(rule));
    }

    @Test
    void whenSavedTimeIsInside_thenIntersected() {
        AvailabilityRule intersectedRule = new AvailabilityRule(UUID.randomUUID(), TestUsers.ALICE.id(),
                DayOfWeek.MONDAY, LocalTime.of(0, 0), LocalTime.of(23, 59),
                Instant.now(), Instant.now());
        assertTrue(ruleRepository.intersects(intersectedRule));
    }

    @Test
    void whenStartTimeIsBeforeSavedEndTime_thenIntersected() {
        AvailabilityRule intersectedRule = new AvailabilityRule(UUID.randomUUID(), TestUsers.ALICE.id(),
                DayOfWeek.MONDAY, LocalTime.of(12, 30), LocalTime.of(13, 0),
                Instant.now(), Instant.now());
        assertTrue(ruleRepository.intersects(intersectedRule));
    }

    @Test
    void whenEndTimeIsAfterSavedStartTime_thenIntersected() {
        AvailabilityRule intersectedRule = new AvailabilityRule(UUID.randomUUID(), TestUsers.ALICE.id(),
                DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(12, 1),
                Instant.now(), Instant.now());
        assertTrue(ruleRepository.intersects(intersectedRule));
    }

    @Test
    void whenEndTimeIsBeforeSavedStartTime_thenNonIntersected() {
        AvailabilityRule nonIntersectedRule = new AvailabilityRule(UUID.randomUUID(), TestUsers.ALICE.id(),
                DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(11, 59),
                Instant.now(), Instant.now());
        assertFalse(ruleRepository.intersects(nonIntersectedRule));
    }

    @Test
    void whenStartTimeIsAfterSavedEndTime_thenNonIntersected() {
        AvailabilityRule intersectedRule = new AvailabilityRule(UUID.randomUUID(), TestUsers.ALICE.id(),
                DayOfWeek.MONDAY, LocalTime.of(13, 1), LocalTime.of(14, 0),
                Instant.now(), Instant.now());
        assertFalse(ruleRepository.intersects(intersectedRule));
    }

    @Test
    void whenEndTimeEqualsSavedStartTime_thenNonIntersected() {
        AvailabilityRule nonIntersectedRule = new AvailabilityRule(UUID.randomUUID(), TestUsers.ALICE.id(),
                DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(12, 0),
                Instant.now(), Instant.now());
        assertFalse(ruleRepository.intersects(nonIntersectedRule));
    }

    @Test
    void whenStartTimeEqualsSavedEndTime_thenNonIntersected() {
        AvailabilityRule nonIntersectedRule = new AvailabilityRule(UUID.randomUUID(), TestUsers.ALICE.id(),
                DayOfWeek.MONDAY, LocalTime.of(13, 0), LocalTime.of(14, 0),
                Instant.now(), Instant.now());
        assertFalse(ruleRepository.intersects(nonIntersectedRule));
    }

    @Test
    void whenReturnOnlyUserRulesOrdered() {
        UUID aliceId = TestUsers.ALICE.id();
        UUID bobId = TestUsers.BOB.id();

        AvailabilityRule rule1 = new AvailabilityRule(UUID.randomUUID(), aliceId, DayOfWeek.TUESDAY,
                LocalTime.of(9, 0), LocalTime.of(10, 0),
                Instant.now(), Instant.now());
        AvailabilityRule rule2 = new AvailabilityRule(UUID.randomUUID(), aliceId, DayOfWeek.MONDAY,
                LocalTime.of(8, 0), LocalTime.of(9, 0),
                Instant.now(), Instant.now());
        AvailabilityRule rule3 = new AvailabilityRule(UUID.randomUUID(), bobId, DayOfWeek.MONDAY,
                LocalTime.of(12, 0), LocalTime.of(13, 0),
                Instant.now(), Instant.now());

        ruleRepository.save(rule1);
        ruleRepository.save(rule2);
        ruleRepository.save(rule3);

        List<AvailabilityRule> aliceRules = ruleRepository.getAllRulesByUser(aliceId);
        List<AvailabilityRule> bobRules = ruleRepository.getAllRulesByUser(bobId);

        assertTrue(aliceRules.stream().allMatch(r -> r.userId().equals(aliceId)));

        assertEquals(3, aliceRules.size());
        assertEquals(1, bobRules.size());

        List<AvailabilityRule> sortedCopy = new ArrayList<>(aliceRules);
        sortedCopy.sort(Comparator
                .comparing(AvailabilityRule::weekday)
                .thenComparing(AvailabilityRule::startTime));
        assertEquals(sortedCopy, aliceRules);
    }

    @Test
    void whenNoRulesThenReturnEmptyList() {
        UUID unknownUserId = UUID.randomUUID();
        List<AvailabilityRule> rules = ruleRepository.getAllRulesByUser(unknownUserId);
        assertNotNull(rules);
        assertTrue(rules.isEmpty());
    }
}
