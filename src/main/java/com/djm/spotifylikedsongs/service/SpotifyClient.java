package com.djm.spotifylikedsongs.service;

import com.djm.spotifylikedsongs.config.AppConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class SpotifyClient {
    private OkHttpClient httpClient;
    private ObjectMapper objectMapper;
    private AppConfig appConfig;
    private static final String tokenUri = "https://accounts.spotify.com/api/token";
    private static final String redirect = "http://127.0.0.1:8888/callback";

    public SpotifyClient(AppConfig appConfig){
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.appConfig = appConfig;
    }

    public List<JsonNode> getLikedSongs(String accessToken, int offset){

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

            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    rawSongs.add(item);
                }
            } else {
                System.err.println("No songs were returned. ");
            }
            return rawSongs;

        } catch (Exception e){
            System.err.println("Error: nothing returned from Spotify.");
            e.getMessage().toString();
            return null;
        }
    }

    public String getAccessToken(String code){
        String formBody = "grant_type=authorization_code" +
                "&code=" + code +
                "&redirect_uri=" + URLEncoder.encode(redirect, StandardCharsets.UTF_8);

        RequestBody body = RequestBody.create(formBody,
                MediaType.parse("application/x-www-form-urlencoded"));

        String encodedCredentials = Base64.getEncoder()
                .encodeToString((appConfig.getClientId() + ":" + appConfig.getClientSecret()).getBytes());

        Request request = new Request.Builder()
                .url(tokenUri)
                .post(body)
                .addHeader("Content-type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Basic " + encodedCredentials)
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

    public String getAuthorizationUrl(){
        return "https://accounts.spotify.com/en/authorize?response_type=code&client_id=" +
                appConfig.getClientId() +
                "&scope=user-library-read&redirect_uri=" +
                redirect +
                "&state=" + UUID.randomUUID();
    }
}
