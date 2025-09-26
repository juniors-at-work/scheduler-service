package com.example.scheduler.application.usecase;

import com.example.scheduler.domain.exception.NotEnoughAuthorityException;
import com.example.scheduler.domain.exception.NotFoundException;
import com.example.scheduler.domain.model.Booking;
import com.example.scheduler.domain.model.Credential;
import com.example.scheduler.domain.model.Event;
import com.example.scheduler.domain.model.EventType;
import com.example.scheduler.domain.model.User;
import com.example.scheduler.domain.repository.EventRepository;
import com.example.scheduler.domain.repository.SlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.example.scheduler.domain.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class CancelBookingUseCase {

    private final SlotRepository slotRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Autowired
    public CancelBookingUseCase(SlotRepository slotRepository,
                                UserRepository userRepository,
                                EventRepository eventRepository) {
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public void execute(UUID bookingId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new NotEnoughAuthorityException("User not authenticated");
        }

        Credential credential = (Credential) auth.getPrincipal();
        User user = userRepository.findByUsername(credential.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Booking booking = slotRepository.findBookingById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (booking.isCanceled()) {
            throw new IllegalStateException("Booking already canceled");
        }

        if (!booking.inviteeEmail().equals(user.email())) {
            throw new NotEnoughAuthorityException("You can only cancel your own bookings");
        }

        Event event = eventRepository.getEventById(booking.eventTemplateId())
                .orElseThrow(() -> new NotFoundException("Event not found"));

        Instant now = Instant.now();
        slotRepository.cancelBooking(bookingId, now);

        boolean shouldBeAvailable = event.eventType() == EventType.ONE2ONE ||
                slotRepository.hasAvailableSlots(event.id(), event.maxParticipants());

        slotRepository.updateSlotAvailability(booking.slotId(), shouldBeAvailable, now);
    }
}
