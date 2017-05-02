package com.yang.eric.a17010.ui.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.mapbar.license.License;
import com.mapbar.mapdal.Auth;
import com.mapbar.mapdal.NativeEnv;
import com.mapbar.mapdal.NativeEnvParams;
import com.mapbar.mapdal.SdkAuth;
import com.mapbar.mapdal.WorldManager;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketClientDelegate;
import com.vilyever.socketclient.helper.SocketResponsePacket;
import com.yang.eric.a17010.MapsApplication;
import com.yang.eric.a17010.R;
import com.yang.eric.a17010.contract.MainContract;
import com.yang.eric.a17010.presenter.MainPresenter;
import com.yang.eric.a17010.service.JobService;
import com.yang.eric.a17010.ui.fragment.MapsFragment;
import com.yang.eric.a17010.utils.Constants;
import com.yang.eric.a17010.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.yang.eric.a17010.utils.Constants.APP_NAME;


public class MainActivity extends AppCompatActivity implements SocketClientDelegate, MainContract.View {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MainContract.Presenter presenter = new MainPresenter(this);
    private PopupWindow popupWindow;
    private BottomNavigationView navigation;
    private MapsFragment mapsFragment;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        initViews(null);

        if (savedInstanceState != null) {
            mapsFragment = (MapsFragment) getSupportFragmentManager().getFragment(savedInstanceState, "MapsFragment");
        } else {
            initLicense();
            mapsFragment = MapsFragment.newInstance();
        }

