package com.djm.spotifylikedsongs.model;

import com.djm.spotifylikedsongs.util.ReleaseDateUtils;

import java.time.LocalDate;
import java.util.Objects;

public class Song {
    String name;
    String artist;
    String album;
    String releaseDate;
    String coverUrl;

    public Song(String name, String artist, String album, String releaseDate, String coverUrl) {
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.releaseDate = releaseDate;
        this.coverUrl = coverUrl;
        removeDateFromName();
    }

    private void removeDateFromName() {
        String regex = "\\b" + this.releaseDate + "\\b" + "\\s*[,\\-/]?";
        String result = this.name.replaceAll(regex, "");
        result = result.replaceAll("\\s+", " ").trim();

        if (!this.name.equals(result) && !result.equals("")){
            this.name = result;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Song song = (Song) obj;
        return Objects.equals(name, song.name) &&
                Objects.equals(artist, song.artist) &&
                Objects.equals(album, song.album) &&
                Objects.equals(coverUrl, song.coverUrl);
    }

    public LocalDate getReleaseDateAsLocalDate() {
        return ReleaseDateUtils.convertStringToDate(releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, artist, album, releaseDate, coverUrl);
    }

    @Override
    public String toString() {
        return name + "\n" +
                artist + "\n" +
                album + " (" + ReleaseDateUtils.makeDateStringReadable(releaseDate) + ")\n"
                + coverUrl + "\n"
                + "***** \n";
    }
}
