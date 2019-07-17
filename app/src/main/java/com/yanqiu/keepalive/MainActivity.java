package com.yanqiu.keepalive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.yanqiu.keepalive.impl.KeepAliveService;
import com.yanqiu.keepalive.impl.ScreenStateReceiver;

/**
 *本篇demo改编自 https://github.com/fanqieVip/keeplive
 */
//https://github.com/fanqieVip/keeplive
//https://www.jianshu.com/p/dd01580743e7
//https://zhuanlan.zhihu.com/p/21987083
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, KeepAliveService.class));
    }
}
