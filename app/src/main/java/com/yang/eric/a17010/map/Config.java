package com.yang.eric.a17010.map;

import android.graphics.Point;

public class Config {

	// 是否调试
	public static final boolean DEBUG = true;
	// 默认在线或离线
	public static boolean online = false;
	// 默认地图中心点
	public static Point centerPoint = new Point(11639754, 3990758);
	// 逆地理坐标
	public static Point inverSePoint = centerPoint;
	// Search
	public static String SEARCH_DEFAULT_CITY = "北京市";
	public static String SEARCH_DEFAULT_PROVINCE = "北京市";
	public static String SEARCH_BY_KEY_TEXT = "酒店";
	public static String SEARCH_BY_TYPE_TEXT = "停车场";
	public static String SEARCH_BY_NEAR_TEXT = "酒店";

	public static String getOnlineText(boolean isonline) {
		if (isonline) {
			return "在线";
		} else {
			return "离线";
		}
	}
}
