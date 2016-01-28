package com.tcl.wechat;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;

import com.android.http.RequestManager;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.baidu.mapapi.SDKInitializer;
import com.tcl.wechat.action.imageloader.ImageLruCache;
import com.tcl.wechat.database.AppInfoDao;
import com.tcl.wechat.database.DeviceDao;
import com.tcl.wechat.database.WeiQrDao;
import com.tcl.wechat.model.AppInfo;
import com.tcl.wechat.model.DeviceInfo;
import com.tcl.wechat.model.QrInfo;
import com.tcl.wechat.utils.DataFileTools;
import com.tcl.wechat.utils.SystemInfoUtil;
import com.tcl.wechat.xmpp.WeiXmppManager;
import com.tcl.wechat.xmpp.WeiXmppService;
import com.tencent.wechat.AirKissHelper;

public class WeApplication extends Application {

	private static final String TAG = "WeApplication";
	
	private static Context mContext;
	
	private static WeApplication mInstance;
	
	private static ImageLoader mImageLoader;
	
	private static RequestQueue mRequestQueue;

	private static ImageLruCache mImageCache;
	
	private static ExecutorService mExecutorPool;
	
	private Typeface mTypeface1; //字体1
	private Typeface mTypeface2; //字体2
			
	public static Context getContext(){
		return mContext;
	}
	
	public static WeApplication getInstance(){
		return mInstance;
	}
	
	public static ImageLoader getImageLoader() {
		return mImageLoader;
	}
	
	public static RequestQueue getRequestQueue(){
		return mRequestQueue;
	}
	
	public static ImageLruCache getImageLruCache(){
		return mImageCache;
	}
	
	public static ExecutorService getExecutorPool(){
		 return mExecutorPool;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mContext = this;
		mInstance = this;
		mExecutorPool = Executors.newCachedThreadPool();
		
		//百度地图初始化
        SDKInitializer.initialize(mContext);
		
		//初始化设备信息
		initDevice();
		
		//用户注册模块
		initAuth();
		
		//初始化图片加载器
		initImageLoader();
		
		//注册设备
		initAirkissMode();
		
    	//初始化字体
		try {
			mTypeface1 = Typeface.createFromAsset(getAssets(), "fonts/oop.TTF");
			mTypeface2 = Typeface.createFromAsset(getAssets(), "fonts/regular.TTF");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化设备信息，为后面的注册，登录等做准备
	 */
	private void initDevice() {
		initFilePath();
		if (!WeiXmppManager.getInstance().isRegister()){
			
			//初始化二维码数据库
			String uuidStr = WeiQrDao.getInstance().getUUID();
			if (TextUtils.isEmpty(uuidStr) || uuidStr.equalsIgnoreCase("")){
				uuidStr = "";
				UUID uuid = UUID.randomUUID();
				String arrayStr[] = uuid.toString().split("-");
			    for(int i = 0; i < arrayStr.length; i++){
			    	uuidStr = uuidStr + arrayStr[i];
			    }	
			    QrInfo qrInfo = new QrInfo("", uuidStr);
			    if (!WeiQrDao.getInstance().addQr(qrInfo)){
			    	Log.e(TAG, "updateUuid ERROR!!");
			    }
			} 
			
			//初始化设备数据库
			String macAddr = DeviceDao.getInstance().getMACAddr();
			String deviceId = DeviceDao.getInstance().getDeviceId();
			if (TextUtils.isEmpty(macAddr) || macAddr.equalsIgnoreCase("") ||
					TextUtils.isEmpty(deviceId) || deviceId.equalsIgnoreCase("")){
				macAddr = SystemInfoUtil.getLocalMacAddress();
				deviceId = SystemInfoUtil.getSerialNumber();//使用序列号
				DeviceInfo deviceInfo = new DeviceInfo(deviceId, macAddr, null);
				if (!DeviceDao.getInstance().addDeviceInfo(deviceInfo)){
					Log.e(TAG, "addDeviceInfo ERROR!!");
				}
			}
			
			//将预安装的apk的名称写入数据库
			AppInfo appInfo = AppInfoDao.getInstance().getAppInfo();
			if (appInfo == null){
				appInfo = SystemInfoUtil.getAppInfo();
				if (appInfo != null){
					if (!AppInfoDao.getInstance().addAppInfo(appInfo)){
						Log.e(TAG, "addAppInfo ERROR!!");
					}
				}
			}
		}
			
	}
	
	/**
	 * 文件目录初始化
	 */
	private void initFilePath(){
		File audioPath = new File(DataFileTools.getRecordAudioPath());
		if (!audioPath.exists()){
			audioPath.mkdirs();
		}
		
		File videoPath = new File(DataFileTools.getRecordVideoPath());
		if (!videoPath.exists()){
			videoPath.mkdirs();
		}
		
		File imagePath = new File(DataFileTools.getRecordImagePath());
		if (!imagePath.exists()){
			imagePath.mkdirs();
		}
		
		File filePath = new File(DataFileTools.getRecordFilePath());
		if (!filePath.exists()){
			filePath.mkdirs();
		}
		
		File tempFile = new File(DataFileTools.getTempPath());
		if (!tempFile.exists()){
			tempFile.mkdirs();
		}
	}
	
	/**
	 * 用户注册
	 * 	 启动服务，进行用户注册
	 * 	
	 */
	private void initAuth() {
		Intent serviceIntent = new Intent(this, WeiXmppService.class);
		//serviceIntent.putExtra("startmode", StartServiceMode.OWN);
		//serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startService(serviceIntent);
	}

	/**
	 * 初始化图片加载器
	 */
	private void initImageLoader() {
		RequestManager.getInstance().init(mContext);
		mRequestQueue = RequestManager.getInstance().getRequestQueue();
		mImageCache = new ImageLruCache();
		mImageLoader = new ImageLoader(mRequestQueue, mImageCache);
	}
	
	/**
	 * 注册设备（注入Airkiss模式）
	 */
	private void initAirkissMode(){
		
		String memberId = DeviceDao.getInstance().getMemberId();
		
		if (!TextUtils.isEmpty(memberId)){
			AirKissHelper.getInstance().start(memberId);
		}
	}

	/**
	 * 字体：娃娃体
	 */
	public Typeface getTypeface1() {
		return mTypeface1;
	}
	
	/**
	 * 字体：楷体
	 */
	public Typeface getTypeface2() {
		return mTypeface2;
	}
	
	static {
		System.loadLibrary("airkiss3");
	}
	
	
}
