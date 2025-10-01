package com.example.scheduler.infrastructure.repository;

import com.example.scheduler.domain.exception.EventNotFoundException;
import com.example.scheduler.domain.model.Event;
import com.example.scheduler.domain.repository.EventRepository;
import com.example.scheduler.infrastructure.mapper.EventRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EventRepositoryImpl implements EventRepository {

    private static final String EVENT_NOT_FOUND_MSG = "Event [%s] not found";

    private static final String SAVE_QUERY = """
            INSERT INTO event_templates (id, user_id, title, description, duration_minutes, buffer_before_minutes,
              buffer_after_minutes, event_type, max_participants, is_active, slug, start_date, end_date, created_at,
              updated_at)
            VALUES (:id, :ownerId, :title, :description, :durationMinutes, :bufferBeforeMinutes, :bufferAfterMinutes,
              :eventType, :maxParticipants, :isActive, :slug, :startDate, :endDate, :createdAt, :updatedAt)
            RETURNING *
            """;

    private static final String UPDATE_SLUG_QUERY = """
            UPDATE event_templates
              SET slug = :slug
            WHERE id = :id
            RETURNING *
            """;

    private static final String GET_EVENT_BY_SLUG = """
            SELECT
              *
            FROM event_templates
            WHERE slug = :slug
            """;

    private static final String FIND_ACTIVE_BY_PUBLIC_ID_QUERY = """
            SELECT
              *
            FROM event_templates
            WHERE slug = :publicId AND is_active = TRUE
            """;

    private static final String TOGGLE_EVENT_QUERY = """
            UPDATE event_templates
              SET is_active = NOT is_active
            WHERE id = :id
            RETURNING *
            """;

    private static final String FIND_BY_ID_QUERY = """
            SELECT
              *
            FROM event_templates
            WHERE id = :id
            """;

    private static final String UPDATE_QUERY = """
            UPDATE event_templates
            SET
              title = :title,
              description = :description,
              duration_minutes = :durationMinutes,
              buffer_before_minutes = :bufferBeforeMinutes,
              buffer_after_minutes = :bufferAfterMinutes,
              event_type = :eventType,
              max_participants = :maxParticipants,
              is_active = :isActive,
              start_date = :startDate,
              end_date = :endDate,
              updated_at = now()
            WHERE id = :id
            RETURNING *
            """;

    private static final String GET_ALL_ACTIVE_EVENTS = """
            SELECT
              *
            FROM event_templates
            WHERE user_id = :ownerId AND is_active=true
            """;

    private static final String DELETE_QUERY = """
            DELETE
            FROM event_templates
            WHERE id = :id
            """;

    private final NamedParameterJdbcTemplate jdbc;
    private final EventRowMapper mapper;

    @Autowired
    public EventRepositoryImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.mapper = new EventRowMapper();
    }

    @Override
    public Event save(Event event) {
        SqlParameterSource params = mapToRow(event);
        return jdbc.queryForObject(SAVE_QUERY, params, mapper);
    }

    @Override
    public Event regenerateSlug(UUID id) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("slug", UUID.randomUUID());
        try {
            return jdbc.queryForObject(UPDATE_SLUG_QUERY, params, mapper);
        } catch (EmptyResultDataAccessException _) {
            throw new EventNotFoundException(EVENT_NOT_FOUND_MSG.formatted(id));
        }
    }

    @Override
    public Optional<Event> getEventById(UUID id) {
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(FIND_BY_ID_QUERY, params, mapper));
        } catch (EmptyResultDataAccessException _) {
            return Optional.empty();
        }
    }

    @Override
    public Event toggleActiveEvent(UUID id) {
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return jdbc.queryForObject(TOGGLE_EVENT_QUERY, params, mapper);
        } catch (EmptyResultDataAccessException _) {
            throw new EventNotFoundException(EVENT_NOT_FOUND_MSG.formatted(id));
        }
    }

    @Override
    public void update(Event e) {
        SqlParameterSource params = mapToRow(e);
        try {
            jdbc.queryForObject(UPDATE_QUERY, params, mapper);
        } catch (EmptyResultDataAccessException _) {
            throw new EventNotFoundException(EVENT_NOT_FOUND_MSG.formatted(e.id()));
        }
    }

    @Override
    public List<Event> getAllEvents(UUID ownerId) {
        SqlParameterSource params = new MapSqlParameterSource("ownerId", ownerId);
        return jdbc.query(GET_ALL_ACTIVE_EVENTS, params, mapper);
    }

    @Override
    public void delete(UUID id) {
        SqlParameterSource params = new MapSqlParameterSource("id", id);
        jdbc.update(DELETE_QUERY, params);
    }

    @Override
    public Optional<Event> getEventBySlug(UUID slug) {
        SqlParameterSource params = new MapSqlParameterSource("slug", slug.toString());
        try {
            return Optional.ofNullable(jdbc.queryForObject(GET_EVENT_BY_SLUG, params, mapper));
        } catch (EmptyResultDataAccessException _) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Event> getActiveByPublicId(UUID publicId) {
        SqlParameterSource params = new MapSqlParameterSource("publicId", publicId.toString());
        try {
            return Optional.ofNullable(jdbc.queryForObject(FIND_ACTIVE_BY_PUBLIC_ID_QUERY, params, mapper));
        } catch (EmptyResultDataAccessException _) {
            return Optional.empty();
        }
    }

    private SqlParameterSource mapToRow(Event event) {
        return new MapSqlParameterSource()
                .addValue("id", event.id())
                .addValue("ownerId", event.ownerId())
                .addValue("title", event.title())
                .addValue("description", event.description())
                .addValue("isActive", event.isActive())
                .addValue("maxParticipants", event.maxParticipants())
                .addValue("durationMinutes", event.durationMinutes())
                .addValue("bufferBeforeMinutes", event.bufferBeforeMinutes())
                .addValue("bufferAfterMinutes", event.bufferAfterMinutes())
                .addValue("eventType", event.eventType().name())
                .addValue("slug", event.slug())
                .addValue("startDate", Timestamp.from(event.startDate()))
                .addValue("endDate", event.endDate() == null ? null : Timestamp.from(event.endDate()))
                .addValue("createdAt", Timestamp.from(event.createdAt()))
                .addValue("updatedAt", Timestamp.from(event.updatedAt()));
    }
}
