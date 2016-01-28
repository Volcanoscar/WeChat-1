package com.tcl.wechat.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.tcl.wechat.R;
import com.tcl.wechat.model.WeiXinMessage;
import com.tcl.wechat.utils.WeixinToast;

/**
 * 百度地图显示位置信息
 * @author rex.lei
 *
 */
public class BaiduMapActivity extends Activity {
	
	private Context mContext;
	// 城市名称
	private TextView mCityNameTv = null;
	// 百度地图控件  
    private MapView mMapView = null;  
    // 百度地图对象  
    private BaiduMap mBaiduMap;  
    
    // 位置信息
    private LatLng mLatLng;
    private String mAddress ; 
    
    private BaiduSDKReceiver mBaiduReceiver;
    
    public LocationClient mLocationClient = null;
	
	public MapLocationListenner locationListenner = new MapLocationListenner();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_mapview);
		
		mContext = BaiduMapActivity.this;
		
		initData();
		initView();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();
	}

	private void initData() {
		// TODO Auto-generated method stub
		Bundle bundle = getIntent().getExtras();
		if (bundle != null){
			WeiXinMessage recorder = bundle.getParcelable("WeiXinMsgRecorder");
			if (recorder == null){
				return ;
			}
			double latx = Double.parseDouble(recorder.getLocation_x());
			double laty = Double.parseDouble(recorder.getLocation_y());
			mAddress = recorder.getLabel();
			if (TextUtils.isEmpty(mAddress)){
				mAddress = getResources().getString(R.string.unknown_location);
			}
			mLatLng = new LatLng(latx, laty);
		}
		
	}

	private void initView() {
		mCityNameTv = (TextView) findViewById(R.id.tv_city);
		mMapView = (MapView) findViewById(R.id.view_mapview);
		mBaiduMap = mMapView.getMap();
		
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);
		
		mMapView = new MapView(this,
				new BaiduMapOptions().mapStatus(new MapStatus.Builder()
						.target(mLatLng).build()));
		
		showMap(mLatLng.latitude, mLatLng.longitude, mAddress);
		
		//获取当前城市名称
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener(locationListenner );
		setLocationOption();
		mLocationClient.start();
		
		// 注册 SDK 广播监听者
		IntentFilter filter = new IntentFilter();
		filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mBaiduReceiver = new BaiduSDKReceiver();
		registerReceiver(mBaiduReceiver, filter);
	}
	
	
	private void showMap(double latitude, double longtitude, String address) {
		LatLng llA = new LatLng(latitude, longtitude);
		CoordinateConverter converter = new CoordinateConverter();
		converter.coord(llA);
		converter.from(CoordinateConverter.CoordType.COMMON);
		LatLng convertLatLng = converter.convert();
		OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory
				.fromResource(R.drawable.icon_location))
				.title(mAddress)
				.zIndex(4).draggable(true);
		mBaiduMap.addOverlay(ooA);
		OverlayOptions textOption = new TextOptions()
									.bgColor(0xAAFFFF00)    
									.fontSize(24)    
									.fontColor(0xFFFF00FF)    
									.text(mAddress)    
									.rotate(0)    
									.position(convertLatLng);
		mBaiduMap.addOverlay(textOption);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
		mBaiduMap.animateMapStatus(u);
	}
	
	//设置相关参数
	private void setLocationOption(){
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); //打开gps
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setPoiExtraInfo(true);
		option.setAddrType("all");
		option.setPriority(LocationClientOption.NetWorkFirst);
		option.setPriority(LocationClientOption.GpsFirst);
		option.setPoiNumber(10);
		option.disableCache(true);
		mLocationClient.setLocOption(option);
	}
	
	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class BaiduSDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				WeixinToast.makeText(mContext, R.string.key_error).show();
			} else if (action.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				WeixinToast.makeText(mContext, R.string.network_not_available).show();
			}
		}
	}
	
	public class MapLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			mCityNameTv.setText(location.getCity());
		}
	
		@Override
		public void onReceivePoi(BDLocation arg0) {
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mBaiduReceiver);
		mLocationClient.stop();
		mBaiduMap.setMyLocationEnabled(false);  
        mMapView.onDestroy();  
        mMapView = null;  
	}
}
