package com.yang.eric.a17010.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Yang on 2017/2/17.
 */

public class NetworkState {

	// 检查是否连接到网络
	public static boolean networkConnected(Context context){

		if (context != null){
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = manager.getActiveNetworkInfo();
			if (networkInfo != null)
				return networkInfo.isAvailable();
		}
		return false;
	}
	// 检查WiFi是否连接
	public static boolean wifiConnected(Context context){

		if (context != null){
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = manager.getActiveNetworkInfo();
			if (networkInfo != null){
				if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
					return networkInfo.isAvailable();
				}
			}
		}

		return false;
	}
	// 检查移动网络是否连接
	public static boolean mobileDataConnected(Context context){

		if (context != null){
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = manager.getActiveNetworkInfo();
			if (networkInfo != null){
				if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
					return networkInfo.isAvailable();
				}
			}
		}

		return false;
	}
}
