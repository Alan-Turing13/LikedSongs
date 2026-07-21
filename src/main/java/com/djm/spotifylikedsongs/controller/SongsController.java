package com.djm.spotifylikedsongs.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.djm.spotifylikedsongs.config.AppConfig;
import com.djm.spotifylikedsongs.model.Song;
import com.djm.spotifylikedsongs.service.SongOrder;
import com.djm.spotifylikedsongs.service.SpotifyClient;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SongsController {

    private final Logger LOGGER = LoggerFactory.getLogger(SongsController.class);
    private final AppConfig config;

    @Autowired
    SpotifyClient spotifyClient;

    public SongsController(AppConfig config){
        this.config = config;
    }

    @GetMapping("/")
    public String landingPage(){
        return "home";
    }

    @GetMapping("/login")
    public String spotifyLogin(HttpServletResponse response) throws IOException{
        try {
            String authUrl = spotifyClient.getAuthorizationUrl();
            LOGGER.info("\n***Redirecting to " + authUrl + " ***\n");
            return "redirect:" + authUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }
    }

    @GetMapping("/callback")
    public String getSongs(@RequestParam("code") String code, Model model) throws Exception{

        // OAuth
        String accessToken = spotifyClient.getAccessToken(code);

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
