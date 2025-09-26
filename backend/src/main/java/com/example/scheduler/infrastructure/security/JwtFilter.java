package com.example.scheduler.infrastructure.security;

import com.example.scheduler.domain.model.Credential;
import com.example.scheduler.infrastructure.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final JwtTokenProvider provider;

    public JwtFilter(JwtTokenProvider provider, JwtUtil jwtUtil) {
        this.provider = provider;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = jwtUtil.extractTokenFromRequest(request);
        if (Objects.nonNull(accessToken) && provider.isAccessTokenValid(accessToken)) {
            UUID userId = provider.getUserIdFromAccessToken(accessToken);
            String username = provider.getUsernameFromAccessToken(accessToken);
            String userRole = provider.getUserRoleFromAccessToken(accessToken);
            Credential credential = new Credential(userId, username, null, userRole,
                    true);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(credential, null,
                            credential.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
