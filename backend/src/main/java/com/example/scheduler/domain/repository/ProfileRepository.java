package com.example.scheduler.domain.repository;

import com.example.scheduler.domain.exception.ProfileAlreadyExistException;
import com.example.scheduler.domain.exception.ProfileNotFoundException;
import com.example.scheduler.domain.model.Profile;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository {

    Profile insert(Profile profile) throws ProfileAlreadyExistException;

    Profile update(Profile profile) throws ProfileNotFoundException;

    Optional<Profile> findByUserId(UUID userId);
}
