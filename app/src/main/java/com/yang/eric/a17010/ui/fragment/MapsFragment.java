package com.yang.eric.a17010.ui.fragment;


import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.mapbar.map.Annotation;
import com.mapbar.map.CustomAnnotation;
import com.mapbar.map.MapRenderer;
import com.mapbar.map.Vector2DF;
import com.yang.eric.a17010.R;
import com.yang.eric.a17010.map.DemoMapView;


public class MapsFragment extends Fragment implements View.OnClickListener {


    private ImageButton btnZoomIn;
    private ImageButton btnZoomOut;
    private ImageButton btnLock;
    private ImageButton btnSearch;
    private ImageButton btnSwitch;
	// 地图控制类
	private DemoMapView mapView;
	// 地图渲染
	private MapRenderer renderer;

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
        mapView = (DemoMapView) view.findViewById(R.id.map);
        mapView.setZoomHandler(handler);
        btnZoomIn = (ImageButton) view.findViewById(R.id.btn_zoom_in);
        btnZoomOut = (ImageButton) view.findViewById(R.id.btn_zoom_out);
        btnLock = (ImageButton) view.findViewById(R.id.btn_lock);
        btnSearch = (ImageButton) view.findViewById(R.id.btn_search);
        btnSwitch = (ImageButton) view.findViewById(R.id.btn_switch);
        btnZoomIn.setOnClickListener(this);
        btnZoomOut.setOnClickListener(this);
        btnLock.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnSwitch.setOnClickListener(this);
        return view;
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
                Annotation annotation = new CustomAnnotation(2,new Point(11639999, 3990999),777,new Vector2DF(0.5f, 0.82f),
                        BitmapFactory.decodeResource(getResources(), R.drawable.ic_place_red_400_24dp));
                mapView.addAnnotations(1,1,annotation);
                Annotation annotation2 = new CustomAnnotation(2,new Point(11639000, 3990000),777,new Vector2DF(0.5f, 0.82f),
                        BitmapFactory.decodeResource(getResources(), R.drawable.ic_place_red_400_24dp));
                mapView.addAnnotations(3,1,annotation2);
                break;
            case R.id.btn_switch:
                mapView.removeAnnotationByTag(1);
                break;
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
}
