package com.likedsongs;

public class Song {
    String name;
    String artist;
    String album;
    String coverUrl;

    public Song(String name, String artist, String album, String coverUrl) {
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.coverUrl = coverUrl;
    }

    @Override
    public String toString() {
        return name + "\n" + artist + "\n" + album + "\n" + coverUrl + "\n" + "***** \n";
    }
}
