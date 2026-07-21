package com.djm.spotifylikedsongs.controller;

import java.util.ArrayList;
import java.util.List;

import com.djm.spotifylikedsongs.config.AppConfig;
import com.djm.spotifylikedsongs.model.Song;
import com.djm.spotifylikedsongs.service.SongsService;
import com.djm.spotifylikedsongs.service.SpotifyClient;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SongsController {

    private final Logger LOGGER = LoggerFactory.getLogger(SongsController.class);
//    private final AppConfig config;

    @Autowired
    SpotifyClient spotifyClient;

    @Autowired
    SongsService songsService;

//    public SongsController(AppConfig config){
//        this.config = config;
//    }

    @GetMapping("/")
    public String landingPage(){
        return "home";
    }

    @GetMapping("/login")
    public String spotifyLogin(@RequestParam(defaultValue = "alphabetical") String order) {
        String authUrl = spotifyClient.getAuthorizationUrl(order);
        return "redirect:" + authUrl;
    }

    @GetMapping("/callback")
    public String getSongs(@RequestParam("code") String code,
                           @RequestParam("state") String state, Model model) throws Exception{

        String songsOrder = state.split(":")[0];

        // OAuth
        String accessToken = spotifyClient.getAccessToken(code);
        List<JsonNode> likedSongsJson = spotifyClient.getLikedSongsJson(accessToken, 0);

        for (int offset = 0; offset < spotifyClient.getTotalSongsAmount(); offset+=50) {
            likedSongsJson = spotifyClient.getLikedSongsJson(accessToken, offset);
            songsService.addSongs(likedSongsJson);
        }

        ArrayList<Song> likedSongs = switch (songsOrder){
            case "chronological" -> songsService.getLikedSongsChronologically();
            case "alphabetical" -> songsService.getLikedSongsAlphabetically();
            case "random" -> songsService.getLikedSongsRandomly();
            default -> songsService.getLikedSongs();
        };

        model.addAttribute("songs", likedSongs);
        model.addAttribute("order", songsOrder);
        LOGGER.info("Liked songs added in {} order.", songsOrder);
        return "songs";
    }
}
