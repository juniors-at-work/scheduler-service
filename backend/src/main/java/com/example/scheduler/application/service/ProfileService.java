package com.example.scheduler.application.service;

import com.example.scheduler.adapters.dto.CreateProfileRequest;
import com.example.scheduler.adapters.dto.ProfilePublicDto;
import com.example.scheduler.adapters.dto.ProfileResponse;
import com.example.scheduler.adapters.dto.UpdateProfileRequest;
import com.example.scheduler.adapters.mapper.ProfileMapper;
import com.example.scheduler.domain.exception.NotEnoughAuthorityException;
import com.example.scheduler.domain.exception.ProfileNotFoundException;
import com.example.scheduler.domain.exception.UserNotAuthorizedException;
import com.example.scheduler.domain.model.Credential;
import com.example.scheduler.domain.model.Profile;
import com.example.scheduler.domain.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final Clock clock;

    public ProfileService(ProfileRepository profileRepository, ProfileMapper profileMapper, Clock clock) {
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
        this.clock = clock;
    }

    public ProfileResponse createProfile(UUID userId, CreateProfileRequest request, Credential credential) {
        Objects.requireNonNull(userId, "userId cannot be null");
        Objects.requireNonNull(request, "request cannot be null");
        requireOwnerAuthority(userId, credential, "user can create profile for themselves only");
        Profile prepared = new Profile(
                userId,
                request.fullName(),
                request.timezone(),
                request.description(),
                true,
                request.logo(),
                Instant.now(clock),
                Instant.now(clock)
        );
        Profile created = profileRepository.insert(prepared);
        log.info("Created profile for user {}", userId);
        log.debug("Profile created = {}", created);
        return profileMapper.toDto(created, credential.getUsername());
    }

    public ProfileResponse getProfile(UUID userId, Credential credential) {
        Objects.requireNonNull(userId, "userId cannot be null");
        requireOwnerAuthority(userId, credential, "user can get profile for themselves only");
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(
                () -> new ProfileNotFoundException("profile not found for user " + userId)
        );
        return profileMapper.toDto(profile, credential.getUsername());
    }

    public ProfileResponse updateProfile(UUID userId, UpdateProfileRequest request, Credential credential) {
        Objects.requireNonNull(userId, "userId cannot be null");
        Objects.requireNonNull(request, "request cannot be null");
        requireOwnerAuthority(userId, credential, "user can update profile for themselves only");
        Profile profile = profileRepository.findByUserId(userId).orElseThrow(
                () -> new ProfileNotFoundException("profile not found for user " + userId)
        );
        Profile updateProfile = new Profile(
                userId,
                request.fullName() == null ? profile.fullName() : request.fullName(),
                request.timezone() == null ? profile.timezone() : request.timezone(),
                request.description() == null ? profile.description() : request.description(),
                true,
                request.logo() == null ? profile.logo() : request.logo(),
                profile.createdAt(),
                Instant.now(clock)
        );
        Profile updated = profileRepository.update(updateProfile);
        log.info("Updated profile for user {}", userId);
        log.debug("Profile updated = {}", updated);
        return profileMapper.toDto(updated, credential.getUsername());
    }

    public ProfilePublicDto getPublicProfile(UUID userId) {
        Objects.requireNonNull(userId, "userId cannot be null");

        Profile profile = profileRepository.findByUserId(userId).orElseThrow(
                () -> new ProfileNotFoundException("profile not found for user " + userId)
        );

        return profileMapper.toPublicDto(profile);
    }

    private void requireOwnerAuthority(UUID userId, Credential credential, String noAuthorityMessage) {
        if (credential == null) {
            throw new UserNotAuthorizedException("user %s is not authorized".formatted(userId));
        } else if (!userId.equals(credential.getId())) {
            throw new NotEnoughAuthorityException(noAuthorityMessage);
        }
    }
}
