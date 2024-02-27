package com.likedsongs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Authenticate {

    private static final String TOKEN_URI = "https://accounts.spotify.com/api/token";
    private static final String CLIENT_ID = "333019fa8b494c60830378bfdf9c467b";
    private static final String CLIENT_SECRET = "2746b8feff164346bd1b04be4f482785";
    private static final String REDIRECT = "http://localhost:8888/callback";

    public static String getAccessToken(String code) {
        String accessToken = "";

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(("grant_type=authorization_code&code=" + code +
                "&redirect_uri=" + REDIRECT).getBytes(StandardCharsets.UTF_8));

        String encodedCredentials = Base64.getEncoder()
                .encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());

        Request request = new Request.Builder()
                .url(TOKEN_URI)
                .post(body)
                .addHeader("Content-type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Basic " + encodedCredentials)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Access token Http response: " + response);

            String jsonResponse = response.body().string();

            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

            accessToken = (String) (
                    jsonObject.get("access_token")).toString().replaceAll("\"", ""
            );

            System.out.println("Data returned: " + accessToken);

            return accessToken;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
