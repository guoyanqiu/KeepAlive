package com.yanqiu.keepalive.impl;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.fanjun.keeplive.KeepLive;
import com.fanjun.keeplive.config.NotificationUtils;
import com.yanqiu.keepalive.R;
import com.yanqiu.keepalive.utils.DeviceUtils;

import androidx.annotation.Nullable;

/**
 * 播放无声音乐，来保持进程包活
 */
//播放无声音乐，来保持进程保活
public class KeepAliveService extends Service {
    private boolean isScreenON = true;//控制暂停
    private MediaPlayer mediaPlayer;
    //锁屏广播监听
    private ScreenStateReceiver screenStateReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("KeepAliveService", "---KeepAliveService 启动---");
        //注册锁屏广播
        registerScreenStateReceiver();
        //初始化播放器
        initMediaPlayer();
        //开启前台Service
        startForeground(this);
        //start HideNotifactionService
        startHideNotificationService();

        //绑定守护进程
        bindRemoteService();
        return START_STICKY;
    }

    private void bindRemoteService(){
        Intent intent = new Intent(this, RemoteService.class);
        bindService(intent,connection,Context.BIND_ABOVE_CLIENT);
    }
    private void startHideNotificationService() {
        try {
//            if(Build.VERSION.SDK_INT < 25){
            startService(new Intent(this, HideNotificationService.class));
//            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //将keepAliveBinder交给RemoteService
    public IBinder onBind(Intent intent) {
        return keepAliveBinder;
    }

    private GuardAidl.Stub keepAliveBinder = new GuardAidl.Stub() {
        @Override
        public void notifyAlive() throws RemoteException {
            Log.i(null, "Hello RemoteService!");
        }
    };

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Intent remoteService = new Intent(KeepAliveService.this,
                    RemoteService.class);
            KeepAliveService.this.startService(remoteService);

            Intent intent = new Intent(KeepAliveService.this, RemoteService.class);
            //将KeepAliveService和RemoteService进行绑定
            KeepAliveService.this.bindService(intent, connection,
                    Context.BIND_ABOVE_CLIENT);
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                //与RemoteService绑定成功
                GuardAidl remoteBinder = GuardAidl.Stub.asInterface(service);
                remoteBinder.notifyAlive();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("KeepAliveService", "---KeepAliveService onDestroy---");
        if (screenStateReceiver != null) {
            unregisterReceiver(screenStateReceiver);
        }
    }

    public static void startForeground(Service service) {
        Intent intent = new Intent(service.getApplicationContext(), com.fanjun.keeplive.receiver.NotificationClickReceiver.class);
        intent.setAction(com.fanjun.keeplive.receiver.NotificationClickReceiver.CLICK_NOTIFICATION);
        Notification notification = NotificationUtils.createNotification(service, "1", "2", R.drawable.ic_launcher_background, intent);
        service.startForeground(13691, notification);
    }

    private void registerScreenStateReceiver() {
        screenStateReceiver = new ScreenStateReceiver();
        screenStateReceiver.addListener(new ScreenStateReceiver.ScreenStateListener() {
            @Override
            public void screenOn() {
                isScreenON = true;
                Log.e("ScreenStateReceiver", "---音乐关闭---");
                pause();
            }

            @Override
            public void screenOff() {
                isScreenON = false;
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
        if (!DeviceUtils.isScreenOn(this)) {
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
