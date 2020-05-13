package com.bytedance.videoplayer;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoPlayer extends AppCompatActivity {
    VideoView videoView;
    LinearLayout linear;
    ImageButton button;
    SeekBar seekbar;
    TextView text1;
    TextView text2;
    static int position = 0;
    static long ti = 0;
    Handler handler = new Handler();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getResources().getConfiguration().orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        videoView = findViewById(R.id.videoView);
        linear = findViewById(R.id.linear);
        button = findViewById(R.id.button);
        seekbar = findViewById(R.id.seekBar);
        text1 = findViewById(R.id.textViewNow);
        text2 = findViewById(R.id.textViewLength);

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (videoView != null) {
                    //handler.removeCallbacks(r);
                    linear.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            linear.setVisibility(View.INVISIBLE);
                            button.setVisibility(View.INVISIBLE);
                        }
                    }, 2000);
                    return true;
                }
                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    button.setImageResource(R.drawable.play);
                } else {
                    button.setImageResource(R.drawable.stop);
                    videoView.start();
                }
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (!videoView.isPlaying()) {
                        videoView.start();
                    }
                    videoView.seekTo(videoView.getDuration() * progress / 100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Intent intent = getIntent();
        videoView.setVideoURI(intent.getData());
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                text2.setText(getTimeFormat(videoView.getDuration()));
            }
        });
        if (System.currentTimeMillis() - ti < 1000)
            videoView.seekTo(position);
        videoView.start();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (videoView != null && videoView.isPlaying()) {
                    seekbar.setProgress(videoView.getCurrentPosition() * 100 / videoView.getDuration());
                    position = videoView.getCurrentPosition();
                    ti = System.currentTimeMillis();
                    text1.setText(getTimeFormat(videoView.getCurrentPosition()));
                }
                handler.postDelayed(this, 500);
            }
        }, 100);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                linear.setVisibility(View.INVISIBLE);
                button.setVisibility(View.INVISIBLE);
            }
        }, 2000);
    }

    public static String getTimeFormat(long msec) {
        long sec = msec / 1000;
        long min = sec / 60;
        long hour = min / 60;
        sec = sec % 60;
        min = min % 60;
        String res = "";
        if (hour > 0)
            res += hour + ":";
        if (min > 0 || hour > 0)
            res += min + ":";
        res += sec;
        return res;
    }


}