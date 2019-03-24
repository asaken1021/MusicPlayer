package net.asaken1021.musicplayer;

import io.realm.RealmObject;

public class MusicPlaylist extends RealmObject {
    private String playlistName;
    private int id;
    private int relationId;

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRelationId() {
        return relationId;
    }

    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }
}