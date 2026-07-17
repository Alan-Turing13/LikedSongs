package com.djm.spotifylikedsongs.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private final String clientId;
    private final String clientSecret;
    private final int totalSongs;
    private final String outputFilePath;

    private AppConfig(String clientId, String clientSecret, int totalSongs, String outputFilePath){
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.totalSongs = totalSongs;
        this.outputFilePath = outputFilePath;
    }

    public static AppConfig load(){
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
            prop.load(input);

            String clientId = prop.getProperty("spring.security.oauth2.client.registration.spotify.client-id");
            String clientSecret = prop.getProperty("spring.security.oauth2.client.registration.spotify.client-secret");
            int totalSongs = Integer.parseInt(prop.getProperty("total.songs", "100"));
            String outputPath = prop.getProperty("output.file", "likedsongs.txt");

            return new AppConfig(clientId, clientSecret, totalSongs, outputPath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not load config", ex);
        }
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
