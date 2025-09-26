package com.example.scheduler.domain.repository;

import com.example.scheduler.domain.model.Event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository {

    Event save(Event event);

    void update(Event event);

    Event regenerateSlug(UUID id);

    Optional<Event> getEventById(UUID id);

    Event toggleActiveEvent(UUID id);

    List<Event> getAllEvents(UUID ownerId);

    void delete(UUID id);

    Optional<Event> getEventBySlug(UUID slug);

    Optional<Event> getActiveByPublicId(UUID publicId);
}
