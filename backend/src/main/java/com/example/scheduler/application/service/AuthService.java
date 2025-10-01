package com.example.scheduler.application.service;

import com.example.scheduler.adapters.dto.AuthResponse;
import com.example.scheduler.domain.exception.TokenRefreshException;
import com.example.scheduler.domain.model.Credential;
import com.example.scheduler.domain.model.User;
import com.example.scheduler.domain.repository.JwtRepository;
import com.example.scheduler.domain.repository.UserRepository;
import com.example.scheduler.infrastructure.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtRepository tokenRepository;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       JwtRepository tokenRepository,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse createTokens(Credential userDetails) {
        return createPair(userDetails);
    }

    public AuthResponse refreshTokens(String refreshToken) {
        String username = tokenProvider.getUsernameFromRefreshToken(refreshToken);
        User user = userRepository.findByUsername(username).orElseThrow();
        Credential credential = Credential.fromUser(user);

        if (!tokenProvider.isRefreshTokenValid(refreshToken) ||
                !tokenRepository.contains(credential.getId(), refreshToken)) {
            throw new TokenRefreshException(refreshToken);
        }

        return createPair(credential);
    }

    private AuthResponse createPair(Credential userDetails) {
        String accessToken = tokenProvider.generateAccessToken(userDetails);
        String newRefreshToken = createRefreshToken(userDetails);

        return new AuthResponse(accessToken, newRefreshToken);
    }

    private String createRefreshToken(Credential userDetails) {
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);
        tokenRepository.deleteByUserId(userDetails.getId());
        tokenRepository.save(userDetails.getId(), refreshToken);
        return refreshToken;
    }
}
