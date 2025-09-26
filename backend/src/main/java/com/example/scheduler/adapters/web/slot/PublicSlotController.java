package com.example.scheduler.adapters.web.slot;

import com.example.scheduler.adapters.dto.slot.PublicSlotDto;
import com.example.scheduler.application.usecase.GetAvailableSlotsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class PublicSlotController {
    private static final Logger log = LoggerFactory.getLogger(PublicSlotController.class);

    private final GetAvailableSlotsUseCase getAvailableSlotsUseCase;

    public PublicSlotController(GetAvailableSlotsUseCase getAvailableSlotsUseCase) {
        this.getAvailableSlotsUseCase = getAvailableSlotsUseCase;
    }

    @GetMapping("/api/v1/public/events/{eventPublicId}/slots")
    public List<PublicSlotDto> findSlots(@PathVariable UUID eventPublicId) {
        log.info("Received request for event slots: eventPublicId = {}", eventPublicId);
        List<PublicSlotDto> slots = getAvailableSlotsUseCase.getAvailableSlotsByEventPublicId(eventPublicId);
        log.info("Responded with event slots requested: eventPublicId = {}", eventPublicId);
        log.debug("Requested slots = {}", slots);
        return slots;
    }
}
