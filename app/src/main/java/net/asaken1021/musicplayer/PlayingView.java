package net.asaken1021.musicplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayingView extends AppCompatActivity {
    private ImageView coverImageView;
    private TextView musicTitleTextView;
    private SeekBar musicPositionSeekBar;
    private Button previousButton;
    private Button playStopButton;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_fragment);

        coverImageView = (ImageView) findViewById(R.id.musicCoverImageView);
        musicTitleTextView = (TextView) findViewById(R.id.musicTitleTextView);
        musicPositionSeekBar = (SeekBar) findViewById(R.id.musicSeekBar);
        previousButton = (Button) findViewById(R.id.musicPreviousButton);
        playStopButton = (Button) findViewById(R.id.musicPlayStopButton);
        nextButton = (Button) findViewById(R.id.musicNextButton);

        playStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
