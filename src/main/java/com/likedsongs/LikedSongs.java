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

            // response.body() comes in bytes (raw data). this first has to be converted to string
            String jsonResponse = response.body().string();

            // I convert the string into a Json object
            JsonNode jn = o.readTree(jsonResponse);
            // Zoom in to the items field
            jn = jn.get("items");
            // add each song item to the rawSongs list as a JsonNode
            for (JsonNode item : jn) rawSongs.add(item);

            System.out.println("Forwarding list of " + rawSongs.size() + " JsonNodes into WriteFile");
            return rawSongs;
//            for (Object song

//            Gson gson = new Gson();
//
//            Map<String, Object> map = gson.fromJson(jsonObject, new TypeToken<Map<String, Object>>() {}.getType());
//            map.forEach((x, y) -> System.out.println("key: " + x + ", value: " + y));
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
