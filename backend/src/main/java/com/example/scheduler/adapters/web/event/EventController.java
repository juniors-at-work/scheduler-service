package com.example.scheduler.adapters.web.event;

import com.example.scheduler.adapters.dto.EventFullDto;

import com.example.scheduler.adapters.dto.CreateEventRequest;
import com.example.scheduler.adapters.dto.EventResponse;
import com.example.scheduler.adapters.dto.EventShortDto;
import com.example.scheduler.application.service.EventService;
import com.example.scheduler.application.usecase.GenerateSlotsUseCase;
import com.example.scheduler.domain.model.Credential;
import com.example.scheduler.infrastructure.util.EntityAction;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.example.scheduler.adapters.web.Headers.AUTH_HEADER;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;
    private final GenerateSlotsUseCase generateSlotsUseCase;

    public EventController(EventService eventService, GenerateSlotsUseCase generateSlotsUseCase) {
        this.eventService = eventService;
        this.generateSlotsUseCase = generateSlotsUseCase;
    }

    /**
     * POST /api/events - Добавление события
     */
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody
                                                     @Validated(EntityAction.OnCreate.class)
                                                     CreateEventRequest request,
                                                     @AuthenticationPrincipal
                                                     Credential userDetails) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.createEvent(request, userDetails.getId()));
    }

    /**
     * POST /api/events/{id}/regenerate-link - Обновление ссылки
     */
    @PostMapping("/{id}/regenerate-link")
    public ResponseEntity<EventResponse> refreshSlug(@PathVariable UUID id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.refreshSlug(id));
    }

    /**
     * GET /api/events/{eventId} - Получение конкретного события
     */
    @GetMapping("/{eventId}")
    public EventFullDto getEventById(
            @RequestHeader(AUTH_HEADER) UUID userId,
            @PathVariable UUID eventId,
            @AuthenticationPrincipal Credential currentUser
    ) {
        log.info("Received request for event [{}]: userId = [{}]", eventId, userId);
        EventFullDto response = eventService.getEventById(userId, eventId, currentUser);
        log.info("Responded with requested event [{}]: userId = [{}]", eventId, userId);
        log.debug("Event requested = {}", response);
        return response;
    }

    /**
     * PUT /api/events/{id} - Обновление события
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateEvent(@PathVariable UUID id,
                                            @RequestBody @Valid CreateEventRequest request) {
        eventService.updateEvent(id, request);
        return ResponseEntity.ok().build();
    }

    /**
     * PATCH /api/events/{id}/activate - Активация события
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<EventResponse> toggleEvent(@PathVariable UUID id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.toggleActiveEvent(id));
    }

    /**
     * GET /api/events - Получение всех событий пользователя
     */
    @GetMapping()
    public ResponseEntity<List<EventShortDto>> getAllEventsByUser(@AuthenticationPrincipal Credential userDetails) {
        var events = eventService.getAllEvents(userDetails.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(events);
    }

    /**
     * POST /api/events/{id}/generate-slots - Генерация слотов для события
     *
     * @param id UUID события
     * @return 201 - в случае успешного создания слотов
     */
    @PostMapping("/{id}/generate-slots")
    public ResponseEntity<Void> generateSlots(@PathVariable UUID id) {
        generateSlotsUseCase.execute(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * DELETE /api/events/{id} - Удаление события
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
