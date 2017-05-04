package com.yang.eric.a17010.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketClientDelegate;
import com.vilyever.socketclient.helper.SocketResponsePacket;
import com.yang.eric.a17010.MapsApplication;
import com.yang.eric.a17010.R;
import com.yang.eric.a17010.contract.LoginContract;
import com.yang.eric.a17010.presenter.LoginPresenter;
import com.yang.eric.a17010.utils.Constants;
import com.yang.eric.a17010.utils.LogUtils;

import static com.yang.eric.a17010.R.string.login;

public class LoginActivity extends AppCompatActivity implements SocketClientDelegate, LoginContract.View {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private Button btnLogin;
    private EditText etUsername;
    private String username;
    private EditText etPassword;
    private String password;

    private LoginContract.Presenter presenter = new LoginPresenter(this);
    private ProgressDialog progressDialog;
    private boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(login);
        setListener();
        initViews(null);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SETTINGS, Context.MODE_PRIVATE);
        isLogin = sharedPreferences.getBoolean(Constants.ISLOGIN, false);
    }

    @Override
    public void setListener() {
        MapsApplication.getClient().getSocketClient().registerSocketClientDelegate(this);
    }

    @Override
    public void removeListener() {
        MapsApplication.getClient().getSocketClient().removeSocketClientDelegate(this);
    }

    @Override
    public void initViews(View view) {
        btnLogin = (Button) findViewById(R.id.btn_login);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();
                presenter.login(username, password);
            }
        });
    }

    @Override
    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            //点击提示框外面是否取消提示框
            progressDialog.setCanceledOnTouchOutside(false);
            //点击返回键是否取消提示框
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("登录中");
            progressDialog.show();
        }
    }


    @Override
    public void stopLoading() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.cancel();
            }
            progressDialog = null;
        }
    }

    @Override
    public void showMessage(String error, boolean flag) {
       Snackbar snackbar =  Snackbar.make(btnLogin, error, Snackbar.LENGTH_LONG);
        if (flag) {
            snackbar.setAction("重新登录", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.login(username,password);
                }
            });
        }
        snackbar.show();
    }

    @Override
    public void go2Main() {
        Toast.makeText(this, "登录成功!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }


    @Override
    public void onConnected(SocketClient client) {
        Log.e(TAG, "onConnected");
        presenter.sendLogin(username,password);
    }

    @Override
    public void onDisconnected(SocketClient client) {
        LogUtils.e(TAG, "onDisconnected");
//        showMessage("连接断开!", false);
        stopLoading();
        Toast.makeText(this, "连接断开!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(SocketClient client, @NonNull SocketResponsePacket responsePacket) {
        LogUtils.e(TAG, "onResponse" + responsePacket.getMessage());
//        if ( responsePacket.getMessage() != null && responsePacket.getMessage().equals("1")) {
//            stopLoading();
//            showMessage("登录成功!",false);
//            go2Main();
//        }
        presenter.loginResponse(responsePacket.getData());
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        if(presenter != null) {
            this.presenter = presenter;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.disconnect();
    }
}
