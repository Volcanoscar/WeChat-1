package com.tcl.wechat;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class WeChatApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		//初始化图片加载器
		initImageLoader(getApplicationContext());
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
