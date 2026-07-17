package com.djm.spotifylikedsongs.auth;

import com.djm.spotifylikedsongs.config.AppConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public class Authenticator {
    private static final String tokenUri = "https://accounts.spotify.com/api/token";
    private static final String redirect = "http://127.0.0.1:8888/callback";
    private final String clientId;
    private final String clientSecret;
    private final OkHttpClient httpClient;

    // main constructor
    public Authenticator(AppConfig appConfig) {
        this.clientId = appConfig.getClientId();
        this.clientSecret = appConfig.getClientSecret();
        this.httpClient = new OkHttpClient();
    }

    // constructor for testing
    public Authenticator(String clientId, String clientSecret, OkHttpClient httpClient) {
        this.clientId = Objects.requireNonNull(clientId, "Client ID cannot be null");
        this.clientSecret = Objects.requireNonNull(clientSecret, "Client Secret cannot be null");
        this.httpClient = Objects.requireNonNull(httpClient, "OkHttpClient cannot be null");
    }

    public String getAccessToken(String code) {
        String formBody = "grant_type=authorization_code" +
                "&code=" + code +
                "&redirect_uri=" + URLEncoder.encode(redirect, StandardCharsets.UTF_8);

        RequestBody body = RequestBody.create(formBody,
                MediaType.parse("application/x-www-form-urlencoded"));

        String encodedCredentials = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());

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
}
