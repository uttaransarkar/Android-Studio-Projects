package com.example.eggtimer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView timerTextView;
    SeekBar timerSeekBar;
    Button timerButton;
    boolean counterActive = false;
    CountDownTimer countDownTimer;

    public void reset() {
        timerSeekBar.setEnabled(true);
        timerButton.setText("SET!");
        timerSeekBar.setProgress(30);
        timerTextView.setText("0:30");
        counterActive = false;
    }

    public void startTimer(View view) {

        if (counterActive) {    //counter is on and user would want to stop and reset
            countDownTimer.cancel();
            reset();
        } else {    //counter hasn't yet started
            int duration = timerSeekBar.getProgress();
            timerSeekBar.setEnabled(false);
            timerButton.setText("STOP");
            counterActive = true;

            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm);

            countDownTimer = new CountDownTimer(duration * 1000 + 100, 1000) {
                public void onTick(long millisecondsLeft) {
                    long mins = millisecondsLeft / 60000;
                    long secs = (millisecondsLeft % 60000) / 1000;
                    if (secs < 10)
                        timerTextView.setText(mins + ":0" + secs);
                    else
                        timerTextView.setText(mins + ":" + secs);
                }

                public void onFinish() {
                    mediaPlayer.start();
                    reset();
                }
            }.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerTextView = (TextView) findViewById(R.id.timerTextView);
        timerSeekBar = (SeekBar) findViewById(R.id.timerSeekBar);
        timerButton = findViewById(R.id.timerButton);

        timerSeekBar.setMax(600);
        timerSeekBar.setProgress(30);

        timerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int totalSecs = progress;
                int mins = totalSecs / 60;
                int secs = totalSecs % 60;
                if (secs < 10)
                    timerTextView.setText(mins + ":0" + secs);
                else
                    timerTextView.setText(mins + ":" + secs);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}