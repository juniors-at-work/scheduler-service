package com.example.scheduler.application.usecase;

import com.example.scheduler.adapters.dto.ProfilePublicDto;
import com.example.scheduler.application.service.EventService;
import com.example.scheduler.application.service.ProfileService;
import com.example.scheduler.domain.model.Event;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetEventHostUseCase {
    private final EventService eventService;
    private final ProfileService profileService;

    public GetEventHostUseCase(EventService eventService, ProfileService profileService) {
        this.eventService = eventService;
        this.profileService = profileService;
    }

    public ProfilePublicDto getEventHostByEventPublicId(UUID eventPublicId) {
        Event event = eventService.getActiveByPublicId(eventPublicId);
        return profileService.getPublicProfile(event.ownerId());
    }
}
