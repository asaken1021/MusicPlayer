package net.asaken1021.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // ファイル検索関連
    private File[] mp3Files;
    private List<String> fileNameList = new ArrayList<String>();
    private ListView listView;
    private final String sdCardPath = "/storage/9C33-6BBD/TestDirectory/";

    // MP3再生関連
    private List<Uri> songsUri;
    private List<SongMetaTag> songs;
    private MediaBrowserCompat mBrowserCompat;
    private MediaControllerCompat mControllerCompat;

    // UI関連
    private TextView musicTitleTextView;
    private ImageView musicCoverImageView;
    private Button musicPlayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicTitleTextView = (TextView) findViewById(R.id.musicTitleTextView);
        musicCoverImageView = (ImageView) findViewById(R.id.musicCoverImageView);
        musicPlayButton = (Button) findViewById(R.id.musicPlayButton);

        Log.d("sdCardPath", "Search  Path: " + sdCardPath);
        mp3Files = new File(sdCardPath).listFiles();
        if (mp3Files != null) {
            for (int x = 0; x < mp3Files.length; x++) {
                if (mp3Files[x].isFile() && mp3Files[x].getName().endsWith("mp3")) {
                    fileNameList.add(mp3Files[x].getName());
                    Log.d("mp3FileCheck", "Found: " + mp3Files[x].getName());
                }
            }
            listView = (ListView) findViewById(R.id.nameList);
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1);
            listView.setAdapter(adapter);

            for (int x = 0; x < fileNameList.size(); x++) {
                adapter.add(fileNameList.get(x));
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListView lv = (ListView) parent;
                    String item = (String) lv.getItemAtPosition(position);
                    Toast.makeText(getApplicationContext(), "ファイル名: " + item, Toast.LENGTH_SHORT).show();
                }
            });
        }

        startService(new Intent(this, MusicService.class));

        mBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, MusicService.class), connectionCallback, null);
        mBrowserCompat.connect();

        songs = new ArrayList<>();

        for (int x = 0; x < 1; x++) {
            SongMetaTag smt = new SongMetaTag();
//            songsUri.add(Uri.parse(sdCardPath + fileNameList.get(x)));
//            smt.setMusicUri(Uri.parse(sdCardPath + fileNameList.get(x)));
            smt.setMusicUri(Uri.parse("file://storage/9C33-6BBD/TestDirectory/238.mp3"));
            songs.add(smt);
            Log.d("SMT_MUSIC_ADD", "Music added: " + songs.get(x));
        }

        MusicLibrary.setMediaItems(songs);
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
        @Override
        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
            if (mControllerCompat.getPlaybackState() == null && children.size() != 0) {
                Play(children.get(0).getMediaId());
            }
        }
    };

    private void Play(String id) {
        mControllerCompat.getTransportControls().playFromMediaId(id, null);
    }

    private MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            musicTitleTextView.setText(metadata.getDescription().getTitle());
//            musicCoverImageView.setImageBitmap(metadata.getDescription().getIconBitmap());

        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                musicPlayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mControllerCompat.getTransportControls().pause();
                    }
                });
                musicPlayButton.setText("Play");
            } else {
                musicPlayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick (View v) {
                        mControllerCompat.getTransportControls().play();
                    }
                });
                musicPlayButton.setText("Pause");
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
}
