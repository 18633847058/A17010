package com.yang.eric.a17010.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.yang.eric.a17010.utils.HandlerUtils;

/**
 * Created by Yang on 2017/4/12.
 */

public class BaseActivity extends AppCompatActivity implements HandlerUtils.OnReceiveMessageListener {

    private static final String TAG = "MainActivity";

    private Messenger service;
    private boolean isCon;
    private Messenger messenger;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = new Messenger(iBinder);
            isCon = true;
            Log.e("SERVICE" , "connected!");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
            isCon = false;
            Log.e("SERVICE" , "disconnected!");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMessenger();
        bindServiceInvoked();
    }
    private void bindServiceInvoked() {
        Intent intent = new Intent();
        intent.setAction("com.yang.eric.service");
        intent.setPackage(getPackageName());
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Log.e(TAG, "bindService invoked !");
    }

    public void sendMessage(Message msg) {
        msg.replyTo = messenger;
        if(isCon){
            try {
                service.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    public void initMessenger(){
        messenger = new Messenger(new HandlerUtils.HandlerHolder(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    public void handlerMessage(Message msg) {

    }
}
