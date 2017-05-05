package com.yang.eric.a17010.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.yang.eric.a17010.MapsApplication;
import com.yang.eric.a17010.contract.MainContract;
import com.yang.eric.a17010.protocol.AuthMsg;
import com.yang.eric.a17010.protocol.Response;
import com.yang.eric.a17010.utils.Constants;

/**
 * Created by Yang on 2017/4/21.
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private SharedPreferences sharedPreferences;

    private String username;
    private long uuid;

    public MainPresenter(MainContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
        this.sharedPreferences = MapsApplication.getApplication()
                .getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
    }

    @Override
    public void sendAuth() {
        username = sharedPreferences.getString(Constants.USERNAME, "");
        uuid = sharedPreferences.getLong(Constants.CHECK_COED, 0);
        if (!username.isEmpty() && uuid != 0) {
            AuthMsg authMsg = new AuthMsg();
            authMsg.setUsername(username);
            authMsg.setUUID(uuid);
            authMsg.setBDID(123456);
            MapsApplication.getClient().getSocketClient().sendData(authMsg.encode());
        }
    }

    @Override
    public void receive(byte[] message) {
        switch (message[0]) {
            case 0x00:
                Response r = new Response();
                if (r.decode(message)) {
                    if (r.getResponseType() == 0x02) {
                        if (r.getResult() == 0x00) {
                            view.showMessage("鉴权成功!", false);
                        } else if (r.getResult() == 0x01){
                            view.showMessage("鉴权码错误,请重新登录!", true);
                            wipeData();
                            disconnect();
                            view.go2Login();
                        }
                    }
                } else {
                    MapsApplication.getClient().disconnect();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void wipeData() {
        sharedPreferences.edit().putString(Constants.USERNAME, "").apply();
        sharedPreferences.edit().putLong(Constants.CHECK_COED, 0).apply();
        sharedPreferences.edit().putBoolean(Constants.ISLOGIN, false).apply();
    }

    @Override
    public void disconnect() {
        view.removeListener();
        MapsApplication.getClient().disconnect();
    }

    @Override
    public void start() {

    }
}
