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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    private List<MusicListViewItem> musicListItems;
    private MusicListArrayAdapter musicListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicTitleTextView = (TextView) findViewById(R.id.bar_musicTitleTextView);
        musicCoverImageView = (ImageView) findViewById(R.id.bar_musicCoverImageView);
        musicPlayButton = (Button) findViewById(R.id.bar_musicPlayButton);

        Log.d("MusicPlayer_fileSearch", "Search Path: " + sdCardPath);
        mp3Files = new File(sdCardPath).listFiles();
        if (mp3Files != null) {
            for (int x = 0; x < mp3Files.length; x++) {
                if (mp3Files[x].isFile() && mp3Files[x].getName().endsWith("mp3")) {
                    fileNameList.add(mp3Files[x].getName());
                    Log.d("MusicPlayer_fileCheck", "mp3 Found: " + mp3Files[x].getName());
                }
            }
            listView = (ListView) findViewById(R.id.musicList);
            musicListItems = new ArrayList<MusicListViewItem>();
            musicListViewAdapter = new MusicListArrayAdapter(this, R.layout.music_list_view_item, musicListItems);
            listView.setAdapter(musicListViewAdapter);
        }

        songs = new ArrayList<>();

        Bitmap droid = BitmapFactory.decodeResource(getResources(), R.drawable.droid);
        mediaMetadataRetriever = new MediaMetadataRetriever();

        for (int x = 0; x < fileNameList.size(); x++) {
            SongMetaTag smt = new SongMetaTag();
//            songsUri.add(Uri.parse(sdCardPath + fileNameList.get(x)));
            filePath = sdCardPath + fileNameList.get(x);
            smt.setMusicUri(Uri.parse("file://" + filePath));
            songs.add(smt);
            mediaMetadataRetriever.setDataSource(filePath);
            musicTitle = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            musicArtist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            musicLength = String2TimeString(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            musicListViewAdapter.add(new MusicListViewItem(droid, musicTitle, musicArtist, musicLength));
            Log.d("MusicPlayer_songMetaTag", "Music added: " + songs.get(x));
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
        Log.d("MusicPlayer_play", "MusicID:" + id);
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
        if (mControllerCompat.getPlaybackState().getState() != PlaybackStateCompat.STATE_PLAYING) {
            stopService(new Intent(this, MusicService.class));
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
