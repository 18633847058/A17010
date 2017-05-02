package com.yang.eric.a17010.map;

import java.math.BigDecimal;

public class Utils {
	private static String speedText;
	private static int hour;
	private static int minuter;
	/**
	 * 返回路程
	 * 
	 * @param roadValue
	 * @return
	 */
	public static String getRoadLenght(float roadValue) {
		speedText = String.valueOf(roadValue / 1000);
		if (speedText.contains(".")) {
			BigDecimal b = new BigDecimal(roadValue / 1000);
			float speed = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
			return String.valueOf(speed);
		} else {
			return speedText;
		}

	}
	/**
	 * 返回剩余的时间
	 * 
	 * @param timeValue
	 * @return
	 */
	public static String getRoadTime(int timeValue) {
		hour = timeValue / 3600;
		minuter = (timeValue % 3600) / 60;
		if (hour == 0 && minuter == 0) {
			return timeValue+"秒";
		} else if (hour == 0 && minuter != 0) {
			return minuter + "分钟";
		} else if (hour != 0 && minuter == 0) {
			return hour + "小时";
		} else {
			return hour + "小时" + minuter + "分钟";
		}
	}

}
