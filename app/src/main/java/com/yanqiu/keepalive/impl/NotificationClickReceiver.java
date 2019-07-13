package com.yanqiu.keepalive.impl;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fanjun.keeplive.KeepLive;

/**
 * 点击通知栏的时候关闭
 */
public class NotificationClickReceiver extends BroadcastReceiver {
    public final static String CLICK_NOTIFICATION = "CLICK_NOTIFICATION";

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(CLICK_NOTIFICATION)) {
            Log.e("6666666","NotificationClickReceiver");
        }
    }
}
