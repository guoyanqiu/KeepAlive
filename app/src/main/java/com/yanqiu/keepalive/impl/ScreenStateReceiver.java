package com.yanqiu.keepalive.impl;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 注意在屏幕开关的时候之所以添加Handler延迟消息，就是为了防止屏幕快速锁屏和截屏的操作
 * 屏幕开关监听
 */
public class ScreenStateReceiver extends BroadcastReceiver {
    private Handler mHandler;
    private boolean isScreenOn = true;
    private PendingIntent  pendingIntent;
    private List<ScreenStateListener> screenStateListeners = null;
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i("ScreenStateReceiver", "---屏幕锁屏监听---");
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            Log.i("ScreenStateReceiver", "---屏幕关闭---");
            isScreenOn = false;
            startOnePixelActivity(context);

        } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
            isScreenOn = true;
            Log.i("ScreenStateReceiver", "---屏幕打开---");
            if(pendingIntent!=null){
                pendingIntent.cancel();
            }
            notifyScreenOn();
        }
    }

    private void notifyScreenOn(){
        if(screenStateListeners==null){
            return;
        }
        for(ScreenStateListener listener:screenStateListeners){
            listener.screenOn();
        }
    }

    private void notifyScreenOff(){
        if(screenStateListeners==null){
            return;
        }
        for(ScreenStateListener listener:screenStateListeners){
            listener.screenOff();
        }
    }
    public void addListener(ScreenStateListener listener){
        if(listener==null){
            return;
        }
        if(screenStateListeners==null){
            screenStateListeners = new ArrayList<>();
        }
        screenStateListeners.add(listener);
    }
    private void startOnePixelActivity(final Context context){
        if(mHandler==null){
            mHandler = new Handler(Looper.myLooper());
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isScreenOn){
                    return;
                }
                if(pendingIntent!=null){
                    pendingIntent.cancel();
                }
                Intent startOnePixelActivity = new Intent(context, OnePixelActivity.class);
                startOnePixelActivity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                //启动一像素包活activity
                pendingIntent = PendingIntent.getActivity(context, 0, startOnePixelActivity, 0);
                try {
                    pendingIntent.send();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                notifyScreenOff();
            }
        },1000);
    }

    public interface ScreenStateListener{
        void screenOn();
        void screenOff();
    }
}
