package com.example.scheduler.domain.repository;

import java.util.UUID;

public interface JwtRepository {

    boolean contains(UUID userId, String token);

    void save(UUID userId, String token);

    void deleteByUserId(UUID userId);
}