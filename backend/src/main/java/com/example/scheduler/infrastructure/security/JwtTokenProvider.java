package com.example.scheduler.infrastructure.security;

import com.example.scheduler.domain.model.Credential;
import com.example.scheduler.infrastructure.util.JwtUtil;
import com.example.scheduler.infrastructure.util.SecretUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    private final SecretKey accessSecretKey;
    private final long accessTokenValidityMillis;
    private final SecretKey refreshSecretKey;
    private final long refreshTokenValidityMillis;
    private final JwtUtil jwtUtil;

    public JwtTokenProvider(@Value("${jwt.access.secret}") String accessSecretKey,
                            @Value("${jwt.access.ttl}") String accessTokenValidityMillis,
                            @Value("${jwt.refresh.secret}") String refreshSecretKey,
                            @Value("${jwt.refresh.ttl}") String refreshTokenValidityMillis,
                            JwtUtil jwtUtil) {
        this.accessSecretKey = SecretUtil.hmacShaKeyFor(accessSecretKey);
        this.accessTokenValidityMillis = Long.parseLong(accessTokenValidityMillis);
        this.refreshSecretKey = SecretUtil.hmacShaKeyFor(refreshSecretKey);
        this.refreshTokenValidityMillis = Long.parseLong(refreshTokenValidityMillis);
        this.jwtUtil = jwtUtil;
    }

    public String generateAccessToken(Credential user) {
        return jwtUtil.generateAccessToken(user, accessTokenValidityMillis, accessSecretKey);
    }

    public String generateRefreshToken(Credential user) {
        return jwtUtil.generateRefreshToken(user, refreshTokenValidityMillis, refreshSecretKey);
    }

    public String getUsernameFromAccessToken(String accessToken) {
        return jwtUtil.extractUserName(accessToken, accessSecretKey);
    }

    public UUID getUserIdFromAccessToken(String accessToken) {
        return jwtUtil.extractUserId(accessToken, accessSecretKey);
    }

    public String getUserRoleFromAccessToken(String accessToken) {
        return jwtUtil.extractUserRole(accessToken, accessSecretKey);
    }

    public String getUsernameFromRefreshToken(String refreshToken) {
        return jwtUtil.extractUserName(refreshToken, refreshSecretKey);
    }

    public boolean isAccessTokenValid(String accessToken) {
        return jwtUtil.isTokenValid(accessToken, accessSecretKey);
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        return jwtUtil.isTokenValid(refreshToken, refreshSecretKey);
    }
}