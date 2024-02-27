package com.likedsongs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class GetCode {

    private static String CLIENT_ID;

    static {        
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            prop.load(input);
            CLIENT_ID = prop.getProperty("client.id");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static final String AUTHENTICATION_CODE_URI = "https://accounts.spotify.com/en/authorize?response_type=code&client_id=" + CLIENT_ID + "&scope=user-library-read&redirect_uri=http://localhost:8888/callback";
    private static final CountDownLatch latch = new CountDownLatch(1);
    private String authCode = null;

    public String getCode() throws IOException, InterruptedException {
        int port = 8888;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/callback", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                String query = exchange.getRequestURI().getQuery();
                System.out.println("code = " + query);

                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    if (pair.substring(0, idx).equals("code")) {
                        authCode = pair.substring(idx + 1);
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
            Desktop.getDesktop().browse(new URI(AUTHENTICATION_CODE_URI));
        } catch (URISyntaxException e) {
            System.out.println("Error");
        }

        latch.await();
        server.stop(1);
        return authCode;
    }
}
