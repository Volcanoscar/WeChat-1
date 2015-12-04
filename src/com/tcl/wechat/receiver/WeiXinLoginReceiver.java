/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.receiver;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.WeApplication;
import com.tcl.wechat.common.IConstant.CommandAction;
import com.tcl.wechat.common.IConstant.EventReason;
import com.tcl.wechat.common.IConstant.EventType;
import com.tcl.wechat.common.IConstant.SystemShared;
import com.tcl.wechat.database.WeiQrDao;
import com.tcl.wechat.database.WeiUserDao;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.utils.SystemInfoUtil;
import com.tcl.wechat.utils.SystemShare.SharedEditer;
import com.tcl.wechat.xmpp.WeiXmppCommand;
import com.tcl.wechat.xmpp.XmppEvent;
import com.tcl.wechat.xmpp.XmppEventListener;

/**
 * 微信登录成功监听器
 * @author rex.lei
 *
 */
public class WeiXinLoginReceiver extends BroadcastReceiver {

	private static final String TAG = WeiXinLoginReceiver.class.getSimpleName();
	
	private int mRequestCnt = 0;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		Log.i(TAG, "Received login successfully Broadcast!");
		
		//1、执行获取绑定用户命令
		exeCmdToGetBindUser();
		
		//2、执行获取二维码信息命令
		exeCmdToGetNetQr();
		
		//3、 上报终端信息
		exeCmdToReportTerminalInfo();
	}
	

	/**
	 * 执行获取绑定用户列表命令
	 */
	private void exeCmdToGetBindUser(){
		
		new WeiXmppCommand(EventType.TYPE_GET_BINDUSER, null, mListener).execute();
	}
	
	/**
	 * 执行获取网络二维码命令
	 */
	private void exeCmdToGetNetQr(){
		new WeiXmppCommand(EventType.TYPE_GET_QR, null, mListener).execute();
	}
	
	/**
	 * 上报终端信息
	 */
	private void exeCmdToReportTerminalInfo(){
		SharedEditer editer = new SharedEditer(SystemShared.SHARE_TERMINAL_INFO);
		String localIp = editer.getString("localip", "");
		String curIp = SystemInfoUtil.getLocalIpAddress();
		if (!curIp.equals(localIp)){
			editer.putString("localip", curIp);
			final HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("deviceid",SystemInfoUtil.getDeviceId());
			hashMap.put("lanip", curIp);
			hashMap.put("messageboxid", "0000000000000000");
			hashMap.put("mac", SystemInfoUtil.getLocalMacAddress());
			hashMap.put("version", SystemInfoUtil.getVersionName());
			new WeiXmppCommand(EventType.TYPE_REPORT_DEVICEINFO, hashMap, mListener).execute();
		}
	}
	
	private XmppEventListener mListener = new XmppEventListener() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void onEvent(XmppEvent event) {
			// TODO Auto-generated method stub
			switch (event.getType()) {
			case EventType.TYPE_GET_QR:
				if (event.getReason() == EventReason.REASON_COMMON_SUCCESS){
					String url = (String) event.getEventData();
					saveQr(url);
					mRequestCnt ++;
					if(mRequestCnt == 2 ){//事务处理完成
						commandCompleted();
					}
				} 
				
				break;
				
			case EventType.TYPE_GET_BINDUSER:
				if (event.getReason() == EventReason.REASON_COMMON_SUCCESS){
					ArrayList<BindUser> users = (ArrayList<BindUser>) event.getEventData();
					saveBindUser(users);
					mRequestCnt ++;
					if (mRequestCnt == 2){//事务处理完成
						commandCompleted();
					}
				} 
				
				/**
				 * fix by rex.lei 2015-10-09
				 * 修改为在登录成功后，直接进行监听
				 */
				//发送接收离线消息
				//InitFinish initfinish = new InitFinish(WeiXmppManager.getInstance().getConnection(), null);	
				//initfinish.sentPacket();
				break;
			default:
				break;
			}
		}
	};
	
	
	/**
	 * 保存用户信息
	 * @param usrsList 绑定用户列表
	 */
	private void saveBindUser(ArrayList<BindUser> usrsList){

		if (usrsList == null || usrsList.isEmpty()){
			return ;
		}
		Log.i(TAG, "saveBindUser usrsList:" + usrsList.toString());
		if (!WeiUserDao.getInstance().addUserList(usrsList)){
			Log.e(TAG, "addUserList ERROR!!");
		}
	}
	
	/**
	 * 保存二维码信息
	 * @param qrUrl
	 */
	private void saveQr(String qrUrl){
		Log.i(TAG, "saveQr Url:" + qrUrl);
		if (!TextUtils.isEmpty(qrUrl)){
			if (!WeiQrDao.getInstance().updateUrl(qrUrl) ){
				Log.e(TAG, "updateQr ERROR!!");
				return ;
			}
		}
	}
	
	/**
	 * 命令执行完成
	 */
	private void commandCompleted(){
		mRequestCnt = 0;
		
		//发送广播通知更新
		Intent intent = new Intent(CommandAction.ACTION_MSG_USER);
		WeApplication.getContext().sendBroadcast(intent);
	}
}
