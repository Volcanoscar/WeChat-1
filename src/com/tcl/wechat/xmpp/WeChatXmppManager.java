package com.tcl.wechat.xmpp;

import java.util.UUID;

import android.content.Context;
import android.util.Log;

import com.tcl.wechat.db.DeviceDao;
import com.tcl.wechat.db.QrDao;

/**
 * 微信协议管理类
 * @author rex.lei
 *
 */
public class WeChatXmppManager {
	
	private static final String TAG = "WeChatXmppManager";
	
	private DeviceDao mDeviceDao;
	
	private static class WeChatXmppManagerInstace{
		private static final WeChatXmppManager mInstance = new WeChatXmppManager();
	}
	
	public WeChatXmppManager() {
		super();
	}

	public WeChatXmppManager getInstance(){
		return WeChatXmppManagerInstace.mInstance;
	}
	
	/**
	 * 实例化设备数据库
	 */
	public void initDeviceDao(Context context){
		
		String deviceid = null;
		String uuid = null;
		
		mDeviceDao = new DeviceDao(context);
		deviceid = mDeviceDao.getDeviceId();
		Log.i(TAG, "deviceid:" + deviceid);
		
		//判断当前用户是否生有用户号，如有，代表老用户，uuid置为空，否则生成uuid并保存数据库；
		if (deviceid != null && !deviceid.equalsIgnoreCase("")){
//			WeiXmppManager.getInstance().setMemberid(deviceid);
		}else{
			QrDao weiQrDao = new QrDao(context);
			uuid = weiQrDao.getQrUUID();
			if(uuid.equalsIgnoreCase("")){
				UUID.randomUUID();
				String arrayStr[] = uuid.toString().split("-");
			    for(int i=0; i<arrayStr.length; i++){
			    	uuid = uuid + arrayStr[i];
			    }	
				weiQrDao.updateUUID(uuid);
			}			
		}
	}
}
