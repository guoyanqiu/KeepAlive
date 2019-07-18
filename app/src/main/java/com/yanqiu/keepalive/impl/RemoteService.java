package com.yanqiu.keepalive.impl;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.fanjun.keeplive.service.LocalService;

import androidx.annotation.Nullable;

public class RemoteService extends Service {
    public IBinder onBind(Intent intent) {
        return remoteBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bindService(new Intent(RemoteService.this, LocalService.class),
                connection, Context.BIND_ABOVE_CLIENT);
        return super.onStartCommand(intent, flags, startId);
    }


    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Intent intent = new Intent(RemoteService.this, KeepAliveService.class);
            //将KeepAliveService和RemoteService进行绑定
            RemoteService.this.bindService(intent, connection,
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
    private GuardAidl.Stub remoteBinder = new GuardAidl.Stub(){
        @Override
        public void notifyAlive() throws RemoteException {
            Log.i(null,"Hello KeepAliveService!");
        }
    };
}
