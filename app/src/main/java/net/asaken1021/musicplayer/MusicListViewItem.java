package net.asaken1021.musicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MusicListViewItem {
    public Bitmap musicCoverBitmapImage;
    public String musicTitleText;
    public String musicArtistText;
    public String musicLengthText;

    public MusicListViewItem(byte[] coverByteArray, String title, String artist, String length) {
        this.musicCoverBitmapImage = BitmapFactory.decodeByteArray(coverByteArray, 0, coverByteArray.length);
        this.musicTitleText = title;
        this.musicArtistText = artist;
        this.musicLengthText = length;
    }

    public MusicListViewItem(Bitmap coverBitmap, String title, String artist, String length) {
        this.musicCoverBitmapImage = coverBitmap;
        this.musicTitleText = title;
        this.musicArtistText = artist;
        this.musicLengthText = length;
    }

}
