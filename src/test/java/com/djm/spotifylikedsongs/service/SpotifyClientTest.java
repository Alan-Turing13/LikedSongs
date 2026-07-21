package com.djm.spotifylikedsongs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpotifyClientTest {

    @Mock
    private OkHttpClient mockClient;

    @Mock
    private Call mockCall;

    @Mock
    private Response mockResponse;

    @Mock
    private ResponseBody mockResponseBody;

    private ObjectMapper objectMapper = new ObjectMapper();
    private SpotifyClient likedSongs;

    @BeforeEach
    void setUp(){
        likedSongs = new SpotifyClient(mockClient, objectMapper);
    }

    @Test
    void getLikedSongsTest() throws IOException {
        String accessToken = "fake-token";
        int offset = 0;
        String json = """
            {
              "items": [
                {"track": {"name": "Song 1"}},
                {"track": {"name": "Song 2"}}
              ]
            }
            """;

        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(json);

        List<JsonNode> result = likedSongs.getLikedSongsJson(accessToken, offset);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Song 1", result.get(0).get("track").get("name").asText());
    }

    @Test
    void failToGetLikedSongsTest() throws IOException {

        when(mockClient.newCall(any())).thenReturn(mockCall);
        when(mockCall.execute()).thenThrow(new IOException("Network error"));

        List<JsonNode> result = likedSongs.getLikedSongsJson("token", 0);
        assertNull(result);
    }

}