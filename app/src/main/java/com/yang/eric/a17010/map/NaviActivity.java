package com.yang.eric.a17010.map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbar.map.MapRenderer;
import com.mapbar.mapdal.GpsTracker;
import com.mapbar.mapdal.GpsTracker.GPSEvent;
import com.mapbar.mapdal.GpsTracker.GPSEventHandler;
import com.mapbar.mapdal.NaviCoreUtil;
import com.mapbar.mapdal.PoiFavorite;
import com.mapbar.mapdal.WmrObject;
import com.mapbar.mapdal.WorldManager;
import com.mapbar.navi.ArrowInfo;
import com.mapbar.navi.CameraSystem;
import com.mapbar.navi.ExpandView;
import com.mapbar.navi.NaviSession;
import com.mapbar.navi.NaviSessionData;
import com.mapbar.navi.NaviSessionParams;
import com.mapbar.navi.NaviSpeaker;
import com.mapbar.navi.RouteBase;
import com.mapbar.navi.RouteCollection;
import com.mapbar.navi.RouteDescriptionItem;
import com.mapbar.navi.RoutePlan;
import com.mapbar.navi.RouterErrorInfo;
import com.mapbar.poiquery.PoiQuery;
import com.yang.eric.a17010.R;

import java.util.ArrayList;

/**
 * 驾车导航
 * 
 */
public class NaviActivity extends Activity implements OnClickListener, NaviSession.EventHandler, GPSEventHandler {

	private final int BUS_QUERY_ROUTE_START = 1; // 换乘起点
	private final int BUS_QUERY_ROUTE_END = 2; // 换乘终点

	// 测试起点
	private String mStartName;
	private Point mStartPoint = new Point();
	// 途经点
	// private Point mWayPoint = Config.NAVI_WAYPOINT;
	// 测试终点
	private String mEndName;
	private Point mEndPoint = new Point();
	// 全程所需的时间
	private int estimatedTime;
	// 全程的路程长度
	private int length;
	/**
	 * 地图引擎相关
	 */
	private DemoMapView mDemoMapView;
	private MapRenderer mRenderer;
	/**
	 * 导航引擎相关
	 */
	private NaviSession mNaviSession;
	// 导航相关参数设置
	private NaviSetting mNaviSetting = null;
	// 路线规划结果集
	private RouteCollection mRouteCollection = null;
	// 路线详情数据集合
	private ArrayList<RouteDescriptionItem> mRouteDetailItems = null;
	// 路线规划类型
	private int mNaviIndex = 0;
	private RouteBase mRouteBase = null;
	private final float SPEED_LIMIT_MAX = 5.0f;
	private final float SPEED_LIMIT_MIN = 1.0f;
	private final float SPEED_CHANGE_STEP = 2.0f;
	private boolean isTmc = false;

	/**
	 * UI相关
	 */
	// 展示路线详情
	private ListView mListview;
	// 导航路线规划区域
	private RelativeLayout mNaviRouteLayout;
	// 导航操作区域
	private LinearLayout mNaviOperaLayout;
	// 导航路线操作区域
	private LinearLayout mRouteOperaLayout;
	// 路线详细信息区域
	private RelativeLayout mRouteDetailLayout;
	// 在线 导航信息
	private LinearLayout mOnlineExpandLayout;
	// 离线 导航信息
	private LinearLayout mOfflineExpandLayout;
	// 路线规划规则
	private ImageView mRecommendedBtn;
	private ImageView mShortestBtn;
	private ImageView mFastestBtn;
	private ImageView mEconomicBtn;
	private ImageView mCongestionBtn;
	private TextView tvCongestion;
	private ImageView mWalkBtn;
	// 导航按钮
	private Button mNaviButton;

	// 模拟导航按钮
	private TextView mTestNaviButton;// 模拟导航按钮

	// 暂停模拟导航按钮
	private Button mTestNaviPauseButton;
	// 导航路口剩余距离
	private TextView mNaviSpaceTextView;
	// 导航路线名称
	private TextView mNaviAddressTextView;
	// 导航距离进度条
	private ProgressBar mProgressBar;
	// 起点名称
	private EditText mStartNameView;
	// 终点名称
	private EditText mEndNameView;

	// 起点终点对调位置
	private Button mTranChange;

	// 路线信息ListView适配器
	private RouteDetailAdapter mRouteDetailAdapter;
	private ImageButton mTmcImageView;
	private ImageButton mZoomInImageView;
	private ImageButton mZoomOutImageView;
	private ProgressDialog myDialog;

