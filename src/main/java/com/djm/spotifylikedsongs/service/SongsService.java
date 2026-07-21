package com.djm.spotifylikedsongs.service;

import com.djm.spotifylikedsongs.model.Song;
import com.djm.spotifylikedsongs.util.ReleaseDateUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SongsService {
    private List<Song> likedSongs;

    public SongsService() {
        this.likedSongs = new ArrayList<>();
    }

    public ArrayList<Song> eliminateDuplicates(){
        return new ArrayList<>(new HashSet<>(likedSongs));
    }

    public ArrayList<Song> getLikedSongs(){
        return eliminateDuplicates();
    }

    public ArrayList<Song> getLikedSongsChronologically() {
        ArrayList<Song> formattedChronList = eliminateDuplicates();
        formattedChronList.sort(Comparator.comparing(Song::getReleaseDateAsLocalDate));
        return formattedChronList;
    }

    public ArrayList<Song> getLikedSongsAlphabetically(){
        ArrayList<Song> formattedAlphaList = eliminateDuplicates();
        formattedAlphaList.sort(Comparator.comparing(Song::getName, String.CASE_INSENSITIVE_ORDER));
        return formattedAlphaList;
    }

    public ArrayList<Song> getLikedSongsRandomly(){
        ArrayList<Song> formattedAlphaList = eliminateDuplicates();
        Collections.shuffle(formattedAlphaList);
        return formattedAlphaList;
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
            likedSongs.add(new Song(name, artist, album, releaseDate, coverUrl));
        }
    }

    private static String getTextOrEmpty(JsonNode node, String field){
        if (node == null || node.get(field) == null) return "";
        return node.get(field).asText("");
    }
}
