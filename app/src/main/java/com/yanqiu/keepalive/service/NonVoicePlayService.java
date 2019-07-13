package com.yanqiu.keepalive.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.yanqiu.keepalive.R;
import com.yanqiu.keepalive.impl.ScreenStateReceiver;
import com.yanqiu.keepalive.utils.DeviceUtils;

import androidx.annotation.Nullable;

/**
 * 播放无声音乐，来保持进程包活
 */
public class NonVoicePlayService extends Service {
    private boolean isScreenON = true;//控制暂停
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private ScreenStateReceiver screenStateReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        if (handler == null) {
            handler = new Handler();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("NonVoicePlayService", "---NonVoicePlayService 启动---");
        initMediaPlayer();
        registerScreenStateReceiver();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(screenStateReceiver!=null){
            unregisterReceiver(screenStateReceiver);
        }
    }

    private void registerScreenStateReceiver(){
        screenStateReceiver = new ScreenStateReceiver();
        screenStateReceiver.addListener(new ScreenStateReceiver.ScreenStateListener() {
            @Override
            public void screenOn() {
                isScreenON=true;
                Log.i("ScreenStateReceiver", "---音乐关闭---");
                pause();
            }

            @Override
            public void screenOff() {
               isScreenON=false;
                Log.i("ScreenStateReceiver", "---音乐开启---");
               play();
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenStateReceiver, intentFilter);
    }
    private void initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.novioce);
        }
        mediaPlayer.setVolume(0f, 0f);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (isScreenON) {
                    return;
                }
                //循环播放
                play();
            }
        });

        if(!DeviceUtils.isScreenOn(this)){
            play();
        }

    }
    private void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }
}
