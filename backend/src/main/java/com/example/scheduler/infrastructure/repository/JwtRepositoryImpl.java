package com.example.scheduler.infrastructure.repository;

import com.example.scheduler.domain.repository.JwtRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JwtRepositoryImpl implements JwtRepository {
    private static final String SELECT_COUNT_BY_USER_ID_AND_TOKEN_QUERY = """
    SELECT COUNT(*) FROM tokens WHERE user_id = ? AND token = ?
    """;
    private static final String SAVE_QUERY = "INSERT INTO tokens (user_id, token) VALUES (?, ?)";
    private static final String DELETE_BY_USER_ID_QUERY = "DELETE FROM tokens WHERE user_id=?";

    private final JdbcTemplate jdbc;

    public JwtRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public boolean contains(UUID userId, String token) {
        Optional<Integer> count = Optional.ofNullable(
                jdbc.queryForObject(SELECT_COUNT_BY_USER_ID_AND_TOKEN_QUERY, Integer.class, userId, token));
        return count.isPresent() && count.get() > 0;
    }

    @Override
    public void save(UUID userId, String token) {
        jdbc.update(SAVE_QUERY, userId, token);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        jdbc.update(DELETE_BY_USER_ID_QUERY, userId);
    }
}
