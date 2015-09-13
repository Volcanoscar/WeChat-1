package com.tcl.wechat;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.controller.DatabaseController;
import com.tcl.wechat.xmpp.WeiXmppService;

/**
 * WeChatApplication
 * @author rex.lei
 *
 */
public class WeChatApplication extends Application {

	public static Context gContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		gContext = this;
		
		//初始化数据库
		initDateBase();

		//用户注册模块
		initAuth();
		
		//初始化图片加载器
		initImageLoader(getApplicationContext());
	}

	/**
	 * 初始化数据库
	 */
	private void initDateBase() {
		DatabaseController.getController(getApplicationContext()).initDataBase();
	}
	
	/**
	 * 用户注册
	 * 	 启动服务，进行用户注册
	 * 	
	 */
	private void initAuth() {
		Intent serviceIntent = new Intent(this, WeiXmppService.class);
		serviceIntent.putExtra("startmode", WeiConstant.StartServiceMode.OWN);
		serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startService(serviceIntent);
	}

	/**
	 * 初始化图片加载器
	 * @param context
	 */
	private void initImageLoader(Context context) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        	.showImageOnFail(R.drawable.ic_launcher).showImageForEmptyUri(R.drawable.ic_launcher)
        	.showStubImage(R.drawable.ic_launcher).cacheInMemory().cacheOnDisc()
        	.bitmapConfig(Bitmap.Config.ARGB_8888).build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
        	.defaultDisplayImageOptions(options).threadPriority(Thread.MIN_PRIORITY)
        	.discCacheSize(50 * 1024 * 1024).threadPoolSize(10).build();
		ImageLoader.getInstance().init(config);
	}
}
