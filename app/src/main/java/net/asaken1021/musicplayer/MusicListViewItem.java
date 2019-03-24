package net.asaken1021.musicplayer;

import android.graphics.Bitmap;

public class MusicListViewItem {
    public Bitmap musicCoverImage;
    public String musicTitleText;
    public String musicArtistText;
    public String musicLengthText;

    public MusicListViewItem(Bitmap cover, String title, String artist, String length) {
        this.musicCoverImage = cover;
        this.musicTitleText = title;
        this.musicArtistText = artist;
        this.musicLengthText = length;
    }

}
