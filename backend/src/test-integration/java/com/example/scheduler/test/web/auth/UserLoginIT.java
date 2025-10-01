package com.example.scheduler.test.web.auth;

import com.example.scheduler.test.config.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.reactive.server.WebTestClient;

@IntegrationTest
class UserLoginIT {

    private static final String HOST = "http://localhost";
    private static final String BASE_URL = "/api/v1/public/auth/login";

    @LocalServerPort
    private int port;

    @Test
    void givenUserNotRegistered_WhenLogin_ThenRespondWithUnauthorized() {
        webClient().post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "username": "david",
                          "password": "12345"
                        }
                        """)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                        {
                          "timestamp": "2001-02-03T04:05:06Z",
                          "status": 401,
                          "error": "Unauthorized",
                          "message": "Username or password does not match",
                          "path": "/api/v1/public/auth/login"
                        }
                        """, JsonCompareMode.STRICT);
    }

    @Test
    void givenRegisteredUserWithWrongPassword_WhenLogin_ThenRespondWithUnauthorized() {
        webClient().post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "username": "alice",
                          "password": "54321"
                        }
                        """)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                        {
                          "timestamp": "2001-02-03T04:05:06Z",
                          "status": 401,
                          "error": "Unauthorized",
                          "message": "Username or password does not match",
                          "path": "/api/v1/public/auth/login"
                        }
                        """, JsonCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"alice", "ALICE"})
    void givenRegisteredUserIgnoreUsernameCase_WhenLogin_ThenRespondWithOkAndAuthResponse(String username) {
        webClient().post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "username": "%s",
                          "password": "12345"
                        }
                        """.formatted(username))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.size()").isEqualTo(2)
                .jsonPath("$.accessToken").isNotEmpty()
                .jsonPath("$.refreshToken").isNotEmpty();
    }

    private WebTestClient webClient() {
        return WebTestClient.bindToServer()
                .baseUrl(HOST + ":" + port + BASE_URL)
                .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US")
                .build();
    }
}
