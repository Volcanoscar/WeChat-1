/**
 * -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */

package com.tcl.wechat.xmpp;



import java.io.File;
import java.util.List;
import java.util.UUID;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.common.WeiConstant.CommandBroadcast;
import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.db.DeviceDao;
import com.tcl.wechat.db.LocalAppDao;
import com.tcl.wechat.db.WeiQrDao;
import com.tcl.wechat.modle.AppInfo;
import com.tcl.wechat.modle.WeiNotice;
import com.tcl.wechat.modle.WeiRemoteBind;
import com.tcl.wechat.modle.WeiXinMsg;
import com.tcl.wechat.receiver.ConnectionChangeReceiver;
import com.tcl.wechat.utils.BaseUIHandler;
import com.tcl.wechat.utils.CommonsFun;
import com.tcl.wechat.utils.NanoHTTPD;

/**
 * @ClassName: WeiXmppService
 * @Description: weibo服务类，接收服务器推送处理*/

public class WeiXmppService extends Service{
	
	
	private static final String TAG = "WeiXmppService";

	
	private DeviceDao dao ;
	private ICallback iCallback = null;
	private NanoHTTPD nanoHTTPD=null;
	private ConnectionChangeReceiver mConnectionChangeReceiver;
//	private static final String SET_SHARE_STYLE_NAME = "setshare_pref";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.d(TAG, "WeiXmppService onCreate");
		initDao();
		//获取当前系统内存配置
		CommonsFun.getConfigure();
	
		WeiXmppManager.getInstance().setmHandler(mHandler);
		WeiXmppManager.getInstance().setmContext(this);

		Log.d(TAG, "----------------------------------weixmppservice oncreate ------1");
		//启动httpserver
		File file ;
		file = new File("/");
		Log.d(TAG, "----------------------------------weixmppservice oncreate ------2");
		try {
			Log.d(TAG, "----------------------------------weixmppservice oncreate ------2.1");

			nanoHTTPD=new NanoHTTPD(WeiConstant.httpServicePort,file,this.getApplicationContext());
			Log.d(TAG, "----------------------------------weixmppservice oncreate ------3");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("lyr","NanoHTTPD Error:"+e.getMessage());
		}


