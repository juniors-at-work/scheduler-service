package com.example.scheduler.infrastructure.repository;

import com.example.scheduler.adapters.dto.BookingGeneralInfo;
import com.example.scheduler.domain.model.Booking;
import com.example.scheduler.domain.model.TimeInterval;
import com.example.scheduler.domain.repository.BookingRepository;
import com.example.scheduler.infrastructure.mapper.BookingGeneralInfoRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BookingRepositoryImpl implements BookingRepository {
    private static final String FIND_BOOKING_BY_ID = """
            SELECT id, event_template_id, slot_id, invitee_name,
             invitee_email, is_canceled, created_at, updated_at FROM bookings WHERE id = ?
            """;

    private static final String FIND_ALL_BY_EVENT_OWNER_ID_ORDER_BY_START_TIME_QUERY = """
            SELECT
              b.*,
              s.start_time,
              s.end_time,
              e.title
            FROM bookings AS b
            INNER JOIN time_slots AS s ON b.slot_id = s.id
            INNER JOIN event_templates AS e ON s.event_template_id = e.id
            WHERE e.user_id = ?
            ORDER BY s.start_time
            """;

    private static final String GET_TIME_OF_BOOKINGS_FOR_USER_BY_ID = """
            SELECT ts.start_time, ts.end_time FROM booking_participants AS bp
            LEFT JOIN bookings AS b ON b.id = bp.booking_id
            LEFT JOIN time_slots AS ts ON b.slot_id = ts.id
            WHERE bp.user_id = ?
            """;

    private final JdbcTemplate jdbc;
    private final RowMapper<Booking> bookingRowMapper;
    private final RowMapper<TimeInterval> timeIntervalRowMapper;
    private final RowMapper<BookingGeneralInfo> bookingGeneralInfoRowMapper;


    @Autowired
    public BookingRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.bookingRowMapper = new DataClassRowMapper<>(Booking.class);
        this.timeIntervalRowMapper = new DataClassRowMapper<>(TimeInterval.class);
        this.bookingGeneralInfoRowMapper = new BookingGeneralInfoRowMapper();
    }

    @Override
    public Optional<Booking> getBookingById(UUID bookingId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(FIND_BOOKING_BY_ID, bookingRowMapper, bookingId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<BookingGeneralInfo> findAllByEventOwnerIdOrderByStartTime(UUID eventOwnerId) {
        return jdbc.query(FIND_ALL_BY_EVENT_OWNER_ID_ORDER_BY_START_TIME_QUERY, bookingGeneralInfoRowMapper,
                eventOwnerId);
    }

    @Override
    public List<TimeInterval> getTimeOfBookingsForUser(UUID userId) {
        return jdbc.query(GET_TIME_OF_BOOKINGS_FOR_USER_BY_ID, timeIntervalRowMapper, userId);
    }
}
