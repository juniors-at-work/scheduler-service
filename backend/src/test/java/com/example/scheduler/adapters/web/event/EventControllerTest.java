package com.example.scheduler.adapters.web.event;

import com.example.scheduler.adapters.fixture.TestEventFullDtos;
import com.example.scheduler.adapters.web.Headers;
import com.example.scheduler.application.service.EventService;
import com.example.scheduler.application.usecase.GenerateSlotsUseCase;
import com.example.scheduler.domain.fixture.TestCredentials;
import com.example.scheduler.domain.fixture.TestEvents;
import com.example.scheduler.domain.model.Credential;
import com.example.scheduler.infrastructure.config.WithSecurityStubs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

@WebMvcTest(EventController.class)
@WithSecurityStubs
class EventControllerTest {

    private static final String BASE_URL = "/api/events";

    @MockitoBean
    private EventService mockEventService;
    @MockitoBean
    private GenerateSlotsUseCase generateSlotsUseCase;

    @Autowired
    private MockMvcTester mockMvcTester;

    @Test
    void whenNoAuthHeader_WhenGetEventById_ThenRespondWithBadRequest() {

        MvcTestResult response = mockMvcTester
                .get()
                .with(authentication(toAuthentication(TestCredentials.alice())))
                .uri(BASE_URL + "/{id}", TestEvents.demo().id())
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        then(response)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson().isEqualTo("""
                       {
                         "timestamp": "2001-02-03T04:05:06Z",
                         "status": 400,
                         "error": "Bad Request",
                         "message": "Required request header 'X-USER-ID' for method parameter type UUID is not present",
                         "path": "/api/events/8840ddd5-e176-46d8-8f1b-babb00d989cd"
                       }
                       """);
    }

    @Test
    void givenAuthHeaderHasWrongFormat_WhenGetEventById_ThenRespondWithBadRequest() {

        MvcTestResult response = mockMvcTester
                .get()
                .with(authentication(toAuthentication(TestCredentials.alice())))
                .uri(BASE_URL + "/{id}", TestEvents.demo().id())
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.AUTH_HEADER, TestCredentials.alice().getUsername())
                .exchange();

        then(response)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson().isEqualTo("""
                        {
                          "timestamp": "2001-02-03T04:05:06Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Method parameter 'X-USER-ID': Failed to convert value of type 'java.lang.String' \
                        to required type 'java.util.UUID'; Invalid UUID string: alice",
                          "path": "/api/events/8840ddd5-e176-46d8-8f1b-babb00d989cd"
                        }
                        """);
    }

    @Test
    void givenEventIdHasWrongFormat_WhenGetEventById_ThenRespondWithBadRequest() {

        MvcTestResult response = mockMvcTester
                .get()
                .with(authentication(toAuthentication(TestCredentials.alice())))
                .uri(BASE_URL + "/{id}", "wrong-event-id")
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.AUTH_HEADER, TestCredentials.alice().getId())
                .exchange();

        then(response)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson().isEqualTo("""
                        {
                          "timestamp": "2001-02-03T04:05:06Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Method parameter 'eventId': Failed to convert value of type 'java.lang.String' \
                        to required type 'java.util.UUID'; Invalid UUID string: wrong-event-id",
                          "path": "/api/events/wrong-event-id"
                        }
                        """);
    }

    @Test
    void givenCorrectAuthHeaderAndEventId_WhenGetEventById_ThenRespondWithEventRequested() {
        given(mockEventService.getEventById(
                TestCredentials.alice().getId(),
                TestEvents.demo().id(),
                TestCredentials.alice()
        )).willReturn(TestEventFullDtos.demo());

        MvcTestResult response = mockMvcTester
                .get()
                .with(authentication(toAuthentication(TestCredentials.alice())))
                .uri(BASE_URL + "/{id}", TestEvents.demo().id())
                .accept(MediaType.APPLICATION_JSON)
                .header(Headers.AUTH_HEADER, TestCredentials.alice().getId())
                .exchange();

        then(response)
                .hasStatus(HttpStatus.OK)
                .hasContentType(MediaType.APPLICATION_JSON)
                .bodyJson().isEqualTo("""
                        {
                          "id": "8840ddd5-e176-46d8-8f1b-babb00d989cd",
                          "ownerId": "d3e68c3b-2d6d-48a1-a037-99a390e9433e",
                          "title": "Demo",
                          "description": "Sprint #42 demo",
                          "durationMinutes": 60,
                          "bufferBeforeMinutes": 10,
                          "bufferAfterMinutes": 15,
                          "maxParticipants": 1,
                          "isActive": true,
                          "eventType": "ONE2ONE",
                          "slug": "b452644a-dba8-427a-8e44-d5c1bc528231",
                          "startDate": "2024-07-01T10:00:00Z",
                          "endDate": "2024-07-04T17:00:00Z",
                          "createdAt": "2001-02-03T04:05:06.789012Z",
                          "updatedAt": "2001-03-04T05:06:07.890123Z"
                        }
                        """);
    }

    private Authentication toAuthentication(Credential credential) {
        return new UsernamePasswordAuthenticationToken(credential, null, credential.getAuthorities());
    }
}