	// 在线或离线
	private boolean online = true;
    //导航标控件
	private ImageView mNavigationMark;
	//标题栏对象
	private View mTitle;
	//导航总信息
	private LinearLayout mNavigationCountInfo;
	//路程总长度控件
	private TextView mCountLength;
	//到达需要时间的控件
	private TextView mCountTime;
	// For Debugging
	private final static String TAG = "[NaviActivity]";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_nav);
		// 初始化UI控件
		initView();
		// 初始引擎
		init();
		// 自定义语音
		NaviSpeaker.enqueue("欢迎使用图吧导航");
	}

	/**
	 * 用于接收GLMapRenderer加载完的消息
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				// 地图控件加载完毕
				mRenderer = mDemoMapView.getMapRenderer();
				mRenderer.setDataMode(online ? MapRenderer.DataMode.online : MapRenderer.DataMode.offline);
				mDemoMapView.setExpandView((ImageView) findViewById(R.id.offline_expandView));
				break;
			case 100:
				// 监听地图缩放 修改按钮状态
				Bundle b = msg.getData();
				mZoomInImageView.setEnabled(b.getBoolean("zoomOut"));
				mZoomOutImageView.setEnabled(b.getBoolean("zoomIn"));
				break;
			case 101:
				// PoiFavorite fav = (PoiFavorite) msg.obj;
				// String name = fav.name;
				// if (!setStartAndEndFlag) {
				// mStartPoint = fav.pos;
				// mStartNameView.setText(name);
				// } else {
				// mEndPoint = fav.pos;
				// mEndNameView.setText(name);
				// }
				break;
			case 102:
				PoiFavorite fav2 = (PoiFavorite) msg.obj;
				mEndPoint = fav2.pos;
				// mWayPoint = null;
				setRoute();
				break;
			}
		}
	};

	/**
	 * 初始化UI控件
	 */
	public void initView() {
		// 导航路线规划区域
		mNaviRouteLayout = (RelativeLayout) findViewById(R.id.nav_span);
		// 导航操作区域
		mNaviOperaLayout = (LinearLayout) findViewById(R.id.layout_nav_operate);
		// 导航路线操作区域
		mRouteOperaLayout = (LinearLayout) findViewById(R.id.layout_route_operate);
		// 路线详情展示区域
		mRouteDetailLayout = (RelativeLayout) findViewById(R.id.nav_span21);
		// 在线 导航信息
		mOnlineExpandLayout = (LinearLayout) findViewById(R.id.ll_online_expand_view);
		// 离线 导航信息
		mOfflineExpandLayout = (LinearLayout) findViewById(R.id.ll_offline_expand_view);
		// 展示路线详情listview
		mListview = (ListView) findViewById(R.id.nav_detail_list);
		// 路线规划规则
		mRecommendedBtn = (ImageView) findViewById(R.id.btn_recommended);
		mRecommendedBtn.setOnClickListener(this);
		mShortestBtn = (ImageView) findViewById(R.id.btn_shortest);
		mShortestBtn.setOnClickListener(this);
		mFastestBtn = (ImageView) findViewById(R.id.btn_fastest);
		mFastestBtn.setOnClickListener(this);
		mEconomicBtn = (ImageView) findViewById(R.id.btn_economic);
		mEconomicBtn.setOnClickListener(this);
		mCongestionBtn = (ImageView) findViewById(R.id.btn_congestion);
		mCongestionBtn.setOnClickListener(this);
		tvCongestion = (TextView) findViewById(R.id.tv_congestion);
		mWalkBtn = (ImageView) findViewById(R.id.btn_walk);
		mWalkBtn.setOnClickListener(this);
		// tmc
		mTmcImageView = (ImageButton) findViewById(R.id.btn_tmc);
		// 地图放大按钮
		mZoomInImageView = (ImageButton) findViewById(R.id.btn_zoom_in);
		// 地图缩小按钮
		mZoomOutImageView = (ImageButton) findViewById(R.id.btn_zoom_out);
		// 导航按钮
		mNaviButton = (Button) findViewById(R.id.btn_nav_start);
		// 模拟导航暂停按钮
		mTestNaviPauseButton = (Button) findViewById(R.id.btn_nav_pause);
		// 起点名称
		mStartNameView = (EditText) findViewById(R.id.et_start_place);
		// 终点名称
		mEndNameView = (EditText) findViewById(R.id.et_end_place);
		// 起点终点位置对调
		mTranChange = (Button) findViewById(R.id.btn_tran_change);

		// 导航路口剩余距离
		mNaviSpaceTextView = (TextView) findViewById(R.id.tv_nav_space);
		// 导航路线名称
		mNaviAddressTextView = (TextView) findViewById(R.id.tv_nav_address);
		// 导航距离进度条
		mProgressBar = (ProgressBar) findViewById(R.id.pb_nav_progress_bar);

		// 控件监听事件
		mRouteDetailAdapter = new RouteDetailAdapter(this);
		// 设置listview数据
		mListview.setAdapter(mRouteDetailAdapter);
		// tmc
		mTmcImageView.setOnClickListener(this);
		// 地图放大按钮
		mZoomInImageView.setOnClickListener(this);
		// 地图缩小按钮
		mZoomOutImageView.setOnClickListener(this);
		// 导航按钮
		mNaviButton.setOnClickListener(this);
		// 开始设置起点
		mStartNameView.setOnClickListener(this);
		// 开始设置终点
		mEndNameView.setOnClickListener(this);

		// 起点终点是否对调
		mTranChange.setOnClickListener(this);

		// 关闭路线详细列表
		findViewById(R.id.btn_nav_list_cancel).setOnClickListener(this);
		// 导航路线规划按钮
		findViewById(R.id.btn_search).setOnClickListener(this);
		// 模拟导航按钮
		mTestNaviButton = (TextView) findViewById(R.id.btn_test_start);
		mTestNaviButton.setOnClickListener(this);
		// 线路详情
		findViewById(R.id.btn_route_detail).setOnClickListener(this);
		// 删除路线
		findViewById(R.id.btn_route_del).setOnClickListener(this);
		// 暂停导航
		findViewById(R.id.btn_nav_pause).setOnClickListener(this);
		// 导航加速
		findViewById(R.id.btn_nav_faster).setOnClickListener(this);
		// 导航减速
		findViewById(R.id.btn_nav_slower).setOnClickListener(this);
		// 导航结束
		findViewById(R.id.btn_nav_end).setOnClickListener(this);
		// 锁车
		findViewById(R.id.btn_lock).setOnClickListener(this);
		mStartNameView.setText(mStartName);
		mEndNameView.setText(mEndName);
		//导航标
		mNavigationMark = (ImageView) findViewById(R.id.imageButton1);
		//导航总信息
		mNavigationCountInfo = (LinearLayout)findViewById(R.id.ll_count_information);
		mCountLength = (TextView)findViewById(R.id.tv_count_length);
		mCountTime = (TextView)findViewById(R.id.tv_count_time);
	}

	/**
	 * 初始化数据
	 */
	private void init() {
		// 初始化地图引擎
		try {
			if (Config.DEBUG) {
				Log.d(TAG, "Before - Initialize the GLMapRenderer Environment");
			}
			// 加载地图
			mDemoMapView = (DemoMapView) findViewById(R.id.nav);
			mDemoMapView.setZoomHandler(handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mNaviSetting = NaviSetting.getInstance();
		// 初始化路线计划
		NaviSessionParams params = new NaviSessionParams();
		params.modules = NaviSession.Module.arrowRenderer | NaviSession.Module.cameraWarning | NaviSession.Module.speedLimitWarning
				| NaviSession.Module.expandView | NaviSession.Module.adminSpeaker | NaviSession.Module.tmcReport
				| NaviSession.Module.highwayGuide;
		params.expandViewWidth = params.expandViewHeight = 512;
		params.expandViewSmallFontSize = 22;
		params.expandViewBigFontSize = 30;
		// 使用引擎提供的GPS模块定位
		params.useNaviCoreGPS = true;
		// 算路完成后是否自动采纳路线结果用于导航.
		params.autoTakeRoute = true;
		// 偏航后是否自动重计算
		params.autoReroute = true;
		mNaviSession = NaviSession.getInstance();
		try {
			mNaviSession.init(this, this, params);
			GpsTracker.getInstance().registerGpsTrackerListener(this,null);
			// 默认暂停导航
			mNaviSession.pauseNavi();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 默认在线导航
		mNaviSession.setNaviMode(online ? NaviSession.Mode.online : NaviSession.Mode.offline);
		// 当前路线详情的生成模式
		mNaviSetting.setRouteDirectionsFlag(RouteBase.DirectionsFlag.origin);
		/**
		 * 设置算路方式 单条的 NaviSession.RouteMethod.single 多规则
		 * NaviSession.RouteMethod.multipleRule 多结果
		 * NaviSession.RouteMethod.multipleResult
		 **/
		mNaviSetting.setRouteMethod(NaviSession.RouteMethod.single);
		// 默认在线查询
		mNaviSetting.setPoiQueryMode(online ? PoiQuery.Mode.online : PoiQuery.Mode.offline);
	}

	@Override
	public void onClick(View v) {
		float speed = SPEED_LIMIT_MIN;
		switch (v.getId()) {

		case R.id.btn_search:
			if (TextUtils.isEmpty(mStartNameView.getText().toString()) || TextUtils.isEmpty(mEndNameView.getText().toString())) {
				Toast.makeText(this, "请先输入起终点信息", Toast.LENGTH_SHORT).show();
				break;
			}

			boolean isWalk = mNaviIndex == 4;

			mDemoMapView.setCarOrWalk(isWalk);
			if (isWalk) {
				mNaviButton.setTextColor(getResources().getColor(R.color.Gray));
				mTestNaviButton.setTextColor(getResources().getColor(R.color.Gray));
				mNaviButton.setClickable(false);
				mTestNaviButton.setClickable(false);
			} else {
				mNaviButton.setTextColor(getResources().getColor(R.color.white));
				mTestNaviButton.setTextColor(getResources().getColor(R.color.white));
				mNaviButton.setClickable(true);
				mTestNaviButton.setClickable(true);
			}

			// 导航线路规划
			setRoute();
			break;
		case R.id.btn_nav_start:
			// 导航按钮
			if ("导航".equals(mNaviButton.getText().toString())) {
				mNaviButton.setText("暂停");
				GPSProgressDialog();
				mNaviSession.endManeualStartState();
				// 点击导航按钮 恢复导航
				if (mNaviSession.isNaviPaused())
					mNaviSession.resumeNavi();
			} else {
				mNaviButton.setText("导航");
				if(myDialog.isShowing()){
					myDialog.dismiss();
				}
				// 点击导航 暂停导航
				if (!mNaviSession.isNaviPaused())
					mNaviSession.pauseNavi();
				//导航结束，显示导航总信息界面
				mOnlineExpandLayout.setVisibility(View.GONE);
				mNavigationCountInfo.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.btn_test_start:
			// 模拟导航
			startNavi();
			mTestNaviPauseButton.setText("暂停");
			break;
		case R.id.btn_route_detail:
			// 路线详情
			if (mRouteBase != null) {
				int num = mRouteBase.getDescriptionNumber();
				mRouteDetailItems = new ArrayList<RouteDescriptionItem>();
				for (int i = 0; i < num; i++) {
					mRouteDetailItems.add(mRouteBase.getDescriptionItem(i, RouteBase.NO_USE_CURRENT_DISTANCE));
				}
				if (mRouteDetailItems.size() > 0) {
					mRouteDetailAdapter.notifyDataSetChanged();
					mRouteDetailLayout.setVisibility(View.VISIBLE);
				} else {
					Toast.makeText(this, "没有路线信息", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "请先规划路线", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btn_route_del:
			//显示标题栏，隐藏导航详细信息界面
			mTitle.setVisibility(View.VISIBLE);
			mOnlineExpandLayout.setVisibility(View.GONE);
			// 删除路线
			mDemoMapView.setCarOrWalk(false);
			mNaviSession.removeRoute();
			mDemoMapView.removeRoute();
			mRouteBase = null;
			mRouteCollection = null;
			// 显示导航路线规划区域
			mNaviRouteLayout.setVisibility(View.VISIBLE);
			// 隐藏导航路线操作区域显示
			mRouteOperaLayout.setVisibility(View.GONE);
			// 隐藏路况按钮
			mTmcImageView.setVisibility(View.GONE);
			// 暂停导航
			if (!mNaviSession.isNaviPaused()) {
				mNaviSession.pauseNavi();
			}
			mNaviButton.setText("导航");
			break;
		case R.id.btn_nav_pause:
			// 暂停
			if (mNaviSession.isSimulationPaused()) {
				mNaviSession.resumeSimulation();
				mTestNaviPauseButton.setText("暂停");

			} else {
				mNaviSession.pauseSimulation();
				mTestNaviPauseButton.setText("继续");
			}
			break;
		case R.id.btn_nav_faster:
			// 加速
			speed = mNaviSession.getSimulationSpeed();
			if (speed < SPEED_LIMIT_MAX) {
				speed += SPEED_CHANGE_STEP;
				mNaviSession.setSimulationSpeed(speed);
			}
			break;
		case R.id.btn_nav_slower:
			// 减速
			speed = mNaviSession.getSimulationSpeed();
			if (speed > SPEED_LIMIT_MIN) {
				speed -= SPEED_CHANGE_STEP;
				mNaviSession.setSimulationSpeed(speed);
			}
			break;
		case R.id.btn_nav_end:
			//导航结束后，显示标题栏
			mNavigationCountInfo.setVisibility(View.VISIBLE);
			// 导航结束
			// 模拟导航结束
			if (mRouteBase != null) {
				if (mNaviSession.isInSimulation()) {
					mNaviSession.endSimulation();
				}
			}
			// 显示导航路线操作区域
			mRouteOperaLayout.setVisibility(View.VISIBLE);
			// 隐藏导航操作区域
			mNaviOperaLayout.setVisibility(View.GONE);
			// 隐藏在线导航信息
			mOnlineExpandLayout.setVisibility(View.GONE);
			// 隐藏离线导航信息
			mOfflineExpandLayout.setVisibility(View.GONE);
			break;
		case R.id.btn_nav_list_cancel:
			// 关闭路线详细列表
			mRouteDetailLayout.setVisibility(View.GONE);
			break;
		case R.id.btn_lock:
			// 锁车
			mRenderer.setWorldCenter(mDemoMapView.getCarPosition());
			if (mNaviSession.isInSimulation()) {
				boolean lock = mDemoMapView.carIsLocked();
				if (lock) {
					mDemoMapView.lockCar(false);
				} else {
					mDemoMapView.lockCar(true);
				}
			}
			break;
		case R.id.btn_tmc:
			// tmc
			if (isTmc) {
				isTmc = false;
				mTmcImageView.setBackgroundResource(R.drawable.traffic_closed);
				mDemoMapView.setRouteTmc(isTmc);
			} else {
				if (mNaviSession.getNaviMode() == 0) {
					Toast.makeText(this, "离线模式不能开启TMC", Toast.LENGTH_SHORT).show();
				} else {
					isTmc = true;
					mTmcImageView.setBackgroundResource(R.drawable.trafic_open);
					mDemoMapView.setRouteTmc(isTmc);
				}
			}
			break;
		case R.id.btn_recommended:
			// 系统推荐
			mNaviIndex = 0;
			naviRoutePlanBtn();
			break;
		case R.id.btn_shortest:
			// 路线最短
			mNaviIndex = 1;
			naviRoutePlanBtn();
			break;
		case R.id.btn_fastest:
			// 用时最少
			mNaviIndex = 2;
			naviRoutePlanBtn();
			break;
		case R.id.btn_economic:
			// 花费最少
			mNaviIndex = 3;
			naviRoutePlanBtn();
			break;
		case R.id.btn_walk:
			// 步行规划
			mNaviIndex = 4;
			naviRoutePlanBtn();
			break;
		case R.id.btn_congestion:
			// 规避拥堵
			if (NaviSetting.getInstance().getUseTmc()) {
				NaviSetting.getInstance().setUseTmc(false);
			} else {
				NaviSetting.getInstance().setUseTmc(true);
			}
			naviRoutePlanBtn();
			break;
		case R.id.btn_zoom_in:
			// 地图放大
			mDemoMapView.mapZoomIn(mZoomInImageView, mZoomOutImageView);
			break;
		case R.id.btn_zoom_out:
			// 地图缩小
			mDemoMapView.mapZoomOut(mZoomInImageView, mZoomOutImageView);
			break;

		case R.id.et_start_place:
			// 换乘起点
			Intent intent = new Intent(this, SearchBusActivity.class);
			intent.putExtra("__from", NaviSetting.FROM_NAVI_SEARCH);
			intent.putExtra("wmrId", getIdByCityName(Config.SEARCH_DEFAULT_CITY));
			intent.putExtra("online", online);
			startActivityForResult(intent, BUS_QUERY_ROUTE_START);
			break;
		case R.id.et_end_place:
			// 换乘终点
			Intent intent2 = new Intent(this, SearchBusActivity.class);
			intent2.putExtra("__from", NaviSetting.FROM_NAVI_SEARCH);
			intent2.putExtra("wmrId", getIdByCityName(Config.SEARCH_DEFAULT_CITY));
			intent2.putExtra("online", online);
			startActivityForResult(intent2, BUS_QUERY_ROUTE_END);
			break;
		case R.id.btn_tran_change:
			// 交互起终点
			if (TextUtils.isEmpty(mStartNameView.getText().toString()) || TextUtils.isEmpty(mEndNameView.getText().toString())) {
				Toast.makeText(this, "请先输入起终点信息", Toast.LENGTH_SHORT).show();
				break;
			}

			Point pointI = mStartPoint;
			mStartPoint = mEndPoint;
			mEndPoint = pointI;

			String tmpName = mStartName;
			mStartName = mEndName;
			mEndName = tmpName;
			setStartViewContent();
			setEndViewContent();

			break;
		}
	}

	// 设置起点输入框内容
	private void setStartViewContent() {
		mStartNameView.setText(mStartName + "(" + mStartPoint.x + "," + mStartPoint.y + ")");
	}

	// 设置终点输入框内容
	private void setEndViewContent() {
		mEndNameView.setText(mEndName + "(" + mEndPoint.x + "," + mEndPoint.y + ")");
	}

	/**
	 * 模拟导航
	 */
	public void startNavi() {
		NaviSpeaker.stop();
		// 锁车
		mDemoMapView.lockCar(true);
		// 设置车标位置
		mDemoMapView.setCarPosition(mStartPoint);
		// 设置地图中心点
		mRenderer.setWorldCenter(mStartPoint);
		// 开始导航
		mNaviSession.startSimulation();
		// 设置速度
		mNaviSession.setSimulationSpeed(SPEED_LIMIT_MIN);
		// 隐藏导航路线操作区域
		mRouteOperaLayout.setVisibility(View.GONE);
		// 显示导航操作区域
		mNaviOperaLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * 规划路线
	 */
	public void setRoute() {
		// 导航线路规划
		mDemoMapView.setStartPoint(mStartPoint,mStartName);
		mDemoMapView.setDestination(mEndPoint,mEndName);
		// if (mWayPoint != null) {
		// mDemoMapView.setWayPoint(mWayPoint);
		// }
		ProgressDialog();
		NaviSetting.getInstance().setRouteRule(mNaviIndex);
		boolean rflag = mDemoMapView.startRoute();
		if (!rflag) {
			Toast.makeText(this, "请设置起终点", Toast.LENGTH_SHORT).show();
			myDialog.dismiss();
		}
	}

	/**
	 * 导航回调 
	 * routeStarted – 开始算路 
	 * rerouteStarted – 重新开始算路 
	 * routeComplete – 算路完成(会接收到一个类型为RouteCollection的对象，记录路线集合) 
	 * rerouteComplete – 重新算路完成(会接收到一个类型为RouteBase的对象，重新计算出的道路) 
	 * routeFailed – 算路失败(会接收到一个类型为RouterErrorInfo的对象，返回错误信息) 
	 * rerouteFailed – 重算路失败(会接收到一个类型为RouterErrorInfo的对象，返回错误信息) 
	 * destArrived – 到达目的地 
	 * tracking – 车的位置发生改变(会接收到一个类型为NaviSessionData的对象，记录车的实时信息) 
	 * routing – 正在算路中(此时可以通过绘制等待信息提示用户) 
	 * routeCancelled – 取消算路 
	 * rerouteCancelled – 取消重算路
	 * manualStartStateBegin – 开启手动起点模式(手动起点模式，即GPS定位后仍可以将起点设置为非GPS定位点)
	 * manualStartStateEnd – 结束手动起点模式 
	 * newRouteTaken – 采纳新路线 
	 * needsReroute – 需要重新算路 
	 * simNaviBegin – 模拟导航开始 
	 * simNaviEnd – 模拟导航结束
	 */
	@Override
	public void onNaviSessionEvent(int event, Object data) {
		switch (event) {
		case NaviSession.Event.destArrived:

			break;
		case NaviSession.Event.manualStartStateBegin:

			break;
		case NaviSession.Event.manualStartStateEnd:

			break;
		case NaviSession.Event.needsReroute:

			break;
		case NaviSession.Event.newArrow:
			ArrowInfo arrowInfo = (ArrowInfo) data;
			mDemoMapView.drawArrow(arrowInfo);
			break;
		case NaviSession.Event.deleteArrow:
			// 删除转向标
			mDemoMapView.delArrow();
			break;
		case NaviSession.Event.newRouteTaken:
			// 删除原来路线
			// 如果当前存在路线，那么应该将路线隐藏后仅绘制当前使用的路线
			mDemoMapView.setRoute(mNaviSession.getRoute(), isTmc);
			// 设置地图中心点
			mRenderer.setWorldCenter(mDemoMapView.getCarPosition());
			break;
		case NaviSession.Event.rerouteCancelled:

			break;
		case NaviSession.Event.rerouteComplete:
			// 重新算路后获取路线
			mRouteBase = (RouteBase) data;
			break;
		case NaviSession.Event.rerouteFailed:
			break;
		case NaviSession.Event.rerouteStarted:

			break;
		case NaviSession.Event.routeCancelled:

			break;
		case NaviSession.Event.routeComplete: {
			// 获取路线规划信息
			mRouteCollection = (RouteCollection) data;
			// 获取单条道路信息
			mRouteBase = mRouteCollection.routes[0];
			// 获取路线
			mNaviSession.takeRoute(mRouteCollection.routes[0]);
			// 设置全览路线
			mRenderer.fitWorldArea(mRouteBase.getBoundingBox());
			// 修改比例尺
			mDemoMapView.zoomChange();
			// 语音播报
			NaviSpeaker.enqueue("路线规划完毕");
			// 当前路程全程所需的时间，单位秒
			estimatedTime = mRouteBase.getEstimatedTime();
			// 当前路程全程的距离，单位米
			length = mRouteBase.getLength();
			//规划好路线隐藏标题栏
			mTitle.setVisibility(View.GONE);
			//模拟导航开始，隐藏道路总信息
			mNavigationCountInfo.setVisibility(View.VISIBLE);
			// 路线规划完毕后 显示路况按钮
			mTmcImageView.setVisibility(View.VISIBLE);
			// 隐藏导航路线规划区域
			mNaviRouteLayout.setVisibility(View.GONE);
			// 清空道路信息
			mProgressBar.setProgress(0);
			mNaviSpaceTextView.setText("");
			mNaviAddressTextView.setText("");
			mCountLength.setText("路程：" + Utils.getRoadLenght(length) + "公里");
			mCountTime.setText("时间：" + Utils.getRoadTime(estimatedTime));
			// 显示导航路线操作区域显示
			mNaviOperaLayout.setVisibility(View.GONE);
			mRouteOperaLayout.setVisibility(View.VISIBLE);
			if (myDialog != null)
				myDialog.dismiss();
		}
			break;
		case NaviSession.Event.routeFailed: {
			RouterErrorInfo re = (RouterErrorInfo) data;
			Log.e(TAG, re.errCode + "");
			String msg = null;
			switch (re.errCode) {
			case RouterErrorInfo.Type.ComputeFailed:
				msg = "路线计算失败";
				break;
			case RouterErrorInfo.Type.destAuthError:
				msg = "终点所在位置数据授权错误";
				break;
			case RouterErrorInfo.Type.destNoData:
				msg = "重点所在位置没有数据";
				break;
			case RouterErrorInfo.Type.MissingSubfiles:
				msg = "缺少途径省份数据";
				break;
			case RouterErrorInfo.Type.NetworkError:
				msg = "网络连接错误";
				break;
			case RouterErrorInfo.Type.None:
				// 没有错误信息
				break;
			case RouterErrorInfo.Type.NotEnoughMemory:
				msg = "没有足够的内存可以使用";
				break;
			case RouterErrorInfo.Type.oriAuthError:
				msg = "起点所在位置数据授权错误";
				break;
			case RouterErrorInfo.Type.OriDestTooNear:
				msg = "起点和终点距离太近";
				break;
			case RouterErrorInfo.Type.oriNoData:
				msg = "起点所在位置没有数据";
				break;
			case RouterErrorInfo.Type.SetDestFailed:
				msg = "设置终点失败";
				break;
			case RouterErrorInfo.Type.SetOriFailed:
				msg = "设置起点失败";
				break;
			case RouterErrorInfo.Type.waypointAuthError:
				msg = "途经点所在位置数据授权错误";
				break;
			case RouterErrorInfo.Type.waypointNoData:
				msg = "途经点所在位置没有数据";
				break;
			}
			if (msg != null) {
				Toast.makeText(NaviActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
			NaviSpeaker.enqueue("路线规划失败");
			// 关闭等待
			if (myDialog != null)
				myDialog.dismiss();
		}
			break;
		case NaviSession.Event.routeStarted:

			break;
		case NaviSession.Event.routing:

			break;
		case NaviSession.Event.simNaviBegin:

			break;
		case NaviSession.Event.simNaviEnd:
			// 导航结束时将进度条置满
			mProgressBar.setProgress(10000000);
			break;
		case NaviSession.Event.tracking:
			NaviSessionData d = (NaviSessionData) data;
			float ori = d.carOri < 90 ? (d.carOri + 270.0f) : (d.carOri - 90.0f);
			if (mDemoMapView != null && mDemoMapView.isInited()) {
				double a = d.turnIconProgress;
				double b = a / 128;
				if (d.hasTurn) {
					// 设置进度条进度
					mProgressBar.setProgress((int) (b * 1E7));
					// 设置剩余距离
					String turnDistanceStr = NaviCoreUtil.distance2String(d.distanceToTurn, NaviCoreUtil.DistanceUnit.normal, false).distanceString;
					mNaviSpaceTextView.setText(turnDistanceStr+"后");
					// 设置路名
					mNaviAddressTextView.setText(d.roadName);
					//更改导航标图片
					mNavigationMark.setBackgroundResource(MNaviElements.imgActions[d.turnIcon]);
				}
				// 更改车标位置
				mDemoMapView.setCarPosition(d.carPos);
				mOnlineExpandLayout.setVisibility(View.VISIBLE);
				mNavigationCountInfo.setVisibility(View.GONE);
				// 更改角度
				mDemoMapView.setCarOriented(ori);
				// 绘制箭头
				// mDemoMapView.drawArrow(mRouteBase);
				// 绘制摄像头
				mDemoMapView.drawCameras(CameraSystem.getCameras());
				// 绘制路口放大图
				mDemoMapView.drawExpandView();
				mDemoMapView.drawHighwayGuide();

				if (ExpandView.shouldDisplay()) {
					mOfflineExpandLayout.setVisibility(View.VISIBLE);
				} else {
					mOfflineExpandLayout.setVisibility(View.GONE);
				}
				// 没有路口放大图时显示mOnlineExpandLayout里面的导航信息
				if (mDemoMapView.mBitmap == null) {
					mOnlineExpandLayout.setVisibility(View.VISIBLE);
				} else {
					mOnlineExpandLayout.setVisibility(View.GONE);
				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 加载等待
	 */
	private void ProgressDialog() {
		myDialog = new ProgressDialog(this);
		myDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		myDialog.setMessage("算路中，请稍后");
		myDialog.setIndeterminate(false);
		myDialog.setCancelable(true);
		myDialog.show();
	}
	/**
	 * Gps无连接时的提示框
	 */
	private void GPSProgressDialog() {
		myDialog = new ProgressDialog(this);
		myDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		myDialog.setMessage("GPS连接中，请稍后......");
		myDialog.setIndeterminate(false);
		myDialog.setCancelable(true);
		myDialog.show();
	}

	/**
	 * 根据城市名称获取城市id
	 * 
	 * @param cname
	 * @return
	 */
	private int getIdByCityName(String cname) {
		// 根据城市名称获取城市中心点
		WmrObject wb = new WmrObject(0);
		int id = wb.getChildIdByName(cname);
		if (id == WmrObject.INVALID_ID) {
			int zzid = wb.getChildIdByName(Config.SEARCH_DEFAULT_PROVINCE);
			WmrObject wb2 = new WmrObject(zzid);
			id = wb2.getChildIdByName(cname);
		}
		return id;
	}

	private void naviRoutePlanBtn() {
		mRecommendedBtn.setBackgroundResource(R.drawable.ic_unchecked_red);
		mShortestBtn.setBackgroundResource(R.drawable.ic_unchecked_red);
		mFastestBtn.setBackgroundResource(R.drawable.ic_unchecked_red);
		mEconomicBtn.setBackgroundResource(R.drawable.ic_unchecked_red);
		mWalkBtn.setBackgroundResource(R.drawable.ic_unchecked_red);
		if (mNaviIndex == RoutePlan.Rule.walk) {
			mCongestionBtn.setVisibility(View.GONE);
			tvCongestion.setVisibility(View.GONE);
		} else {
			mCongestionBtn.setVisibility(View.VISIBLE);
			tvCongestion.setVisibility(View.VISIBLE);
		}

		switch (mNaviIndex) {
		case RoutePlan.Rule.recommended:
			// 系统推荐
			mRecommendedBtn.setBackgroundResource(R.drawable.ic_check_red);
			break;
		case RoutePlan.Rule.shortest:
			// 路线最短
			mShortestBtn.setBackgroundResource(R.drawable.ic_check_red);
			break;
		case RoutePlan.Rule.fastest:
			// 用时最少
			mFastestBtn.setBackgroundResource(R.drawable.ic_check_red);
			break;
		case RoutePlan.Rule.economic:
			// 话费最少
			mEconomicBtn.setBackgroundResource(R.drawable.ic_check_red);
			break;
		case RoutePlan.Rule.walk:
			// 步行规划
			mWalkBtn.setBackgroundResource(R.drawable.ic_check_red);
			break;
		}
		if (NaviSetting.getInstance().getUseTmc()) {
			mCongestionBtn.setBackgroundResource(R.mipmap.check);
		} else {
			mCongestionBtn.setBackgroundResource(R.mipmap.uncheck);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果是返回键,直接返回到桌面
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPause() {
		super.onPause();
		// 其他操作
		// 暂停地图
		if (mDemoMapView != null) {
			mDemoMapView.onPause();
		}
		// 其他操作

	}

	@Override
	public void onResume() {
		super.onResume();
		// 其他操作
		// 恢复地图
		if (mDemoMapView != null) {
			mDemoMapView.onResume();
		}
		// 其他操作
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			return;
		}
		// 注意看这里用的是requestCode!!!!
		switch (requestCode) {
		case BUS_QUERY_ROUTE_START:
			mStartPoint.x = data.getIntExtra("poiX", 0);
			mStartPoint.y = data.getIntExtra("poiY", 0);
			mStartName = data.getStringExtra("name");
			setStartViewContent();
			break;
		case BUS_QUERY_ROUTE_END:
			mEndPoint.x = data.getIntExtra("poiX", 0);
			mEndPoint.y = data.getIntExtra("poiY", 0);
			mEndName = data.getStringExtra("name");
			setEndViewContent();
			break;
		default:
			break;
		}
	}

	/**
	 * 销毁
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mDemoMapView != null) {
			// 此Activity销毁时，销毁地图控件
			mDemoMapView.onDestroy();
		}
		WorldManager.getInstance().cleanup();
		mDemoMapView = null;
		mNaviSession.cleanup();
		mNaviSession = null;
		// 其它资源的清理
		// ...
		//退出当前页面的时候清理GPS定位监听
		GpsTracker.getInstance().unregisterGpsTrackerListener();
	}

	/**
	 * listView Adapter适配器
	 * 
	 * @author malw
	 * 
	 */
	private class RouteDetailAdapter extends BaseAdapter {
		LayoutInflater mLayoutInflater = null;

		public RouteDetailAdapter(Context context) {
			super();
			mLayoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (mRouteDetailItems != null) {
				return mRouteDetailItems.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mRouteDetailItems != null) {
				return mRouteDetailItems.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (mRouteDetailItems == null) {
				return null;
			}
			TextView title = null;
			TextView text = null;
			TextView icon = null;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.item_route_detail, null);
			}
			title = (TextView) convertView.findViewById(R.id.route_detail_item_address);
			text = (TextView) convertView.findViewById(R.id.route_detail_item_text);
			icon = (TextView) convertView.findViewById(R.id.route_detail_item_icon);
			title.setText(mRouteDetailItems.get(position).title);
			int dis = mRouteDetailItems.get(position).distance;
			text.setText(dis + "m");
			icon.setText(position + 1 + "");
			return convertView;
		}
	}

	@Override
	public void onGPSEvent(int arg0, Object arg1) {
		switch (arg0) {
		case GpsTracker.GPSEvent.disconnected:
			if(!mNaviSession.isInSimulation()){
				//如果是非模拟导航模式，丢失gps信号时，弹出对话框
				GPSProgressDialog();
			}
			break;
		case GPSEvent.connected:
			//连接上GPS，就消失对话框
			if(myDialog.isShowing()){
				myDialog.dismiss();
			}
			break;
		}
	}

}
