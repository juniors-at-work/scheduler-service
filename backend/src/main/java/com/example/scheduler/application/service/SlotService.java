package com.example.scheduler.application.service;

import com.example.scheduler.domain.model.Slot;
import com.example.scheduler.domain.model.TimeInterval;
import com.example.scheduler.domain.repository.BookingRepository;
import com.example.scheduler.domain.repository.SlotRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SlotService {
    private final SlotRepository slotRepository;
    private final BookingRepository bookingRepository;

    public SlotService (SlotRepository slotRepository, BookingRepository bookingRepository) {
        this.slotRepository = slotRepository;
        this.bookingRepository = bookingRepository;
    }

    public List<Slot> getAvailableSlots(UUID eventId, UUID userId) {
        List<Slot> requiredEventSlots = slotRepository.getAllSlotsForEvent(eventId);
        List<TimeInterval> occupiedTime = bookingRepository.getTimeOfBookingsForUser(userId);
        return requiredEventSlots.stream().filter(slot -> occupiedTime.stream()
                .noneMatch(timeInterval -> timeInterval.overlapsWithSlot(slot))).toList();
    }

    public List<Slot> findAllByEventIdOrderByStartTime(UUID eventId) {
        return slotRepository.getAllSlotsForEvent(eventId);
    }

    public List<Slot> findAllBookedByEventOwnerIdOrderByStartTime(UUID eventOwnerId) {
        return slotRepository.findAllBookedByEventOwnerIdOrderByStartTime(eventOwnerId);
    }
}
