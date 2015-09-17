package com.tcl.wechat.service;

import android.app.IntentService;
import android.content.Intent;

/**
 * 下载服务类
 * @author rex.lei
 *
 */
public class DownLoadService extends IntentService{
	
	public DownLoadService() {
		super("ServiceDownloader");
	}
	
	/**
	 * 下载
	 */
	private void download() {
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String type = intent.getType();
		if ("QrImage".equals(type)){
			
		} else if ("BinderUserImage".equals(type)){
			
		}
	}
}
