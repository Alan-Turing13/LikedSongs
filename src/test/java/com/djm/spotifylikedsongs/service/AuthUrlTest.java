package com.djm.spotifylikedsongs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthUrlTest {

    @Autowired
    private SpotifyClient spotifyClient;

    @org.junit.jupiter.api.Test
    void getCodeTest() {
        assertDoesNotThrow(() -> {
            String authUrl = spotifyClient.getAuthorizationUrl();

            assertNotNull(authUrl, "Authentication URL cannot be null");
            assertFalse(authUrl.isBlank(), "Authentication URL cannot be empty");

            System.out.println("Successfully got authentication URL " + authUrl);
        });
    }
}