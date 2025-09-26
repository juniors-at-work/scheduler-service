package com.example.scheduler.infrastructure.repository;

import com.example.scheduler.domain.exception.EmailAlreadyExistException;
import com.example.scheduler.domain.exception.UsernameAlreadyExistException;
import com.example.scheduler.domain.model.User;
import com.example.scheduler.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final String INSERT_QUERY = """
            INSERT INTO users (id, username, email, password_hash, role, created_at, updated_at)
            VALUES (:id, :username, :email, :passwordHash, :role, :createdAt, :updatedAt)
            RETURNING *
            """.stripIndent();
    private static final String FIND_BY_USERNAME = """
            SELECT * FROM users WHERE UPPER(username) = UPPER(:username)
            """.stripIndent();

    private final NamedParameterJdbcTemplate jdbc;
    private final RowMapper<User> mapper;

    @Autowired
    public UserRepositoryImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.mapper = new DataClassRowMapper<>(User.class);
    }

    @Override
    public User insert(User user) throws UsernameAlreadyExistException, EmailAlreadyExistException {
        SqlParameterSource params = new ExtendedBeanPropertySqlParameterSource(user);
        try {
            return jdbc.queryForObject(INSERT_QUERY, params, mapper);
        } catch (DuplicateKeyException e) {
            if (e.getMessage().contains("username_unique_idx")) {
                throw new UsernameAlreadyExistException("Username already exists: " + user.username());
            } else if (e.getMessage().contains("email_unique_idx")) {
                throw new EmailAlreadyExistException("Email already exists: " + user.email());
            } else {
                throw e;
            }
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        SqlParameterSource params = new MapSqlParameterSource("username", username);
        try {
            return Optional.ofNullable(jdbc.queryForObject(FIND_BY_USERNAME, params, mapper));
        } catch (EmptyResultDataAccessException _) {
            return Optional.empty();
        }
    }
}
