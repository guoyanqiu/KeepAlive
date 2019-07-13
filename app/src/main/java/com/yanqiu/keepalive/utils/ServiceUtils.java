package com.yanqiu.keepalive.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.Iterator;
import java.util.List;

public class ServiceUtils {
    /**
     * 判断service是否在运行
     * @param ctx
     * @param className
     * @return
     */
    public static boolean isServiceRunning(Context ctx, String className) {
        if(ctx==null){
            return false;
        }

        ActivityManager activityManager = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> servicesList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        if(servicesList==null){
            return false;
        }
        boolean isRunning = false;
        Iterator<ActivityManager.RunningServiceInfo> iterator = servicesList.iterator();
        while (iterator.hasNext()) {
            ActivityManager.RunningServiceInfo si =iterator.next();
            if (className.equals(si.service.getClassName())) {
                isRunning = true;
                break;
            }
        }//end while
        return isRunning;
    }
}
