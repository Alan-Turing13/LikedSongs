package com.djm.spotifylikedsongs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
    private final String clientId;
    private final String clientSecret;

    public AppConfig(
        @Value("${spring.security.oauth2.client.registration.spotify.client-id}") String clientId,
        @Value("${spring.security.oauth2.client.registration.spotify.client-secret}") String clientSecret
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getClientId() {
        return clientId;
    }
}
