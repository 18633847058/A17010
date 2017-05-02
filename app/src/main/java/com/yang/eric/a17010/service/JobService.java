package com.yang.eric.a17010.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class JobService extends Service {

    public JobService() {
    }

    private static final int MSG_NUM = 0x110;

    private Messenger messenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msgFormClient) {
            Message msgToClient = Message.obtain(msgFormClient);
            switch (msgFormClient.what){
                case MSG_NUM:
                    msgToClient.what = MSG_NUM;
                    try {
                        Thread.sleep(2000);
                        msgToClient.arg2 = msgFormClient.arg1 + msgFormClient.arg2;
                        msgFormClient.replyTo.send(msgToClient);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            super.handleMessage(msgFormClient);
        }
    });
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
