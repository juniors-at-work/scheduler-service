package com.example.scheduler.application.service;

import com.example.scheduler.adapters.dto.AuthResponse;
import com.example.scheduler.adapters.fixture.TestRegisterRequests;
import com.example.scheduler.domain.fixture.TestUsers;
import com.example.scheduler.domain.repository.UserRepository;
import com.example.scheduler.infrastructure.config.TestClockConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class UserServiceImplTest {

    private UserRepository mockUserRepository;
    private AuthService mockAuthService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        mockUserRepository = Mockito.mock(UserRepository.class);
        mockAuthService = Mockito.mock(AuthService.class);

        PasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(
                "noop",
                Map.of("noop", NoOpPasswordEncoder.getInstance())
        );

        userService = new UserServiceImpl(
                mockUserRepository,
                passwordEncoder,
                mockAuthService,
                TestClockConfig.fixedClock()
        );
    }

    @Test
    void givenRequestIsNull_WhenRegisterUser_ThenThrowNullPointerException() {
        Throwable throwable = catchThrowable(() -> userService.registerUser(null));

        then(throwable)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("request cannot be null");
    }

    @Test
    void givenRequestNotNull_WhenRegisterUser_ThenRegisterUserAndReturnTokenPair() {
        // репозиторий должен принять именно TestUsers.ALICE -> фиксим UUID
        given(mockUserRepository.insert(TestUsers.ALICE)).willReturn(TestUsers.ALICE);
        // после вставки сервис должен вызвать выпуск токенов
        given(mockAuthService.createTokens(any()))
                .willReturn(new AuthResponse("ACCESS", "REFRESH"));

        AuthResponse response;
        try (var ignored = new MockUUID(UUID.fromString("d3e68c3b-2d6d-48a1-a037-99a390e9433e"))) {
            response = userService.registerUser(TestRegisterRequests.ALICE);
        }

        then(response).isEqualTo(new AuthResponse("ACCESS", "REFRESH"));
    }

    private static class MockUUID implements AutoCloseable {
        private final MockedStatic<UUID> mockUUID;

        MockUUID(UUID uuid) {
            this.mockUUID = Mockito.mockStatic(UUID.class, Mockito.CALLS_REAL_METHODS);
            this.mockUUID.when(UUID::randomUUID).thenReturn(uuid);
        }

        @Override
        public void close() {
            mockUUID.close();
        }
    }
}