package com.djm.spotifylikedsongs.auth;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GetAuthCode {
    private final String authCodeUri;

    public GetAuthCode(String clientId) {
        this.authCodeUri = "https://accounts.spotify.com/en/authorize?response_type=code&client_id=" +
                clientId +
                "&scope=user-library-read&redirect_uri=http://127.0.0.1:8888/callback";
    }

    private final CountDownLatch latch = new CountDownLatch(1);
    private String authCode = null;

    public String getCode() throws IOException, InterruptedException {
        int port = 8888;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/callback", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                String query = exchange.getRequestURI().getQuery();
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    if (pair.substring(0, idx).equals("code")) {
                        authCode = pair.substring(idx + 1);
                        System.out.println("Authentication code obtained");
                        break;
                    }
                }

                String response = "You can close this window now.";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();

                latch.countDown();
            }
        });

        server.start();
        System.out.println("Server is listening on port " + port);

        try {
            Desktop.getDesktop().browse(new URI(authCodeUri));
            System.out.println("Authentication code URI launched in browser");
        } catch (URISyntaxException e) {
            System.err.println("Error launching the browser.");
        }

        // user has 20 seconds to log in to their Spotify account
        latch.await(20, TimeUnit.SECONDS);
        server.stop(1);

        return authCode;
    }
}
