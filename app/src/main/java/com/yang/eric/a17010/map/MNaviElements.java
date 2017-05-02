package com.yang.eric.a17010.map;


import com.yang.eric.a17010.R;

import java.util.HashMap;

public class MNaviElements {
	public final static String[] mTurnType = { "直行", "左转", "调头", "右转",
			"Slightly Left", "Slightly Right", "Hard Left", "Hard Right",
			"靠左行驶", "靠右行驶" };

	public final static String[] mMsgType = { "进入", "进入", "进入", "到达目的地",
			"进入主路", "驶离主路", "进入匝道", "进入环岛", "驶离环岛", "进入多岔道", "进入", "进入",
			"进入隧道", "采纳轮渡", "路名改变" };

	public final static String getHeading(int heading) {
		heading = heading < 0 ? 360 + heading : heading;
		String re = "";
		if (heading <= 338 && heading > 293)
			re = "东南";
		else if (heading <= 293 && heading > 248)
			re = "南";
		else if (heading <= 248 && heading > 203)
			re = "西南";
		else if (heading <= 203 && heading > 158)
			re = "西";
		else if (heading <= 158 && heading > 113)
			re = "西北";
		else if (heading <= 113 && heading > 68)
			re = "北";
		else if (heading <= 68 && heading > 23)
			re = "东北";
		else
			re = "东";
		return re;
	}

	public final static int[] imgCMR = { 10, 15, 20, 25, 30, 35, 40, 45, 50,
			55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 100, // 占位105
			110, 110, 120 };

	public final static String[] highwayTYPE = { "出口", "匝道", "服务区", "停车区",
			"收费站" };
	public final static int[] highwayICON = { R.drawable.highway_ic,
			R.drawable.highway_jc, R.drawable.highway_sa,
			R.drawable.highway_pa, R.drawable.highway_tg };

	public final static int[] imgActions = { R.drawable.turn_icons0, // 无图片
			R.drawable.turn_icons1, // 终点 1
			R.drawable.turn_icons2, // 掉头 2
			R.drawable.turn_icons3, // 左前 3
			R.drawable.turn_icons4, // 右前 4
			R.drawable.turn_icons5, // 直行 5
			R.drawable.turn_icons6, // 右前 6
			R.drawable.turn_icons7, // 左前 7
			R.drawable.turn_icons8, // 左转 8
			R.drawable.turn_icons9, // 右转
			R.drawable.turn_icons10, // 左前
			R.drawable.turn_icons11, // 右前
			R.drawable.turn_icons12, // 环岛第一个出口
			R.drawable.turn_icons13, // 环岛第2个出口
			R.drawable.turn_icons14, // 环岛第3个出口
			R.drawable.turn_icons15, // 环岛第4个出口
			R.drawable.turn_icons16, // 环岛第5个出口
			R.drawable.turn_icons17, // 环岛第6个出口
			R.drawable.turn_icons18, // 环岛第7个出口
			R.drawable.turn_icons19, // 环岛第8个出口
			R.drawable.turn_icons20, // 环岛第9个出口
			R.drawable.turn_icons21, // 保持左侧行驶
			R.drawable.turn_icons22, // 保持右侧行驶
			R.drawable.turn_icons23, // 向左急转弯
			R.drawable.turn_icons24, // 向右急转弯
			R.drawable.turn_icons25, // 向左转并保持左侧行驶
			R.drawable.turn_icons26, // 向左转并保持右侧侧行驶
			R.drawable.turn_icons27, // 向右转并保持左侧行驶
			R.drawable.turn_icons28, // 向右转并保持右侧行驶
			R.drawable.turn_icons29, // 进入隧道
			R.drawable.turn_icons30, // 坐船
			R.drawable.turn_icons31, // 31 起点
			R.drawable.turn_icons32, // 32 途经点 1
			R.drawable.turn_icons33, // 33 途经点 2
			R.drawable.turn_icons34, // 34 途经点 3
			R.drawable.turn_icons35, // 35 出口指示
			R.drawable.turn_icons36, // 36 方向指示
			R.drawable.turn_icons37, // 37 非盘桥立交桥
			R.drawable.turn_icons38, // 38 盘桥立交桥
			R.drawable.turn_icons39, // 39 靠左直行
			R.drawable.turn_icons40, // 40 靠右直行
			R.drawable.turn_icons41,
			R.drawable.turn_icons42,
			R.drawable.turn_icons43,
			R.drawable.turn_icons44,
			R.drawable.turn_icons45,
			R.drawable.turn_icons46,
			R.drawable.turn_icons47,
			R.drawable.turn_icons48,
			R.drawable.turn_icons49,
			R.drawable.turn_icons50,
			R.drawable.turn_icons51,
	};

	public final static HashMap<Integer, Integer> arActions;
	static {
		arActions = new HashMap<Integer, Integer>();
		arActions.put(2, R.drawable.ar_leftturn); // 掉头
		arActions.put(3, R.drawable.ar_straight); // 直行
		arActions.put(4, R.drawable.ar_straight); // 直行
		arActions.put(5, R.drawable.ar_straight); // 直行
		arActions.put(6, R.drawable.ar_straight); // 直行
		arActions.put(7, R.drawable.ar_straight); // 直行、
		arActions.put(8, R.drawable.ar_left); // 左转
		arActions.put(9, R.drawable.ar_right); // 右转
		arActions.put(10, R.drawable.ar_straight); // 直行
		arActions.put(11, R.drawable.ar_straight); // 直行
		arActions.put(12, R.drawable.ar_right); // 右转
		arActions.put(13, R.drawable.ar_straight); // 直行
		arActions.put(14, R.drawable.ar_left); // 左转
		arActions.put(15, R.drawable.ar_left); // 左转
		arActions.put(16, R.drawable.ar_left); // 左转
		arActions.put(17, R.drawable.ar_left); // 左转
		arActions.put(18, R.drawable.ar_left); // 左转
		arActions.put(19, R.drawable.ar_left); // 左转
		arActions.put(20, R.drawable.ar_left); // 左转
		arActions.put(21, R.drawable.ar_straight); // 直行
		arActions.put(22, R.drawable.ar_straight); // 直行
		arActions.put(23, R.drawable.ar_left); // 左转
		arActions.put(24, R.drawable.ar_right); // 右转
		arActions.put(25, R.drawable.ar_left); // 左转
		arActions.put(26, R.drawable.ar_left); // 左转
		arActions.put(27, R.drawable.ar_right); // 右转
		arActions.put(28, R.drawable.ar_right); // 右转
	}
}
