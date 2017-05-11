package com.yang.eric.a17010.ui.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbar.map.Annotation;
import com.mapbar.map.MapRenderer;
import com.mapbar.mapdal.WmrObject;
import com.mapbar.navi.NaviSession;
import com.mapbar.navi.NaviSessionParams;
import com.mapbar.poiquery.PoiQuery;
import com.mapbar.poiquery.PoiQueryInitParams;
import com.yang.eric.a17010.R;
import com.yang.eric.a17010.map.Config;
import com.yang.eric.a17010.map.DemoMapView;
import com.yang.eric.a17010.ui.anim.AnimEndListener;
import com.yang.eric.a17010.ui.anim.ViewAnimUtils;

import java.util.List;


public class MapsFragment extends Fragment implements View.OnClickListener, View.OnKeyListener, PoiQuery.EventHandler {

    private LinearLayout llSearch;
    private LinearLayout llState;
    private boolean flag = false;

    private ImageButton btnZoomIn;
    private ImageButton btnZoomOut;
    private ImageButton btnLock;
    private ImageButton btnSearch;
    private ImageButton btnSwitch;

    private ImageView netState;
    private ImageView gpsState;
    private ImageView rdState;
    private ImageView inState;
    private TextView nearbyState;

    private ListView listView;
    private SearchViewHelper searchViewHelper;
    private PoiQuery poiQuery;


    private EditText etSearch;
	// 地图控制类
	private DemoMapView mapView;
	// 地图渲染
	private MapRenderer renderer;
    // 导航相关
    private NaviSession naviSession;





	public MapsFragment() {
		// Required empty public constructor
	}

	public static MapsFragment newInstance() {
		return new MapsFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        initViews(view);
//        initNavi();
//        initQuery();
        return view;
	}

