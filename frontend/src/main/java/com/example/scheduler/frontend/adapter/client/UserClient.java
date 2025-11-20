package com.example.scheduler.frontend.adapter.client;

import com.example.scheduler.frontend.adapter.dto.TokenPairDto;
import com.example.scheduler.frontend.adapter.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserClient {
    private static final String REGISTER_ENDPOINT = "/api/v1/public/auth/register";
    private final RestClient client;

    public UserClient(@Value("${backend.url}") String baseUrl) {
        this.client = RestClient.create(baseUrl);
    }

    public TokenPairDto registerUser(UserDto user) {
        return client.post()
                .uri(REGISTER_ENDPOINT)
                .body(user)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(TokenPairDto.class);
    }
}
