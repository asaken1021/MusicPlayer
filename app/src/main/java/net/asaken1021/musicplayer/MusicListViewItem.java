package net.asaken1021.musicplayer;

import android.graphics.Bitmap;

public class MusicListViewItem {
    public String musicCoverImageUri;
    public String musicTitleText;
    public String musicArtistText;
    public String musicLengthText;

    public MusicListViewItem(String coverUri, String title, String artist, String length) {
        this.musicCoverImageUri = coverUri;
        this.musicTitleText = title;
        this.musicArtistText = artist;
        this.musicLengthText = length;
    }

}
