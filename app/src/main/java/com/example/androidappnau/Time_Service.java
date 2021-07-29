package com.example.androidappnau;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Time_Service extends Service {

    public static String str_receiver = "com.countdowntimerservice.receiver";

    private Handler mHandler = new Handler();
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String strDate;
    Date date_current, date_diff;
    SharedPreferences mpref;
    SharedPreferences.Editor mEditor;
MediaPlayer player;

    private Timer mTimer = null;
    public static final long NOTIFY_INTERVAL = 1000;
    Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEditor = mpref.edit();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 5, NOTIFY_INTERVAL);
        intent = new Intent(str_receiver);
    }


    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {

                    calendar = Calendar.getInstance();
                    simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    strDate = simpleDateFormat.format(calendar.getTime());
                    Log.e("strDate", strDate);
                    twoDatesBetweenTime();

                }

            });
        }

    }

    public String twoDatesBetweenTime() {


        try {
            date_current = simpleDateFormat.parse(strDate);
        } catch (Exception e) {

        }

        try {
            date_diff = simpleDateFormat.parse(mpref.getString("data", ""));
        } catch (Exception e) {

        }

        try {


            long diff = date_current.getTime() - date_diff.getTime();
            int int_hours = Integer.valueOf(mpref.getString("minutes", ""));

            long int_timer = TimeUnit.MINUTES.toMillis(int_hours);
            long long_hours = int_timer - diff;
            long diffSeconds2 = long_hours / 1000 % 60;
            long diffMinutes2 = long_hours / (60 * 1000) % 60;
            long diffHours2 = long_hours / (60 * 60 * 1000) % 24;


            if (long_hours > 0) {
                String str_testing = diffHours2 + ":" + diffMinutes2 + ":" + diffSeconds2;

                Log.e("TIME", str_testing);

                fn_update(str_testing);
            } else {
                player = MediaPlayer.create(this, (Settings.System.DEFAULT_ALARM_ALERT_URI));
                player.setLooping(false);
                player.start();
                mEditor.putBoolean("finish", true).commit();
                mTimer.cancel();


            }
        }catch (Exception e){
            mTimer.cancel();
            mTimer.purge();


        }

        return "";

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Service finish","Finish");

    }

    private void fn_update(String str_time){

        intent.putExtra("time",str_time);
        sendBroadcast(intent);
    }
}