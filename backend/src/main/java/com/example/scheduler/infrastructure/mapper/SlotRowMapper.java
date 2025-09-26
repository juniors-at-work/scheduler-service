package com.example.scheduler.infrastructure.mapper;

import com.example.scheduler.domain.model.Slot;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SlotRowMapper implements RowMapper<Slot> {
    @Override
    public Slot mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Slot(
                rs.getObject("id", UUID.class),
                rs.getObject("event_template_id", UUID.class),
                rs.getTimestamp("start_time").toInstant(),
                rs.getTimestamp("end_time").toInstant(),
                rs.getBoolean("is_available")
        );
    }
}
