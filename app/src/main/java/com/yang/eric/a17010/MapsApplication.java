package com.yang.eric.a17010;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.yang.eric.a17010.greendao.gen.DaoMaster;
import com.yang.eric.a17010.greendao.gen.DaoSession;
import com.yang.eric.a17010.service.LocationService;
import com.yang.eric.a17010.utils.LogUtils;
import com.yang.eric.a17010.utils.TcpClient;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Yang on 2017/4/10.
 */

public class MapsApplication extends Application {

    private static MapsApplication instance;
    private static TcpClient client;
    public static int i = 2;
    public LocationService locationService;

    public static final boolean ENCRYPTED = true;
    private DaoSession daoSession;
    public static boolean isConnected = false;

	@Override
	public void onCreate() {
		super.onCreate();
        instance = this;
        LogUtils.debug(true);

        locationService = new LocationService(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,  ENCRYPTED ? "a17010-db-encrypted" : "a17010-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
	}

    public DaoSession getDaoSession() {
        return daoSession;
    }

	public static MapsApplication getInstance() {
        return instance;
    }

    public static TcpClient getClient() {
        if (client == null) {
            client = new TcpClient();
        }
        return client;
    }

}
