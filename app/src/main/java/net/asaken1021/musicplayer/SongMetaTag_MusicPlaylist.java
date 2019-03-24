package net.asaken1021.musicplayer;

import io.realm.RealmObject;

public class SongMetaTag_MusicPlaylist extends RealmObject {
    private int itemID;
    private int somgMetaTagID;
    private int musicPlaylistID;

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getSomgMetaTagID() {
        return somgMetaTagID;
    }

    public void setSomgMetaTagID(int somgMetaTagID) {
        this.somgMetaTagID = somgMetaTagID;
    }

    public int getMusicPlaylistID() {
        return musicPlaylistID;
    }

    public void setMusicPlaylistID(int musicPlaylistID) {
        this.musicPlaylistID = musicPlaylistID;
    }
}
