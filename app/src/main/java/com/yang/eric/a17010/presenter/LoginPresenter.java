package com.yang.eric.a17010.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.yang.eric.a17010.MapsApplication;
import com.yang.eric.a17010.contract.LoginContract;
import com.yang.eric.a17010.protocol.AuthMsg;
import com.yang.eric.a17010.protocol.LoginMsg;
import com.yang.eric.a17010.protocol.LoginResponse;
import com.yang.eric.a17010.protocol.Response;
import com.yang.eric.a17010.utils.Constants;
import com.yang.eric.a17010.utils.NetworkState;

/**
 * Created by Yang on 2017/4/20.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private SharedPreferences sharedPreferences;

    private String username = "";
    private long uuid;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
        this.view.setPresenter(this);
        this.sharedPreferences = MapsApplication.getInstance()
                .getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
    }

    @Override
    public boolean checkInput(String username, String password) {

        if (username.isEmpty()||password.isEmpty()) {
            view.showMessage("用户名、密码为空！", false);
            return false;
        } else {
            if (username.length() > 16||username.length() < 1) {
                view.showMessage("用户名长度出现问题!", false);
                return false;
            }
            if (password.length() > 16||password.length() < 1) {
                view.showMessage("密码长度出现问题!", false);
                return false;
            }
        }
        return true;
    }

    @Override
    public void login(String username, String password) {
        if (!NetworkState.networkConnected(MapsApplication.getInstance())) {
            view.showMessage("无法连接到网络!", false);
            return;
        }
        if(checkInput(username,password)) {
            view.showLoading();
            MapsApplication.getClient().connect();
        }
    }

    @Override
    public void sendLogin(String username, String password) {
        LoginMsg msg = new LoginMsg();
        msg.setUsername(username);
        this.username = username;
        msg.setPassword(password);
        msg.setID(sharedPreferences.getString(Constants.IMEI, ""));
        msg.setBDID(123456);
        MapsApplication.getClient().getSocketClient().sendData(msg.encode());
    }

    @Override
    public void sendAuth() {
        AuthMsg authMsg = new AuthMsg();
        authMsg.setUsername(username);
        authMsg.setUUID(uuid);
        authMsg.setBDID(123456);
        MapsApplication.getClient().getSocketClient().sendData(authMsg.encode());
    }

    @Override
    public void loginResponse(byte[] message) {
        switch (message[0]) {
            case (byte) 0x81:
                LoginResponse response = new LoginResponse();
                if (response.decode(message)) {
                    if (response.getResult() == 0) {
                        view.stopLoading();
                        uuid = response.getUUID();
                        saveCode();
                        sendAuth();
                        view.showMessage("登录成功!", false);
                        view.go2Main();
                    } else {
                        view.stopLoading();
                        view.showMessage("登录失败,账号密码错误!", false);
                    }
                } else {
                    view.stopLoading();
                    view.showMessage("解析返回错误,请重新登录!", true);
                    MapsApplication.getClient().disconnect();
                }
                break;
            case 0x00:
                Response r = new Response();
                if (r.decode(message)) {
                    if (r.getResponseType() == 0x02) {
                        if (r.getResult() == 0x00) {
                            view.stopLoading();
//                            view.showMessage("登录成功!", false);
                            view.go2Main();
                        } else if (r.getResult() == 0x01){
                            view.stopLoading();
                            view.showMessage("登录失败!", false);
                        }
                    }
                } else {
                    view.stopLoading();
                    view.showMessage("解析返回错误,请重新登录!", true);
                    MapsApplication.getClient().disconnect();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void saveCode() {
        sharedPreferences.edit().putLong(Constants.CHECK_COED, uuid).apply();
        sharedPreferences.edit().putBoolean(Constants.ISLOGIN, true).apply();
        sharedPreferences.edit().putString(Constants.USERNAME, username).apply();
    }

    @Override
    public void disconnect() {
        view.removeListener();
        if (!sharedPreferences.getBoolean(Constants.ISLOGIN, false)) {
            MapsApplication.getClient().disconnect();
        }
    }

    @Override
    public void start() {

    }
}
