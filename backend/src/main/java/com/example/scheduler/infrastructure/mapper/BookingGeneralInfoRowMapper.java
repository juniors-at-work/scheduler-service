package com.example.scheduler.infrastructure.mapper;

import com.example.scheduler.adapters.dto.BookingGeneralInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookingGeneralInfoRowMapper implements RowMapper<BookingGeneralInfo> {
    @Override
    public BookingGeneralInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new BookingGeneralInfo(
                rs.getString("title"),
                rs.getString("invitee_name"),
                rs.getString("invitee_email"),
                rs.getTimestamp("start_time").toInstant(),
                rs.getTimestamp("end_time").toInstant(),
                rs.getBoolean("is_canceled"),
                rs.getTimestamp("created_at").toInstant()
        );
    }
}
