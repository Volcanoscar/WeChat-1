package com.tcl.wechat.controller;

import android.graphics.Bitmap;
import android.os.Handler;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tcl.wechat.R;

/**
 * 消息下载器
 * @author rex.lei
 *
 */
public class MsgImageLoader {

	/**
	 * 消息下载器实例对象
	 * @author rex.lei
	 *
	 */
	private static class MsgImageLoaderInstance{
		private static final MsgImageLoader mInstance = new MsgImageLoader();
	}
	
	private MsgImageLoader() {
		super();
	}

	public static MsgImageLoader getInstance(){
		return MsgImageLoaderInstance.mInstance;
	}
	
	
	public void loadImage(String url){
		this.loadImage(url, null);
	}
	
	/**
	 * 下载图片资源
	 * @param url
	 * @param handler
	 */
	public void loadImage(String url, Handler handler){
		DisplayImageOptions options = null ;
		options = new DisplayImageOptions.Builder()
         	.showImageOnFail(R.drawable.load_error)
         	.showImageForEmptyUri(R.drawable.load_error)
         	.showStubImage(R.drawable.load_error)
         	.cacheInMemory()
         	.cacheOnDisc()
         	.bitmapConfig(Bitmap.Config.RGB_565)
         	.handler(handler)
         	.build();
		
		if (options != null){
			ImageLoader.getInstance().loadImage(url, options, null);
		} else {
			ImageLoader.getInstance().loadImage(url, null);
		}
	}
	
}
