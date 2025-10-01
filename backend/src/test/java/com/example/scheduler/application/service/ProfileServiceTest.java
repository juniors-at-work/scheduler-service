package com.example.scheduler.application.service;

import com.example.scheduler.adapters.fixture.TestUpdateProfileRequest;
import com.example.scheduler.domain.model.Profile;
import com.example.scheduler.infrastructure.config.TestClockConfig;
import com.example.scheduler.adapters.dto.ProfileResponse;
import com.example.scheduler.adapters.fixture.TestCreateProfileRequests;
import com.example.scheduler.adapters.fixture.TestProfileResponses;
import com.example.scheduler.adapters.mapper.ProfileMapperImpl;
import com.example.scheduler.domain.exception.NotEnoughAuthorityException;
import com.example.scheduler.domain.exception.ProfileNotFoundException;
import com.example.scheduler.domain.exception.UserNotAuthorizedException;
import com.example.scheduler.domain.fixture.TestCredentials;
import com.example.scheduler.domain.fixture.TestProfiles;
import com.example.scheduler.domain.repository.ProfileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

class ProfileServiceTest {

    private ProfileRepository mockRepository;

    private ProfileService service;

    private final ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);

    @BeforeEach
    void setUp() {
        mockRepository = Mockito.mock(ProfileRepository.class);
        service = new ProfileService(mockRepository, new ProfileMapperImpl(), TestClockConfig.fixedClock());
    }

    @Test
    void givenUserIdIsNull_WhenCreateProfile_ThenThrowNullPointerException() {

        Throwable throwable = catchThrowable(() -> service.createProfile(
                null,
                TestCreateProfileRequests.alice(),
                TestCredentials.alice()
        ));

        then(throwable)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("userId cannot be null");
    }

    @Test
    void givenRequestIsNull_WhenCreateProfile_ThenThrowNullPointerException() {

        Throwable throwable = catchThrowable(() -> service.createProfile(
                TestCredentials.alice().getId(),
                null,
                TestCredentials.alice()
        ));

        then(throwable)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("request cannot be null");
    }

    @Test
    void givenCredentialIsNull_WhenCreateProfile_ThenThrowUserNotAuthorizedException() {

        Throwable throwable = catchThrowable(() -> service.createProfile(
                TestCredentials.alice().getId(),
                TestCreateProfileRequests.alice(),
                null
        ));

        then(throwable)
                .isInstanceOf(UserNotAuthorizedException.class)
                .hasMessage("user %s is not authorized", TestCredentials.alice().getId());
    }

    @Test
    void givenCredentialNotMatchUserId_WhenCreateProfile_ThenThrowNotEnoughAuthorityException() {

        Throwable throwable = catchThrowable(() -> service.createProfile(
                TestCredentials.alice().getId(),
                TestCreateProfileRequests.alice(),
                TestCredentials.bob()
        ));

        then(throwable)
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("user can create profile for themselves only");
    }

    @Test
    void givenRequestNotNullAndCredentialMatchUserId_WhenCreateProfile_ThenCreateProfileAndReturnIt() {
        given(mockRepository.insert(TestProfiles.alice())).willReturn(TestProfiles.alice());

        ProfileResponse response = service.createProfile(
                TestCredentials.alice().getId(),
                TestCreateProfileRequests.alice(),
                TestCredentials.alice()
        );

        then(response).isEqualTo(TestProfileResponses.alice());
    }

    @Test
    void givenUserIdIsNull_WhenGetProfile_ThenThrowNullPointerException() {

        Throwable throwable = catchThrowable(() -> service.getProfile(
                null,
                TestCredentials.alice()
        ));

        then(throwable)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("userId cannot be null");
    }

    @Test
    void givenCredentialIsNull_WhenGetProfile_ThenThrowUserNotAuthorizedException() {

        Throwable throwable = catchThrowable(() -> service.getProfile(
                TestCredentials.alice().getId(),
                null
        ));

        then(throwable)
                .isInstanceOf(UserNotAuthorizedException.class)
                .hasMessage("user %s is not authorized", TestCredentials.alice().getId());
    }

    @Test
    void givenCredentialNotMatchUserId_WhenGetProfile_ThenThrowNotEnoughAuthorityException() {

        Throwable throwable = catchThrowable(() -> service.getProfile(
                TestCredentials.alice().getId(),
                TestCredentials.bob()
        ));

        then(throwable)
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("user can get profile for themselves only");
    }

    @Test
    void givenProfileNotExist_WhenGetProfile_ThenThrowProfileNotFoundException() {
        given(mockRepository.findByUserId(TestCredentials.alice().getId())).willReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> service.getProfile(
                TestCredentials.alice().getId(),
                TestCredentials.alice()
        ));

        then(throwable)
                .isInstanceOf(ProfileNotFoundException.class)
                .hasMessage("profile not found for user " + TestCredentials.alice().getId());
    }

    @Test
    void givenProfileExistAndCredentialMatchUserId_WhenGetProfile_ThenReturnProfile() {
        given(mockRepository.findByUserId(TestCredentials.alice().getId()))
                .willReturn(Optional.of(TestProfiles.alice()));

        ProfileResponse response = service.getProfile(
                TestCredentials.alice().getId(),
                TestCredentials.alice()
        );

        then(response).isEqualTo(TestProfileResponses.alice());
    }

    @Test
    void updateAllPossibleFieldsTest() {
        given(mockRepository.findByUserId(TestCredentials.alice().getId()))
                .willReturn(Optional.of(TestProfiles.alice()));
        given(mockRepository.update(Mockito.any()))
                .willReturn(TestProfiles.aliceUpdated());

        service.updateProfile(TestCredentials.alice().getId(), TestUpdateProfileRequest.aliceUpdateEveryField(),
                TestCredentials.alice());

        Mockito.verify(mockRepository).update(profileCaptor.capture());
        Profile capturedProfile = profileCaptor.getValue();

        Assertions.assertEquals("Alice Arnold", capturedProfile.fullName());
        Assertions.assertEquals(ZoneId.of("UTC"), capturedProfile.timezone());
        Assertions.assertEquals("Test description updated", capturedProfile.description());
        Assertions.assertEquals("Logo updated", capturedProfile.logo());
    }

    @Test
    void updateOnlyNameTest() {
        given(mockRepository.findByUserId(TestCredentials.alice().getId()))
                .willReturn(Optional.of(TestProfiles.alice()));
        given(mockRepository.update(Mockito.any()))
                .willReturn(TestProfiles.aliceUpdated());

        service.updateProfile(TestCredentials.alice().getId(), TestUpdateProfileRequest.aliceUpdateOnlyName(),
                TestCredentials.alice());

        Mockito.verify(mockRepository).update(profileCaptor.capture());
        Profile capturedProfile = profileCaptor.getValue();

        Assertions.assertEquals("Alice Arne", capturedProfile.fullName());
        Assertions.assertEquals(ZoneId.of("Europe/Paris"), capturedProfile.timezone());
        Assertions.assertEquals("Test description", capturedProfile.description());
        Assertions.assertEquals("Logo", capturedProfile.logo());
    }
}
