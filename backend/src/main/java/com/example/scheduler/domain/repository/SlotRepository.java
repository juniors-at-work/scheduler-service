package com.example.scheduler.domain.repository;

import com.example.scheduler.adapters.dto.BookingRequest;
import com.example.scheduler.adapters.dto.BookingResponse;
import com.example.scheduler.domain.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SlotRepository {

    BookingResponse bookSlot(Event event, BookingRequest request) throws IllegalAccessException;

    BookingResponse bookSlot(Event event, User user, BookingRequest request) throws IllegalAccessException;

    Optional<Slot> getSlotById(UUID id);

    Optional<Booking> findBookingById(UUID bookingId);

    void cancelBooking(UUID bookingId, Instant updatedAt);

    void updateSlotAvailability(UUID slotId, boolean isAvailable, Instant updatedAt);

    boolean hasAvailableSlots(UUID eventId, int maxParticipants);

    List<Slot> getAllSlotsForEvent(UUID eventId);

    List<Slot> findAllBookedByEventOwnerIdOrderByStartTime(UUID eventOwnerId);

    void saveSlots(List<Slot> slots);
}
