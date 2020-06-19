package com.example.soundplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Switch Toggle;
    SeekBar Seek;
    ImageView button1;
    ImageView button2;
    ImageView button3;
    ImageView forward;
    ImageView rewind;
    ImageView pause;
    TextView head;
    TextView name1;
    TextView name2;
    TextView name3;

    boolean paused = false;
    boolean active = false;
    private Handler handler;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        head = findViewById(R.id.header);
        name1 = findViewById(R.id.textView2);
        name2 = findViewById(R.id.textView3);
        name3 = findViewById(R.id.textView4);
        Toggle = findViewById(R.id.theme);
        Seek = findViewById(R.id.seeks);
        handler = new Handler();

        button1 = findViewById(R.id.imageView);
        button2 = findViewById(R.id.imageView1);
        button3 = findViewById(R.id.imageView2);
        forward = findViewById(R.id.forwards);
        rewind = findViewById(R.id.backwards);
        pause = findViewById(R.id.pause);

        name1.setText(R.string.Song1);
        name2.setText(R.string.Song2);
        name3.setText(R.string.Song3);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSong("shotgun");
                setProgress();
                pause.setImageResource(android.R.drawable.ic_media_pause);
                paused = false;

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getSong("bone");
                setProgress();
                pause.setImageResource(android.R.drawable.ic_media_pause);
                paused = false;
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSong("Weight");
                setProgress();
                pause.setImageResource(android.R.drawable.ic_media_pause);
                paused = false;
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                }
            }
        });

        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
                }
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    if (!paused) {
                        mediaPlayer.pause();
                        pause.setImageResource(android.R.drawable.ic_media_play);
                        paused = true;
                    } else {
                        mediaPlayer.start();
                        pause.setImageResource(android.R.drawable.ic_media_pause);
                        paused = false;
                    }

                }
            }
        });
        checkTheme(Toggle);
// Changes the applications theme
        Toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                }
            }
        });


    }

    //Check what theme the application is in to set theme specific variables
    public void checkTheme(Switch Toggle) {
        int nightModeFlags = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Toggle.setChecked(true);
                Toggle.setText("Light Mode");
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccentDark)));
                Toggle.setTextColor(getResources().getColor(R.color.darktext));
                Seek.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorAccentDark), PorterDuff.Mode.MULTIPLY);
                Seek.getThumb().setColorFilter(getResources().getColor(R.color.darktext), PorterDuff.Mode.SRC_ATOP);

                break;

            case Configuration.UI_MODE_NIGHT_NO:
                Toggle.setChecked(false);
                Toggle.setText("Dark Mode");
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));


                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                Toggle.setChecked(false);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                break;
        }
    }

    public void getSong(String id) {
        if (active) {
            mediaPlayer.stop();
            active = false;
        }
        switch (id) {
            case "shotgun":
                mediaPlayer = MediaPlayer.create(this, R.raw.shotgun);
                head.setText(R.string.Song1);
                active = true;
                break;
            case "bone":
                mediaPlayer = MediaPlayer.create(this, R.raw.bone);
                head.setText(R.string.Song2);
                active = true;
                break;
            case "Weight":
                mediaPlayer = MediaPlayer.create(this, R.raw.gold);
                head.setText(R.string.Song3);
                active = true;
                break;
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Seek.setMax(mp.getDuration());
                mp.start();
                Updatebar();
            }
        });

    }

    public void setProgress() {
        Seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void Updatebar() {

        Seek.setProgress(mediaPlayer.getCurrentPosition());

        if (mediaPlayer.isPlaying()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Updatebar();
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }
}
