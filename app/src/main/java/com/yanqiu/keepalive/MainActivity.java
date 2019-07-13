package com.yanqiu.keepalive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.yanqiu.keepalive.impl.ScreenStateReceiver;
import com.yanqiu.keepalive.service.NonVoicePlayService;

//https://github.com/fanqieVip/keeplive
//https://www.jianshu.com/p/dd01580743e7
//https://zhuanlan.zhihu.com/p/21987083
public class MainActivity extends AppCompatActivity {
    private ScreenStateReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, NonVoicePlayService.class));
        //register();
    }

    public  void register(){
        receiver = new ScreenStateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, intentFilter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
