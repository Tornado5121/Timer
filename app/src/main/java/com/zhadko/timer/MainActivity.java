package com.zhadko.timer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    SeekBar seekBar;
    Button startButton;
    TextView textView;
    boolean isRunnable;
    CountDownTimer countDownTimer;
    int currentTime;
    SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
    Date date = new Date();
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        startButton = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        isRunnable = false;

        sdf = new SimpleDateFormat("mm:ss");
        date = new Date();

        seekBar.setMax(100000);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentTime = seekBar.getProgress();
                date.setTime(currentTime);
                textView.setText(sdf.format(date));

//                currentTime = seekBar.getProgress();
//                textView.setText(String.valueOf(currentTime/1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                date.setTime(currentTime);
                textView.setText(sdf.format(date));
//                textView.setText(String.valueOf(seekBar.getProgress()/1000));
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunnable) {
                    startButton.setText("Stop");
                    seekBar.setEnabled(false);
                    isRunnable = true;

                    countDownTimer = new CountDownTimer(currentTime, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            int millis = (int) millisUntilFinished;
                                date.setTime(millisUntilFinished);
                                textView.setText(sdf.format(date));
                                seekBar.setProgress(millis);
//                            textView.setText(String.valueOf(millisUntilFinished/1000));
                        }

                        @Override
                        public void onFinish() {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            if (sharedPreferences.getBoolean("enabled_sound", true)) {
                                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bell_sound);
                                mediaPlayer.start();
                            }



                            date.setTime(60000);
                            textView.setText(sdf.format(date));
                            startButton.setText("Start");
                            countDownTimer.cancel();
                            isRunnable = false;
                            seekBar.setEnabled(true);
                            currentTime = 60000;
                            seekBar.setProgress(60000);
                        }
                    };

                    countDownTimer.start();

                } else {
                    startButton.setText("Start");
                    countDownTimer.cancel();
                    isRunnable = false;
                    seekBar.setEnabled(true);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.timer_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent settingIntent = new Intent(this, SettingActivity.class);
        startActivity(settingIntent);
        return super.onOptionsItemSelected(item);
    }
}
