package com.example.scheduler.infrastructure.repository;

import com.example.scheduler.domain.exception.ProfileAlreadyExistException;
import com.example.scheduler.domain.exception.ProfileNotFoundException;
import com.example.scheduler.domain.model.Profile;
import com.example.scheduler.domain.repository.ProfileRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProfileRepositoryImpl implements ProfileRepository {

    private static final String INSERT_QUERY = """
            INSERT INTO profiles (user_id, full_name, timezone, description, is_active, logo, created_at, updated_at)
            VALUES (:userId, :fullName, :timezone, :description, :active, :logo, :createdAt, :updatedAt)
            RETURNING *
            """.stripIndent();
    private static final String FIND_BY_ID_QUERY = """
            SELECT * FROM profiles WHERE user_id = :id
            """.stripIndent();
    private static final String UPDATE_QUERY = """
            UPDATE profiles
            SET full_name = :fullName,
                timezone = :timezone,
                description = :description,
                is_active = :active,
                logo = :logo,
                created_at = :createdAt,
                updated_at = :updatedAt
            WHERE user_id = :userId
            RETURNING *
            """.stripIndent();

    private final NamedParameterJdbcTemplate jdbc;
    private final RowMapper<Profile> mapper;

    public ProfileRepositoryImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.mapper = new DataClassRowMapper<>(Profile.class);
    }

    @Override
    public Profile insert(Profile profile) throws ProfileAlreadyExistException {
        SqlParameterSource params = new ExtendedBeanPropertySqlParameterSource(profile);
        try {
            return jdbc.queryForObject(INSERT_QUERY, params, mapper);
        } catch (DuplicateKeyException _) {
            throw new ProfileAlreadyExistException("profile already exists for user " + profile.userId());
        }
    }

    @Override
    public Optional<Profile> findByUserId(UUID userId) {
        SqlParameterSource params = new MapSqlParameterSource("id", userId);
        try {
            return Optional.ofNullable(jdbc.queryForObject(FIND_BY_ID_QUERY, params, mapper));
        } catch (EmptyResultDataAccessException _) {
            return Optional.empty();
        }
    }

    @Override
    public Profile update(Profile profile) throws ProfileNotFoundException {
        SqlParameterSource params = new ExtendedBeanPropertySqlParameterSource(profile);
        try {
            return jdbc.queryForObject(UPDATE_QUERY, params, mapper);
        } catch (EmptyResultDataAccessException _) {
            throw new ProfileNotFoundException("profile not found for user " + profile.userId());
        }
    }
}
