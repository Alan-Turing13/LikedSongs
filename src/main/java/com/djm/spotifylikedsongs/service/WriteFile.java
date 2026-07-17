package com.djm.spotifylikedsongs.service;

import com.djm.spotifylikedsongs.model.Song;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriteFile {
    // hard-coded file name
    public static void writeFile(List<Song> songs) throws IOException {
        writeFile(songs, "likedsongs.txt");
    }

    public static void writeFile(List<Song> songs, String filePath) throws IOException{
        try (FileWriter fw = new FileWriter(filePath)){
            for (Song song : songs) {
                String line = song.toString();
                fw.write(line + System.lineSeparator());
                System.out.println(song + "added to " + filePath + "\n");
            }
        }
    }
}
