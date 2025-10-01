package com.example.scheduler.infrastructure.repository;

import com.example.scheduler.domain.fixture.TestEvents;
import com.example.scheduler.domain.fixture.TestUsers;
import com.example.scheduler.domain.model.Event;
import com.example.scheduler.infrastructure.mapper.EventMapperImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@Import({EventRepositoryImpl.class,
        EventMapperImpl.class})
class EventRepoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EventRepositoryImpl eventRepository;

    @Test
    @DisplayName("Regenerate slug in DB and check for equality")
    void shouldUpdateSlugInDatabase() {

        Event updatedEvent = eventRepository.regenerateSlug(TestEvents.demo().id());

        assertNotNull(updatedEvent);

        String actualSlug = jdbcTemplate.queryForObject(
                "SELECT slug FROM event_templates WHERE id = ?",
                String.class,
                TestEvents.demo().id()
        );

        assertEquals(updatedEvent.slug(), actualSlug);
    }

    @Test
    @DisplayName("getAllEvents")
    void getAllEvents() {

        List<Event> activeOwnerEvents = eventRepository.getAllEvents(TestUsers.ALICE.id());

        then(activeOwnerEvents).containsExactlyInAnyOrder(TestEvents.demo(), TestEvents.daily());
    }

    @Test
    @DisplayName("getEventBySlug")
    void getEventBySlugTest() {
        Optional<Event> testEvent =
                eventRepository.getEventBySlug(UUID.fromString("71c2becb-a8eb-43e5-b2aa-d6525708a142"));
        Assertions.assertTrue(testEvent.isPresent());
    }
}
