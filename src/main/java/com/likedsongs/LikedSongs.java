package com.likedsongs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.ArrayList;
import java.util.List;

public class LikedSongs {

    public static List<JsonNode> getLikedSongs(String accessToken, int offset){
        OkHttpClient client = new OkHttpClient();

        String getBody = "market=GB&limit=50&offset=" + String.valueOf(offset);

        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/tracks?" + getBody)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        System.out.println(request);

        try (Response response = client.newCall(request).execute()) {
            System.out.println("User songs Http response: " + response);
            ObjectMapper o = new ObjectMapper();
            List<JsonNode> rawSongs = new ArrayList<>();

            String jsonResponse = response.body().string();

            JsonNode jn = o.readTree(jsonResponse);
            jn = jn.get("items");
            for (JsonNode item : jn) rawSongs.add(item);

            System.out.println("Forwarding list of " + rawSongs.size() + " JsonNodes into WriteFile");
            return rawSongs;

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
