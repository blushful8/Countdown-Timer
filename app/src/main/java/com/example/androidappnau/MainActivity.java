package com.example.androidappnau;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.timepicker.TimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText time;
    private TextView data;
    private TextView textcontinue;
    private long Mtime;
    private CountDownTimer mCountDownTimer;
    String TAG = "Main";
    private  Button start;
    private Button reset;
    private Boolean mTimerRunning;
    private Button mData;
    private MediaPlayer click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(100);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        time = findViewById(R.id.et_set_time_off_device);
        start = findViewById(R.id.b_first_button);
        data = findViewById(R.id.tv_data);
        reset = findViewById(R.id.v_second_button);
        textcontinue = findViewById(R.id.tv_continue_time);
        mData = findViewById(R.id.b_get_data);

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        data.setText("Сьогодні: "+ currentDate);


        click = MediaPlayer.create(this, R.raw.click);
        click.setLooping(false);


        start.setOnClickListener(this);
        reset.setOnClickListener(this);
    }





    private void starttimer(){
        Mtime = Integer.parseInt(time.getText().toString());
        Intent intent = new Intent(this, CustomService.class);


        mCountDownTimer = new CountDownTimer(Mtime*60000,1000) { // adjust the milli seconds here
            @Override
            public void onTick(long millisUntilFinished) {
                mTimerRunning = true;
                Mtime = millisUntilFinished;
                updateCountdown();
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
            click.start();

            starttimer();
            updateButton();

            Intent intent = new Intent(this, CustomService.class);//У випадку повторного нажаття
            stopService(intent);
        }




        if(view == reset) {
            mTimerRunning = false;
            mCountDownTimer.cancel();
            time.setText("");
            click.start();

            Intent intent = new Intent(this, CustomService.class);
            stopService(intent);

            Mtime = 0;
            updateButton();

            Log.i(TAG, "Service stop");
        }

        if(view == mData){
            click.start();
            startActivity(new Intent(getApplicationContext(),GetData.class));
        }

    }

    private void saveData(){
        String name_txt = time.getText().toString().trim();

        UserModel model = new UserModel();
        model.setName(name_txt);
        DatabaseClass.getDatabase(getApplicationContext()).getDao().insertAllData(model);

        time.setText("");

        Toast.makeText(this,"Текст успішно збережено", Toast.LENGTH_SHORT).show();
    }

}