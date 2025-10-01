package com.example.scheduler.infrastructure.mapper;

import com.example.scheduler.domain.model.Event;
import com.example.scheduler.domain.model.EventType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet res, int rowNum) throws SQLException {
        return new Event(
                res.getObject("id", UUID.class),
                res.getObject("user_id", UUID.class),
                res.getString("title"),
                res.getString("description"),
                res.getBoolean("is_active"),
                res.getInt("max_participants"),
                res.getInt("duration_minutes"),
                res.getInt("buffer_before_minutes"),
                res.getInt("buffer_after_minutes"),
                EventType.valueOf(res.getString("event_type")),
                res.getString("slug"),
                res.getTimestamp("start_date").toInstant(),
                res.getTimestamp("end_date") == null
                        ? null
                        : res.getTimestamp("end_date").toInstant(),
                res.getTimestamp("created_at").toInstant(),
                res.getTimestamp("updated_at").toInstant()
        );
    }
}
