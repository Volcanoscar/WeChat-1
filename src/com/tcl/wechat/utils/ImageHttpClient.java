/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.tcl.wechat.common.WeiConstant.CommandReturnType;
import com.tcl.wechat.common.WeiConstant.CommandType;


/**
 * @ClassName: ImageHttpClient
 */

public class ImageHttpClient {
	private BaseUIHandler hander = null;
	private String uri = null;
	private Bitmap bitmap = null;
	public ImageHttpClient(String uri,BaseUIHandler hander){
		this.hander = hander;
		this.uri = uri;
	}
	
	public void start(){
		
		new Thread(new Runnable() {

			@SuppressLint("NewApi")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				 for (int i = 0 ; i < 3 ;i++){
					 InputStream is = null;
					 URL imageUrl = null;
					 HttpURLConnection conn = null;
					 try {
						    Log.d("ImageHttpClient", "开始获取原图uri="+uri);
						  	if (uri == null){
						  		return;
						  	}
						  	//String redirectUrl = HttpsUtils.redirct(uri);
							imageUrl = new URL(uri);
							conn = (HttpURLConnection) imageUrl.openConnection();
							conn.setConnectTimeout(10000);
							conn.setReadTimeout(10000);
							is = conn.getInputStream();
							bitmap = BitmapFactory.decodeStream(is);
							Log.d("ImageHttpClient", "bitmap.getByteCount()="+bitmap.getByteCount());
							 if (bitmap != null && bitmap.getByteCount()>0){
								 Log.d("ImageHttpClient", "第"+i+"次加载图片");
								 hander.setStatus(CommandReturnType.STATUS_SUCCESS);
								 hander.setData(bitmap);
								 hander.sendEmptyMessage(CommandType.COMMAND_GET_ICON_PIC);
								
								 return;
							 }
							
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							Log.d("junjian", "MalformedURLException="+e.getMessage());
							if (conn != null){
								conn.disconnect();
							}
							bitmap = null;
						}catch (IOException e1) {
							// TODO Auto-generated catch block
							if (conn != null){
								conn.disconnect();
							}
							Log.d("junjian", "IOException="+e1.getMessage());
							e1.printStackTrace();
							bitmap = null;
						}
				 }
				 if (bitmap == null){
					 hander.setStatus(CommandReturnType.STATUS_FAILED);
					 hander.sendEmptyMessage(CommandType.COMMAND_GET_ICON_PIC);
				 }
				
			}
		}).start();
	}
}
