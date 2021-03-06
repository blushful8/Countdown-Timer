package com.example.androidappnau;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;

public class CustomService extends Service {


    String TAG = "Service TAG";
        private MediaPlayer player;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        player = MediaPlayer.create(this, (Settings.System.DEFAULT_ALARM_ALERT_URI));
        player.setLooping(true);
        player.start();
        Log.i(TAG, "Starting alarm");


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Stopped timer");
        player.stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}


