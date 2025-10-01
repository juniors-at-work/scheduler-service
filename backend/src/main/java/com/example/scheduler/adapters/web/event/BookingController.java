package com.example.scheduler.adapters.web.event;

import com.example.scheduler.adapters.dto.BookingGeneralInfo;
import com.example.scheduler.adapters.dto.BookingRequest;
import com.example.scheduler.adapters.dto.BookingResponse;
import com.example.scheduler.application.service.BookingService;
import com.example.scheduler.application.usecase.BookSlotUseCase;
import com.example.scheduler.application.usecase.CancelBookingUseCase;
import com.example.scheduler.domain.model.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.example.scheduler.adapters.web.Headers.AUTH_HEADER;

@RestController
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);
    private final BookSlotUseCase bookSlotUseCase;
    private final CancelBookingUseCase cancelBookingUseCase;
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookSlotUseCase bookSlotUseCase, CancelBookingUseCase cancelBookingUseCase,
                             BookingService bookingService) {
        this.bookSlotUseCase = bookSlotUseCase;
        this.cancelBookingUseCase = cancelBookingUseCase;
        this.bookingService = bookingService;
    }

    /**
     * POST /api/v1/public/events/{eventPublicId}/bookings - Создание брони
     */
    @PostMapping("/api/v1/public/events/{eventPublicId}/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse bookSlot(@PathVariable UUID eventPublicId, @RequestBody BookingRequest request)
            throws IllegalAccessException {
        return bookSlotUseCase.execute(eventPublicId, request);
    }

    /**
     * GET /booking - Список всех бронирований пользователя
     */
    @GetMapping("/bookings")
    public List<BookingGeneralInfo> getBookings(
            @RequestHeader(AUTH_HEADER) UUID userId,
            @AuthenticationPrincipal Credential credential
    ) {
        log.info("Received request for bookings: userId = {}", userId);
        List<BookingGeneralInfo> response = bookingService.findAllBookingsByUserId(userId, credential);
        log.info("Responded with bookings: userId = {}", userId);
        return response;
    }

    /**
     * GET /bookings/{bookingId} - Получение конкретного бронирования
     */
    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<BookingGeneralInfo> getBooking(@PathVariable UUID bookingId) {
        log.info("Request for booking id {}", bookingId);
        BookingGeneralInfo bookingInfoResponse = bookingService.getOneBooking(bookingId);
        log.debug("Get booking {}", bookingInfoResponse.toString());
        return ResponseEntity.ok(bookingInfoResponse);
    }

    /**
     * DELETE /bookings/{bookingId} - Отмена бронирования
     */
    @DeleteMapping("/bookings/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID bookingId) {
        cancelBookingUseCase.execute(bookingId);
        return ResponseEntity.noContent().build();
    }
}
