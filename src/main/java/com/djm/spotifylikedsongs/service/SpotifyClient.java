package com.djm.spotifylikedsongs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpotifyClient {
    private OkHttpClient client;
    private ObjectMapper objectMapper;

    public SpotifyClient(){
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public SpotifyClient(OkHttpClient c, ObjectMapper o){
        this.client = c;
        this.objectMapper = o;
    }

    public List<JsonNode> getLikedSongs(String accessToken, int offset){

        String getBody = "market=GB&limit=50&offset=" + String.valueOf(offset);

        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/tracks?" + getBody)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
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
}
