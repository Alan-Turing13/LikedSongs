package com.djm.spotifylikedsongs.controller;

import java.util.ArrayList;
import java.util.List;

import com.djm.spotifylikedsongs.config.AppConfig;
import com.djm.spotifylikedsongs.model.Song;
import com.djm.spotifylikedsongs.service.SongsService;
import com.djm.spotifylikedsongs.service.SpotifyClient;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SongsController {

    private final Logger LOGGER = LoggerFactory.getLogger(SongsController.class);

    @Autowired
    SpotifyClient spotifyClient;

    @Autowired
    SongsService songsService;

    @GetMapping("/")
    public String landingPage(){
        return "home";
    }

    @GetMapping("/start")
    public String spotifyLogin(@RequestParam(defaultValue = "alphabetical") String order,
                               HttpServletResponse response) {
        Cookie cookie = new Cookie("liked_songs_order", order);
        cookie.setMaxAge(300);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        String authUrl = spotifyClient.getAuthorizationUrl();
        return "redirect:" + authUrl;
    }

    @GetMapping("/callback")
    public String getSongs(@RequestParam("code") String code, HttpServletRequest request,
                           @CookieValue(value = "liked_songs_order", defaultValue = "chronological") String order,
                           Model model) throws Exception{

        String songsOrder = order;

        // Proof Key for Code Exchange
        String accessToken = spotifyClient.getAccessToken(code);

        if (spotifyClient.getLikedSongsJson(accessToken, 0) == null){
            return "empty";
        }

        for (int offset = 0; offset < spotifyClient.getTotalSongsAmount(); offset+=50) {
            songsService.addSongs(spotifyClient.getLikedSongsJson(accessToken, offset));
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
