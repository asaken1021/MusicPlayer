package net.asaken1021.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.MenuPopupWindow;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.source.MediaSource;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements MusicListItemListener {

    // ファイル検索関連
    private File[] mp3Files;
    private List<String> fileNameList = new ArrayList<String>();
    private ListView listView;
    private final String sdCardPath = "/storage/9C33-6BBD/TestDirectory/th16/";
    private String filePath;

    // MP3ファイル情報関連
    private MediaMetadataRetriever mediaMetadataRetriever;
    private String musicTitle;
    private String musicArtist;
    private Bitmap musicCover;
    private String musicLength;

    // MP3再生関連
    private List<SongMetaTag> songs;
    private MediaBrowserCompat mBrowserCompat;
    private MediaControllerCompat mControllerCompat;

    // UI関連
    private TextView musicTitleTextView;
    private ImageView musicCoverImageView;
    private Button musicPlayButton;
    private MusicListArrayAdapter musicListViewAdapter;
    private List<MusicListViewItem> musicListItems;

    // データベース関連
    private Realm realm;
    private RealmResults<SongMetaTag> realmResults;
    private List<String> addedFilePath;
    private SongMetaTag temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicTitleTextView = (TextView) findViewById(R.id.bar_musicTitleTextView);
        musicCoverImageView = (ImageView) findViewById(R.id.bar_musicCoverImageView);
        musicPlayButton = (Button) findViewById(R.id.bar_musicPlayButton);

        realm = Realm.getDefaultInstance();
        realmResults = realm.where(SongMetaTag.class).findAll();

        Log.d("MusicPlayer_pathCheck", "Search Path-> " + sdCardPath);
        mp3Files = new File(sdCardPath).listFiles();
        if (mp3Files != null) {
            for (int x = 0; x < mp3Files.length; x++) {
                if (mp3Files[x].isFile() && mp3Files[x].getName().endsWith("mp3")) {
                    fileNameList.add(mp3Files[x].getName());
                    Log.d("MusicPlayer_fileSearch", "mp3 Found-> " + mp3Files[x].getName());
                }
            }
        }

        songs = new ArrayList<>();

        String droid = "file:///storage/9C33-6BBD/TestDirectory/droid.png";
        mediaMetadataRetriever = new MediaMetadataRetriever();

        listView = (ListView) findViewById(R.id.musicList);
        musicListItems = new ArrayList<>();
        musicListViewAdapter = new MusicListArrayAdapter(this, R.layout.music_list_view_item, musicListItems);
        listView.setAdapter(musicListViewAdapter);

        addedFilePath = new ArrayList<>();

        temp = new SongMetaTag();

//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                realmResults.deleteAllFromRealm();
//            }
//        });

        for (int x = 0; x < realmResults.size(); x++) { // 最初にRealmにある曲データを追加する
            temp = realmResults.get(x);
            mediaMetadataRetriever.setDataSource(temp.getMusicUri());
            musicTitle = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            musicArtist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            musicLength = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            musicListViewAdapter.add(new MusicListViewItem(droid, musicTitle, musicArtist, musicLength));

            addedFilePath.add(temp.getMusicUri());
            songs.add(temp);
            Log.d("MusicPlayer_add", "Music added from Realm database-> \"" + musicTitle + "\"");
        }

        for (int x = 0; x < fileNameList.size(); x++) { // その後、Realmとディレクトリから取得してきたものを比較し、Realmにないものを追加する
            filePath = sdCardPath + fileNameList.get(x);
            if (!addedFilePath.contains(filePath)) { // Realmに保存されているファイルパスのリストにない場合
                temp.setMusicUri("file://" + filePath);
                mediaMetadataRetriever.setDataSource(filePath);
                musicTitle = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                musicArtist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                musicLength = String2TimeString(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                temp.setTitle(musicTitle);
                temp.setArtist(musicArtist);
                temp.setLength(Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
                musicListViewAdapter.add(new MusicListViewItem(droid, musicTitle, musicArtist, musicLength));

                songs.add(temp);
                Log.d("MusicPlayer_add", "Music added from local mp3 file->\"" + musicTitle + "\"");

                realm.beginTransaction();
                SongMetaTag temp_realm = realm.createObject(SongMetaTag.class);
                temp_realm.setMusicUri(temp.getMusicUri());
                temp_realm.setTitle(temp.getTitle());
                temp_realm.setArtist(temp.getArtist());
                temp_realm.setLength(temp.getLength());
                realm.commitTransaction();
                Log.d("MusicPlayer_realmAdd", "Music metatag added to Realm database->\"" + musicTitle + "\"");
            }
        }

        MusicLibrary.setMediaItems(songs);

        startService(new Intent(this, MusicService.class));

        mBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, MusicService.class), connectionCallback, null);
        mBrowserCompat.connect();
    }

    private MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            try {
                mControllerCompat = new MediaControllerCompat(MainActivity.this, mBrowserCompat.getSessionToken());
                mControllerCompat.registerCallback(controllerCallback);

                if (mControllerCompat.getPlaybackState() != null && mControllerCompat.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
                    controllerCallback.onMetadataChanged(mControllerCompat.getMetadata());
                    controllerCallback.onPlaybackStateChanged(mControllerCompat.getPlaybackState());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mBrowserCompat.subscribe(mBrowserCompat.getRoot(), subscriptionCallback);
        }
    };

    private MediaBrowserCompat.SubscriptionCallback subscriptionCallback = new MediaBrowserCompat.SubscriptionCallback() {
//        @Override
//        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
//            if (mControllerCompat.getPlaybackState() == null && children.size() != 0) {
//                Play(children.get(0).getMediaId());
//            }
//        }
    };

    private void Play(String id) {
        mControllerCompat.getTransportControls().playFromMediaId(id, null);
        Log.d("MusicPlayer_play", "MusicID->" + id);
    }

    private MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            Log.d("MusicPlayer_event", "onMetadataChanged called");
            musicTitleTextView.setText(metadata.getDescription().getTitle());
//            musicCoverImageView.setImageBitmap(metadata.getDescription().getIconBitmap());

        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            Log.d("MusicPlayer_event", "onPlaybackStateChanged called");
            if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                musicPlayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mControllerCompat.getTransportControls().pause();
                    }
                });
                musicPlayButton.setText("Pause");
                ;
            } else {
                musicPlayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mControllerCompat.getTransportControls().play();
                    }
                });
                musicPlayButton.setText("Play");
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBrowserCompat.disconnect();
        realm.close();
        try {
            if (mControllerCompat.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
                stopService(new Intent(this, MusicService.class));
            }
        } catch (NullPointerException e) {
            // 何もしない
        }
    }

    @Override
    public void playListViewItem(int index) {
        Play(String.valueOf(index));
    }

    private String String2TimeString(String src) {
        String mm = String.valueOf(Integer.parseInt(src) / 1000 / 60);
        String ss = String.valueOf((Integer.parseInt(src) / 1000) % 60);

        //秒は常に二桁じゃないと変
        if (ss.length() == 1) ss = "0" + ss;

        return mm + ":" + ss;
    }
}
