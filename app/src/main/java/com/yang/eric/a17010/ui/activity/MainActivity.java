package com.yang.eric.a17010.ui.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.mapbar.license.License;
import com.mapbar.mapdal.NativeEnv;
import com.mapbar.mapdal.WorldManager;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketClientDelegate;
import com.vilyever.socketclient.helper.SocketResponsePacket;
import com.yang.eric.a17010.MapsApplication;
import com.yang.eric.a17010.R;
import com.yang.eric.a17010.contract.MainContract;
import com.yang.eric.a17010.map.MapLicense;
import com.yang.eric.a17010.presenter.MainPresenter;
import com.yang.eric.a17010.service.JobService;
import com.yang.eric.a17010.ui.fragment.MapsFragment;
import com.yang.eric.a17010.utils.LogUtils;


public class MainActivity extends AppCompatActivity implements SocketClientDelegate, MainContract.View {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MainContract.Presenter presenter = new MainPresenter(this);
    private PopupWindow popupWindow;
    private BottomNavigationView navigation;
    private MapsFragment mapsFragment;

    private MapLicense mapLicense;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        initViews(null);
        mapLicense = new MapLicense(this);

        if (savedInstanceState != null) {
            mapsFragment = (MapsFragment) getSupportFragmentManager().getFragment(savedInstanceState, "MapsFragment");
        } else {
            mapLicense.initLicense();
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
                        go2Message();
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

    public void go2Message() {
        Intent intent = new Intent(MainActivity.this, MessageActivity.class);
        startActivity(intent);
        this.finish();
    }


    @Override
    public void onConnected(SocketClient client) {
        LogUtils.e(TAG, "onConnected");
        presenter.sendAuth();
        mapsFragment.setOnline();
    }

    @Override
    public void onDisconnected(SocketClient client) {
        LogUtils.e(TAG, "onDisconnected");
        mapsFragment.setOffline();
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
}
