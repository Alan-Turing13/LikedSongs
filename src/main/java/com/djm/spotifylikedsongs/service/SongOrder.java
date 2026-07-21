package com.djm.spotifylikedsongs.service;

import com.djm.spotifylikedsongs.model.Song;
import com.djm.spotifylikedsongs.util.ReleaseDateUtils;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SongOrder {
    private List<Song> likedSongs;

    public SongOrder() {
        this.likedSongs = new ArrayList<>();
    }

    public ArrayList<Song> getLikedSongs() {
        likedSongs.sort(Comparator.comparing(Song::getReleaseDateAsLocalDate));
        return (ArrayList<Song>) likedSongs;
    }

    public void addSongs(List<JsonNode> json){
        for (JsonNode rawSongData : json) {
            JsonNode trackInfo = rawSongData.get("track");
            String name = getTextOrEmpty(trackInfo, "name");
            String artist = getTextOrEmpty(trackInfo.path("artists").get(0), "name");
            String album = getTextOrEmpty(trackInfo.path("album"), "name");
            String releaseDate = ReleaseDateUtils.trimReleaseDate(
                    getTextOrEmpty(trackInfo.path("album"), "release_date")
            );
            String coverUrl = getTextOrEmpty(
                    trackInfo.path("album").path("images").get(0), "url"
            );
            System.out.println("Song " + name + " has coverUrl " + coverUrl);
            likedSongs.add(new Song(name, artist, album, releaseDate, coverUrl));
        }
    }

    private static String getTextOrEmpty(JsonNode node, String field){
        if (node == null || node.get(field) == null) return "";
        return node.get(field).asText("");
    }
}
