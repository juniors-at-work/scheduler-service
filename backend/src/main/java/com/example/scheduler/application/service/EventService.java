package com.example.scheduler.application.service;

import com.example.scheduler.adapters.dto.CreateEventRequest;
import com.example.scheduler.adapters.dto.EventFullDto;
import com.example.scheduler.adapters.dto.EventResponse;
import com.example.scheduler.adapters.dto.EventShortDto;
import com.example.scheduler.domain.model.Credential;
import com.example.scheduler.domain.model.Event;

import java.util.List;
import java.util.UUID;

public interface EventService {

    EventResponse createEvent(CreateEventRequest request, UUID ownerId);

    EventResponse refreshSlug(UUID eventId);

    EventResponse toggleActiveEvent(UUID id);

    EventFullDto getEventById(UUID userId, UUID eventId, Credential currentUser);

    Event getActiveByPublicId(UUID publicId);

    void updateEvent(UUID id, CreateEventRequest request);

    List<EventShortDto> getAllEvents(UUID ownerId);

    void deleteEvent(UUID id);
}
