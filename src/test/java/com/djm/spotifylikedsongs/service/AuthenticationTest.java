package com.djm.spotifylikedsongs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AuthenticationTest {

    @Mock
    private OkHttpClient mockClient;

    @Mock
    private Call mockCall;

    @Mock
    private Response mockResponse;

    @Mock
    private ResponseBody mockResponseBody;

    private SpotifyClient spotifyClient;

    @BeforeEach
    void setUp(){
        spotifyClient = new SpotifyClient(mockClient, new ObjectMapper());
        spotifyClient.setCodeVerifier("E9L6k4_x2jN8QZpA-L0sB1vW3yT5uI7oP9rE2aS4dF6gH8jK0lM2nO4pQ6rS8tU1");
    }

    @Test
    void getAccessTokenTest() throws IOException {


        String fakeJsonResponse = """
            {
                "access_token": "BQD123456789abcdef",
                "token_type": "Bearer",
                "expires_in": 3600
            }
            """;

        when(mockResponseBody.string()).thenReturn(fakeJsonResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);

        String token = assertDoesNotThrow(() ->
                spotifyClient.getAccessToken("fake-authorisation")
        );

        assertNotNull(token);
        assertEquals("BQD123456789abcdef", token);
    }

    @Test
    void throwExceptionOnBadResponse() throws IOException {
        String errorJson = """
            {
                "error":"invalid_grant",
                "error_description":"Invalid authorisation code"
            }
            """;

        when(mockResponseBody.string()).thenReturn(errorJson);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockResponse.code()).thenReturn(400);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);

        assertThrows(RuntimeException.class,
                () -> spotifyClient.getAccessToken("invalid code"));
    }
}