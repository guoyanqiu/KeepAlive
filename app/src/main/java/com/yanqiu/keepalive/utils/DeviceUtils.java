package com.yanqiu.keepalive.utils;

import android.content.Context;
import android.os.PowerManager;

public class DeviceUtils {
    /**
     * 判断屏幕是否亮了
     * @param context
     * @return
     */
    public static boolean isScreenOn(Context context){
        if(context==null){
            return false;
        }

        PowerManager pm = (PowerManager)context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if(pm==null){
            return false;
        }
        return pm.isScreenOn();
    }
}
