package com.likedsongs;

public class SpotifyDriver {

    public static final int TOTAL_SONGS = 1138;

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