    private void initQuery() {
        try {
            PoiQueryInitParams param = new PoiQueryInitParams();
            // 如果授权不通过将抛出异常
            PoiQuery.getInstance().init(param); // 初始化搜索
            poiQuery = PoiQuery.getInstance();
            poiQuery.setWmrId(getIdByCityName("北京市"));
            poiQuery.setMode(0);
            //设置搜索城市id
            poiQuery.setCallback(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews(View view) {
        mapView = (DemoMapView) view.findViewById(R.id.map);
        mapView.setZoomHandler(handler);
        btnZoomIn = (ImageButton) view.findViewById(R.id.btn_zoom_in);
        btnZoomOut = (ImageButton) view.findViewById(R.id.btn_zoom_out);
        btnLock = (ImageButton) view.findViewById(R.id.btn_lock);
        btnSearch = (ImageButton) view.findViewById(R.id.btn_search);
        btnSwitch = (ImageButton) view.findViewById(R.id.btn_switch);
        llSearch = (LinearLayout) view.findViewById(R.id.ll_search);
        llState = (LinearLayout) view.findViewById(R.id.ll_state);

        netState = (ImageView) view.findViewById(R.id.net_state);
        gpsState = (ImageView) view.findViewById(R.id.gps_state);
        rdState = (ImageView) view.findViewById(R.id.rd_state);
        inState = (ImageView) view.findViewById(R.id.ins_state);
        nearbyState = (TextView) view.findViewById(R.id.nearby_state);


        searchViewHelper = new SearchViewHelper(view);
        etSearch = (EditText) view.findViewById(R.id.et_search);
        etSearch.setOnKeyListener(this);
        etSearch.setOnClickListener(this);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnZoomIn.setOnClickListener(this);
        btnZoomOut.setOnClickListener(this);
        btnLock.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnSwitch.setOnClickListener(this);
    }

    public void setOffline() {
        netState.setImageResource(R.drawable.ic_net_error);
    }
    public void setOnline() {
        netState.setImageResource(R.drawable.ic_net_ok);
    }

    private void initNavi(){
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

        naviSession = NaviSession.getInstance();
        //GPSTracker
        //默认暂停导航
        naviSession.pauseNavi();

        //默认在线导航
        naviSession.setNaviMode(NaviSession.Mode.online);
    }
    /**
     * 用于接收DemoMapView的消息
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 地图控件加载完毕
                    renderer = mapView.getMapRenderer();
                    renderer.setDataMode(MapRenderer.DataMode.online);
                    renderer.setZoomLevel(12);
                    break;
                case 100:
                    // 监听地图缩放 修改按钮状态
                    Bundle b = msg.getData();
                    btnZoomOut.setEnabled(b.getBoolean("zoomOut"));
                    btnZoomIn.setEnabled(b.getBoolean("zoomIn"));
                    break;
                case 103:
                    Bundle bundle = msg.getData();
                    Annotation a = (Annotation) msg.obj;
                    List<Annotation> list = mapView.getAnnotationsByClick(a.getPosition(),bundle.getFloat("z"));
                    if (!list.isEmpty()) {
                        for (Annotation a1:list) {
                            Toast.makeText(getContext(), "周围存在气泡" + a1.getTitle(), Toast.LENGTH_SHORT).show();
                        }
                    }
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_zoom_in:
                // 地图放大
                mapView.mapZoomIn(btnZoomIn, btnZoomOut);
                break;
            case R.id.btn_zoom_out:
                // 地图缩小
                mapView.mapZoomOut(btnZoomIn, btnZoomOut);
                break;
            case R.id.btn_lock:
                // 锁车
                renderer.setWorldCenter(mapView.getCarPosition());
                break;
            case R.id.btn_search:
                Annotation annotation = mapView.createAnnotation(new Point(11639999, 3990999), 1, R.drawable.ic_place_red_400_24dp, "车1");
                mapView.addAnnotations(1,1,annotation);
                Annotation annotation3 = mapView.createAnnotation(new Point(11639999, 3990999), 1, R.drawable.ic_place_red_400_24dp, "车3");
                mapView.addAnnotations(2,2,annotation3);
                Annotation annotation2 = mapView.createAnnotation(new Point(11639000, 3990000), 2, R.drawable.ic_place_red_400_24dp, "车2");
                mapView.addAnnotations(3,1,annotation2);
                break;
            case R.id.btn_switch:
                if (!flag) {
                    llState.setVisibility(View.GONE);
                    llSearch.setVisibility(View.VISIBLE);
                    flag = true;
                } else {
                    llSearch.setVisibility(View.GONE);
                    llState.setVisibility(View.VISIBLE);
                    flag = false;
                }
                break;
            case R.id.et_search:
                getIdByCityName("天津市");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 其他操作
        // 暂停地图
        if (mapView != null) {
            mapView.onPause();
        }
        // 其他操作
    }

    @Override
    public void onResume() {
        super.onResume();
        // 其他操作
        // 恢复地图
        if (mapView != null) {
            mapView.onResume();
        }
        // 其他操作
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            // 此Activity销毁时，销毁地图控件
            mapView.onDestroy();
        }
        mapView = null;
        // 其它资源的清理
        // ...
    }

    /**
     * 根据城市名称获取城市id
     *
     * @param name
     * @return
     */
    private int getIdByCityName(String name) {
        // 根据城市名称获取城市中心点
        WmrObject wb = new WmrObject(0);
        int id = wb.getChildIdByName(name);
        if (id == WmrObject.INVALID_ID) {
            int childId = wb.getChildIdByName(Config.SEARCH_DEFAULT_PROVINCE);
            WmrObject child = new WmrObject(childId);
            id = child.getChildIdByName(name);
        }
        return id;
    }
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            hideKeyboard(etSearch);
//            mSearchMapsPresenter.searchPoi(getActivity().getApplicationContext(), mSearchEditText.getText().toString(), "");
            return true;
        }
        return false;
    }

    /**
     * 隐藏键盘
     * @param view
     */
    private void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 显示键盘
     * @param view
     */
    private void showKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    @Override
    public void onPoiQuery(int event, int e, Object o) {
        switch (event) {
            case PoiQuery.Event.start:
                hideKeyboard(etSearch);
                break;
            case PoiQuery.Event.failed:
                String msg = null;
                switch (e) {
                    case PoiQuery.Error.canceled:
                        // msg = "取消搜索操作";
                        break;
                    case PoiQuery.Error.netError:
                        msg = "请连接网络";
                        break;
                    case PoiQuery.Error.noData:
                        msg = "没有本地离线数据";
                        break;
                    case PoiQuery.Error.noPermission:
                        msg = "没有公交权限";
                        break;
                    case PoiQuery.Error.none:
                        msg = "无错误";
                        break;
                    case PoiQuery.Error.noResult:
                        msg = "无搜索结果";
                        break;
                    case PoiQuery.Error.notSupport:
                        msg = "不支持的功能操作";
                        break;
                }
                if (msg != null) {
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                }
                break;
            case PoiQuery.Event.succ:
                break;
            case PoiQuery.Event.pageLoaded: {
//                refreshResult();
            }
            break;
            default:
                break;
        }
    }
    class SearchViewHelper {
        View searchMaskView;
        View rlZoom;
        ImageButton btnSwitch;
        ImageButton btnLock;
        EditText etSearch;
        ListView listView;
        private boolean isInSearch = false;

        public boolean isInSearch() {
            return isInSearch;
        }

        public SearchViewHelper(View rootView) {
            searchMaskView = rootView.findViewById(R.id.search_mask);
            btnSwitch = (ImageButton) rootView.findViewById(R.id.btn_switch);
            etSearch = (EditText) rootView.findViewById(R.id.et_search);
            listView = (ListView) rootView.findViewById(R.id.lv_search_result);
            rlZoom = rootView.findViewById(R.id.rl_zoom);
            btnLock = (ImageButton) rootView.findViewById(R.id.btn_lock);
        }

        public void enterSearch() {
            isInSearch = true;
            rlZoom.setVisibility(View.GONE);
            btnLock.setVisibility(View.GONE);

//            if (listView.getAdapter().getCount() == 0){
//                List<PoiSearchTip> tips = MapsApplication.getDaoSession().getPoiSearchTipDao().loadAll();
//                if (tips.isEmpty()){
//                    listView.setVisibility(View.GONE);
//                } else{
//                    mPoiSearchAdapter.addResultTips(tips);
//                }
//            }

            ViewAnimUtils.popupinWithInterpolator(searchMaskView, new AnimEndListener() {
                @Override
                public void onAnimEnd() {
                    searchMaskView.setVisibility(View.VISIBLE);
                }
            });

            if (listView.getAdapter().getCount() > 0){
                ViewAnimUtils.popupinWithInterpolator(listView, new AnimEndListener() {
                    @Override
                    public void onAnimEnd() {
                        listView.setVisibility(View.VISIBLE);
                    }
                });
            }


            btnSwitch.setImageResource(R.drawable.ic_back_black);
            etSearch.setCursorVisible(true);

            showKeyboard(etSearch);
        }

        public void exitSearch() {
            isInSearch = false;

            if (searchMaskView.getVisibility() == View.VISIBLE){
                ViewAnimUtils.popupoutWithInterpolator(searchMaskView, new AnimEndListener() {
                    @Override
                    public void onAnimEnd() {
                        searchMaskView.setVisibility(View.GONE);
                    }
                });
            }


            if (listView.getVisibility() == View.VISIBLE){
                ViewAnimUtils.popupoutWithInterpolator(listView, new AnimEndListener() {
                    @Override
                    public void onAnimEnd() {
                        listView.setVisibility(View.GONE);
                        rlZoom.setVisibility(View.VISIBLE);
                        btnLock.setVisibility(View.VISIBLE);
                    }
                });
            } else{
                rlZoom.setVisibility(View.VISIBLE);
                btnLock.setVisibility(View.VISIBLE);
            }


            btnSwitch.setImageResource(R.drawable.ic_menu_black);
            etSearch.setText("");
            etSearch.setCursorVisible(false);

            hideKeyboard(etSearch);


        }

        public void showSuggestTips() {
            isInSearch = true;
            listView.setVisibility(View.GONE);
            rlZoom.setVisibility(View.GONE);
            btnLock.setVisibility(View.GONE);
            etSearch.setCursorVisible(false);
            searchMaskView.setVisibility(View.GONE);
            btnSwitch.setImageResource(R.drawable.ic_back_black);
        }
    }
}
