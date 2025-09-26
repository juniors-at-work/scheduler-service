package com.example.scheduler.application.service;

import com.example.scheduler.adapters.dto.PublicEventResponse;
import com.example.scheduler.domain.exception.NotFoundException;
import com.example.scheduler.domain.model.Event;
import com.example.scheduler.domain.model.Profile;
import com.example.scheduler.domain.repository.EventRepository;
import com.example.scheduler.domain.repository.ProfileRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PublicEventService {
    private final EventRepository eventRepository;
    private final ProfileRepository profileRepository;

    public PublicEventService(EventRepository eventRepository, ProfileRepository profileRepository) {
        this.eventRepository = eventRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public PublicEventResponse getEventBySlug (UUID sharedLink) {
        Optional<Event> requiredEvent = eventRepository.getEventBySlug(sharedLink);
        if (requiredEvent.isEmpty() || !requiredEvent.get().isActive()) {
            throw new NotFoundException("Событие не найдено");
        }
        Profile eventOwnerProfile = profileRepository.findByUserId(requiredEvent.get().ownerId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return convertToPublicResponse(requiredEvent.get(),  eventOwnerProfile);
    }

    private PublicEventResponse convertToPublicResponse (Event event, Profile eventOwnerProfile) {
        return new PublicEventResponse(event.title(), event.durationMinutes(), event.eventType(),
                eventOwnerProfile.timezone());
    }
}
