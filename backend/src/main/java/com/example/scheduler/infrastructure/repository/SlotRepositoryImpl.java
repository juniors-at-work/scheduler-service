package com.example.scheduler.infrastructure.repository;

import com.example.scheduler.adapters.dto.BookingRequest;
import com.example.scheduler.adapters.dto.BookingResponse;
import com.example.scheduler.domain.exception.NotFoundException;
import com.example.scheduler.domain.model.*;
import com.example.scheduler.domain.repository.SlotRepository;
import com.example.scheduler.infrastructure.mapper.SlotRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SlotRepositoryImpl implements SlotRepository {
    private static final String ADD_PARTICIPANTS = """
            INSERT INTO booking_participants (id, booking_id, user_id, email, name, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?::booking_status, ?, ?)
            """;

    private static final String ADD_NEW_BOOKING_QUERY_WITH_RETURN = """
            INSERT INTO bookings (event_template_id,
            slot_id,
            invitee_name,
            invitee_email,
            is_canceled)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id, created_at
            """;

    private static final String UPDATE_SLOT_QUERY = """
            UPDATE time_slots
            SET is_available = ?, updated_at = ?
            WHERE id = ?
            """;

    private static final String GET_SLOT_QUERY = """
            SELECT *
            FROM time_slots
            WHERE id = ?
            """;

    private static final String COUNT_PARTICIPANTS_QUERY = """
            SELECT COUNT(id) AS participants_count
            FROM bookings
            WHERE is_canceled = false AND event_template_id = ?
            """;

    private static final String ADD_NEW_SLOT = """
            INSERT INTO time_slots (event_template_id, start_time, end_time, is_available)
            VALUES(?, ?, ?, ?)
            """;

    private static final String GET_ALL_SLOTS_FOR_EVENT = """
            SELECT
              *
            FROM time_slots WHERE event_template_id = ?
            ORDER BY start_time
            """;

    private static final String FIND_ALL_BOOKED_BY_EVENT_OWNER_ID_ORDER_BY_START_TIME_QUERY = """
            SELECT
              s.*
            FROM time_slots AS s
            INNER JOIN bookings AS b ON s.id = b.slot_id
            INNER JOIN event_templates AS e ON s.event_template_id = e.id
            WHERE e.user_id = ?
            ORDER BY s.start_time
            """;

    private static final String FIND_BOOKING_BY_ID = """
            SELECT id, event_template_id, slot_id, invitee_name, invitee_email,
                   is_canceled, created_at, updated_at
            FROM bookings
            WHERE id = ?
            """;

    private static final String CANCEL_BOOKING = """
            UPDATE bookings
            SET is_canceled = true, updated_at = ?
            WHERE id = ? AND is_canceled = false
            """;

    private static final String HAS_AVAILABLE_SLOTS = """
            SELECT (COUNT(*) < ?) FROM bookings
            WHERE event_template_id = ? AND is_canceled = false
            """;

    private final JdbcTemplate jdbc;
    private final RowMapper<Slot> mapper;

    @Autowired
    public SlotRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.mapper = new SlotRowMapper();
    }

    @Override
    public void saveSlots(List<Slot> slots) {
        for (Slot slot : slots) {
            jdbc.update(ADD_NEW_SLOT, slot.eventId(), Timestamp.from(slot.startTime()), Timestamp.from(slot.endTime()),
                    slot.isAvailable());
        }
    }

    @Override
    public BookingResponse bookSlot(Event event, User user, BookingRequest request) throws IllegalAccessException {
        return privateBookSlot(event, user, request);
    }

    @Override
    public BookingResponse bookSlot(Event event, BookingRequest request) throws IllegalAccessException {
        return privateBookSlot(event, null, request);
    }

    @Override
    public Optional<Slot> getSlotById(UUID slotId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(GET_SLOT_QUERY,
                    (res, _) -> new Slot(res.getObject("id", UUID.class),
                            res.getObject("event_template_id", UUID.class),
                            res.getTimestamp("start_time").toInstant(),
                            res.getTimestamp("end_time").toInstant(),
                            res.getBoolean("is_available")),
                    slotId));
        } catch (EmptyResultDataAccessException _) {
            return Optional.empty();
        }
    }

    @Override
    public List<Slot> getAllSlotsForEvent(UUID eventId) {
        return jdbc.query(GET_ALL_SLOTS_FOR_EVENT, mapper, eventId);
    }

    @Override
    public List<Slot> findAllBookedByEventOwnerIdOrderByStartTime(UUID eventOwnerId) {
        return jdbc.query(FIND_ALL_BOOKED_BY_EVENT_OWNER_ID_ORDER_BY_START_TIME_QUERY, mapper, eventOwnerId);
    }

    @Override
    public Optional<Booking> findBookingById(UUID bookingId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(FIND_BOOKING_BY_ID,
                    (rs, _) -> new Booking(
                            rs.getObject("id", UUID.class),
                            rs.getObject("event_template_id", UUID.class),
                            rs.getObject("slot_id", UUID.class),
                            rs.getString("invitee_name"),
                            rs.getString("invitee_email"),
                            rs.getBoolean("is_canceled"),
                            rs.getTimestamp("created_at").toInstant(),
                            rs.getTimestamp("updated_at").toInstant()
                    ),
                    bookingId));
        } catch (EmptyResultDataAccessException _) {
            return Optional.empty();
        }
    }

    @Override
    public void cancelBooking(UUID bookingId, Instant updatedAt) {
        jdbc.update(CANCEL_BOOKING,
                java.sql.Timestamp.from(updatedAt),
                bookingId);
    }

    @Override
    public void updateSlotAvailability(UUID slotId, boolean isAvailable, Instant updatedAt) {
        jdbc.update(UPDATE_SLOT_QUERY,
                isAvailable,
                java.sql.Timestamp.from(updatedAt),
                slotId);
    }

    @Override
    public boolean hasAvailableSlots(UUID eventId, int maxParticipants) {
        Boolean available = jdbc.queryForObject(
                HAS_AVAILABLE_SLOTS,
                Boolean.class,
                maxParticipants,
                eventId);
        return Boolean.TRUE.equals(available);
    }

    private void addParticipants(User user,
                                 UUID bookedSlotId,
                                 LocalDateTime now,
                                 String anonymousUsername,
                                 String anonymousEmail) {
        UUID id = UUID.randomUUID();
        UUID userId = user != null ? user.id() : null;
        String username = user != null ? user.username() : anonymousUsername;
        String email = user != null ? user.email() : anonymousEmail;

        jdbc.update(ADD_PARTICIPANTS,
                id,
                bookedSlotId,
                userId,
                email,
                username,
                "PENDING",
                now,
                now
        );
    }

    private BookingResponse privateBookSlot(Event event, User user, BookingRequest request)
            throws IllegalAccessException {
        String username = user == null ? request.name() : user.username();
        String email = user == null ? request.email() : user.email();

        Slot slot = getSlotById(request.slotId())
                .orElseThrow(() -> new NotFoundException("Slot not found"));
        if (!event.id().equals(slot.eventId())) {
            throw new NotFoundException("Slot not found");
        }

        if (!slot.isAvailable()) {
            throw new IllegalAccessException("Slot already have booked");
        }

        List<Pair<UUID, LocalDateTime>> ids = jdbc.query(
                ADD_NEW_BOOKING_QUERY_WITH_RETURN,
                ps -> {
                    ps.setObject(1, event.id());
                    ps.setObject(2, slot.id());
                    ps.setString(3, username);
                    ps.setString(4, email);
                    ps.setBoolean(5, false);
                },
                (rs, _) -> Pair.of(rs.getObject("id", UUID.class),
                        rs.getTimestamp("created_at").toLocalDateTime())
        );

        UUID id = ids.isEmpty() ? null : ids.getFirst().getFirst();
        LocalDateTime now = ids.isEmpty() ? null : ids.getFirst().getSecond();

        try {
            toggleSlotAvailability(event, slot.id(), now);
            addParticipants(user, id, now, username, email);
            return new BookingResponse(id,
                    event.id(),
                    slot.id(),
                    slot.startTime(),
                    slot.endTime(),
                    false);
        } catch (DataAccessException e) {
            System.err.println("Failed to insert participant: " + e.getMessage());
            throw new IllegalStateException("Failed to add participants");
        }
    }

    private void toggleSlotAvailability(Event event,
                                        UUID slotId,
                                        LocalDateTime now) {
        int currParticipants = countParticipants(event.id());

        boolean isAvailableAfter = false;

        if (event.eventType().equals(EventType.GROUP)) {
            if (event.maxParticipants() < currParticipants) {
                throw new IllegalStateException("Limit of participants has reached");
            }
        } else {
            if (countParticipants(event.id()) > 1) {
                throw new IllegalStateException("Limit of participants has reached");
            }
        }

        if (event.eventType().equals(EventType.GROUP) && (event.maxParticipants() >= currParticipants + 1)) {
            isAvailableAfter = true;
        }

        jdbc.update(UPDATE_SLOT_QUERY, isAvailableAfter, now, slotId);

    }

    private int countParticipants(UUID eventId) {
        Integer count = jdbc.queryForObject(
                COUNT_PARTICIPANTS_QUERY,
                Integer.class,
                eventId
        );

        return count != null ? count : 0;
    }
}
