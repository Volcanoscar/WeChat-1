package com.tcl.wechat.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.tcl.wechat.R;
import com.tcl.wechat.model.WeiXinMsgRecorder;

/**
 * 百度地图显示位置信息
 * @author rex.lei
 *
 */
public class BaiduMapActivity extends Activity {

	// 百度地图控件  
    private MapView mMapView = null;  
    // 百度地图对象  
    private BaiduMap mBaiduMap;  
    
    // 位置信息
    private float latx = 0.0f;  
    private float laty = 0.0f;  
    private String lableName = ""; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_mapview);
		
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
			WeiXinMsgRecorder recorder = bundle.getParcelable("WeiXinMsgRecorder");
			if (recorder == null){
				return ;
			}
			latx = Float.parseFloat(recorder.getLocation_x());
			laty = Float.parseFloat(recorder.getLocation_y());
			lableName = recorder.getLabel();
		}
		
	}

	private void initView() {
		mMapView = (MapView) findViewById(R.id.view_mapview);  
		mBaiduMap = mMapView.getMap();
		
		initMyLocation() ;  
  
        //普通地图    
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);   
        //开启交通图     
        mBaiduMap.setTrafficEnabled(true);  
          
        //定义Maker坐标点    
        LatLng point = new LatLng(latx, laty);    
        
        //定义文字所显示的坐标点    
        LatLng llText = new LatLng(latx, laty);    
        //构建文字Option对象，用于在地图上添加文字    
        OverlayOptions textOption = new TextOptions()    
        			.bgColor(0xAAFFFF00)    
        			.fontSize(22)    
        			.fontColor(0xFFFF00FF)    
        			.text(lableName)    
        			.rotate(0)    
        			.position(llText);   
        
        //构建Marker图标    
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_location);    
        //构建MarkerOption，用于在地图上添加Marker    
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);    
        //在地图上添加Marker，并显示    
        mBaiduMap.addOverlay(option); 
        
        //在地图上添加该文字对象并显示    
        mBaiduMap.addOverlay(textOption);
	}
	
	/**
	 * 初始化位置信息
	 */
	private void initMyLocation()    {    
		mBaiduMap.setMyLocationEnabled(true);  
		MyLocationData locData = new MyLocationData.Builder().accuracy(100)    
	         .direction(90.0f)    
	         .latitude(latx)    
	         .longitude(laty).build();   
	   
		float f = mBaiduMap.getMaxZoomLevel();//19.0 最小比例尺  
		mBaiduMap.setMyLocationData(locData);  
		mBaiduMap.setMyLocationEnabled(true);  
		LatLng ll = new LatLng(latx,laty);  
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, f-2);
		mBaiduMap.animateMapStatus(u);  
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
		mBaiduMap.setMyLocationEnabled(false);  
        mMapView.onDestroy();  
        mMapView = null;  
	}
}
