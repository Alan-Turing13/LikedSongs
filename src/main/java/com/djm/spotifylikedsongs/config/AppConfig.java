package com.djm.spotifylikedsongs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
    private final String clientId;
    private final String spotifyRedirectUri;

    public AppConfig(
        @Value("${client-id}") String clientId,
        @Value("${spotify.redirect-uri}") String redirectUri
    ) {
        this.clientId = clientId;
        this.spotifyRedirectUri = redirectUri;
    }

    public String getSpotifyRedirectUri() {
        return spotifyRedirectUri;
    }

    public String getClientId() {
        return clientId;
    }
}
