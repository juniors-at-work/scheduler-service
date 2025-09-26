package com.example.scheduler.application.usecase;

import com.example.scheduler.adapters.dto.slot.PublicSlotDto;
import com.example.scheduler.adapters.mapper.SlotMapper;
import com.example.scheduler.application.service.EventService;
import com.example.scheduler.application.service.SlotService;
import com.example.scheduler.domain.model.Event;
import com.example.scheduler.domain.model.Slot;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GetAvailableSlotsUseCase {
    private final EventService eventService;
    private final SlotService slotService;
    private final SlotMapper slotMapper;

    public GetAvailableSlotsUseCase(EventService eventService, SlotService slotService, SlotMapper slotMapper) {
        this.eventService = eventService;
        this.slotService = slotService;
        this.slotMapper = slotMapper;
    }

    public List<PublicSlotDto> getAvailableSlotsByEventPublicId(UUID eventPublicId) {
        Event event = eventService.getActiveByPublicId(eventPublicId);
        List<Slot> slots = slotService.findAllByEventIdOrderByStartTime(event.id());
        if (slots.isEmpty()) {
            return List.of();
        }
        List<Slot> booked = slotService.findAllBookedByEventOwnerIdOrderByStartTime(event.ownerId());
        if (booked.isEmpty()) {
            return slotMapper.toPublicSlotDto(slots);
        }
        List<Slot> available = skipSlotsWithIntersections(slots, booked);
        return slotMapper.toPublicSlotDto(available);
    }

    private List<Slot> skipSlotsWithIntersections(List<Slot> slots, List<Slot> bookedSlots) {
        List<Slot> available = new ArrayList<>();
        int slotIdx = 0;
        int bookedIdx = 0;
        while (slotIdx < slots.size() && bookedIdx < bookedSlots.size()) {
            Slot slot = slots.get(slotIdx);
            Slot booked = bookedSlots.get(bookedIdx);
            if (booked.endTime().isAfter(slot.startTime())) {
                if (!slot.endTime().isAfter(booked.startTime())) {
                    available.add(slot);
                }
                slotIdx++;
            } else {
                bookedIdx++;
            }
        }
        while (slotIdx < slots.size()) {
            Slot slot = slots.get(slotIdx);
            available.add(slot);
            slotIdx++;
        }
        return available;
    }
}
