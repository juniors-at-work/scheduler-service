package com.example.scheduler.adapters.web.profile;

import com.example.scheduler.adapters.web.Headers;
import com.example.scheduler.application.service.ProfileService;
import com.example.scheduler.domain.model.Credential;
import com.example.scheduler.adapters.fixture.TestCreateProfileRequests;
import com.example.scheduler.domain.fixture.TestCredentials;
import com.example.scheduler.adapters.fixture.TestProfileResponses;
import com.example.scheduler.infrastructure.config.WithSecurityStubs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

@WebMvcTest(ProfileController.class)
@WithSecurityStubs
class ProfileControllerTest {

    private static final String BASE_URL = "/profiles";

    @MockitoBean
    private ProfileService mockProfileService;

    @Autowired
    private MockMvcTester mockMvcTester;

    @ParameterizedTest
    @ValueSource(strings = {"null", "\"\"", "\" \""})
    void givenFullNameIsBlank_WhenCreateProfile_ThenRespondWithBadRequest(String fullName) {

        MvcTestResult response = mockMvcTester
                .post()
                .with(authentication(toAuthentication(TestCredentials.alice())))
                .uri(BASE_URL)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.AUTH_HEADER, TestCredentials.alice().getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "fullName": %s,
                          "timezone": "Europe/Paris",
                          "description": "Test description",
                          "logo": "Logo"
                        }
                        """.formatted(fullName))
                .exchange();

        then(response).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(strings = {"null", "\"\"", "\" \""})
    void givenTimezoneIsBlank_WhenCreateProfile_ThenRespondWithBadRequest(String timezone) {

        MvcTestResult response = mockMvcTester
                .post()
                .with(authentication(toAuthentication(TestCredentials.alice())))
                .uri(BASE_URL)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.AUTH_HEADER, TestCredentials.alice().getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "fullName": "Alice Arno",
                          "timezone": %s,
                          "description": "Test description",
                          "logo": "Logo"
                        }
                        """.formatted(timezone))
                .exchange();

        then(response).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void givenNoAuthHeader_WhenCreateProfile_ThenRespondWithBadRequest() {

        MvcTestResult response = mockMvcTester
                .post()
                .with(authentication(toAuthentication(TestCredentials.alice())))
                .uri(BASE_URL)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "fullName": "Alice Arno",
                          "timezone": "Europe/Paris",
                          "description": "Test description",
                          "logo": "Logo"
                        }
                        """)
                .exchange();

        then(response).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void givenAuthHeaderHasWrongFormat_WhenCreateProfile_ThenRespondWithBadRequest() {

        MvcTestResult response = mockMvcTester
                .post()
                .with(authentication(toAuthentication(TestCredentials.alice())))
                .uri(BASE_URL)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.AUTH_HEADER, TestCredentials.alice().getUsername())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "fullName": "Alice Arno",
                          "timezone": "Europe/Paris",
                          "description": "Test description",
                          "logo": "Logo"
                        }
                        """)
                .exchange();

        then(response).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void givenCorrectBodyAndAuthHeader_WhenCreateProfile_ThenRespondWithProfileCreated() {
        given(mockProfileService.createProfile(
                TestCredentials.alice().getId(),
                TestCreateProfileRequests.alice(),
                TestCredentials.alice())
        ).willReturn(TestProfileResponses.alice());

        MvcTestResult response = mockMvcTester
                .post()
                .with(authentication(toAuthentication(TestCredentials.alice())))
                .uri(BASE_URL)
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.AUTH_HEADER, TestCredentials.alice().getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "fullName": "Alice Arno",
                            "timezone": "Europe/Paris",
                            "description": "Test description",
                            "logo": "Logo"
                        }
                        """)
                .exchange();

        then(response)
                .hasStatus(HttpStatus.CREATED)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson().isEqualTo("""
                        {
                            "userId": "d3e68c3b-2d6d-48a1-a037-99a390e9433e",
                            "username": "alice",
                            "fullName": "Alice Arno",
                            "timezone": "Europe/Paris",
                            "description": "Test description",
                            "isActive": true,
                            "logo": "Logo"
                        }
                        """);
    }

    @Test
    void givenNoAuthHeader_WhenGetProfile_ThenRespondWithBadRequest() {

        MvcTestResult response = mockMvcTester
                .get()
                .with(authentication(toAuthentication(TestCredentials.alice())))
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        then(response).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void givenAuthHeaderHasWrongFormat_WhenGetProfile_ThenRespondWithBadRequest() {

        MvcTestResult response = mockMvcTester
                .get()
                .with(authentication(toAuthentication(TestCredentials.alice())))
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.AUTH_HEADER, TestCredentials.alice().getUsername())
                .exchange();

        then(response).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void givenCorrectAuthHeader_WhenGetProfile_ThenRespondWithProfileRequested() {
        given(mockProfileService.getProfile(
                TestCredentials.alice().getId(),
                TestCredentials.alice()
        )).willReturn(TestProfileResponses.alice());

        MvcTestResult response = mockMvcTester
                .get()
                .with(authentication(toAuthentication(TestCredentials.alice())))
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.AUTH_HEADER, TestCredentials.alice().getId())
                .exchange();

        then(response)
                .hasStatus(HttpStatus.OK)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson().isEqualTo("""
                                                {
                            "userId": "d3e68c3b-2d6d-48a1-a037-99a390e9433e",
                            "username": "alice",
                            "fullName": "Alice Arno",
                            "timezone": "Europe/Paris",
                            "description": "Test description",
                            "isActive": true,
                            "logo": "Logo"
                        }
                        """);
    }

    private Authentication toAuthentication(Credential credential) {
        return new UsernamePasswordAuthenticationToken(credential, null, credential.getAuthorities());
    }
}
