package com.example.androidappnau;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.androidappnau.EntityClass.UserModel;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;


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
    private Button getData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        time = findViewById(R.id.et_set_time_off_device);
        start = findViewById(R.id.b_first_button);
        data = findViewById(R.id.tv_data);
        reset = findViewById(R.id.v_second_button);
        textcontinue = findViewById(R.id.tv_continue_time);

        getData = findViewById(R.id.btn_getData);
        name = time;

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        data.setText("Сьогодні: " + currentDate);


        click = MediaPlayer.create(this, R.raw.click);
        click.setLooping(false);

        start.setOnClickListener(this);
        reset.setOnClickListener(this);
        getData.setOnClickListener(this);


    }



    private void starttimer() {
        Mtime = Integer.parseInt(time.getText().toString());
        Intent intent = new Intent(this, CustomService.class);


        mCountDownTimer = new CountDownTimer(Mtime * 60000, 1000) { // adjust the milli seconds here
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
                reset.setVisibility(View.INVISIBLE);
            }
            start.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {

        if (view == start) {
            try{
                saveData();
                mTimerRunning = true;
                click.start();

                starttimer();
                updateButton();

                Intent intent = new Intent(this, CustomService.class);//У випадку повторного нажаття
                stopService(intent);
            }catch(Exception exeption){
                exeption.printStackTrace();;
            }

        }


        if (view == reset) {
            try {


                mTimerRunning = false;
                mCountDownTimer.cancel();
                time.setText("");
                click.start();

                Intent intent = new Intent(this, CustomService.class);
                stopService(intent);

                Mtime = 0;
                updateButton();

                Log.i(TAG, "Service stop");
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        if(view == getData){
            click.start();
            startActivity(new Intent(getApplicationContext(), GetData.class));
        }

    }
    private void saveData() {

        String name_txt = name.getText().toString().trim();



        UserModel model = new UserModel();
        model.setName(name_txt);

        DatabaseClass.getDatabase(getApplicationContext()).getDao().insertAllData(model);

        Toast.makeText(this, "Data Successfully Saved", Toast.LENGTH_SHORT).show();


    }
}