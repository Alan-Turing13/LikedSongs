package com.djm.spotifylikedsongs.controller;

import java.util.ArrayList;
import java.util.List;

import com.djm.spotifylikedsongs.auth.Authenticator;
import com.djm.spotifylikedsongs.auth.GetAuthCode;
import com.djm.spotifylikedsongs.config.AppConfig;
import com.djm.spotifylikedsongs.model.Song;
import com.djm.spotifylikedsongs.service.SongOrder;
import com.djm.spotifylikedsongs.service.SpotifyClient;
import com.djm.spotifylikedsongs.service.WriteFile;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/liked-songs")
public class SongController {

    private final Logger LOGGER = LoggerFactory.getLogger(SongController.class);

    @GetMapping("/")
    public String landingPage(){
        return "home";
    }

    @GetMapping("/get")
    public String getSongs() throws Exception{

        // config
        AppConfig config = AppConfig.load();

        // auth
        GetAuthCode authCodeGetter = new GetAuthCode(config.getClientId());
        Authenticator authenticator = new Authenticator(config);

        String authCode = authCodeGetter.getCode();
        String accessToken = authenticator.getAccessToken(authCode);

        // service
        SpotifyClient spotifyClient = new SpotifyClient();
        SongOrder songOrder = new SongOrder();

        for (int offset = 0; offset < config.getTotalSongs(); offset+=50) {
            List<JsonNode> songsJson = spotifyClient.getLikedSongs(accessToken, offset);
            songOrder.addSongs(songsJson);
        }

        ArrayList<Song> likedSongs = songOrder.getLikedSongs();
        WriteFile writer = new WriteFile();

        if (likedSongs != null && !likedSongs.isEmpty()) {
            writer.writeFile(likedSongs, config.getOutputFilePath());
        }
        LOGGER.info("Liked songs added.");
        return "songs";
    }
}
