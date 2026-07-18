package com.djm.spotifylikedsongs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
    private final String clientId;
    private final String clientSecret;
    private final int totalSongs;
    private final String outputFilePath;

    public AppConfig(
        @Value("${spring.security.oauth2.client.registration.spotify.client-id}") String clientId,
        @Value("${spring.security.oauth2.client.registration.spotify.client-secret}") String clientSecret,
        @Value("${total.songs:100}") int totalSongs,
        @Value("${output.file:likedsongs.txt}") String outputFilePath
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.totalSongs = totalSongs;
        this.outputFilePath = outputFilePath;
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

    public String getOutputFilePath() {
        return outputFilePath;
    }
}
