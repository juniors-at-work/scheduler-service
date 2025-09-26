package com.example.scheduler.adapters.web.profile;

import com.example.scheduler.adapters.dto.ProfilePublicDto;
import com.example.scheduler.application.usecase.GetEventHostUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class PublicProfileController {
    private static final Logger log = LoggerFactory.getLogger(PublicProfileController.class);

    private final GetEventHostUseCase getEventHostUseCase;

    public PublicProfileController(GetEventHostUseCase getEventHostUseCase) {
        this.getEventHostUseCase = getEventHostUseCase;
    }

    @GetMapping("/api/v1/public/events/{eventPublicId}/host")
    public ProfilePublicDto getPublicProfile(@PathVariable UUID eventPublicId) {
        log.info("Received request for event host: eventPublicId = {}", eventPublicId);
        ProfilePublicDto response = getEventHostUseCase.getEventHostByEventPublicId(eventPublicId);
        log.info("Responded with event host info: eventPublicId = {}", eventPublicId);
        log.debug("Event host = {}", response);
        return response;
    }
}
