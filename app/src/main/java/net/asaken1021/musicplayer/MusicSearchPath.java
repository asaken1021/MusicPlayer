package net.asaken1021.musicplayer;

import io.realm.RealmObject;

public class MusicSearchPath extends RealmObject {
    private String directoryPath;

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }
}
