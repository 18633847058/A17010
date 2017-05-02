package com.yang.eric.a17010.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.mapbar.map.Annotation;
import com.mapbar.map.ArrowOverlay;
import com.mapbar.map.CalloutStyle;
import com.mapbar.map.CustomAnnotation;
import com.mapbar.map.IconOverlay;
import com.mapbar.map.MapRenderer;
import com.mapbar.map.MapState;
import com.mapbar.map.MapView;
import com.mapbar.map.ModelOverlay;
import com.mapbar.map.Overlay;
import com.mapbar.map.RouteOverlay;
import com.mapbar.map.Vector2DF;
import com.mapbar.mapdal.NaviCoreUtil;
import com.mapbar.mapdal.PoiFavorite;
import com.mapbar.navi.ArrowInfo;
import com.mapbar.navi.CameraData;
import com.mapbar.navi.ExpandView;
import com.mapbar.navi.HighwayGuide;
import com.mapbar.navi.HighwayGuideItem;
import com.mapbar.navi.NaviSession;
import com.mapbar.navi.NaviSpeaker;
import com.mapbar.navi.RouteBase;
import com.mapbar.navi.RouteCollection;
import com.mapbar.navi.RoutePlan;
import com.yang.eric.a17010.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class DemoMapView extends MapView {
	private Context mContext;
	private static final int CAMERAS_MAX = 16;
	private boolean mInited = false;
	public static Handler mHandler;
	private static final int[] mRouteOverlayColors = { 0xffaa0000, 0xff00aa00, 0xff0000aa, 0xff4578fc };
	// 步行显示图标
	private IconOverlay walkIcon = null;
	// 小车
	private ModelOverlay mCarOverlay = null;
	// 电子眼
	private Annotation[] mCameraAnnotations = null;
	// 箭头
	private ArrowOverlay mArrowOverlay = null;
	// 小车状态
	private boolean mIsLockedCar = false;
	private float mCarOriented = 0.0f;
	private Point mCarPosition = null;
	// 路线计划
	private RoutePlan mRoutePlan = null;
	// 地图当前的状态
	private MapState mMapState = null;
	// 需要绘制的路线
	private RouteOverlay[] mRouteCollectionOverlays = null;
	private int mRouteOverlayNumber = 0;
	// 用来绘制放大图
	private ImageView mExpandView = null;
	public Bitmap mBitmap = null;
	// 选中的POI
	private Annotation mPoiAnnotation = null;
	private Annotation mPositionAnnotation = null;

	private MapRenderer mRenderer = null;

	private static final float ZOOM_STEP = 0.5f;
	private Vector2DF mZoomLevelRange = null;
	private static final int BITMAP_WIDTH = 480;
	private static final int BITMAP_HEIGHT = 480;

	public static Point mClickPoint = null;

	private final int SCALE_TYPE = MapRenderer.CameraSetting.zoomLevel + MapRenderer.CameraSetting.scale;
	private final int ELEVATION_SCALE_TYPE = MapRenderer.CameraSetting.zoomLevel + MapRenderer.CameraSetting.scale
			+ MapRenderer.CameraSetting.elevation;
	private final int DOUBLE_TYPE = MapRenderer.CameraSetting.zoomLevel + MapRenderer.CameraSetting.scale
			+ MapRenderer.CameraSetting.worldCenter;
	private final int ELEVATION_DOUBLE_TYPE = MapRenderer.CameraSetting.zoomLevel + MapRenderer.CameraSetting.scale
			+ MapRenderer.CameraSetting.worldCenter + MapRenderer.CameraSetting.elevation;


    //存储添加到当前地图中的标记和覆盖物
    private Map<MarkerType,Annotation> annotations = new HashMap<>();
    private Map<MarkerType,Overlay> overlays = new HashMap<>();


    /**
     * 添加覆盖物到地图,覆盖物可以有多种类型
     *  圆CircleOverlay/线 多边形面PolylineOverlay/多边形透明PolygonOverlay
     * @param tag
     * @param groups
     * @param overlay
     */
    public void addOverley(int tag,int groups, Overlay overlay) {
        MarkerType type = new MarkerType(tag, groups);
        Iterator<Map.Entry<MarkerType,Overlay>> ite = overlays.entrySet().iterator();
        while(ite.hasNext()){
            Map.Entry<MarkerType, Overlay> entry = ite.next();
            if(type.equals(entry.getKey())){
                mRenderer.removeOverlay(entry.getValue());
                ite.remove();
            }
        }
        overlays.put(new MarkerType(tag,groups), overlay);
        mRenderer.addOverlay(overlay);
    }

    /**
     * 移除具体的某个覆盖物对象
     * @param overlay
     */
    public void removeOverlay(Overlay overlay) {
        Iterator<Map.Entry<MarkerType,Overlay>> ite = overlays.entrySet().iterator();
        while(ite.hasNext()){
            Map.Entry<MarkerType, Overlay> entry = ite.next();
            if(overlay == entry.getValue()){
                mRenderer.removeOverlay(entry.getValue());
                ite.remove();
            }
        }
    }

    /**
     * 通过标签移除一部分覆盖物
     * @param tag
     */
    public void removeOverlayByTag(int tag) {
        Iterator<Map.Entry<MarkerType,Overlay>> ite = overlays.entrySet().iterator();
        while(ite.hasNext()){
            Map.Entry<MarkerType, Overlay> entry = ite.next();
            if(tag == entry.getKey().getTag()){
                mRenderer.removeOverlay(entry.getValue());
                ite.remove();
            }
        }
    }

    /**
     * 通过分组移除覆盖物
     * @param groupId
     */
    public void removeOverlayByGroupId(int groupId) {
        Iterator<Map.Entry<MarkerType,Overlay>> ite = overlays.entrySet().iterator();
        while(ite.hasNext()){
            Map.Entry<MarkerType, Overlay> entry = ite.next();
            if(groupId == entry.getKey().getTag()){
                mRenderer.removeOverlay(entry.getValue());
                ite.remove();
            }
        }
    }

	/**
	 * 添加标记到地图,分组分标签管理
	 * @param tag
	 * @param groups
	 * @param annotation
	 */
    public void addAnnotations(int tag, int groups, Annotation annotation) {
        MarkerType type = new MarkerType(tag, groups);
        Iterator<Map.Entry<MarkerType,Annotation>> ite = annotations.entrySet().iterator();
        while(ite.hasNext()){
            Map.Entry<MarkerType, Annotation> entry = ite.next();
            if(type.equals(entry.getKey())){
                hiddenAnnotation(entry.getValue());
                entry.getValue().freeNativeMemeory();
                ite.remove();
            }
        }
        annotations.put(new MarkerType(tag,groups), annotation);
        mRenderer.addAnnotation(annotation);
    }

	/**
	 * 删除具体的一个标记对象
	 * @param annotation
	 */
	public void removeAnnotation(Annotation annotation) {
		Iterator<Map.Entry<MarkerType,Annotation>> ite = annotations.entrySet().iterator();
		while(ite.hasNext()){
			Map.Entry<MarkerType, Annotation> entry = ite.next();
			if(annotation == entry.getValue()){
				hiddenAnnotation(entry.getValue());
				entry.getValue().freeNativeMemeory();
				ite.remove();
			}
		}
	}

    /**
     * 通过标签移除同同标签所有标记
     * @param tag
     */
	public void removeAnnotationByTag(int tag) {
        Iterator<Map.Entry<MarkerType,Annotation>> ite = annotations.entrySet().iterator();
        while(ite.hasNext()){
            Map.Entry<MarkerType, Annotation> entry = ite.next();
            if(tag == entry.getKey().getTag()){
                hiddenAnnotation(entry.getValue());
                entry.getValue().freeNativeMemeory();
                ite.remove();
            }
        }
    }
    /**
     * 通过标签移除同同标签所有标记
     * @param groupId
     */
    public void removeAnnotationByGroupId(int groupId) {
        Iterator<Map.Entry<MarkerType,Annotation>> ite = annotations.entrySet().iterator();
        while(ite.hasNext()){
            Map.Entry<MarkerType, Annotation> entry = ite.next();
            if(groupId == entry.getKey().getGroupId()){
                hiddenAnnotation(entry.getValue());
                entry.getValue().freeNativeMemeory();
                ite.remove();
            }
        }
    }


	public boolean isInited() {
		return mInited;
	}

	private void init(Context context) {
		mContext = context;
		mIsLockedCar = true;
		mRouteCollectionOverlays = new RouteOverlay[4];
	}

	// TODO: 拋出異常

	public DemoMapView(Context context) {
		super(context);
		init(context);
	}

	public DemoMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void setZoomHandler(Handler handler) {
		mHandler = handler;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);
		// 防止重复创建
		if (!mInited) {
			mRenderer = super.getMapRenderer();
			if (mRenderer == null)
				return;
			mRenderer.setWorldCenter(Config.centerPoint);
			mClickPoint = new Point(mRenderer.getWorldCenter());
			Vector2DF pivot = new Vector2DF(0.5f, 0.82f);

			// 步行导航时显示的标 ,默认隐藏
			walkIcon = new IconOverlay("res/cars.png", true);
			walkIcon.markAsAnimated(4, "a1000;b30;c30;d30;c40;b60;a200;b30;c30;d30;c40;b60;");
			walkIcon.setPosition(mClickPoint);
			walkIcon.setOrientAngle(0.0f);
			walkIcon.setScaleFactor(1.5f);
			mRenderer.addOverlay(walkIcon);
			walkIcon.setHidden(true);

			// 初始化Overlay和Annotation 添加车标
			mCarOverlay = new ModelOverlay(NaviCoreUtil.buildPathInPacket("models/car_model.obj"), true);

			mCarOverlay.setScaleFactor(0.3f);
			mCarOverlay.setPosition(mClickPoint);
			mCarOverlay.setHeading(0);
			mRenderer.addOverlay(mCarOverlay);


			// 添加点击气泡
			mPoiAnnotation = new CustomAnnotation(2, mClickPoint,
			// 此参数为气泡id 不能重复
					666, pivot, BitmapFactory.decodeResource(getResources(), R.drawable.ic_place_red_400_24dp));
			mPositionAnnotation = new CustomAnnotation(2, mClickPoint,
			// 此参数为气泡id 不能重复
					888, pivot, BitmapFactory.decodeResource(getResources(), R.drawable.ic_place_red_400_24dp));
			CalloutStyle calloutStyle = mPoiAnnotation.getCalloutStyle();
			calloutStyle.anchor.set(0.5f, 0.0f);
            calloutStyle.leftIcon = 101;
			calloutStyle.rightIcon = 1001;
			mPoiAnnotation.setCalloutStyle(calloutStyle);
			mPositionAnnotation.setTitle("选取点");
			mPositionAnnotation.setCalloutStyle(calloutStyle);
			mRenderer.addAnnotation(mPoiAnnotation);
			mRenderer.addAnnotation(mPositionAnnotation);

			hiddenAllAnnot();

			// 电子眼
			mCameraAnnotations = new Annotation[CAMERAS_MAX];
			Vector2DF cameraPivot = new Vector2DF(0.5f, 0.9f);
			for (int i = 0; i < mCameraAnnotations.length; i++) {
				mCameraAnnotations[i] = new Annotation(2, mClickPoint, 1300, cameraPivot);
				mCameraAnnotations[i].setHidden(true);
				mRenderer.addAnnotation(mCameraAnnotations[i]);
			}

			// 实例化路线计划
			mRoutePlan = new RoutePlan();
			mInited = true;
			// 创建完毕通知
			if (mHandler != null) {
				mHandler.sendEmptyMessage(1);
			}
		}
	}

	/**
	 * 初始化放大图绘制使用的view
	 * 
	 * @param view
	 */
	public void setExpandView(ImageView view) {
		mExpandView = view;
		ExpandView.resizeScreen(BITMAP_WIDTH, BITMAP_HEIGHT);
	}

	/**
	 * 开始模拟导航
	 */
	public void startSimulation() {
		backupMapStateBeforeSimulation();
	}

	/**
	 * 结束模拟导航
	 */
	public void endSimulation() {
		resetMapStateAfterSimulation();
		if (mArrowOverlay != null) {
			mRenderer.removeOverlay(mArrowOverlay);
			mArrowOverlay = null;
		}
		mBitmap = null;
		drawCameras(null);
	}

	public void drawHighwayGuide() {
		HighwayGuideItem[] items = HighwayGuide.getItems();
		for (int i = 0; i < items.length; i++) {
			Log.d("[drawHighwayGuid]", items[i].toString());
		}
	}

	/**
	 * 绘制放大图
	 */
	public void drawExpandView() {
		if (ExpandView.shouldDisplay()) {
			if (mBitmap == null)
				mBitmap = Bitmap.createBitmap(BITMAP_WIDTH, BITMAP_HEIGHT, Bitmap.Config.ARGB_8888);
			ExpandView.render(mBitmap);
			mExpandView.setImageBitmap(mBitmap);
		} else {
			mBitmap = null;
		}
		if (ExpandView.shouldDisplayOpenButton()) {

		}
	}

	/**
	 * 删除箭头
	 */
	public void delArrow() {
		if (mArrowOverlay != null) {
			mRenderer.removeOverlay(mArrowOverlay);
			mArrowOverlay = null;
		}
	}

	/**
	 * 绘制指定Maneuver的箭头
	 * 
	 * @param arrowInfo
	 *            路线
	 */
	public void drawArrow(ArrowInfo arrowInfo) {
		// ArrowInfo arrowInfo = ArrowRenderer.getArrowInfo();
		if (arrowInfo.valid()) {
			if (mArrowOverlay != null) {
				mRenderer.removeOverlay(mArrowOverlay);
				mArrowOverlay = null;
			}
			mArrowOverlay = new ArrowOverlay(arrowInfo);
			mRenderer.addOverlay(mArrowOverlay);
		}
		arrowInfo = null;
	}

	/**
	 * 绘制摄像头
	 * 
	 * @param cameras
	 *            需要绘制的摄像头数组
	 */
	public void drawCameras(final CameraData[] cameras) {
		for (int i = 0; i < mCameraAnnotations.length; i++) {
			mCameraAnnotations[i].setHidden(true);
		}
		if (cameras != null) {
			for (int i = 0; i < cameras.length; i++) {
				mCameraAnnotations[i].setPosition(cameras[i].position);
				mCameraAnnotations[i].setHidden(false);
			}
		} else {
			Log.e("[GLMapRenderer]", "===================================Camera is null====================================");
		}
	}

	/**
	 * 绘制出多条路线
	 * 
	 * @param routeCollection
	 * @param routeIndex
	 *
	 */
	public void drawRoutes(RouteCollection routeCollection, int routeIndex) {
		// 绘制前先清空路线
		removeRouteOverlay(true);
		mRouteOverlayNumber = routeCollection.num;
		// 把所有的路线先绘制出来
		for (int i = 0; i < routeCollection.num; i++) {
			mRouteCollectionOverlays[i] = new RouteOverlay(routeCollection.routes[i]);
			// 默认将一条路线画到地图上
			mRenderer.addOverlay(mRouteCollectionOverlays[i]);
			mRouteCollectionOverlays[i].setColor(mRouteOverlayColors[i]);
			// mRouteCollectionOverlays[i].enableTmcColors(true);
			// 显示默认选中的路线 其它路线先隐藏
			mRouteCollectionOverlays[i].setHidden(i != routeIndex);
		}
	}

	/**
	 * 绘制单条路线
	 * 
	 * @param  routeBase
	 */
	public void drawRoutes(RouteBase routeBase, boolean isTmc) {
		// 绘制前先清空路线
		removeRouteOverlay(true);
		mRouteCollectionOverlays[0] = new RouteOverlay(routeBase);
		// 默认将一条路线画到地图上
		mRenderer.addOverlay(mRouteCollectionOverlays[0]);
		if (isTmc) {
			mRouteCollectionOverlays[0].enableTmcColors(isTmc);
		}
		if (routeBase.getPlan().getRule() == RoutePlan.Rule.walk) {
			mRouteCollectionOverlays[0].setColor(mRouteOverlayColors[1]);
		}
		// mRouteCollectionOverlays[0].setOutlineColor(0xff4578fc);
		// 显示
		mRouteCollectionOverlays[0].setHidden(false);
		mRouteOverlayNumber = 1;
	}

	/**
	 * 设置路线是否开启Tmc模式
	 */
	public void setRouteTmc(boolean isTmc) {
		if (mRouteCollectionOverlays[0] != null) {
			if (isTmc) {
				mRouteCollectionOverlays[0].enableTmcColors(isTmc);
			} else {
				mRouteCollectionOverlays[0].enableTmcColors(isTmc);
				// mRouteCollectionOverlays[0].setColor(mRouteOverlayColors[0]);
			}
		}
	}

	/**
	 * 将路线显示在地图上
	 * 
	 * @param index
	 */
	public void drawRouteToMap(int index) {
		if (mRouteCollectionOverlays != null && index < mRouteOverlayNumber) {
			mRouteCollectionOverlays[index].setHidden(false);
		}
	}

	/**
	 * 点击气泡
     * 弹出气泡的点击事件处理
	 */
	@Override
	public void onAnnotationClicked(Annotation annot, int area) {
		super.onAnnotationClicked(annot, area);
		Point point = new Point(mClickPoint);
		annot.showCallout(true);
		Message msg = new Message();
		PoiFavorite fav = new PoiFavorite();
		switch (area) {
		case Annotation.Area.leftButton:
			// 气泡左侧搜索周边
			hiddenAnnotation(annot);
			break;
		case Annotation.Area.rightButton:
			// 气泡右边导航按钮
			msg.what = 102;
			fav.name = mPoiAnnotation.getTitle();
			fav.pos = point;
			msg.obj = fav;
			if (mHandler != null) {
				mHandler.sendMessage(msg);
			}
			hiddenAnnotation(annot);
			break;
		case Annotation.Area.middleButton:
			// 气泡中间
			msg.what = 101;
			fav.name = mPoiAnnotation.getTitle();
			fav.pos = point;
			msg.obj = fav;
			if (mHandler != null) {
				mHandler.sendMessage(msg);
			}
			// hiddenAnnotation(annot);
			break;
		case Annotation.Area.none:
		case Annotation.Area.icon:
            Log.e("", "onAnnotationClicked: " +  "1111111111");
            break;
            default:
			break;
		}
	}

	/**
	 * 选择icon
	 */
	@Override
	public void onAnnotationSelected(Annotation arg0) {
		super.onAnnotationSelected(arg0);
		arg0.showCallout(true);
	}

	/**
	 * 取消icon
	 */
	@Override
	public void onAnnotationDeselected(Annotation annot) {
		super.onAnnotationDeselected(annot);
		annot.showCallout(false);
	}

	private void showAnnotation(Annotation annot) {
		hiddenAllAnnot();
		if (annot != null) {
			annot.showCallout(true);
			annot.setHidden(false);
		}
	}

	/**
	 * 隐藏两个气泡
	 */
	public void hiddenAllAnnot() {
		if (mPoiAnnotation != null) {
			mPoiAnnotation.showCallout(false);
			mPoiAnnotation.setHidden(true);
		}
		if (mPositionAnnotation != null) {
			mPositionAnnotation.showCallout(false);
			mPositionAnnotation.setHidden(true);
		}
	}

	/**
	 * 隐藏指定气泡
	 * 
	 */
	private void hiddenAnnotation(Annotation annot) {
		annot.showCallout(false);
		annot.setHidden(true);
	}

	/**
	 * 点击poi
	 */
	@Override
	public void onPoiSelected(String name, Point point) {
		super.onPoiSelected(name, point);
		// TODO: 替換mRenderer方法
		mClickPoint.set(point.x, point.y);
		mPoiAnnotation.setTitle(name);
		mPoiAnnotation.setPosition(mClickPoint);
		showAnnotation(mPoiAnnotation);
		mRenderer.beginAnimations();
		mRenderer.setWorldCenter(mClickPoint);
		mRenderer.commitAnimations(500, MapRenderer.Animation.linear);
	}

	@Override
	public void onPoiDeselected(String name, Point point) {
		super.onPoiDeselected(name, point);
		hiddenAnnotation(mPoiAnnotation);
	}

	/**
	 * 备份地图和小车的状态以便模拟导航之后可以恢复
	 */
	private void backupMapStateBeforeSimulation() {
		mMapState = mRenderer.getMapState();
		mCarPosition = mCarOverlay.getPosition();
		mCarOriented = mCarOverlay.getHeading();
	}

	/**
	 * 模拟导航结束之后恢复地图和小车之前的状态
	 */
	private void resetMapStateAfterSimulation() {
		mRenderer.setMapState(mMapState);
		mCarOverlay.setPosition(mCarPosition);
		mCarOverlay.setHeading((int) mCarOriented);
	}

	/**
	 * 设置路线起点
	 * 
	 * @param point 路线起点
	 * @param name 路线起点名称
	 */
	// TODO: 添加註釋修改邏輯
	public void setStartPoint(Point point,String name) {
		PoiFavorite poi = new PoiFavorite(point);
		poi.name = name;
		mRoutePlan.setStartPoint(poi);
		setCarPosition(point);
	}

	/**
	 * 设置途经点
	 * 
	 * @param point
	 *            路线起点
	 */
	public void setWayPoint(Point point) {
		PoiFavorite poi = new PoiFavorite(point);
		mRoutePlan.addWayPoint(poi);
	}

	/**
	 * 设置目的地
	 * 
	 * @param point  目的地
	 * @param name 路线终点名称
	 */
	public void setDestination(Point point,String name) {
		PoiFavorite poi = new PoiFavorite(point);
		poi.name = name;
		// 如果没有起点，或者已经同时存在起点和终点，那么都应该设置车的位置为新的起点
		int m = mRoutePlan.getDestinationNum();
		if (m != 1) {
			setStartPoint(getCarPosition(),name);
		}
		if (!mRoutePlan.setEndPoint(poi)) {
			NaviSpeaker.enqueue("终点设置失败，请先设置起点");
		}
	}

	/**
     * 设置路线规则
	 * 
	 */

	public void setRouteRule(int rule) {
		mRoutePlan.setRule(rule);
	}

	/**
	 *  开始算路
     *
	 */
	public boolean startRoute() {

		if (mRoutePlan.getDestinationNum() > 1) {
			NaviSetting ns = NaviSetting.getInstance();
			mRoutePlan.setRule(ns.getRouteRule());
			mRoutePlan.setUseTmc(ns.getUseTmc());
			NaviSession.getInstance().startRoute(mRoutePlan, ns.getRouteMethod());
			return true;
		}
		return false;
	}

	/**
	 * 控制是否锁车
	 * 
	 * @param lock
	 *            是否锁车，如果为true则锁车，否则不锁车
	 */
	public void lockCar(boolean lock) {
		if (lock != mIsLockedCar) {
			mIsLockedCar = lock;
			if (mIsLockedCar) {
				mRenderer.setWorldCenter(mCarOverlay.getPosition());
				mRenderer.setHeading(360.0f - mCarOverlay.getHeading());
				mRenderer.setViewShift(0.3f);
			}
		}
	}

	/**
	 * 判断是否为锁车状态
	 * 
	 * @return 如果锁车则返回true，否则返回false
	 */
	public boolean carIsLocked() {
		return mIsLockedCar;
	}

	/**
	 * 设置车的位置，用于在模拟导航时更新车的位置使用
	 * 
	 * @param point
	 *            车所在位置
	 */
	public void setCarPosition(Point point) {
		if (mCarOverlay != null) {
			mCarOverlay.setPosition(point);
		}
		if (walkIcon != null) {
			walkIcon.setPosition(point);
		}

		if (mIsLockedCar && mRenderer != null) {
			mRenderer.setWorldCenter(point);
		}
	}

	/**
	 * 获取车当前的位置
	 * 
	 * @return 车当前的位置坐标
	 */
	public Point getCarPosition() {
		return mCarOverlay.getPosition();
	}

	/**
	 * 设置当前车的角度，用于导航时更新车的角度
	 * 
	 * @param ori
	 *            车的角度
	 */
	public void setCarOriented(float ori) {
		mCarOverlay.setHeading((int) ori);
		walkIcon.setOrientAngle(ori);
		if (mIsLockedCar) {
			mRenderer.setHeading(360.0f - ori);
		}
	}

	/**
	 * 在地图指定位置显示一个POI的信息
	 * 
	 * @param point
	 *            POI所在位置
	 * @param name
	 *            POI名称
	 */
	public void showPoiAnnotation(Point point, String name) {
		mRenderer.setWorldCenter(point);
		mClickPoint.set(point.x, point.y);
		mPoiAnnotation.setTitle(name);
		mPoiAnnotation.setPosition(point);
		showAnnotation(mPoiAnnotation);
	}

	/**
	 * 获取当前车的角度
	 * 
	 * @return 车的较粗
	 */
	public float getCarOriented() {
		return mCarOverlay.getHeading();
	}

	/**
	 * 删除路线
	 */
	public void removeRoute() {
		removeRouteOverlay(true);
		mRoutePlan.clearDestinations();
	}

	/**
	 * 绘制路线
	 * 
	 * @param routeBase
	 *            路线对应的RouteBase
	 * @param isTmc
	 *            是否开启tmc
	 */
	public void setRoute(RouteBase routeBase, boolean isTmc) {
		removeRouteOverlay(true);
		drawRoutes(routeBase, isTmc);
	}

	/**
	 * 将指定的路线隐藏
	 * 
	 * @param index
	 */
	public void removeRouteOverlay(int index) {
		if (mRouteCollectionOverlays[index] != null) {
			mRenderer.removeOverlay(mRouteCollectionOverlays[index]);
			mRouteCollectionOverlays[index] = null;
		}
	}

	/**
	 * 删除所有路线
	 * 
	 * @param removeAll
	 */
	private void removeRouteOverlay(boolean removeAll) {
		for (int i = 0; i < mRouteOverlayNumber; i++) {
			if (removeAll) {
				removeRouteOverlay(i);
			}
		}
		if (removeAll) {
			mRouteOverlayNumber = 0;
		} else {
			mRouteOverlayNumber = 1;
		}
		if (mArrowOverlay != null) {
			mRenderer.removeOverlay(mArrowOverlay);
			mArrowOverlay = null;
		}
	}

	/**
	 * 地图放大操作
	 * 
	 * @param zoomIn
	 *            放大按钮
	 * @param zoomOut
	 *            缩小按钮
	 */
	public void mapZoomIn(ImageButton zoomIn, ImageButton zoomOut) {
		float zoomLevel = mRenderer.getZoomLevel();
		if (mZoomLevelRange == null) {
			mZoomLevelRange = mRenderer.getZoomLevelRange();
		}
		zoomLevel = zoomLevel + ZOOM_STEP;
		if (zoomLevel >= mZoomLevelRange.getY()) {
			zoomLevel = mZoomLevelRange.getY();
			zoomIn.setEnabled(false);
		}
		zoomOut.setEnabled(true);
		mRenderer.beginAnimations();
		mRenderer.setZoomLevel(zoomLevel);
		mRenderer.commitAnimations(300, MapRenderer.Animation.linear);
	}

	/**
	 * 地图缩小操作
	 * 
	 * @param zoomIn
	 *            放大按钮
	 * @param zoomOut
	 *            缩小按钮
	 */
	public void mapZoomOut(ImageButton zoomIn, ImageButton zoomOut) {
		float zoomLevel = mRenderer.getZoomLevel();
		if (mZoomLevelRange == null) {
			mZoomLevelRange = mRenderer.getZoomLevelRange();
		}
		zoomLevel = zoomLevel - ZOOM_STEP;
		if (zoomLevel <= mZoomLevelRange.getX()) {
			zoomLevel = mZoomLevelRange.getX();
			zoomOut.setEnabled(false);
		}
		zoomIn.setEnabled(true);
		mRenderer.beginAnimations();
		mRenderer.setZoomLevel(zoomLevel);
		mRenderer.commitAnimations(300, MapRenderer.Animation.linear);
	}

	/**
	 * 检查网络wifi 2G 3G网络
	 * 
	 * @return TODO
	 */
	public boolean isOpenNet() {
		ConnectivityManager connManager = (ConnectivityManager) mContext.getApplicationContext().getSystemService(
				Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}
		return false;
	}

	/**
	 * 检查gps是否开启
	 * 
	 * @return
	 */
	public boolean isOpenGps() {
		return Settings.Secure.isLocationProviderEnabled(mContext.getContentResolver(), LocationManager.GPS_PROVIDER);
	}

	/**
	 *
	 *  显示车标或者箭头标
	 * @param isWalk
	 *            true箭头标 false 车标
	 * 
	 */
	public void setCarOrWalk(boolean isWalk) {
		if (isWalk) {
			walkIcon.setHidden(false);
			mCarOverlay.setHidden(true);
		} else {
			walkIcon.setHidden(true);
			mCarOverlay.setHidden(false);
		}
	}
	
	@Override
	public void onScrollFinished(boolean arg0) {
		mRenderer.beginAnimations();
		if(getSmallMapRenderer()!=null){
			getSmallMapRenderer().setWorldCenter(mRenderer.getWorldCenter());
		}
		mRenderer.commitAnimations(1000, MapRenderer.Animation.linear);
		super.onScrollFinished(arg0);
	}
	
	@Override
	public void onCameraChanged(int changeTye) {
		super.onCameraChanged(changeTye);
		MapRenderer render = getMapRenderer();
		if (render != null) {
			switch (changeTye) {
			case SCALE_TYPE:
				zoomChange();
				break;
			case DOUBLE_TYPE:
				zoomChange();
				break;
			case ELEVATION_SCALE_TYPE:
				zoomChange();
				break;
			case ELEVATION_DOUBLE_TYPE:
				zoomChange();
				break;
			}
		}
	}

	// ////////////////////////////////////////////////
	// OnTouchListener
	// ////////////////////////////////////////////////

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		}
		int actionAndIndex = event.getAction();
		int action = actionAndIndex & MotionEvent.ACTION_MASK;
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			// 手动无级缩放时 要注意改变放大缩小是否可用
			zoomChange();
		}
		return super.onTouch(v, event);
	}

	/**
	 * 缩放级别改变
	 */
	public void zoomChange() {
		if (mRenderer == null)
			return;
		float zoomLevel = mRenderer.getZoomLevel();
		Message msg = new Message();
		msg.what = 100;
		Bundle b = msg.getData();
		// 默认都可用
		b.putBoolean("zoomIn", true);
		b.putBoolean("zoomOut", true);
		if (mZoomLevelRange == null) {
			mZoomLevelRange = mRenderer.getZoomLevelRange();
		}
		// 判断放大缩小是否可用
		if (zoomLevel <= mZoomLevelRange.getX()) {
			b.putBoolean("zoomIn", false);
		}
		if (zoomLevel >= mZoomLevelRange.getY()) {
			b.putBoolean("zoomOut", false);
		}
		// 发送消息
		if (mHandler != null) {
			mHandler.sendMessage(msg);
		}
	}

	@SuppressWarnings("deprecation")
	private GestureDetector mGestureDetector = new GestureDetector(new OnGestureListener() {
		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			System.out.println("");
			return false;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {

		}

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
			return false;
		}

		@Override
		public void onLongPress(MotionEvent event) {
			int pointerCount = event.getPointerCount();
			if (pointerCount == 1) {
				MapRenderer mr = mRenderer;
				Point point = mRenderer.screen2World(new PointF(event.getX(), event.getY()));
				mClickPoint.set(point.x, point.y);
				mPositionAnnotation.setPosition(mClickPoint);
				showAnnotation(mPositionAnnotation);
				mr.beginAnimations();
				mr.setWorldCenter(mClickPoint);
				mr.commitAnimations(500, MapRenderer.Animation.linear);
			}
		}

		@Override
		public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
			return false;
		}

		@Override
		public boolean onDown(MotionEvent arg0) {
			return false;
		}
	});

}
