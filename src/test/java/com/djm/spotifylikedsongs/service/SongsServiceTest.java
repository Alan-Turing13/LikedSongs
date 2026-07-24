package com.djm.spotifylikedsongs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.djm.spotifylikedsongs.model.Song;
import com.djm.spotifylikedsongs.util.ReleaseDateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class SongsServiceTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SongsService songsService;

    @Test
    void getLikedSongsTest() throws JsonProcessingException {
        String json = """
            [
                {
                    "track": {
                        "name": "Song A", 
                        "artists": [{"name": "Artist 1"}], 
                        "album": {
                            "name": "Album X", 
                            "release_date": "2016",
                            "release_date_precision": "year",
                            "images": [{"url": "https://lh3.googleusercontent.com/fake/test-image-abcdef987654.png"}]
                                 }
                             }
                        },
                {   
                    "track": {
                        "name": "Song B", 
                        "artists": [{"name": "Artist 2"}], 
                        "album": {
                            "name": "Album Y", 
                            "release_date": "2021-07-15",
                             "release_date_precision": "day",
                            "images": [{"url": "https://images.google.com/example/fake-image-12345.jpg"}]
                                 }
                             }
                        }
            ]
            """;
        List<JsonNode> jsonNodes = objectMapper.readValue(json, new TypeReference<>() {});
        songsService.addSongs(jsonNodes);
        ArrayList orderedSongs = songsService.getLikedSongsChronologically();

        Song expected = new Song(
                "Song A",
                "Artist 1",
                "Album X",
                ReleaseDateUtils.trimReleaseDate("2016-07-15"),
                "https://lh3.googleusercontent.com/fake/test-image-abcdef987654.png");

        assertEquals(expected, orderedSongs.get(0));
        assertEquals(2, orderedSongs.size());
    }
}