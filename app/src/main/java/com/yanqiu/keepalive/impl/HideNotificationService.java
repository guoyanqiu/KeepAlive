package com.yanqiu.keepalive.impl;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

/**
 * 隐藏KeepAlveService设置为前台服务时的通知
 */
public class HideNotificationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //设置为前台 Service
        KeepAliveService.startForeground(this);

      //  stopForeground(true);//不调用此方法也能达到效果
        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("HideNotificationService", "---HideNotificationService 销毁222---");
    }
}
