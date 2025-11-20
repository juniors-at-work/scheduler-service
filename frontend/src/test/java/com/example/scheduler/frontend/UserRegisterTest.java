package com.example.scheduler.frontend;

import com.example.scheduler.frontend.adapter.dto.RegisterUserRequest;
import com.example.scheduler.frontend.adapter.dto.TokenPairDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.wiremock.spring.EnableWireMock;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWireMock
class UserRegisterTest {
    private static final String BACKEND_ENDPOINT = "/api/v1/public/auth/register";
    private static final String REGISTER_ENDPOINT = "/register";
    private final MockMvcTester mockMvcTester;
    private final Faker faker;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserRegisterTest(MockMvcTester mockMvcTester, ObjectMapper objectMapper) {
        this.mockMvcTester = mockMvcTester;
        this.faker = new Faker();
        this.objectMapper = objectMapper;
    }

    @Test
    void whenGetRegistrationForm_ThenReturnRegisterViewWithEmptyModel() {
        MvcTestResult response = mockMvcTester
                .get()
                .uri(REGISTER_ENDPOINT)
                .exchange();

        assertThat(response)
                .hasStatus(HttpStatus.OK)
                .hasViewName("register")
                .model().isEmpty();
    }

    @Test
    void whenPasswordAndConfirmPasswordDiffer_ThenReturnRegisterView() {
        RegisterUserRequest request = randomRegisterUserRequest();

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.password())
                .formField("confirmPassword", request.password() + "a")
                .exchange();

