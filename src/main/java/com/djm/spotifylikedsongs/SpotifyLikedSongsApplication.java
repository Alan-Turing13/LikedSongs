package com.djm.spotifylikedsongs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpotifyLikedSongsApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(SpotifyLikedSongsApplication.class, args);
        } catch (Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
