package com.example.scheduler.adapters.web.profile;

import com.example.scheduler.adapters.dto.ProfilePublicDto;
import com.example.scheduler.application.usecase.GetEventHostUseCase;
import com.example.scheduler.infrastructure.config.WithSecurityStubs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.UUID;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;

@WebMvcTest(PublicProfileController.class)
@WithSecurityStubs
class PublicProfileControllerTest {

    private static final UUID EVENT_PUBLIC_ID = UUID.fromString("b452644a-dba8-427a-8e44-d5c1bc528231");
    private static final String BASE_URL = "/api/v1/public/events/" + EVENT_PUBLIC_ID + "/host";


    @MockitoBean
    private GetEventHostUseCase mockGetEventHostUseCase;

    @Autowired
    private MockMvcTester mockMvcTester;

    @Test
    void givenProfileExists_WhenGetPublicProfile_ThenRespondWithProfileData() {
        ProfilePublicDto expectedResponse = new ProfilePublicDto("Alice Arno", "logo.jpg");

        given(mockGetEventHostUseCase.getEventHostByEventPublicId(EVENT_PUBLIC_ID))
                .willReturn(expectedResponse);

        MvcTestResult response = mockMvcTester
                .get()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        then(response)
                .hasStatus(HttpStatus.OK)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson().isEqualTo("""
                        {
                            "fullName": "Alice Arno",
                            "logo": "logo.jpg"
                        }
                        """);
    }
}
