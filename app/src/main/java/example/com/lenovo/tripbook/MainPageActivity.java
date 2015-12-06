package example.com.lenovo.tripbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainPageActivity extends Activity{
	private SlidingLayout slidingLayout;

	private Button face;
	private Button changeAccount;
	private Button exit;

	private ListView timeline;
	private List<TimeLineModel> list;
	private TimeLineAdapter adapter;
	private ImageView glass;
	private ImageButton arrow;
	private CircleButton circle;

	private boolean top=false;
	private float x1 = 0;
	private float x2 = 0;
	private float y1 = 0;
	private float y2 = 0;

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private MyLocationConfiguration.LocationMode mCurrentMode;

	MapView mMapView;
	BaiduMap mBaiduMap;
	boolean isFirstLoc = true;// 是否首次定位
	BitmapDescriptor mCurrentMarker=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(this.getApplicationContext());
		setContentView(R.layout.activity_mainpage);

		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));// 传入null则，默认图标

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();


		slidingLayout = (SlidingLayout) findViewById(R.id.sliding);
		timeline = (ListView) findViewById(R.id.timeline);
		timeline.setDivider(null);
		slidingLayout.setScrollEvent(timeline);
		initData();
		initView();
		face = (Button) findViewById(R.id.face);
		face.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i=new Intent();
				i.setClass(MainPageActivity.this,UserActivity.class);
				startActivity(i);
				finish();
			}
		});
		changeAccount = (Button) findViewById(R.id.changeAccount);
		changeAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(MainPageActivity.this, "功能开发中", Toast.LENGTH_SHORT).show();
			}
		});
		exit = (Button) findViewById(R.id.exit);
		exit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				System.exit(0);
			}
		});
		glass = (ImageView) findViewById(R.id.glass);
		glass.setAlpha(180);
		arrow = (ImageButton) findViewById(R.id.arrow);
		circle= (CircleButton) findViewById(R.id.circleButton);
		arrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(top) {
					top=false;
					FrameLayout.LayoutParams lp1=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					FrameLayout.LayoutParams lp2=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lp1.setMargins(0, face.getHeight() * 9, 0, 0);
					glass.setLayoutParams(lp1);
					timeline.setLayoutParams(lp1);
					lp2.setMargins(face.getHeight() * 34 / 5, face.getHeight() * 9, 0, 0);
					arrow.setLayoutParams(lp2);
					arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up));
					timeline.setSelection(0);
					circle.setVisibility(View.VISIBLE);
				}else{
					top=true;
					FrameLayout.LayoutParams lp1=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					FrameLayout.LayoutParams lp2=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					lp1.setMargins(0, 0, 0, 0);
					glass.setLayoutParams(lp1);
					timeline.setLayoutParams(lp1);
					lp2.setMargins(face.getHeight() * 34 / 5, 0, 0, 0);
					arrow.setLayoutParams(lp2);
					arrow.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down));
					circle.setVisibility(View.GONE);
				}
			}
		});
	}

//	@Override
//	public boolean dispatchTouchEvent(MotionEvent event) {
//		if(event.getAction() == MotionEvent.ACTION_DOWN) {
//			//当手指按下的时候
//			x1 = event.getX();
//			y1 = event.getY();
//		}
//		if(event.getAction() == MotionEvent.ACTION_UP) {
//			//当手指离开的时候
//			x2 = event.getX();
//			y2 = event.getY();
//			if(y1 - y2 > 50) {
//				mMapView.setAlpha(1);
//				glass.setPadding(0,0,0,0);
//				timeline.setPadding(0,0,0,0);
//			} else if(y2 - y1 > 50) {
//				if(timeline.getFirstVisiblePosition()==0){
//					mMapView.setAlpha(1);
//					glass.setPadding(0, face.getHeight()*9,0,0);
//					timeline.setPadding(0, face.getHeight()*9,0,0);
//					timeline.setSelection(0);
//				}
//			} else if(x1 - x2 > 50) {
//				//Toast.makeText(MainPageActivity.this, "向左滑", Toast.LENGTH_SHORT).show();
//			} else if(x2 - x1 > 50) {
//				//Toast.makeText(MainPageActivity.this, "向右滑", Toast.LENGTH_SHORT).show();
//			}
//		}
//		return super.dispatchTouchEvent(event);
//	}

	private void initView() {
		timeline=(ListView) findViewById(R.id.timeline);
		adapter=new TimeLineAdapter(this,list);
		timeline.setAdapter(adapter);
	}

	private void initData() {
		list=new ArrayList<TimeLineModel>();
		list.add(new TimeLineModel(R.drawable.weather_sunny, "\n     Day1 呼和浩特-内蒙古博物馆 \n     说走就走的旅行\n     北纬40.8° 东经111.8°" +
				"     12352步"));
		list.add(new TimeLineModel(R.drawable.weather_gloomy, "\n     Day2 呼和浩特-昭君墓 \n     匈奴文化详细介绍\n     北纬40.8° 东经111.8°" +
				"     16345步"));
		list.add(new TimeLineModel(R.drawable.weather_rainy, "\n     Day3 额济纳 \n     真荒凉\n     北纬40.8° 东经111.8°" +
				"     6943步"));
		list.add(new TimeLineModel(R.drawable.weather_stormy, "\n     Day4 居延海 \n     司机师傅睡过头了\n     北纬40.8° 东经111.8°" +
				"     5674步"));
		list.add(new TimeLineModel(R.drawable.weather_cloud, "\n     Day5 银川 \n     回乡文化之旅\n     北纬40.8° 东经111.8°" +
				"     12009步"));
		list.add(new TimeLineModel(R.drawable.weather_sunny, "\n     Day6 银川-西夏王陵 \n     旅程最后一天\n     北纬40.8° 东经111.8°" +
				"     20568步"));
		list.add(new TimeLineModel(R.drawable.weather_sunny, "\n     Day6 银川-西夏王陵 \n     旅程最后一天\n     北纬40.8° 东经111.8°" +
				"     20568步"));
		list.add(new TimeLineModel(R.drawable.weather_sunny, "\n     Day6 银川-西夏王陵 \n     旅程最后一天\n     北纬40.8° 东经111.8°" +
				"     20568步"));
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
							// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}
}
