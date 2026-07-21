package com.djm.spotifylikedsongs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
    private final String clientId;
    private final String clientSecret;
    private final int totalSongs;

    // the amount of songs should be flexible, even if it's > 100
    public AppConfig(
        @Value("${spring.security.oauth2.client.registration.spotify.client-id}") String clientId,
        @Value("${spring.security.oauth2.client.registration.spotify.client-secret}") String clientSecret,
        @Value("${total.songs:100}") int totalSongs
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.totalSongs = totalSongs;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public int getTotalSongs() {
        return totalSongs;
    }
}
