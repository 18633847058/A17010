package com.yang.eric.a17010.map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbar.map.MapRenderer;
import com.mapbar.mapdal.WmrObject;
import com.mapbar.poiquery.PoiFavoriteInfo;
import com.mapbar.poiquery.PoiQuery;
import com.mapbar.poiquery.PoiQuery.EventHandler;
import com.mapbar.poiquery.PoiQueryInitParams;
import com.yang.eric.a17010.R;

import java.util.ArrayList;

public class SearchBusActivity extends Activity implements OnClickListener, EventHandler {

	private int queryFrom = NaviSetting.FROM_NONE;
	private int mCityId = 1;
	private TextView mSearchButton;
	private EditText mConditionText;
	private EditText mCityEditText;
	private ArrayList<PoiFavoriteInfo> mItems = new ArrayList<PoiFavoriteInfo>();
	private ListView mListView;
	private RelativeLayout mPageLayout;
	private Button mPrevPage;
	private Button mNextPage;

	private static final String TAG = "[SearchActivity]";
	// 等待提示框
	public ProgressDialog myDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bus_search);
		initData();
		initView();
	}

	private void initView() {

		mConditionText = (EditText) findViewById(R.id.img_input_edit);
		mSearchButton = (TextView) findViewById(R.id.nearby_search_condition_search);
		mSearchButton.setOnClickListener(this);
		mCityEditText = (EditText) findViewById(R.id.img_input_edit_city);
		mCityEditText.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.sch_results);
		mPageLayout = (RelativeLayout) findViewById(R.id.bus_search_page);
		mPrevPage = (Button) findViewById(R.id.prev_page);
		mPrevPage.setOnClickListener(this);
		mNextPage = (Button) findViewById(R.id.next_page);
		mNextPage.setOnClickListener(this);
	}

	private void initData() {
		try {
			Bundle bundle = getIntent().getExtras();
			PoiQueryInitParams param = new PoiQueryInitParams();
			// 如果授權不通過那麼將拋出異常
			PoiQuery.getInstance().init(param); // 初始化搜索
			PoiQuery mPoiQuery = PoiQuery.getInstance();
			if (bundle != null) {
				int res = bundle.getInt("__from");
				switch (res) {
				case NaviSetting.FROM_BUS_SEARCH:
					Log.d(TAG, "from bus query");
					queryFrom = NaviSetting.FROM_BUS_SEARCH;
					mPoiQuery.setWmrId(bundle.getInt("wmrId"));
					mCityId = bundle.getInt("wmrId");
					Log.e(TAG, "aWmrId = " + bundle.getInt("wmrId"));
					break;
				case NaviSetting.FROM_NAVI_SEARCH:
					Log.d(TAG, "from bus query");
					queryFrom = NaviSetting.FROM_NAVI_SEARCH;
					mPoiQuery.setWmrId(bundle.getInt("wmrId"));
					mCityId = bundle.getInt("wmrId");
					Log.e(TAG, "aWmrId = " + bundle.getInt("wmrId"));
					break;
				default:
					break;
				}
			}
			boolean online = bundle.getBoolean("online");
			mPoiQuery.setMode(online ? MapRenderer.DataMode.online : MapRenderer.DataMode.offline);
			// 设置搜索城市id
			mPoiQuery.setCallback(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPoiQuery(int event, int err, Object data) {
		switch (event) {
		case PoiQuery.Event.start:
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mConditionText.getWindowToken(), 0);
			break;
		case PoiQuery.Event.failed:
			disDialog();
			String msg = null;
			switch (err) {
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
				Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
			}
			break;
		case PoiQuery.Event.succ:
			disDialog();
			break;
		case PoiQuery.Event.pageLoaded: {
			refereshResult();
			disDialog();
		}
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nearby_search_condition_search:
			String text = mConditionText.getText().toString();
			if (!text.equals("")) {
				PoiQuery pq = PoiQuery.getInstance();
				if (queryFrom == NaviSetting.FROM_BUS_SEARCH || queryFrom == NaviSetting.FROM_NAVI_SEARCH) {
					waitDialog("请求中，请稍后……", true);
					pq.queryByKeyword(text, Config.centerPoint, null);
				}
				Log.e(TAG, "WmrId = " + pq.getWmrId());
			} else {
				Toast.makeText(this, "请输入查询信息", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.img_input_edit_city:
			//选择城市
			// 选择搜索城市 需要进入SelectActivity页面
			Intent intent = new Intent(this, SelectActivity.class);
			intent.putExtra("type", 1);
			startActivityForResult(intent, 1);
			break;
		case R.id.prev_page:
			// 上一页
			PoiQuery pq = PoiQuery.getInstance();
			waitDialog("请求中，请稍后……", true);
			pq.loadPreviousPage(null);
			refereshResult();
			break;
		case R.id.next_page:
			// 下一页
			PoiQuery pq2 = PoiQuery.getInstance();
			waitDialog("请求中，请稍后……", true);
			pq2.loadNextPage(null);
			refereshResult();
			break;
		}
	}

	private void refereshResult() {
		PoiQuery pq = PoiQuery.getInstance();
		if (pq.getResultNumber() != 0) {
			mItems.clear();
			PoiFavoriteInfo info = pq.getOnlineSpecialResult();
			if (info != null) {
				Log.e(TAG, "info: " + info.more);
			}
			int first = pq.getCurrentPageFirstResultIndex();
			int last = pq.getCurrentPageLastResultIndex();
			for (int i = first; i <= last; i++) {
				mItems.add(pq.getResultAsPoiFavoriteInfo(i));
			}
			mListView.setAdapter(new PoiDetialAdapter(SearchBusActivity.this));
			mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					PoiFavoriteInfo poiFavoriteInfo = mItems.get(arg2);
					if (queryFrom == NaviSetting.FROM_BUS_SEARCH || queryFrom == NaviSetting.FROM_NAVI_SEARCH) {
						Intent intent = new Intent();
						intent.putExtra("name", poiFavoriteInfo.fav.name);
						intent.putExtra("poiX", poiFavoriteInfo.fav.pos.x);
						intent.putExtra("poiY", poiFavoriteInfo.fav.pos.y);
						intent.putExtra("cityid", mCityId);
						setResult(RESULT_OK, intent);
						finish();
					}
				}
			});
			// }
			if (pq.getTotalResultNumber() > 10) {
				mPageLayout.setVisibility(View.VISIBLE);
			}
			mNextPage.setEnabled(pq.hasNextPage());
			mPrevPage.setEnabled(pq.getCurrentPageIndex() != 0);
		} else {
			mPageLayout.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 接收选择信息
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			String name = data.getStringExtra("name");
			Integer ids = data.getIntExtra("id", 0);
			if (requestCode == 1) {
				// 获取返回的城市名称和城市id
				mCityEditText.setText(name);
				if (ids != WmrObject.INVALID_ID) {
					// 设置搜索城市id
					PoiQuery.getInstance().setWmrId(ids);
					mCityId = ids;
				}
			}
		}

	}
	
	private class PoiDetialAdapter extends BaseAdapter {

		private LayoutInflater mLayoutInflater = null;

		public PoiDetialAdapter(Context context) {
			super();
			mLayoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (mItems != null) {
				return mItems.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mItems != null) {
				return mItems.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (mItems == null) {
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
			title.setText(mItems.get(position).fav.name);
			text.setText(mItems.get(position).fav.address + "(" + mItems.get(position).distance + "米)" + mItems.get(position).fav.typeName);
			icon.setText(position + 1 + "");

			return convertView;
		}
	}

	/**
	 * 加载等待
	 */
	public void waitDialog(String msg, boolean flag) {
		myDialog = new ProgressDialog(this);
		myDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		myDialog.setMessage(msg);
		myDialog.setIndeterminate(false);
		myDialog.setCancelable(flag);
		myDialog.show();
	}

	/**
	 * 关闭等等
	 */
	public void disDialog() {
		if (myDialog != null) {
			myDialog.dismiss();
		}
	}
}
