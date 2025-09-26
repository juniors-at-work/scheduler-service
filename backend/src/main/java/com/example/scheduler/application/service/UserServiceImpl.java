package com.example.scheduler.application.service;

import com.example.scheduler.adapters.dto.AuthResponse;
import com.example.scheduler.adapters.dto.user.RegisterRequest;
import com.example.scheduler.domain.exception.UserNotAuthorizedException;
import com.example.scheduler.domain.model.Credential;
import com.example.scheduler.domain.model.User;
import com.example.scheduler.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final AuthService authService;
    private final Clock clock;

    public UserServiceImpl(
            UserRepository userRepo,
            PasswordEncoder encoder,
            AuthService authService,
            Clock clock
    ) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.authService = authService;
        this.clock = clock;
    }

    @Override
    public AuthResponse registerUser(RegisterRequest request) {
        Objects.requireNonNull(request, "request cannot be null");
        User prepared = createUser(request, encoder, clock);
        User registered = userRepo.insert(prepared);
        log.info("Registered new user [{}]", registered.id());
        log.debug("User registered = {}", registered);

        Credential credential = Credential.fromUser(registered);
        return authService.createTokens(credential);
    }

    @Override
    public AuthResponse loginUser(String username, String password) {
        Optional<User> userOptional = userRepo.findByUsername(username);
        if (userOptional.isEmpty() || !encoder.matches(password, userOptional.get().passwordHash())) {
            throw new UserNotAuthorizedException("Username or password does not match");
        }
        Credential credential = Credential.fromUser(userOptional.get());
        return authService.createTokens(credential);
    }

    private User createUser(RegisterRequest registerRequest, PasswordEncoder passwordEncoder, Clock clock) {
        String passwordHash = passwordEncoder.encode(registerRequest.password());
        Instant now = Instant.now(clock);
        return User.builder()
                .id(UUID.randomUUID())
                .username(registerRequest.username())
                .email(registerRequest.email())
                .passwordHash(passwordHash)
                .role("USER")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}