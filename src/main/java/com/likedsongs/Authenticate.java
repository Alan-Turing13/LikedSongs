package com.likedsongs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

public class Authenticate {

    private static final String TOKEN_URI = "https://accounts.spotify.com/api/token";
    private static final String REDIRECT = "http://localhost:8888/callback";
    private static String CLIENT_ID;
    private static String CLIENT_SECRET;    

    static {        
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            prop.load(input);
            CLIENT_ID = prop.getProperty("client.id");
            CLIENT_SECRET = prop.getProperty("client.secret");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

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
