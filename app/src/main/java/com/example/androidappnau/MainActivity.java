package com.example.androidappnau;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText time;
    private TextView introduction;
    private TextView textcontinue;
    private long Mtime;
    private CountDownTimer mCountDownTimer;
    String TAG = "Main";
    private  Button start;
    private Button reset;
    private Boolean mTimerRunning;
    private long EndTime;
    private long settext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        time = findViewById(R.id.et_set_time_off_device);
        start = findViewById(R.id.b_first_button);
        introduction = findViewById(R.id.tv_introduction);
        reset = findViewById(R.id.v_second_button);
        textcontinue = findViewById(R.id.tv_continue_time);


        start.setOnClickListener(this);
        reset.setOnClickListener(this);
    }


    private void starttimer(){
        Mtime = Integer.parseInt(time.getText().toString());
        EndTime = System.currentTimeMillis() + Mtime;
        Intent intent = new Intent(this, CustomService.class);


        mCountDownTimer = new CountDownTimer(Mtime,1000) { // adjust the milli seconds here
            @Override
            public void onTick(long millisUntilFinished) {
                mTimerRunning = true;
                Mtime = millisUntilFinished;
                updateCountdown();
                settext = millisUntilFinished;
                updateButton();
            }
            public void onFinish() {


                startService(intent);
                Log.i(TAG, "Service start");
                textcontinue.setText("Час вичерпаний");
                mTimerRunning = false;
                updateButton();
            }
        }.start();
        mTimerRunning = true;
        updateButton();
    }

    private void updateCountdown(){
        int Seconds = (int) Mtime / 1000 % 60;
        int Minutes = (int) Mtime / (60 * 1000) % 60;
        int Hours = (int) Mtime / (60 * 60 * 1000) % 24;
        String timeleftformatted = String.format(Locale.getDefault(),"%02d:%02d:%02d", Hours, Minutes, Seconds);
        textcontinue.setText(timeleftformatted);
    }

    private void updateButton() {
        if (mTimerRunning) {
            reset.setText("Скинути час");
            start.setVisibility(View.INVISIBLE);
            reset.setVisibility(View.VISIBLE);
        } else {
            reset.setText("Стоп аудіо");
            if(Mtime==0){
                reset.setText("Скинути час");
                reset.setVisibility(View.INVISIBLE);
            }
            start.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onClick(View view) {

        if(view == start) {
            mTimerRunning = true;

            starttimer();
            updateButton();

            Intent intent = new Intent(this, CustomService.class);//У випадку повторного нажаття
            stopService(intent);
        }




        if(view == reset) {
            mTimerRunning = false;
            mCountDownTimer.cancel();
            time.setText("");

            Intent intent = new Intent(this, CustomService.class);
            stopService(intent);

            Mtime = 0;
            updateButton();

            Log.i(TAG, "Service stop");
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("millisLeft", Mtime);
        outState.putBoolean("timerRunning", mTimerRunning);
        outState.putLong("endTime", EndTime);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Mtime = savedInstanceState.getLong("millisLeft");
        mTimerRunning = savedInstanceState.getBoolean("timerRunning");
        updateCountdown();
        updateButton();

        if (mTimerRunning) {
            EndTime = savedInstanceState.getLong("endTime");
            Mtime = EndTime - System.currentTimeMillis();
            starttimer();
        }
    }
}