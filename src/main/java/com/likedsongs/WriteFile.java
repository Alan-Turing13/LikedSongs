package com.likedsongs;

import com.fasterxml.jackson.databind.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WriteFile {

    public static void writeFile(List<JsonNode> json) throws IOException{
        Set<Song> songs = new HashSet<Song>();

        ObjectMapper o = new ObjectMapper();
        JsonNode track = null;
        String name;
        String artist;
        String album;
        String coverUrl;

        // array of 50 song nodes
        for (JsonNode rawSong : json) {
            track = rawSong.get("track");
            name = String.valueOf(track.get("name"));
            System.out.println(name);
            artist = String.valueOf(track.get("artists").get(0).get("name"));
            album = String.valueOf(track.get("album").get("name"));
            coverUrl = String.valueOf(track.get("album").get("images").get(0).get("url"));

            songs.add(new Song(name, artist, album, coverUrl));
        }


        FileWriter fw = new FileWriter("likedsongs.txt", true);

        for (Song song : songs) {
            fw.write(song.toString());
            System.out.println(song.toString() + "added to likedsongs.txt");
        }
//        for (JsonElement song : json.getAsJsonArray())
//            // from the song object, I want the "track" property
//            songObj = song.getAsJsonObject();
//            for (var prop : songObj.entrySet()) {
//                track = prop.getValue();
//            }

    }
}
