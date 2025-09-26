package com.example.scheduler.application.service;

import com.example.scheduler.adapters.dto.EventFullDto;
import com.example.scheduler.adapters.fixture.TestEventFullDtos;
import com.example.scheduler.domain.exception.NotEnoughAuthorityException;
import com.example.scheduler.domain.exception.NotFoundException;
import com.example.scheduler.domain.exception.UserNotAuthorizedException;
import com.example.scheduler.domain.fixture.TestCredentials;
import com.example.scheduler.domain.fixture.TestEvents;
import com.example.scheduler.domain.repository.EventRepository;
import com.example.scheduler.infrastructure.mapper.EventMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

class EventServiceImplTest {

    private EventRepository mockRepository;

    private EventService service;

    @BeforeEach
    void setUp() {
        mockRepository = Mockito.mock(EventRepository.class);
        service = new EventServiceImpl(mockRepository, new EventMapperImpl());
    }

    @Test
    void givenUserIdIsNull_WhenGetEventById_ThenThrowNullPinterException() {

        Throwable throwable = catchThrowable(() -> service.getEventById(
                null,
                TestEvents.demo().id(),
                TestCredentials.alice()
        ));

        then(throwable)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("userId cannot be null");
    }

    @Test
    void givenEventIdIsNull_WhenGetEventById_ThenThrowNullPinterException() {

        Throwable throwable = catchThrowable(() -> service.getEventById(
                TestCredentials.alice().getId(),
                null,
                TestCredentials.alice()
        ));

        then(throwable)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("eventId cannot be null");
    }

    @Test
    void givenCurrentUserIsNull_WhenGetEventById_ThenThrowUserNotAuthorizedException() {

        Throwable throwable = catchThrowable(() -> service.getEventById(
                TestCredentials.alice().getId(),
                TestEvents.demo().id(),
                null
        ));

        then(throwable)
                .isInstanceOf(UserNotAuthorizedException.class)
                .hasMessage("User [%s] is not authorized", TestCredentials.alice().getId());
    }

    @Test
    void givenUserIdNotMatchCurrentUser_WhenGetEventById_ThenThrowNotEnoughAuthorityException() {

        Throwable throwable = catchThrowable(() -> service.getEventById(
                TestCredentials.alice().getId(),
                TestEvents.demo().id(),
                TestCredentials.bob()
        ));

        then(throwable)
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("User can get own events only");
    }

    @Test
    void givenUserIdNotMatchEventOwnerId_WhenGetEventById_ThenThrowNotEnoughAuthorityException() {
        given(mockRepository.getEventById(TestEvents.demo().id())).willReturn(Optional.of(TestEvents.demo()));

        Throwable throwable = catchThrowable(() -> service.getEventById(
                TestCredentials.bob().getId(),
                TestEvents.demo().id(),
                TestCredentials.bob()
        ));

        then(throwable)
                .isInstanceOf(NotEnoughAuthorityException.class)
                .hasMessage("User can get own events only");
    }

    @Test
    void givenEventNotExist_WhenGetEventById_ThenThrowNotFoundException() {
        given(mockRepository.getEventById(TestEvents.demo().id())).willReturn(Optional.empty());

        Throwable throwable = catchThrowable(() -> service.getEventById(
                TestCredentials.alice().getId(),
                TestEvents.demo().id(),
                TestCredentials.alice()
        ));

        then(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Event [%s] not found", TestEvents.demo().id());
    }

    @Test
    void givenEventExistAndUserIdMathCurrentUserAndUserIsOwner_WhenGetEventById_ThenReturnEventFullDto() {
        given(mockRepository.getEventById(TestEvents.demo().id())).willReturn(Optional.of(TestEvents.demo()));

        EventFullDto response = service.getEventById(
                TestCredentials.alice().getId(),
                TestEvents.demo().id(),
                TestCredentials.alice()
        );

        then(response).isEqualTo(TestEventFullDtos.demo());
    }
}
