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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.common.WeiConstant.CommandReturnType;
import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.db.WeiQrDao;
import com.tcl.wechat.db.WeiUserDao;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.utils.BaseUIHandler;
import com.tcl.wechat.utils.CommonsFun;
import com.tcl.wechat.xmpp.InitFinish;
import com.tcl.wechat.xmpp.WeiXmppCommand;
import com.tcl.wechat.xmpp.WeiXmppManager;
import com.tcl.wechat.xmpp.WeiXmppService;
//import com.tcl.webchat.homepage.HomePageActivity;
//import com.tcl.xian.StartandroidService.SqlCommon;

/**
 * @ClassName: WeiXinLoginReceiver
 */

public class WeiXinLoginReceiver extends BroadcastReceiver{

	private static final String TAG = WeiXinLoginReceiver.class.getSimpleName();
	
	private Context mContext ;
	private int getData = 0;
	public static Handler mHandler = null;
	private static final String SET_SHARE_STYLE_NAME = "setshare_pref";
	private Handler handler;
	String  topActivityName;//执行关闭service的时候还需要再判断一次微信是否在前台
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG,"receive---WeiXinLoginReceiver");
		mContext = context;
		getData = 0;
		handler = new MyHander();
		//登陸成功后,获取当前绑定的用户，同时更新数据库
		new WeiXmppCommand(new MyHander(), CommandType.COMMAND_GET_BINDER, null).execute();
		//每次都生成一个新的二维码
	     ifGetNetQR();
		
		//读取数据库，文件弹出方式
		SharedPreferences preferences = context.getSharedPreferences(
				SET_SHARE_STYLE_NAME, context.MODE_PRIVATE);
		WeiConstant.SET_SHARE_STYLE = preferences.getBoolean("share", true);
		
		//上报终端信息		
		//读取数据库，本机ip，如果和上次上报的相同则不上报		
		String localip = preferences.getString("localip", "");
		String curip = CommonsFun.getLocalIpAddress();
		if(!localip.equals(curip)){
			Log.i(TAG,"receive---localip="+localip+";curip="+curip);
			Editor editor = preferences.edit();
			editor.putString("localip", curip);
			editor.commit();
			final HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("deviceid",CommonsFun.getDeviceId(mContext));
			hashMap.put("dnum",CommonsFun.getDnum(mContext));
			hashMap.put("huanid",CommonsFun.getHuanid(mContext));
			hashMap.put("lanip",curip);
			hashMap.put("messageboxid","0000000000000000");
			hashMap.put("mac",CommonsFun.getMAC());
			hashMap.put("version", CommonsFun.get_apkver(mContext));
			new WeiXmppCommand(new MyHander(), CommandType.COMMAND_REPORT_DEVICEINFO, hashMap).execute();
		}						
	}
	
	//判断本地数据库是否已经有了网络二维码的地址，有就直接生成一个二维码图片，没有则从网络获取。
		public void ifGetNetQR(){
			 if (WeiQrDao.getInstance().getQr() != null){
				 //	 new QRCodeUtils().createNewQR(qrurl.substring(qrurl.indexOf("ticket=")+7),WeiConstant.CFROM.WeiXin,mContext);
			 } else{
				 new WeiXmppCommand(new MyHander(), CommandType.COMMAND_GET_QR, null).execute();
			 }
		}
		
	/**
	 * 登录获取用户为空，有可能有远程绑定的。10s查询数据库还是没有用户，则推出后台服务
	 */
	private Runnable getUserCountTimeTask = new Runnable() {     
		
		public void run() { 
			topActivityName = CommonsFun.getTopActivityName(mContext);
		    if(WeiUserDao.getInstance().getBindUserNum() == 0 && 
		    		!topActivityName.equals(mContext.getPackageName()) &&
		    		WeiConstant.StartServiceMode.CURRENTMODE.equals(WeiConstant.StartServiceMode.OWN)){
		    	
		    	Intent serviceIntent = new Intent(mContext, WeiXmppService.class);      	 			
		    	mContext.stopService(serviceIntent); 
		    }
		}
	};
	class MyHander extends BaseUIHandler
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case CommandType.COMMAND_GET_TVSTATUS:
					HashMap<String, String> hashMap_tvstatus = new HashMap<String, String>();			
					hashMap_tvstatus = (HashMap<String, String>) msg.obj;
					new WeiXmppCommand(null, CommandType.COMMAND_RESPONSETVSTATUS, hashMap_tvstatus).execute();
					break;
				case CommandType.COMMAND_GET_BINDER:
					 Log.i(TAG,"COMMAND_GET_BINDER");
					 if (this.getStatus().equals(CommandReturnType.STATUS_SUCCESS)){
						 ArrayList<BindUser> usrsList = (ArrayList<BindUser>)this.getData();
						 for(int i = 0; i< usrsList.size(); i++){
							 Log.i(TAG,"files.get(0).headUrl=" + usrsList.get(i).getHeadimageurl());
						 }
						 Log.d(TAG, "当前绑定微信用户大小="+ usrsList.size());
						 WeiUserDao.getInstance().addUserList(usrsList);
					     //没有人绑定且当前应用不在前台，则关闭后台服务。
						 if(usrsList.size() == 0){
							 topActivityName = CommonsFun.getTopActivityName(mContext);
							 Log.i(TAG, "topActivityName="+topActivityName+";mContext.getPackageName()="+mContext.getPackageName());
							 if(topActivityName!=null&&!topActivityName.equals(mContext.getPackageName())){
								 Log.i(TAG,"当前绑定用户为0，并且微信应用不在前台");
								 this.removeCallbacks(getUserCountTimeTask);
							     this.postDelayed(getUserCountTimeTask, 60000);
							 }
						 }
					 }
					 getData++;
					 if(getData==2 && mHandler!=null){//跳转到主页
						 mHandler.removeMessages(CommandType.LOGIN_GETDATA_SUCCESS);
						 mHandler.sendEmptyMessage(CommandType.LOGIN_GETDATA_SUCCESS);
					 }
					//成功获取到用户列表后，再通知服务器，这时候可以接收离线消息了。TV掉线了，扫描绑定。这时候发很多离线消息。下次重启登录，还没有获取到列表之前，消息里面的用户会有很多NULL					
					 //发送接收离线消息
					InitFinish initfinish = new InitFinish( WeiXmppManager.getInstance().getConnection(), null);	
					initfinish.sentPacket();
					 break;
				case CommandType.COMMAND_GET_QR:
					 Log.i(TAG,"COMMAND_GET_QR");
					 if (this.getStatus().equals(CommandReturnType.STATUS_SUCCESS)){
						 String url = (String)this.getData();
						 Log.i("liyulin","COMMAND_GET_QR--url="+url);
						
						 if(url != null){
							 //插入到ContentProvider
							// ProviderFun.insertRecord(mContext,url);
							// getQR_url(mContext);
							 //保存当前绑定的微信二维码
//							 WeiQrDao weiQrDao = new WeiQrDao(mContext);
							 String qrurl = WeiQrDao.getInstance().getUrl();
							 Log.i("liyulin","update=qrurl="+qrurl);
							 if (qrurl != null){
								// Log.i("liyulin","111update=url="+url);
//								 weiQrDao.update(url);
								 WeiQrDao.getInstance().updateUrl(qrurl);
							 }
						 }
					 }
					 getData++;
					 if(getData==2 && mHandler!=null){//跳转到主页
						 mHandler.removeMessages(CommandType.LOGIN_GETDATA_SUCCESS);
						 mHandler.sendEmptyMessage(CommandType.LOGIN_GETDATA_SUCCESS);
					 }
					 break;
				default:
					break;
			}

		}
	}
	public  static void setHandler(BaseUIHandler Handler){
		mHandler = (BaseUIHandler) Handler;
	}
	
	
		          
	
	
}