		if(nanoHTTPD!=null)
			nanoHTTPD.start();
		Log.d(TAG, "----------------------------------weixmppservice oncreate ------4");

	
		//将预安装的apk的名称写入数据库,这个要反复验证是否会读写冲突
		insertSysAppInfo(); 			
		super.onCreate();
	}

	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroy()");
		if(mConnectionChangeReceiver!=null)
			this.unregisterReceiver(mConnectionChangeReceiver);
	 
		if(nanoHTTPD!=null){
			nanoHTTPD.stop();
		}
		 
	 
		Log.d(TAG, "onDestroy()222222222222");
		System.exit(0);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		//开启服务登陆服务器
	
		if (isReg()){
			WeiXmppManager.getInstance().login();
		}else{
			WeiXmppManager.getInstance().register();
		}
		//获取启动方式,其他应用启动级别高。。不重写
		if(WeiConstant.StartServiceMode.CURRENTMODE.equals(WeiConstant.StartServiceMode.OWN))
			try {
				
				String tmpmode =  intent.getStringExtra("startmode");
				if(tmpmode!=null&&!tmpmode.equals(""))
					WeiConstant.StartServiceMode.CURRENTMODE = tmpmode;
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//用于测试，启动服务之后，连接测试服务器
		//WeiXmppManager.getInstance().connectTestServer();
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		// START_STICKY是service被kill掉后自动重写创建
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
	
	/**
	 * 实例化设备数据库
	 */
	private void initDao(){
		dao = new DeviceDao(this);
		String memeberid = dao.find();
		String uuidStr = null;
		Log.d(TAG, "当前memberid="+memeberid);
		//判断当前用户是否生有用户号，如有，代表老用户，uuid置为空，否则生成uuid并保存数据库；
		if (memeberid != null && !memeberid.equalsIgnoreCase("")){
			WeiXmppManager.getInstance().setMemberid(memeberid);
		}else{
			WeiQrDao weiQrDao = new WeiQrDao(this);
			uuidStr = weiQrDao.find_uuid();
			if(uuidStr.equalsIgnoreCase("")){
				UUID uuid = UUID.randomUUID();
				String arrayStr[] = uuid.toString().split("-");
			    for(int i=0; i<arrayStr.length; i++){
			           uuidStr = uuidStr + arrayStr[i];
			    }	
				weiQrDao.update_uuid(uuidStr);
			}			
		}
		Log.d(TAG, "当前uuid="+uuidStr);	
		/* WeiQrDao weiQrDao = new WeiQrDao(this);
		 String qrurl = weiQrDao.find();
		 if (qrurl != null&&qrurl.length()>0){
			 Log.d(TAG, "二维码地址已经存在，插入共享数据库-qrurl="+qrurl);
			 CommonsFun.insertRecord(this,qrurl);
		 }*/
		
		
	}
	public void insertSysAppInfo()
	{
		try
		{
			List<AppInfo> systemAppList = CommonsFun.getSystemApp(this);
			LocalAppDao dao = new LocalAppDao(this);
			
			if(dao.getsize()>0)
				return;
			if(systemAppList==null)
				return;
			for (AppInfo appInfo : systemAppList)
			{
				Log.e(TAG,"保存初始化数据：" + appInfo.getappname());
				dao.saveAppInfo(appInfo);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 判断当前用户是否注册
	 */
	public boolean isReg(){
		boolean flag = false;
		if (WeiXmppManager.getInstance().getMemberid() !=null && !WeiXmppManager.getInstance().getMemberid().equalsIgnoreCase("")){
			flag = true;
		}
		Log.d(TAG, "当前是否注册："+flag);
		return flag;
	}
	
	
	/**
	 * @Fields mHandler : 实例化一个hander,负责处理相关推送消息	
    */
	
	private BaseUIHandler mHandler = new BaseUIHandler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Message m = msg;
			Intent intent;
			Bundle mBundle;
			Log.i(TAG, "msg.what = "+m.what);
			switch (m.what) {
			     //注册返回设备id
			case CommandType.COMMAND_REGISTER:
				 //存储memberid到数据库
				 String memberid = (String)this.getData();
				 if (dao != null && memberid != null){
					dao.update(memberid);
					
					String memeberid = dao.find();
					Log.d(TAG, "注册成功存储memeberid,查询数据库memberid="+memeberid);
					Log.d(TAG, "注册成功存储memeberid,查询数据库memberid="+memeberid);
					if (memeberid != null && !memeberid.equalsIgnoreCase("")){
						WeiXmppManager.getInstance().setMemberid(memeberid);
						 //注册成功并成功写入数据库后，直接登陆
						 WeiXmppManager.getInstance().login();
					}
				 }
				 break;
				 //登陆成功返回
			case CommandType.COMMAND_LOGIN:
				 Log.d(TAG, "WebChat Login Succeed");
				 Toast.makeText(WeiXmppService.this,  "WebChat Login Success", Toast.LENGTH_LONG).show();
				 intent = new Intent(CommandBroadcast.LOGIN_SUCCESS);
				 sendBroadcast(intent);
				 //AIDL对外获取长连接回�?				 
				 if (WeiXmppManager.getInstance().getConnection()!=null && iCallback !=null){
					 try {
						 Object obj = WeiXmppManager.getInstance().getConnection();
						 WeiConnection weiConnection = new WeiConnection(obj);
						 iCallback.setConnection(weiConnection);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 }
				 break;
				 //收到微信用户推送内容		
			case CommandType.COMMAND_GET_WEIXIN_MSG:
				 Log.d(TAG, "weixin COMMAND_GET_WEIXIN_MSG");
				 Log.d(TAG, "WebChat COMMAND_GET_WEIXIN_MSG");
				 //WeiXinMsg weiXinMsg  = (WeiXinMsg)this.getData();
				 WeiXinMsg weiXinMsg  = (WeiXinMsg)msg.obj;
				 intent = new Intent(CommandBroadcast.GET_WEIXIN_MSG);
				 mBundle = new Bundle(); 
				 mBundle.putSerializable("weiXinMsg", weiXinMsg);
				 intent.putExtras(mBundle);
				 sendBroadcast(intent);
				 Log.i(TAG,"weiXinMsg.getMsgtype()="+weiXinMsg.getMsgtype());
				 break;
				//收到微信用户绑定、解绑定提醒
			case CommandType.COMMAND_GET_WEIXIN_NOTICE:
				 Log.d(TAG, "WebChat COMMAND_GET_WEIXIN_NOTICE");
				 WeiNotice weiNotice  = (WeiNotice)this.getData();
				 intent = new Intent(CommandBroadcast.GET_WEIXIN_NOTICE);
				 mBundle = new Bundle(); 
				 mBundle.putSerializable("weiNotice", weiNotice);
				 intent.putExtras(mBundle);
				 sendBroadcast(intent);
				 break;
			case CommandType.COMMAND_GET_WEIXIN_CONTROL:
				 Log.d(TAG, "WebChat COMMAND_GET_WEIXIN_CONTROL");
				 Log.d(TAG, "COMMAND_GET_WEIXIN_CONTROL");
				 WeiXinMsg control  = (WeiXinMsg)this.getData();
				 intent = new Intent(CommandBroadcast.COMMAND_GET_WEIXIN_CONTROL);
				 mBundle = new Bundle(); 
				 mBundle.putSerializable("control", control);
				 intent.putExtras(mBundle);
				 sendBroadcast(intent);
				 break;
			case CommandType.COMMAND_GET_WEIXIN_APP:
				 Log.d(TAG, "WebChat COMMAND_GET_WEIXIN_APP");
				 Log.d(TAG, "COMMAND_GET_WEIXIN_APP");
				 WeiXinMsg app  = (WeiXinMsg)this.getData();
				 intent = new Intent(CommandBroadcast.COMMAND_GET_WEIXIN_APP);
				 mBundle = new Bundle(); 
				 mBundle.putSerializable("app", app);
				 intent.putExtras(mBundle);
				 sendBroadcast(intent);
				 break;
			case CommandType.COMMAND_REMOTEBINDER:
				 Log.d(TAG, "WebChat COMMAND_REMOTEBINDER");
				Log.d(TAG, "COMMAND_GET_WEIXIN_REMOTEBIND");
				 WeiRemoteBind weiRemoteBind  = (WeiRemoteBind)this.getData();
				 intent = new Intent(CommandBroadcast.COMMAND_REMOTEBINDER);
				 mBundle = new Bundle(); 
				 mBundle.putSerializable("remotebind", weiRemoteBind);
				 intent.putExtras(mBundle);
				 sendBroadcast(intent);
				break;
			case CommandType.COMMAND_DEVICEIDNULL:
				Toast.makeText(WeiXmppService.this, "no deviceid", Toast.LENGTH_LONG).show();
				break;
			case CommandType.COMMAND_MACNULL:
				Toast.makeText(WeiXmppService.this, "no mac address",Toast.LENGTH_LONG).show();
				break;
			case WeiConstant.LOG_IN_TIMEOUT:
				 Log.d(TAG, "WebChat LOG_IN_TIMEOUT");
 				Log.i(TAG,"尝试三次重连后登陆失败");
				Toast.makeText(WeiXmppService.this, "WebChat Login Failed", Toast.LENGTH_LONG).show();
				break;
			default:
				 break;
			}
		}
	};		
	/**
	 * 绑定一个服务类
	 */	
	private final IRemoteService.Stub mBinder = new IRemoteService.Stub() {

		@Override
		public void registerCallback(ICallback cb) throws RemoteException {
			iCallback = cb;
		}

		@Override
		public void unregisterCallback() throws RemoteException {
			iCallback = null;
		}			
	};
	
	



}
