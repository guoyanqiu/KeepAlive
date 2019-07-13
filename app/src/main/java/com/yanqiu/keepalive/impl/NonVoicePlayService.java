package com.yanqiu.keepalive.impl;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.fanjun.keeplive.KeepLive;
import com.fanjun.keeplive.config.NotificationUtils;
import com.yanqiu.keepalive.R;
import com.yanqiu.keepalive.utils.DeviceUtils;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * 播放无声音乐，来保持进程包活
 */
//播放无声音乐，来保持进程保活
public class NonVoicePlayService extends Service {
    private boolean isScreenON = true;//控制暂停
    private MediaPlayer mediaPlayer;
    //锁屏广播监听
    private ScreenStateReceiver screenStateReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("NonVoicePlayService", "---NonVoicePlayService 启动---");
        //注册锁屏广播
        registerScreenStateReceiver();
        //初始化播放器
        initMediaPlayer();
        //开启前台
        startForeground();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("NonVoicePlayService", "---NonVoicePlayService onDestroy---");
        if(screenStateReceiver!=null){
            unregisterReceiver(screenStateReceiver);
        }
    }

    private void startForeground(){
        Intent intent = new Intent(getApplicationContext(),NotificationClickReceiver.class);
        intent.setAction(NotificationClickReceiver.CLICK_NOTIFICATION);

        Notification notification = null;
        String packageName = getPackageName();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if(manager!=null){
                NotificationChannel channel = new NotificationChannel(packageName, packageName, NotificationManager.IMPORTANCE_HIGH);
                channel.enableVibration(false);
                channel.enableLights(false);
                channel.enableVibration(false);
                channel.setVibrationPattern(new long[]{0});
                channel.setSound(null, null);
                manager.createNotificationChannel(channel);
            }
            notification= new Notification.Builder(this, packageName)
                    .setContentTitle("1")
                    .setContentText("2")
                    .setAutoCancel(true).setContentIntent(pendingIntent).build();
        } else {
            notification= new NotificationCompat.Builder(this, packageName)
                    .setContentTitle("1")
                    .setContentText("2")
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0})
                    .setContentIntent(pendingIntent).build();
        }

        startForeground(13691, notification);
    }

    private void registerScreenStateReceiver(){
        screenStateReceiver = new ScreenStateReceiver();
        screenStateReceiver.addListener(new ScreenStateReceiver.ScreenStateListener() {
            @Override
            public void screenOn() {
                isScreenON=true;
                Log.e("ScreenStateReceiver", "---音乐关闭---");
                pause();
            }

            @Override
            public void screenOff() {
               isScreenON=false;
                Log.e("ScreenStateReceiver", "---音乐开启---");
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
        }
        //防止Service启动的时候屏幕处于解锁状态
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
