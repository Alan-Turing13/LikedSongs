package com.likedsongs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SpotifyDriver {

    private static int TOTAL_SONGS;

    static {        
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            prop.load(input);
            TOTAL_SONGS = Integer.parseInt(prop.getProperty("total.songs"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @SuppressWarnings("static-access")
    public static void main(String[] args) throws Exception{

        GetCode g = new GetCode();
        Authenticate a = new Authenticate();
        String token = a.getAccessToken(g.getCode());

        LikedSongs ls = new LikedSongs();
        WriteFile wf = new WriteFile();

        for (int offset = 0; offset < TOTAL_SONGS; offset+=50) {
            wf.writeFile(ls.getLikedSongs(token, offset));
            }

        System.out.println("Liked songs added.");
    }
}
