package com.example.androidappnau;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.androidappnau.EntityClass.UserModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText time;
    private TextView data;
    private TextView textcontinue;
    private long Mtime;
    private CountDownTimer mCountDownTimer;
    private String TAG = "Main";
    private Button start;
    private Button reset;
    private Boolean mTimerRunning;
    private MediaPlayer click;
    private EditText name;
    private TextView tRandom;
    private Button getData;
    private Button bRandom;
    String date_time;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    SharedPreferences mpref;
    SharedPreferences.Editor mEditor;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init();

        name = time;

        Calendar mcalendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(mcalendar.getTime());
        data.setText(currentDate);


        click = MediaPlayer.create(this, R.raw.click);
        click.setLooping(false);

       listener();


    }
    public void init(){
        time = findViewById(R.id.et_set_time_off_device);
        start = findViewById(R.id.b_first_button);
        data = findViewById(R.id.tv_data);
        reset = findViewById(R.id.v_second_button);
        textcontinue = findViewById(R.id.tv_continue_time);
        getData = findViewById(R.id.btn_getData);
        bRandom = findViewById(R.id.btn_random);
        tRandom = findViewById(R.id.tv_random);

        mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEditor = mpref.edit();

        try {
            String str_value = mpref.getString("data", "");
            if (str_value.matches("")) {
                time.setEnabled(true);
                start.setEnabled(true);
                textcontinue.setText("");

            } else {

                if (mpref.getBoolean("finish", false)) {
                    time.setEnabled(true);
                    start.setEnabled(true);
                    textcontinue.setText("");
                } else {

                    time.setEnabled(false);
                    start.setEnabled(false);
                    textcontinue.setText(str_value);
                }
            }
        } catch (Exception e) {

        }
    }

    public void listener(){
        start.setOnClickListener(this);
        reset.setOnClickListener(this);
        getData.setOnClickListener(this);
        bRandom.setOnClickListener(this);
    }


    private void starttimer() {
        Mtime = Integer.parseInt(time.getText().toString());
        Intent intent = new Intent(this, CustomService.class);


        mCountDownTimer = new CountDownTimer(Mtime * 60000, 1000) {  //adjust the milli seconds here
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

    private void updateCountdown() {
        int Seconds = (int) Mtime / 1000 % 60;
        int Minutes = (int) Mtime / (60 * 1000) % 60;
        int Hours = (int) Mtime / (60 * 60 * 1000) % 24;
        String timeleftformatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", Hours, Minutes, Seconds);
        textcontinue.setText(timeleftformatted);
    }

    private void updateButton() {
        if (mTimerRunning) {
            reset.setText("Скинути час");
            start.setVisibility(View.INVISIBLE);
            reset.setVisibility(View.VISIBLE);
        } else {
            reset.setText("Стоп аудіо");
            if (Mtime == 0) {
                reset.setText("Скинути час");
                start.setVisibility(View.VISIBLE);
                reset.setVisibility(View.INVISIBLE);
            }
            start.setVisibility(View.VISIBLE);
        }
    }



    @SuppressLint("SimpleDateFormat")
    @Override
    public void onClick(View v) {

        if (v == start) {
            try{

                saveData();
                click.start();

                    {


                        time.setEnabled(false);
                        start.setEnabled(false);


                        calendar = Calendar.getInstance();
                        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                        date_time = simpleDateFormat.format(calendar.getTime());

                        mEditor.putString("data", date_time).commit();
                        mEditor.putString("minutes", time.getText().toString()).commit();


                        Intent intent_service = new Intent(getApplicationContext(), Time_Service.class);
                        startService(intent_service);
                    }
/*
                    mTimer = new Timer();
                    mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 5, NOTIFY_INTERVAL);*/

            }catch(Exception exeption){
                exeption.printStackTrace();;
            }

        }


        if (v == reset) {
            try {

                click.start();

                Intent intent = new Intent(this, CustomService.class);
                stopService(intent);


                Intent intent_time = new Intent(getApplicationContext(),Time_Service.class);
                stopService(intent_time);

                mEditor.clear().commit();

                time.setEnabled(true);
                start.setEnabled(true);
                Log.i(TAG, "Service stop");
                textcontinue.setText("0:0:0");

            }catch(Exception e){
                e.printStackTrace();
            }
        }

        if(v == bRandom){

            try {
                click.start();
                int a;
                a = (int) (Math.random() * (100-1))+1;
                String b;
                b = String.valueOf(a);
                tRandom.setText(b);
            }catch (Exception e){
                e.printStackTrace();
            }


        }

        if(v == getData){
            click.start();
            startActivity(new Intent(getApplicationContext(), GetData.class));
        }

    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String str_time = intent.getStringExtra("time");
            textcontinue.setText(str_time);

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter(Time_Service.str_receiver));

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
    private void saveData() {

        String name_txt = name.getText().toString().trim();
        UserModel model = new UserModel();

        model.setName(name_txt);
        DatabaseClass.getDatabase(getApplicationContext()).getDao().insertAllData(model);

        Toast.makeText(this, "Data Successfully Saved", Toast.LENGTH_SHORT).show();


    }
}