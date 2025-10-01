package com.example.scheduler.infrastructure.repository;

import com.example.scheduler.domain.exception.ProfileAlreadyExistException;
import com.example.scheduler.domain.exception.ProfileNotFoundException;
import com.example.scheduler.domain.model.Profile;
import com.example.scheduler.domain.repository.ProfileRepository;
import com.example.scheduler.domain.fixture.TestCredentials;
import com.example.scheduler.domain.fixture.TestProfiles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

@JdbcTest
@ContextConfiguration(classes = {ProfileRepositoryImpl.class})
class ProfileRepositoryImplTest {

    @Autowired
    private ProfileRepository repository;

    @Test
    void givenProfileExist_WhenFindByUserId_ThenReturnProfile() {
        UUID userId = TestCredentials.alice().getId();

        Optional<Profile> profile = repository.findByUserId(userId);

        then(profile).hasValue(TestProfiles.alice());
    }

    @Test
    void givenProfileNotExist_WhenFindByUserId_ThenReturnEmptyOptional() {
        UUID userId = TestCredentials.bob().getId();

        Optional<Profile> profile = repository.findByUserId(userId);

        then(profile).isEmpty();
    }

    @Test
    void givenProfileNotExist_WhenInsert_ThenInsertProfileIntoDatabaseAndReturnInsertedProfile() {
        Profile profile = TestProfiles.bob();

        Profile inserted = repository.insert(profile);

        then(repository.findByUserId(profile.userId())).hasValue(profile);
        then(inserted).isEqualTo(profile);
    }

    @Test
    void givenProfileExist_WhenInsert_ThenThrowProfileAlreadyExistException() {
        Profile profile = TestProfiles.alice();

        Throwable throwable = catchThrowable(() -> repository.insert(profile));

        then(throwable)
                .isInstanceOf(ProfileAlreadyExistException.class)
                .hasMessage("profile already exists for user " + profile.userId());
    }

    @Test
    void givenProfileExist_WhenUpdate_ThenUpdateProfileInDatabaseAndReturnUpdatedProfile() {
        Profile profile = TestProfiles.aliceUpdated();

        Profile saved = repository.update(profile);

        then(repository.findByUserId(profile.userId())).hasValue(profile);
        then(saved).isEqualTo(profile);
    }

    @Test
    void givenProfileNotExist_WhenUpdate_ThenThrowProfileNotFoundException() {
        Profile profile = TestProfiles.bobUpdated();

        Throwable throwable = catchThrowable(() -> repository.update(profile));

        then(throwable)
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessage("profile not found for user " + profile.userId());
    }
}
