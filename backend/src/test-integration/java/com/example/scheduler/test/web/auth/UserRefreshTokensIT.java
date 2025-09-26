package com.example.scheduler.test.web.auth;

import com.example.scheduler.test.config.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.reactive.server.WebTestClient;

@IntegrationTest
class UserRefreshTokensIT {

    private static final String HOST = "http://localhost";
    private static final String BASE_URL = "/api/v1/public/auth/refresh";

    @LocalServerPort
    private int port;

    @Test
    void givenRefreshTokenExpired_WhenRefreshTokens_ThenRespondWithUnauthorized() {
        webClient().post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5ZTdmN2UzMy00NTc0LTQzYjYtODNkOC1kZWQ3ZjE2OWMwM2YiLC\
                        J1c2VybmFtZSI6ImJvYiIsImV4cCI6OTgxMTczMTA1fQ.IrR7tKqE_vDHKNGBTEo-b_wRCQyuSchemNAlevxllycYDSIY8o\
                        VQ0sqYzfMJIZ5kvzvDf_fVqkvCS0NYq6omqA"
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
                          "message": "JWT expired 1789 milliseconds ago at 2001-02-03T04:05:05.000Z. Current time: 2001\
                        -02-03T04:05:06.789Z. Allowed clock skew: 0 milliseconds.",
                          "path": "/api/v1/public/auth/refresh"
                        }
                        """, JsonCompareMode.STRICT);
    }

    @Test
    void givenRefreshTokenNotExpired_WhenRefreshTokens_ThenRespondWithOkAndAuthResponse() {
        webClient().post()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkM2U2OGMzYi0yZDZkLTQ4YTEtYTAzNy05OWEzOTBlOTQzM2UiLC\
                        J1c2VybmFtZSI6ImFsaWNlIiwiZXhwIjo5ODEyNTk1MDZ9.SmrevpAZjUVIG78-YOBZvNhu91wjSgd37l19_3w3pl7Qbf7r\
                        pUuOEQquY6WwJYMzB37z8tPKawNpFWgT01csxA"
                        }
                        """)
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
