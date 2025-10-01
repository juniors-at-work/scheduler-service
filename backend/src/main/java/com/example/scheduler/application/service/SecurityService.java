package com.example.scheduler.application.service;

import com.example.scheduler.domain.exception.NotFoundException;
import com.example.scheduler.domain.model.Credential;
import com.example.scheduler.domain.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("security")
public class SecurityService {

    private final EventRepository repository;

    @Autowired
    public SecurityService(EventRepository eventRepository) {
        this.repository = eventRepository;
    }

    public boolean isOwner(UUID eventId) {
        UUID authUUID = ((Credential) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        return repository.getEventById(eventId)
                .map(event -> event.ownerId().equals(authUUID))
                .orElseThrow(() -> new NotFoundException("Event [%s] not found".formatted(eventId)));
    }
}
