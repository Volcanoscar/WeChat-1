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

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.action.player.DownloadManager;
import com.tcl.wechat.common.WeiConstant.CommandReturnType;
import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.common.WeiConstant.SystemShared;
import com.tcl.wechat.controller.DownLoadProxy;
import com.tcl.wechat.controller.WeiXinMsgManager;
import com.tcl.wechat.controller.listener.LoginStateListener;
import com.tcl.wechat.db.WeiQrDao;
import com.tcl.wechat.db.WeiUserDao;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.utils.BaseUIHandler;
import com.tcl.wechat.utils.SystemInfoUtil;
import com.tcl.wechat.utils.SystemShare.SharedEditer;
import com.tcl.wechat.xmpp.InitFinish;
import com.tcl.wechat.xmpp.WeiXmppCommand;
import com.tcl.wechat.xmpp.WeiXmppManager;
import com.tcl.wechat.xmpp.WeiXmppService;

/**
 * 微信登录成功监听器
 * @author rex.lei
 *
 */
public class WeiXinLoginReceiver extends BroadcastReceiver{

	private static final String TAG = WeiXinLoginReceiver.class.getSimpleName();
	
	private Context mContext;
	
	private int mRequestCnt = 0;
	
	private SharedEditer mEditer ;
	
	private CommandHandler mCommandHandler = new CommandHandler();
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		Log.i(TAG, "Received login successfully Broadcast!");
		
		mContext = context;
		mEditer = new SharedEditer();
		
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
		
		new WeiXmppCommand(mCommandHandler, CommandType.COMMAND_GET_BINDER, null).execute();
	}
	
	/**
	 * 执行获取网络二维码命令
	 */
	private void exeCmdToGetNetQr(){
		String qrUrl = WeiQrDao.getInstance().getQrUrl();
		if (TextUtils.isEmpty(qrUrl)){
			new WeiXmppCommand(mCommandHandler, CommandType.COMMAND_GET_QR, null).execute();
		}
	}
	
	/**
	 * 上报终端信息
	 */
	private void exeCmdToReportTerminalInfo(){
		mEditer = new SharedEditer(SystemShared.SHARE_TERMINAL_INFO);
		String localIp = mEditer.getString("localip", "");
		String curIp = SystemInfoUtil.getLocalIpAddress();
		if (!curIp.equals(localIp)){
			mEditer.putString("localip", curIp);
			final HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("deviceid",SystemInfoUtil.getDeviceId());
			hashMap.put("dnum",SystemInfoUtil.getDnum());
			hashMap.put("huanid",SystemInfoUtil.getHuanid());
			hashMap.put("lanip",curIp);
			hashMap.put("messageboxid","0000000000000000");
			hashMap.put("mac",SystemInfoUtil.getMacAddr());
			hashMap.put("version", SystemInfoUtil.get_apkver());
			new WeiXmppCommand(mCommandHandler, CommandType.COMMAND_REPORT_DEVICEINFO, hashMap).execute();
		}
	}
	
	/**
	 * 上报TV状态
	 */
	private void exeCmdToReportTvStatus(HashMap<String, String> tvStatus){
		new WeiXmppCommand(null, CommandType.COMMAND_RESPONSETVSTATUS, tvStatus).execute();
	}
	
	/**
	 * 监听Command执行状态，如果超时，则直接进入
	 */
	@SuppressLint("HandlerLeak") 
	private class CommandHandler extends BaseUIHandler{
		
		@Override
		public void handleMessage(Message msg) {
			
			Log.d(TAG, "CommandHandler receive:" + msg.what 
					+ ", status:" + getStatus());
			
			switch (msg.what) {
			case CommandType.COMMAND_GET_BINDER:
				//获取用户列表成功
				if (CommandReturnType.STATUS_SUCCESS.equals(getStatus())){
					saveBindUser();
				} else {
					mRequestCnt ++;
					if (mRequestCnt == 2){//事务处理完成
						commandCompleted();
					}
				}
				//成功获取到用户列表后，再通知服务器，这时候可以接收离线消息了。TV掉线了，扫描绑定。这时候发很多离线消息。下次重启登录，还没有获取到列表之前，消息里面的用户会有很多NULL					
				//发送接收离线消息
				InitFinish initfinish = new InitFinish( WeiXmppManager.getInstance().getConnection(), null);	
				initfinish.sentPacket();
				break;
				
			case CommandType.COMMAND_GET_QR:
				if (CommandReturnType.STATUS_SUCCESS.equals(getStatus())){
					saveQr();
				}
				mRequestCnt ++;
				if(mRequestCnt == 2 ){//事务处理完成
					commandCompleted();
				}
				break;
				
			case CommandType.COMMAND_GET_TVSTATUS:
				HashMap<String, String> tvStatus = new HashMap<String, String>();			
				tvStatus = (HashMap<String, String>) msg.obj;
				exeCmdToReportTvStatus(tvStatus);
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 保存用户信息
	 */
	private void saveBindUser(){

		final ArrayList<BindUser> usrsList = (ArrayList<BindUser>) mCommandHandler.getData();
		 
		//TODO 再次检测用户数据书否更新，如果没更新，则做存储操作
		
		if (usrsList == null || usrsList.isEmpty()){
			return ;
		}
		 
		if (WeiUserDao.getInstance().addUserList(usrsList)){
			SharedEditer editer = new SharedEditer();
			editer.putBoolean(SystemShared.KEY_REGISTENER_SUCCESS, true);
		} else {
			Log.e(TAG, "addUserList ERROR!!");
			return ;
		}
		 
		//下载更新用户头像
		DownLoadProxy.getInstanc().startDownloadUserHeadIcon(usrsList);
		
	}
	
	/**
	 * 保存二维码信息
	 */
	private void saveQr(){
		final String url = (String)mCommandHandler.getData();
		Log.i(TAG, "[saveQr]url:" + url);
		if(url != null){
			String qrUrl = WeiQrDao.getInstance().getUrl();
			if (TextUtils.isEmpty(qrUrl)){
				if (!WeiQrDao.getInstance().updateUrl(url) ){
					Log.e(TAG, "updateQr ERROR!!");
					return ;
				}
			}
			//下载更新二维码
//			DownLoadProxy.getInstanc().startDownloadQr(qrUrl);
			DownloadManager.getInstace().startToDownload(qrUrl, "image");
		 }
	}
	
	/**
	 * 命令执行完成
	 */
	private void commandCompleted(){
		if (SystemInfoUtil.isTopActivity()){
			enter();
		} else {
			logout();
		}
	}
	
	/**
	 * 登录成功进入应用
	 */
	private void enter(){
		//1、已经超时进入主界面：发送广播通知应用更新主界面
		//2、没有超时：直接通知进入
		if (!mEditer.getBoolean(SystemShared.KEY_FLAG_ENTER, false)){
			mEditer.putBoolean(SystemShared.KEY_FLAG_ENTER, true);
			LoginStateListener mListener = WeiXinMsgManager.getInstance().getLoginStateListener();
			if (mListener != null){
				mListener.onLoginSuccess();
			}
		}
	}
	
	/**
	 * 退出
	 */
	private void logout(){
		Intent serviceIntent = new Intent(mContext, WeiXmppService.class);      	 			
    	mContext.stopService(serviceIntent);
	}
}