        assertThat(response)
                .hasStatus(HttpStatus.OK)
                .hasViewName("register");
    }

    @Test
    void whenPasswordAndConfirmPasswordDiffer_ThenDisplayErrorNotMessage() {
        RegisterUserRequest request = randomRegisterUserRequest();

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.password())
                .formField("confirmPassword", request.password() + "a")
                .exchange();

        assertThat(response).model()
                .containsEntry("error", "Password and confirm password do not match")
                .doesNotContainKey("message");
    }

    @Test
    void whenPasswordAndConfirmPasswordDiffer_ThenRetainEmailAndUsername() {
        RegisterUserRequest request = randomRegisterUserRequest();

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.password())
                .formField("confirmPassword", request.password() + "a")
                .exchange();

        assertThat(response).model()
                .containsEntry("email", request.email())
                .containsEntry("username", request.username());
    }

    @Test
    void whenPasswordEqualsConfirmPassword_ThenSendCorrectRequestToBackend() throws Exception {
        RegisterUserRequest request = randomRegisterUserRequest();
        TokenPairDto tokens = randomTokenDto();
        WireMock.stubFor(WireMock.post(BACKEND_ENDPOINT)
                .willReturn(WireMock.created()
                        .withBody(toJson(tokens))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.password())
                .formField("confirmPassword", request.confirmPassword())
                .exchange();

        WireMock.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo(BACKEND_ENDPOINT))
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalToIgnoreCase(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(HttpHeaders.ACCEPT, WireMock.equalToIgnoreCase(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(WireMock.matchingJsonPath("$.email", WireMock.equalTo(request.email())))
                .withRequestBody(WireMock.matchingJsonPath("$.username", WireMock.equalTo(request.username())))
                .withRequestBody(WireMock.matchingJsonPath("$.password", WireMock.equalTo(request.password()))));
    }

    @Test
    void whenBackendRespondsWithBadRequest_ThenReturnRegisterView() {
        RegisterUserRequest request = randomRegisterUserRequest();
        WireMock.stubFor(WireMock.post(BACKEND_ENDPOINT).willReturn(WireMock.badRequest()));

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.confirmPassword())
                .formField("confirmPassword", request.confirmPassword())
                .exchange();

        assertThat(response)
                .hasStatus(HttpStatus.OK)
                .hasViewName("register");
    }

    @Test
    void whenBackendRespondsWithBadRequest_ThenDisplayErrorNotMessage() {
        RegisterUserRequest request = randomRegisterUserRequest();
        WireMock.stubFor(WireMock.post(BACKEND_ENDPOINT).willReturn(WireMock.badRequest()));

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.confirmPassword())
                .formField("confirmPassword", request.confirmPassword())
                .exchange();

        assertThat(response).model()
                .containsEntry("error", "Incorrect data")
                .doesNotContainKey("message");
    }

    @Test
    void whenBackendRespondsWithBadRequest_ThenRetainEmailAndUsername() {
        RegisterUserRequest request = randomRegisterUserRequest();
        WireMock.stubFor(WireMock.post(BACKEND_ENDPOINT).willReturn(WireMock.badRequest()));

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.confirmPassword())
                .formField("confirmPassword", request.confirmPassword())
                .exchange();

        assertThat(response).model()
                .containsEntry("email", request.email())
                .containsEntry("username", request.username());
    }

    @Test
    void whenBackendRespondsWithConflict_ThenReturnRegisterView() {
        RegisterUserRequest request = randomRegisterUserRequest();
        WireMock.stubFor(WireMock.post(BACKEND_ENDPOINT).willReturn(WireMock.status(HttpStatus.CONFLICT.value())));

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.password())
                .formField("confirmPassword", request.confirmPassword())
                .exchange();

        assertThat(response)
                .hasStatus(HttpStatus.OK)
                .hasViewName("register");
    }

    @Test
    void whenBackendRespondsWithConflict_ThenDisplayErrorNotMessage() {
        RegisterUserRequest request = randomRegisterUserRequest();
        WireMock.stubFor(WireMock.post(BACKEND_ENDPOINT).willReturn(WireMock.status(HttpStatus.CONFLICT.value())));

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.password())
                .formField("confirmPassword", request.confirmPassword())
                .exchange();

        assertThat(response).model()
                .containsEntry("error", "User with such email or username already exists")
                .doesNotContainKey("message");
    }

    @Test
    void whenBackendRespondsWithConflict_ThenRetainEmailAndUsername() {
        RegisterUserRequest request = randomRegisterUserRequest();
        WireMock.stubFor(WireMock.post(BACKEND_ENDPOINT).willReturn(WireMock.status(HttpStatus.CONFLICT.value())));

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.password())
                .formField("confirmPassword", request.confirmPassword())
                .exchange();

        assertThat(response).model()
                .containsEntry("email", request.email())
                .containsEntry("username", request.username());
    }

    @Test
    void whenBackendRespondsWithOther4xx_ThenReturnRegisterView() {
        RegisterUserRequest request = randomRegisterUserRequest();
        int errorCode = randomErrorCode(400, 500, Set.of(400, 409));
        WireMock.stubFor(WireMock.post(BACKEND_ENDPOINT).willReturn(WireMock.status(errorCode)));

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.password())
                .formField("confirmPassword", request.confirmPassword())
                .exchange();

        assertThat(response)
                .hasStatus(HttpStatus.OK)
                .hasViewName("register");
    }

    @Test
    void whenBackendRespondsWithOther4xx_ThenDisplayErrorNotMessage() {
        RegisterUserRequest request = randomRegisterUserRequest();
        int errorCode = randomErrorCode(400, 500, Set.of(400, 409));
        WireMock.stubFor(WireMock.post(BACKEND_ENDPOINT).willReturn(WireMock.status(errorCode)));

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.password())
                .formField("confirmPassword", request.confirmPassword())
                .exchange();

        assertThat(response).model()
                .containsEntry("error", "Something went wrong, try again later")
                .doesNotContainKey("message");
    }

    @Test
    void whenBackendRespondsWithOther4xx_ThenRetainEmailAndUsername() {
        RegisterUserRequest request = randomRegisterUserRequest();
        int errorCode = randomErrorCode(400, 500, Set.of(400, 409));
        WireMock.stubFor(WireMock.post(BACKEND_ENDPOINT).willReturn(WireMock.status(errorCode)));

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.password())
                .formField("confirmPassword", request.confirmPassword())
                .exchange();

        assertThat(response).model()
                .containsEntry("email", request.email())
                .containsEntry("username", request.username());
    }

    @Test
    void whenBackendRespondsWith5xx_ThenReturnRegisterView() {
        RegisterUserRequest request = randomRegisterUserRequest();
        int errorCode = randomErrorCode(500, 600, Set.of());
        WireMock.stubFor(WireMock.post(BACKEND_ENDPOINT).willReturn(WireMock.status(errorCode)));

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.password())
                .formField("confirmPassword", request.confirmPassword())
                .exchange();

        assertThat(response)
                .hasStatus(HttpStatus.OK)
                .hasViewName("register");
    }

    @Test
    void whenBackendRespondsWith5xx_ThenDisplayErrorNotMessage() {
        RegisterUserRequest request = randomRegisterUserRequest();
        int errorCode = randomErrorCode(500, 600, Set.of());
        WireMock.stubFor(WireMock.post(BACKEND_ENDPOINT).willReturn(WireMock.status(errorCode)));

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.password())
                .formField("confirmPassword", request.confirmPassword())
                .exchange();

        assertThat(response).model()
                .containsEntry("error", "Something went wrong, try again later")
                .doesNotContainKey("message");
    }

    @Test
    void whenBackendRespondsWith5xx_ThenRetainEmailAndUsername() {
        RegisterUserRequest request = randomRegisterUserRequest();
        int errorCode = randomErrorCode(500, 600, Set.of());
        WireMock.stubFor(WireMock.post(BACKEND_ENDPOINT).willReturn(WireMock.status(errorCode)));

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.password())
                .formField("confirmPassword", request.confirmPassword())
                .exchange();

        assertThat(response).model()
                .containsEntry("email", request.email())
                .containsEntry("username", request.username());
    }

    @Test
    void whenBackendRespondsWithCreated_ThenReturnRegisterView() throws Exception {
        RegisterUserRequest request = randomRegisterUserRequest();
        TokenPairDto tokens = randomTokenDto();
        WireMock.stubFor(WireMock.post(BACKEND_ENDPOINT)
                .willReturn(WireMock.created()
                        .withBody(toJson(tokens))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.password())
                .formField("confirmPassword", request.confirmPassword())
                .exchange();

        assertThat(response)
                .hasStatus(HttpStatus.OK)
                .hasViewName("register");
    }

    @Test
    void whenBackendRespondsWithCreated_ThenDisplayMessageNotError() throws Exception {
        RegisterUserRequest request = randomRegisterUserRequest();
        TokenPairDto tokens = randomTokenDto();
        WireMock.stubFor(WireMock.post(BACKEND_ENDPOINT)
                .willReturn(WireMock.created()
                        .withBody(toJson(tokens))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.password())
                .formField("confirmPassword", request.confirmPassword())
                .exchange();

        assertThat(response).model()
                .containsEntry("message", "User registered successfully")
                .doesNotContainKey("error");
    }

    @Test
    void whenBackendRespondsWithCreated_ThenClearEmailAndUsername() throws Exception {
        RegisterUserRequest request = randomRegisterUserRequest();
        TokenPairDto tokens = randomTokenDto();
        WireMock.stubFor(WireMock.post(BACKEND_ENDPOINT)
                .willReturn(WireMock.created()
                        .withBody(toJson(tokens))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        MvcTestResult response = mockMvcTester
                .post()
                .uri(REGISTER_ENDPOINT)
                .formField("email", request.email())
                .formField("username", request.username())
                .formField("password", request.password())
                .formField("confirmPassword", request.confirmPassword())
                .exchange();

        assertThat(response).model()
                .doesNotContainKey("email")
                .doesNotContainKey("username");
    }

    private RegisterUserRequest randomRegisterUserRequest() {
        String password = faker.credentials().password();
        return new RegisterUserRequest(
                faker.internet().emailAddress(),
                faker.credentials().username(),
                password,
                password
        );
    }

    private TokenPairDto randomTokenDto() {
        return new TokenPairDto(randomString(), randomString());
    }

    private String randomString() {
        return faker.text().text(20, 80, true, true, true);
    }

    private int randomErrorCode(int min, int max, Set<Integer> excluded) {
        int errorCode;
        do {
            errorCode = faker.number().numberBetween(min, max);
        } while (excluded.contains(errorCode));
        return errorCode;
    }

    private String toJson(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
