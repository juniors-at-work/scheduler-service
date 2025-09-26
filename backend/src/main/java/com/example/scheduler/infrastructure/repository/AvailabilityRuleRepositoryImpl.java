package com.example.scheduler.infrastructure.repository;

import com.example.scheduler.domain.model.AvailabilityRule;
import com.example.scheduler.domain.repository.AvailabilityRuleRepository;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AvailabilityRuleRepositoryImpl implements AvailabilityRuleRepository {

    private static final String SAVE_QUERY = """
            INSERT INTO availability_rules (id, user_id, weekday, start_time, end_time, created_at, updated_at)
            VALUES (:id, :userId, :weekday::day_of_week, :startTime, :endTime, :createdAt, :updatedAt)
            RETURNING *
            """;

    private static final String GET_INTERSECTED_COUNT_QUERY = """
            SELECT COUNT(*) FROM availability_rules
            WHERE user_id = :userId AND weekday = :weekday::day_of_week
            AND start_time < :endTime AND end_time > :startTime
            """;

    private static final String GET_ALL_RULES_BY_USER_QUERY = """
            SELECT * FROM availability_rules WHERE user_id = :userId ORDER BY weekday, start_time
            """;

    private final NamedParameterJdbcTemplate jdbc;
    private final RowMapper<AvailabilityRule> mapper;

    public AvailabilityRuleRepositoryImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.mapper = new DataClassRowMapper<>(AvailabilityRule.class);
    }

    @Override
    public AvailabilityRule save(AvailabilityRule rule) {
        SqlParameterSource params = new ExtendedBeanPropertySqlParameterSource(rule);
        return jdbc.queryForObject(SAVE_QUERY, params, mapper);
    }

    @Override
    public boolean intersects(AvailabilityRule rule) {
        SqlParameterSource params = new ExtendedBeanPropertySqlParameterSource(rule);
        Optional<Integer> count = Optional.ofNullable(
                jdbc.queryForObject(GET_INTERSECTED_COUNT_QUERY, params, Integer.class));
        return count.isPresent() && count.get() > 0;
    }

    @Override
    public List<AvailabilityRule> getAllRulesByUser(UUID userId) {
        SqlParameterSource params = new MapSqlParameterSource("userId", userId);
        return jdbc.query(GET_ALL_RULES_BY_USER_QUERY, params, mapper);
    }
}
