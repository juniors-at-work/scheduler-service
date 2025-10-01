package com.example.scheduler.adapters.web.event;

import com.example.scheduler.adapters.dto.CreateEventRequest;
import com.example.scheduler.adapters.dto.EventResponse;
import com.example.scheduler.adapters.dto.EventShortDto;
import com.example.scheduler.application.service.EventService;
import com.example.scheduler.application.usecase.GenerateSlotsUseCase;
import com.example.scheduler.domain.fixture.TestUserDetails;
import com.example.scheduler.domain.model.EventType;
import com.example.scheduler.infrastructure.config.WithSecurityStubs;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@WithSecurityStubs
class SecuredEventControllerWebMvcTest {

    private final CreateEventRequest request = new CreateEventRequest("title", "description",
            EventType.ONE2ONE, 1, 1, 0, 0, Instant.now(), null);

    private final EventResponse response = new EventResponse(UUID.randomUUID(),
            "/api/v1/public/event/%s".formatted(UUID.randomUUID()));

    @MockitoBean
    private EventService eventService;
    @MockitoBean
    private GenerateSlotsUseCase generateSlotsUseCase;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void givenCreateEventRequestWithAuthorizedUser_shouldSucceedWith201() throws Exception {

        when(eventService.createEvent(eq(request), any(UUID.class)))
                .thenReturn(response);

        mvc.perform(post("/api/events")
                        .with(user(new TestUserDetails()))
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(eventService, times(1)).createEvent(eq(request), any(UUID.class));
    }

    @Test
    public void givenCreateEventRequestWithNonAuthorizedUser_shouldErrorWith4xx() throws Exception {

        mvc.perform(post("/api/events")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getAllEvents() throws Exception {
        UUID id1 = UUID.randomUUID();
        EventShortDto dto1 = new EventShortDto(id1, "title1", true, "slug1", EventType.GROUP);
        UUID id2 = UUID.randomUUID();
        EventShortDto dto2 = new EventShortDto(id2, "title2", true, "slug2", EventType.ONE2ONE);
        List<EventShortDto> response = List.of(dto1, dto2);
        TestUserDetails userDetails = new TestUserDetails();
        when(eventService.getAllEvents(userDetails.getId())).thenReturn(response);
        mvc.perform(get("/api/events")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[*].id").isArray())
                .andExpect(jsonPath("$[*].id").isNotEmpty());
    }
}
