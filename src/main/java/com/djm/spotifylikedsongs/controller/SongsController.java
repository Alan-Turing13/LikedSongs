package com.djm.spotifylikedsongs.controller;

import java.util.ArrayList;
import java.util.List;

import com.djm.spotifylikedsongs.config.AppConfig;
import com.djm.spotifylikedsongs.model.Song;
import com.djm.spotifylikedsongs.service.SongOrder;
import com.djm.spotifylikedsongs.service.SpotifyClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/liked-songs")
public class SongsController {

    private final Logger LOGGER = LoggerFactory.getLogger(SongsController.class);

    private final AppConfig config;

    public SongsController(AppConfig config){
        this.config = config;
    }

    @GetMapping("/")
    public String landingPage(){
        return "home";
    }

    @GetMapping("/get")
    public String getSongs(@RegisteredOAuth2AuthorizedClient("spotify")OAuth2AuthorizedClient authClient,
       Model model) throws Exception{

        // OAuth
        String accessToken = authClient.getAccessToken().getTokenValue();

        // service
        SpotifyClient spotifyClient = new SpotifyClient();
        SongOrder songOrder = new SongOrder();

        for (int offset = 0; offset < config.getTotalSongs(); offset+=50) {
            List<JsonNode> songsJson = spotifyClient.getLikedSongs(accessToken, offset);
            songOrder.addSongs(songsJson);
        }

        ArrayList<Song> likedSongs = songOrder.getLikedSongs();
        model.addAttribute("songs", likedSongs);

//        WriteFile writer = new WriteFile();
//
//        if (likedSongs != null && !likedSongs.isEmpty()) {
//            writer.writeFile(likedSongs, config.getOutputFilePath());
//        }
        LOGGER.info("Liked songs added.");
        return "songs";
    }
}
