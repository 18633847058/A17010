package com.yang.eric.a17010.ui.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.helper.SocketClientDelegate;
import com.vilyever.socketclient.helper.SocketResponsePacket;
import com.yang.eric.a17010.MapsApplication;
import com.yang.eric.a17010.R;
import com.yang.eric.a17010.contract.MainContract;
import com.yang.eric.a17010.map.MapLicense;
import com.yang.eric.a17010.presenter.MainPresenter;
import com.yang.eric.a17010.service.JobService;
import com.yang.eric.a17010.service.LocationService;
import com.yang.eric.a17010.ui.fragment.MapsFragment;
import com.yang.eric.a17010.utils.LogUtils;


public class MainActivity extends AppCompatActivity implements SocketClientDelegate, MainContract.View {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MainContract.Presenter presenter = new MainPresenter(this);
    private PopupWindow popupWindow;
    private BottomNavigationView navigation;
    private MapsFragment mapsFragment;
    private MapLicense mapLicense;

    private LocationService locationService;


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
    protected void onStart() {
        super.onStart();

        // -----------location config ------------
        locationService = ((MapsApplication) getApplication()).locationService;
        //获取LocationService实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取LocationService实例的
        locationService.registerListener(mListener);
        //注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();
    }
    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (location != null && (location.getLocType() == 161 || location.getLocType() == 66) || location.getLocType() == 61)  {
//                StringBuffer sb = new StringBuffer(256);
//                sb.append("time : ");
//                /**
//                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
//                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
//                 */
//                sb.append(location.getTime());
//                sb.append("\nlocType : ");// 定位类型
//                sb.append(location.getLocType());
//                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
//                sb.append(location.getLocTypeDescription());
//                sb.append("\nlatitude : ");// 纬度
//                sb.append(location.getLatitude());
//                sb.append("\nlontitude : ");// 经度
//                sb.append(location.getLongitude());
//                sb.append("\nradius : ");// 半径
//                sb.append(location.getRadius());
//                sb.append("\nCountryCode : ");// 国家码
//                sb.append(location.getCountryCode());
//                sb.append("\nCountry : ");// 国家名称
//                sb.append(location.getCountry());
//                sb.append("\ncitycode : ");// 城市编码
//                sb.append(location.getCityCode());
//                sb.append("\ncity : ");// 城市
//                sb.append(location.getCity());
//                sb.append("\nDistrict : ");// 区
//                sb.append(location.getDistrict());
//                sb.append("\nStreet : ");// 街道
//                sb.append(location.getStreet());
//                sb.append("\naddr : ");// 地址信息
//                sb.append(location.getAddrStr());
//                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
//                sb.append(location.getUserIndoorState());
//                sb.append("\nDirection(not all devices have value): ");
//                sb.append(location.getDirection());// 方向
//                sb.append("\nlocationdescribe: ");
//                sb.append(location.getLocationDescribe());// 位置语义化信息
//                sb.append("\nPoi: ");// POI信息
//                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
//                    for (int i = 0; i < location.getPoiList().size(); i++) {
//                        Poi poi = (Poi) location.getPoiList().get(i);
//                        sb.append(poi.getName() + ";");
//                    }
//                }
//                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//                    sb.append("\nspeed : ");
//                    sb.append(location.getSpeed());// 速度 单位：km/h
//                    sb.append("\nsatellite : ");
//                    sb.append(location.getSatelliteNumber());// 卫星数目
//                    sb.append("\nheight : ");
//                    sb.append(location.getAltitude());// 海拔高度 单位：米
//                    sb.append("\ngps status : ");
//                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
//                    sb.append("\ndescribe : ");
//                    sb.append("gps定位成功");
//                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
//                    // 运营商信息
//                    if (location.hasAltitude()) {// *****如果有海拔高度*****
//                        sb.append("\nheight : ");
//                        sb.append(location.getAltitude());// 单位：米
//                    }
//                    sb.append("\noperationers : ");// 运营商信息
//                    sb.append(location.getOperators());
//                    sb.append("\ndescribe : ");
//                    sb.append("网络定位成功");
//                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//                    sb.append("\ndescribe : ");
//                    sb.append("离线定位成功，离线定位结果也是有效的");
//                } else if (location.getLocType() == BDLocation.TypeServerError) {
//                    sb.append("\ndescribe : ");
//                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
//                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//                    sb.append("\ndescribe : ");
//                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
//                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//                    sb.append("\ndescribe : ");
//                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
//                }
//
                presenter.sendLocation(location);
            }
        }
        @Override
        public void onConnectHotSpotMessage(String s, int i){
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapsFragment.isAdded()) {
            getSupportFragmentManager().putFragment(outState, "MapsFragment", mapsFragment);
        }
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (JobService.class.getName().equals(service.service.getClassName())) {
                stopService(new Intent(this, JobService.class));
            }
        }
        removeListener();
        presenter.disconnect();
//        WorldManager.getInstance().cleanup();
//        NativeEnv.cleanup();
//        License.cleanup();
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
        MapsApplication.getClient().getSocketClient().removeSocketClientDelegate(this);
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
    }


    @Override
    public void onConnected(SocketClient client) {
        LogUtils.e(TAG, "onConnected");
        presenter.sendAuth();
        mapsFragment.setOnline();
        MapsApplication.isConnected = true;
    }

    Handler handler = new Handler();

    @Override
    public void onDisconnected(final SocketClient client) {
        MapsApplication.isConnected = false;
        LogUtils.e(TAG, "onDisconnected");
        mapsFragment.setOffline();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /**
                 *要执行的操作
                 */
                client.connect();
            }
        }, 3000);//3秒后执行Runnable中的run方法
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
