package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imageView, imageView1, imageView2, imageView3;
    SeekBar seekBar;
    TextView textViewCurrent, textViewTotal;
    MediaPlayer mediaPlayer;
    Runnable runnable;
    boolean isRepeat = false;
    boolean isShuffle = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.play);
        imageView1 = findViewById(R.id.next);
        imageView2 = findViewById(R.id.previous);
        seekBar = findViewById(R.id.seek);
        textViewTotal = findViewById(R.id.text4);
        textViewCurrent = findViewById(R.id.text3);
        imageView3 = findViewById(R.id.loop);


        mediaPlayer = MediaPlayer.create(this, R.raw.som);
        imageView1.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView.setOnClickListener(this);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // total time
                String totTime = createTimerLabel(mediaPlayer.getDuration());
                textViewTotal.setText(totTime);

                seekBar.setMax(mediaPlayer.getDuration());

                mediaPlayer.start();
                imageView.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);


            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    try {
//
                        if (mediaPlayer.isPlaying()) {
                            Message msg = new Message();
                            msg.what = mediaPlayer.getCurrentPosition();
                            handler.sendMessage(msg);
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        //Repeat Song
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRepeat) {
                    isRepeat = false;
                    Toast.makeText(MainActivity.this, "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    imageView3.setImageResource(R.drawable.ic_repeat_one_black_24dp);
                } else {
                    isRepeat = true;
                    mediaPlayer.setLooping(true);
                    Toast.makeText(MainActivity.this, "Repeat is ON", Toast.LENGTH_SHORT).show();
                    isShuffle = false;
                    imageView3.setImageResource(R.drawable.ic_shuffle_black_24dp);
                }
            }
        });
    }


    public void changeSeeker() {
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        if (!mediaPlayer.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    changeSeeker();
                }
            };
            handler.postDelayed(runnable, 1000);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    imageView.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                } else {
                    mediaPlayer.start();
                    imageView.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                    changeSeeker();

                }
                break;
            case R.id.next:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
                changeSeeker();
                break;
            case R.id.previous:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
                changeSeeker();
                break;

        }
    }

    public String createTimerLabel(int duration) {
        String timerLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;
        timerLabel += min + ":";
        if (sec < 10) timerLabel += "0";
        timerLabel += sec;
        return timerLabel;
    }
//Current Time
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            int current_position = message.what;
            seekBar.setProgress(current_position);
            String cTime = createTimerLabel(current_position);
            textViewCurrent.setText(cTime);
            return true;
        }
    });


}
