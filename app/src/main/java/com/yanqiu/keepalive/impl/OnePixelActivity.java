package com.yanqiu.keepalive.impl;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.yanqiu.keepalive.utils.DeviceUtils;

import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

/**
 * 思路：在锁屏的时候创建一个像素的Activity，解锁的时候关闭一个像素的Activity。
 * 但是据网络资料显示有些手机是删除了解锁屏幕的广播（待验证）。
 *
 * 这就有一个问题：就是在手机不锁屏的情况下可能会被干掉。此时怎么办呢？
 *通过该方案，可以使进程的优先级在屏幕锁屏时间由4提升为最高优先级1
 * 适用场景： 本方案主要解决第三方应用及系统管理工具在检测到锁屏事件后一段时间（一般为5分钟以内）内会杀死后台进程，
 * 已达到省电的目的问题
 *适用版本： 适用于所有的 Android 版本。
 * 需要设置为singIntance模式，是为了防止如下情况，在非singleInstance的情况下操作步骤如下：
 * 1、用户按下home键，app回到后台
 * 2、按下锁屏键盘，此时会启动OnePixelActivity
 * 3、解锁，OnePixelActivity退出，MainActivity会自动从后台切换到前台
 * 因为是自动切换到前台，会对用户造成困扰，所以切换成SingleInstance模式就没有这个问题

 */
public class OnePixelActivity extends Activity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Window window = getWindow();
        if(window!=null){
            window.setGravity(Gravity.START | Gravity.TOP);
            WindowManager.LayoutParams params = window.getAttributes();
            params.x = 0;
            params.y = 0;
            params.height = 1;
            params.width = 1;
            window.setAttributes(params);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("OnePixelActivity","OnePixelActivity已经启动");
        if (DeviceUtils.isScreenOn(this)) {
            Log.i("OnePixelActivity","OnePixelActivity 屏幕打开关闭自己");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("OnePixelActivity","OnePixelActivity已经销毁");
    }
}
