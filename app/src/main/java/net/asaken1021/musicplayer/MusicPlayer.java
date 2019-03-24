package net.asaken1021.musicplayer;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MusicPlayer extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
    }
}
