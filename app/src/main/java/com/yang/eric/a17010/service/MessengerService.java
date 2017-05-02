package com.yang.eric.a17010.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.yang.eric.a17010.beans.User;
import com.yang.eric.a17010.utils.HandlerUtils;

public class MessengerService extends Service implements HandlerUtils.OnReceiveMessageListener {

    private Messenger messenger = new Messenger(new HandlerUtils.HandlerHolder(this));
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void handlerMessage(Message msgFormClient) {
        Message msgToClient = Message.obtain(msgFormClient);
        switch (msgFormClient.what){
            case 1:
                msgToClient.what = 1;
                try {
                    if(msgFormClient.obj != null) {
                        if(((User) msgFormClient.obj).equals(new User("1","1")))
                            msgToClient.arg1 = 1;
                        else
                            msgToClient.arg1 = 0;
                    } else {
                        msgToClient.arg1 = 0;
                    }
                    msgFormClient.replyTo.send(msgToClient);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
