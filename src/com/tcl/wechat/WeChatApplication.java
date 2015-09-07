package com.tcl.wechat;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tcl.wechat.xmpp.WeChatXmppManager;

public class WeChatApplication extends Application {
	
	private Context mContext;
	//第一次进入应用
	private static final String PREF_FIRST_TIME = "first_time_pref";
	
	private WeChatXmppManager mXmppManager;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mContext = getApplicationContext();
		mXmppManager = WeChatXmppManager.getInstance();
		
		//用户认证模块
		initAuth();
		
		//初始化图片加载器
		initImageLoader(getApplicationContext());
	}

	/**
	 * 用户认证
	 */
	private void initAuth() {
		SharedPreferences preferences = mContext.getSharedPreferences(
				PREF_FIRST_TIME, Context.MODE_PRIVATE);
		Boolean bFirstEnter = preferences.getBoolean("isFirst", false);
		/**
		 * 启动Application，判断是否是第一次进入应用
		 * 	是：进行用户注册-->登录
		 * 	否：直接登录
		 */
		if (bFirstEnter){
			mXmppManager.initDeviceDao(mContext);
		}
		
	}
	
	/**
	 * 
	 */
	private void startXmppService(){
		
	}


	/**
	 * 初始化图片加载器
	 * @param context
	 */
	private void initImageLoader(Context context) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        	.showImageOnFail(R.drawable.ic_launcher).showImageForEmptyUri(R.drawable.ic_launcher)
        	.showStubImage(R.drawable.ic_launcher).cacheInMemory(true).cacheOnDisc(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
        	.defaultDisplayImageOptions(options).threadPriority(Thread.MIN_PRIORITY)
        	.discCacheSize(10 * 1024 * 1024).threadPoolSize(10).build();
		ImageLoader.getInstance().init(config);
	}
	

}
