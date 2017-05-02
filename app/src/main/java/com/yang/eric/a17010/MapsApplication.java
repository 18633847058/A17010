package com.yang.eric.a17010;

import android.app.Application;
import android.content.Context;

import com.yang.eric.a17010.utils.LogUtils;
import com.yang.eric.a17010.utils.TcpClient;

/**
 * Created by Yang on 2017/4/10.
 */

public class MapsApplication extends Application {

    private static Context context;
    private static TcpClient client;
    public static int i = 2;

	@Override
	public void onCreate() {
		super.onCreate();
        context = this;
        LogUtils.debug(true);
	}

	public static Context getApplication() {
        return context;
    }

    public static TcpClient getClient() {
        if (client == null) {
            client = new TcpClient();
        }
        return client;
    }
}
