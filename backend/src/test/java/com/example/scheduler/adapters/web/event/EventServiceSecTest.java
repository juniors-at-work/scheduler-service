package com.example.scheduler.adapters.web.event;

import com.example.scheduler.adapters.dto.EventResponse;
import com.example.scheduler.application.service.EventService;
import com.example.scheduler.application.service.SecurityService;
import com.example.scheduler.domain.model.Event;
import com.example.scheduler.domain.model.EventType;
import com.example.scheduler.domain.repository.EventRepository;
import com.example.scheduler.domain.repository.UserRepository;
import com.example.scheduler.infrastructure.mapper.EventMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.verify;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class EventServiceSecTest {

    @Autowired
    private EventService eventService;

    @MockitoBean
    private EventMapper eventMapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private EventRepository eventRepository;

    @MockitoBean(name = "security")
    private SecurityService securityService;

    @Test
    @WithMockUser(username = "test-user")
    @DisplayName("Should succeed when user is owner")
    void shouldSucceedWhenUserIsOwner() {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String slug = UUID.randomUUID().toString();

        Event fakeEvent = new Event(
                eventId,
                userId,
                "Test title",
                "Some description",
                true,
                30,
                5,
                5,
                5,
                EventType.ONE2ONE,
                slug,
                Instant.now().minus(Duration.ofMinutes(30)),
                Instant.now(),
                Instant.now().minus(Duration.ofHours(1)),
                Instant.now()
        );

        EventResponse expectedResponse = new EventResponse(eventId, slug);

        when(securityService.isOwner(eventId)).thenReturn(true);
        when(eventRepository.regenerateSlug(eventId)).thenReturn(fakeEvent);
        when(eventMapper.toResponse(fakeEvent)).thenReturn(expectedResponse);

        EventResponse actual = eventService.refreshSlug(eventId);

        assertEquals(expectedResponse.id(), actual.id());
        assertEquals(expectedResponse.shareLink(), actual.shareLink());

        verify(securityService).isOwner(eventId);
        verify(eventRepository).regenerateSlug(eventId);
        verify(eventMapper).toResponse(fakeEvent);
    }

    @Test
    @WithMockUser(username = "test-user")
    @DisplayName("Should fail when user is not owner")
    void shouldFailWhenUserIsNotOwner() {
        UUID eventId = UUID.randomUUID();

        when(securityService.isOwner(eventId)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> eventService.refreshSlug(eventId));

        verify(securityService).isOwner(eventId);
        verifyNoInteractions(eventRepository);
    }
}
