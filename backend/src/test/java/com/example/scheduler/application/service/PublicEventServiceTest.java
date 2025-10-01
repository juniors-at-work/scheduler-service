package com.example.scheduler.application.service;

import com.example.scheduler.adapters.dto.PublicEventResponse;
import com.example.scheduler.domain.fixture.TestProfiles;
import com.example.scheduler.domain.model.Event;
import com.example.scheduler.domain.model.EventType;
import com.example.scheduler.domain.repository.EventRepository;
import com.example.scheduler.domain.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PublicEventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private ProfileRepository profileRepository;
    private PublicEventService publicEventService;

    @BeforeEach
    public void setup() {
        publicEventService = new PublicEventService(eventRepository, profileRepository);
    }

    @Test
    void getEventBySharedLinkTest() {
        Event event = new Event(UUID.randomUUID(), UUID.randomUUID(), "abc", "defg", true,
                10, 1000, 15, 100, EventType.GROUP,
                UUID.randomUUID().toString(), Instant.now(), Instant.now().plusSeconds(10),
                Instant.now().minusSeconds(2000), Instant.now().minusSeconds(1500));
        when(eventRepository.getEventBySlug(Mockito.any(UUID.class))).thenReturn(Optional.of(event));
        when(profileRepository.findByUserId(Mockito.any(UUID.class))).thenReturn(Optional.of(TestProfiles.alice()));
        PublicEventResponse response = publicEventService.getEventBySlug(UUID.randomUUID());

        assertEquals("abc", response.title());
        assertEquals(1000, response.duration());
        assertEquals(EventType.GROUP, response.groupEvent());
    }
}
