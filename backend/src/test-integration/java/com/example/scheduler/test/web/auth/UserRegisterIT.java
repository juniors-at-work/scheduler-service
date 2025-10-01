package com.example.scheduler.test.web.auth;

import com.example.scheduler.test.config.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class UserRegisterIT {

    private static final String HOST = "http://localhost";
    private static final String BASE_URL = "/api/v1/public/auth/register";

    @LocalServerPort
    private int port;

    @Test
    void givenUsernameIsNull_WhenRegister_ThenRespondWithBadRequest() {
        webClient().post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "password": "12345",
                          "email": "charlie@mail.com"
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                        {
                          "timestamp": "2001-02-03T04:05:06Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "username must not be null",
                          "path": "/api/v1/public/auth/register"
                        }
                        """, JsonCompareMode.STRICT);
    }

    @Test
    void givenUsernameLongerThan255_WhenRegister_ThenRespondWithBadRequest() {
        String username = "a".repeat(256);

        webClient().post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "username": "%s",
                          "password": "12345",
                          "email": "charlie@mail.com"
                        }
                        """.formatted(username))
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                        {
                          "timestamp": "2001-02-03T04:05:06Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "username size must be between 0 and 255",
                          "path": "/api/v1/public/auth/register"
                        }
                        """, JsonCompareMode.STRICT);
    }

    @Test
    void givenPasswordIsNull_WhenRegister_ThenRespondWithBadRequest() {
        webClient().post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "username": "charlie",
                          "email": "charlie@mail.com"
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                        {
                          "timestamp": "2001-02-03T04:05:06Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "password must not be null",
                          "path": "/api/v1/public/auth/register"
                        }
                        """, JsonCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"null", "\"\""})
    void givenEmailIsEmpty_WhenRegister_ThenRespondWithBadRequest(String email) {
        webClient().post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "username": "charlie",
                          "password": "12345",
                          "email": %s
                        }
                        """.formatted(email))
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                        {
                          "timestamp": "2001-02-03T04:05:06Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "email must not be empty",
                          "path": "/api/v1/public/auth/register"
                        }
                        """, JsonCompareMode.STRICT);
    }

    @Test
    void givenEmailIsMalformed_WhenRegister_ThenRespondWithBadRequest() {
        webClient().post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "username": "charlie",
                          "password": "12345",
                          "email": " "
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                        {
                          "timestamp": "2001-02-03T04:05:06Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "email must be a well-formed email address",
                          "path": "/api/v1/public/auth/register"
                        }
                        """, JsonCompareMode.STRICT);
    }

    @Test
    void givenEmailIsLongerThan255_WhenRegister_ThenRespondWithBadRequest() {
        String longEmail = "a".repeat(64)
                + "@" + "b".repeat(63)
                + "." + "c".repeat(63)
                + "." + "d".repeat(59)
                + ".com";

        webClient().post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "username": "charlie",
                          "password": "12345",
                          "email": "%s"
                        }
                        """.formatted(longEmail))
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                        {
                          "timestamp": "2001-02-03T04:05:06Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "email size must be between 0 and 255",
                          "path": "/api/v1/public/auth/register"
                        }
                        """, JsonCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"alice", "ALICE"})
    void givenUsernameExistsIgnoreCase_WhenRegister_ThenRespondWithConflict(String username) {
        webClient().post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "username": "%s",
                          "password": "12345",
                          "email": "charlie@mail.com"
                        }
                        """.formatted(username))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.timestamp").isNotEmpty()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.error").isEqualTo("Conflict")
                .jsonPath("$.path").isEqualTo("/api/v1/public/auth/register")
                .jsonPath("$.message").value(m -> assertThat(String.valueOf(m)).isNotBlank());
    }

    @ParameterizedTest
    @ValueSource(strings = {"alice@mail.com", "ALICE@MAIL.COM"})
    void givenEmailExistsIgnoreCase_WhenRegister_ThenRespondWithConflict(String email) {
        webClient().post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "username": "charlie",
                          "password": "12345",
                          "email": "%s"
                        }
                        """.formatted(email))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().json("""
                        {
                          "timestamp": "2001-02-03T04:05:06Z",
                          "status": 409,
                          "error": "Conflict",
                          "message": "Email already exists: %s",
                          "path": "/api/v1/public/auth/register"
                        }
                        """.formatted(email), JsonCompareMode.STRICT);
    }

    @Test
    void givenCorrectUsernameAndPasswordAndEmail_WhenRegister_ThenRespondWithCreatedAndTokenPair() {
        webClient().post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "username": "charlie",
                          "password": "12345",
                          "email": "charlie@mail.com"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.accessToken").isNotEmpty()
                .jsonPath("$.refreshToken").isNotEmpty()
                .jsonPath("$.size()").isEqualTo(2);
    }

    private WebTestClient webClient() {
        return WebTestClient.bindToServer()
                .baseUrl(HOST + ":" + port + BASE_URL)
                .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US")
                .build();
    }
}
