package com.example.scheduler.infrastructure.util;

import com.example.scheduler.domain.exception.InvalidTokenException;
import com.example.scheduler.domain.model.Credential;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    private static final String USERNAME_CLAIM_NAME = "username";
    private static final String ROLE_CLAIM_NAME = "role";

    private final Clock clock;
    private final io.jsonwebtoken.Clock jwtClock;

    public JwtUtil(Clock clock) {
        this.clock = clock;
        this.jwtClock = () -> Date.from(Instant.now(clock));
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // length of “Bearer “
        }
        return null;
    }

    public String generateAccessToken(Credential user, long validityInMillis, SecretKey secretKey) {
        Instant accessExpiration = Instant.now(clock).plus(validityInMillis, ChronoUnit.MILLIS);
        String role = user.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(user.getId().toString())
                .claims()
                .add(USERNAME_CLAIM_NAME, user.getUsername())
                .add(ROLE_CLAIM_NAME, role)
                .and()
                .expiration(Date.from(accessExpiration))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(Credential user, long validityInMillis, SecretKey secretKey) {
        Instant refreshExpiration = Instant.now(clock).plus(validityInMillis, ChronoUnit.MILLIS);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim(USERNAME_CLAIM_NAME, user.getUsername())
                .expiration(Date.from(refreshExpiration))
                .signWith(secretKey)
                .compact();
    }

    public boolean isTokenValid(String token, SecretKey secret) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .clock(jwtClock)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token expired", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported jwt", e);
        } catch (MalformedJwtException e) {
            log.error("Malformed jwt", e);
        } catch (SignatureException e) {
            log.error("Invalid signature", e);
        } catch (Exception e) {
            log.error("invalid token", e);
        }
        return false;
    }

    public String extractUserName(String token, SecretKey secret) {
        return extractClaims(token, secret).get(USERNAME_CLAIM_NAME, String.class);
    }

    public UUID extractUserId(String token, SecretKey secret) {
        return UUID.fromString(extractClaims(token, secret).getSubject());
    }

    public String extractUserRole(String token, SecretKey secret) {
        return extractClaims(token, secret).get(ROLE_CLAIM_NAME, String.class);
    }

    public Claims extractClaims(String token, SecretKey secret) {
        try {
            return Jwts.parser()
                    .verifyWith(secret)
                    .clock(jwtClock)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new InvalidTokenException(e.getMessage());
        }
    }
}