        if (!mapsFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_content, mapsFragment, "MapsFragment")
                    .commit();
        }
        setListener();
        MapsApplication.getClient().connect();
	}
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapsFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "MapsFragment", mapsFragment);
        }
    }
    @Override
    protected void onDestroy() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (JobService.class.getName().equals(service.service.getClassName())) {
                stopService(new Intent(this, JobService.class));
            }
        }
        presenter.disconnect();
        WorldManager.getInstance().cleanup();
        NativeEnv.cleanup();
        License.cleanup();
        super.onDestroy();
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        if (presenter != null) {
            this.presenter = presenter;
        }
    }

    @Override
    public void initViews(View view) {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_message:
                        return true;
                    case R.id.navigation_dialog:
                        showPop();
                        return true;
                    case R.id.navigation_mine:
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void setListener() {
        MapsApplication.getClient().getSocketClient().registerSocketClientDelegate(this);
    }

    @Override
    public void removeListener() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void showMessage(String error, boolean flag) {
        if (flag) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        } else {
            Snackbar.make(navigation, error, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void showPop() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_menu,null);
        //处理popWindow 显示内容
        handleLogic(contentView);
        //创建并显示popWindow
        int[] location = new int[2];
        navigation.getLocationOnScreen(location);
        if (popupWindow == null) {
            popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setOutsideTouchable(false);
        }
        popupWindow.showAtLocation(navigation, Gravity.NO_GRAVITY ,location[0],
                        location[1] - navigation.getHeight());
    }

    private void handleLogic(View contentView) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow!=null){
                    popupWindow.dismiss();
                }
                String showContent = "";
                switch (v.getId()){
                    case R.id.btn_navigation:
                        showContent = "点击 导航";
                        break;
                    case R.id.btn_punch:
                        showContent = "点击 打卡";
                        break;
                    case R.id.btn_inspection:
                        showContent = "点击 巡检";
                        break;
                    case R.id.btn_upload_image:
                        showContent = "点击 传图";
                        break;
                    case R.id.btn_upload_location:
                        showContent = "点击 报位" ;
                        break;
                    case R.id.btn_location:
                        showContent = "点击 定位" ;
                        break;
                }
                Toast.makeText(MainActivity.this,showContent,Toast.LENGTH_SHORT).show();
            }
        };
        contentView.findViewById(R.id.btn_navigation).setOnClickListener(listener);
        contentView.findViewById(R.id.btn_punch).setOnClickListener(listener);
        contentView.findViewById(R.id.btn_inspection).setOnClickListener(listener);
        contentView.findViewById(R.id.btn_upload_image).setOnClickListener(listener);
        contentView.findViewById(R.id.btn_upload_location).setOnClickListener(listener);
        contentView.findViewById(R.id.btn_location).setOnClickListener(listener);
    }

    @Override
    public void go2Login() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }


    @Override
    public void onConnected(SocketClient client) {
        LogUtils.e(TAG, "onConnected");
        presenter.sendAuth();
    }

    @Override
    public void onDisconnected(SocketClient client) {
        LogUtils.e(TAG, "onDisconnected");
//        client.connect();
    }

    @Override
    public void onResponse(SocketClient client, @NonNull SocketResponsePacket responsePacket) {
        LogUtils.e(TAG, "onResponse" + responsePacket.getMessage());
        presenter.receive(responsePacket.getData());
    }
    //退出时的时间
    private long mExitTime;
    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出!", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }







    /**
     * 初始化license信息
     */
    private void initLicense(){
        //拷贝激活文件到应用目录下
        getActivityCodeFile();
        License.init(this, Constants.APP_PATH, APP_NAME, Constants.KEY, mHandler);
    }
    /**
     * 根据apk名称 获取asset里的apk文件
     * 注意：此处不需要每次都copy这个文件，检查如果有此文件，就不需要再copy
     *          但是如果更换了激活key，则需要删除之前的这个文件，然后再copy
     */
    public void getActivityCodeFile() {
        String filename = "activation_codes.dat";
        String path = Constants.APP_PATH + "/"+filename;
        AssetManager asset = getApplicationContext()
                .getAssets();
        try {
            File pathFile = new File(path);
            File pathFileParent = new File(pathFile.getParent());
            if(!pathFileParent.exists()){
                pathFileParent.mkdirs();
            }
            InputStream is = asset.open(filename+".png");
            FileOutputStream fos = new FileOutputStream(new File(path));
            final int fInt = 1024;
            byte[] buffer = new byte[fInt];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化引擎
     */
    private void NativeEnvironmentInit(String appName, String key) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        NativeEnvParams params = new NativeEnvParams(Constants.APP_PATH, appName,
                dm.densityDpi, key, new NativeEnv.AuthCallback() {
            @Override
            public void onDataAuthComplete(int errorCode) {
                String msg = null;
                switch(errorCode){
                    case Auth.Error.deviceIdReaderError:
                        msg="设备ID读取错误";
                        break;
                    case Auth.Error.expired:
                        msg="数据文件权限已经过期";
                        break;
                    case Auth.Error.licenseDeviceIdMismatch:
                        msg="授权文件与当前设备不匹配";
                        break;
                    case Auth.Error.licenseFormatError:
                        msg="授权文件格式错误";
                        break;
                    case Auth.Error.licenseIncompatible:
                        msg="授权文件存在且有效，但是不是针对当前应用程序产品的";
                        break;
                    case Auth.Error.licenseIoError:
                        msg="授权文件IO错误";
                        break;
                    case Auth.Error.licenseMissing:
                        msg="授权文件不存在";
                        break;
                    case Auth.Error.none:
                        msg="数据授权成功";
                        break;
                    case Auth.Error.noPermission:
                        msg="数据未授权";
                        break;
                    case Auth.Error.otherError:
                        msg="其他错误";
                        break;
                }
                if(msg!=null){
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSdkAuthComplete(int errorCode) {
                String msg=null;
                switch(errorCode){
                    case SdkAuth.ErrorCode.deviceIdReaderError:
                        msg="授权设备ID读取错误";
                        break;
                    case SdkAuth.ErrorCode.expired:
                        msg="授权KEY已经过期";
                        break;
                    case SdkAuth.ErrorCode.keyIsInvalid:
                        msg="授权KEY是无效值，已经被注销";
                        break;
                    case SdkAuth.ErrorCode.keyIsMismatch:
                        msg="授权KEY不匹配";
                        break;
                    case SdkAuth.ErrorCode.keyUpLimit:
                        msg="授权KEY到达激活上线";
                        break;
                    case SdkAuth.ErrorCode.licenseDeviceIdMismatch:
                        msg="设备码不匹配";
                        break;
                    case SdkAuth.ErrorCode.licenseFormatError:
                        msg="SDK授权文件格式错误";
                        break;
                    case SdkAuth.ErrorCode.licenseIoError:
                        msg="SDK授权文件读取错误";
                        break;
                    case SdkAuth.ErrorCode.licenseMissing:
                        msg="SDK授权文件没有准备好";
                        break;
                    case SdkAuth.ErrorCode.networkContentError:
                        msg="网络返回信息格式错误";
                        break;
                    case SdkAuth.ErrorCode.netWorkIsUnavailable:
                        msg="网络不可用，无法请求SDK验证";
                        break;
                    case SdkAuth.ErrorCode.none:
                        msg="SDK验证通过";
                        break;
                    case SdkAuth.ErrorCode.noPermission:
                        msg="模块没有权限";
                        break;
                    case SdkAuth.ErrorCode.otherError:
                        msg="其他错误";
                        break;
                }
                if(msg!=null){
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
        params.sdkAuthOfflineOnly=true;
        NativeEnv.init(getApplicationContext(), params);
        WorldManager.getInstance().init();
    }

    /**
     * 初始化License模块的回调信息
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case License.Initialize.succ: {
                    //授权验证模块初始化成功
                    String code = License.getActivationCodeNew();
                    if(code==null){
                        //授权模式，判断是否激活成功
                        if (License.autoActivateNew()) {
                            //license激活成功，则进行sdk授权
                            NativeEnvironmentInit(APP_NAME,Constants.KEY);

                            Toast.makeText(MainActivity.this, "license activity success", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "license activity fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
                case License.Initialize.deviceIdFailed:
                    //获取不到设备ID，一般来说是MAC地址
                    Toast.makeText(MainActivity.this, "deviceIdFailed", Toast.LENGTH_SHORT).show();
                    break;
                case License.Initialize.noAvailableDataPath:
                    //不存在有效的数据目录
                    Toast.makeText(MainActivity.this, "noAvailableDataPath", Toast.LENGTH_SHORT).show();
                    break;
                case License.Initialize.otherFailed:
                    //授权验证模块初始化失败
                    Toast.makeText(MainActivity.this, "otherFailed", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            NativeEnvironmentInit(APP_NAME, Constants.KEY);
        }
    };
}
