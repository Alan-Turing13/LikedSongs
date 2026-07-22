package com.djm.spotifylikedsongs.service;

import com.djm.spotifylikedsongs.config.AppConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class SpotifyClient {
    private String codeVerifier;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final AppConfig appConfig;
    private int totalSongsAmount;

    // Primary constructor
    @Autowired
    public SpotifyClient(AppConfig appConfig){
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.appConfig = appConfig;
    }

    // Test constructor
    public SpotifyClient(OkHttpClient mockClient, ObjectMapper objMapper){
        this.httpClient = mockClient;
        this.objectMapper = objMapper;
        this.appConfig = new AppConfig(
               "spring.security.oauth2.client.registration.spotify.client-id",
            "spring.security.oauth2.client.registration.spotify.client-secret"
        );
    }

    public List<JsonNode> getLikedSongsJson(String accessToken, int offset){

        String getBody = "market=GB&limit=50&offset=" + String.valueOf(offset);

        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/tracks?" + getBody)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Error getting the songs: " + response);
            } else {
                System.out.println("Http response: " + response + "\n");
            }

            List<JsonNode> rawSongs = new ArrayList<>();
            String jsonResponse = response.body().string();
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode items = root.get("items");

            if (!(root.get("total") == null)) {
                totalSongsAmount = Integer.parseInt(String.valueOf(root.get("total")));
            } else {totalSongsAmount = 0;}

            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    rawSongs.add(item);
                }
            } else {System.err.println("No songs were returned. ");}
            return rawSongs;

        } catch (Exception e){
            System.err.println("Error: nothing returned from Spotify.");
            e.getMessage().toString();
            return null;
        }
    }

    public int getTotalSongsAmount() {
        return totalSongsAmount;
    }

    public String getAccessToken(String code){
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", appConfig.getSpotifyRedirectUri())
                .add("client_id", appConfig.getClientId())
                .add("code_verifier", codeVerifier)
                .build();;

        Request request = new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .post(body)
                .addHeader("Content-type", "application/x-www-form-urlencoded")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            System.out.println("Access token Http response: " + response);

            String jsonResponse = response.body().string();

            if (!response.isSuccessful()){
                try {
                    JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
                    String error = jsonObject.has("error") ?
                            jsonObject.get("error").getAsString() : "Unknown error";
                    throw new RuntimeException("Failed to get access token: " + error);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to get access token. Http Status: " + response.code());
                }
            }

            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonElement tokenElement = jsonObject.get("access_token");

            return tokenElement.getAsString();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to obtain access token");
        }
    }

    private String generateCodeChallenge(String verifier){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(verifier.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }

    private String generateCodeVerifier(){
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String getAuthorizationUrl(){
        codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);

        return "https://accounts.spotify.com/en/authorize" +
                "?client_id=" + appConfig.getClientId() +
                "&response_type=code" +
                "&redirect_uri=" + appConfig.getSpotifyRedirectUri() +
                "&scope=user-library-read" +
                "&code_challenge_method=S256" +
                "&code_challenge=" + codeChallenge;
    }

}
