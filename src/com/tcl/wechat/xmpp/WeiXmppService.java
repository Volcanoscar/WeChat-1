package com.tcl.wechat.xmpp;

import java.util.ArrayList;
import java.util.UUID;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.common.WeiConstant.CommandBroadcast;
import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.db.AppInfoDao;
import com.tcl.wechat.db.DeviceDao;
import com.tcl.wechat.db.WeiQrDao;
import com.tcl.wechat.modle.AppInfo;
import com.tcl.wechat.modle.WeiNotice;
import com.tcl.wechat.modle.WeiRemoteBind;
import com.tcl.wechat.modle.WeiXinMsg;
import com.tcl.wechat.receiver.ConnectionChangeReceiver;
import com.tcl.wechat.utils.BaseUIHandler;
import com.tcl.wechat.utils.CommonsFun;
import com.tcl.wechat.utils.NanoHTTPD;
import com.tcl.wechat.utils.SystemShare.SharedEditer;

/**
 * @ClassName: WeiXmppService
 * @Description: weibo服务类，接收服务器推送处理*/

public class WeiXmppService extends Service{
	
	
	private static final String TAG = "WeiXmppService";

	
	private ICallback iCallback = null;
	private NanoHTTPD nanoHTTPD=null;
	private ConnectionChangeReceiver mConnectionChangeReceiver;
	
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
		super.onCreate();
		
		//初始化设备信息
		initDeviceDao();
		
		//获取当前系统内存配置
		CommonsFun.getConfigure();
	
		WeiXmppManager.getInstance().setmHandler(mHandler);
		WeiXmppManager.getInstance().setmContext(this);

		Log.d(TAG, "=====================start httpserver===============");
		//启动httpserver
		try {
			Log.i(TAG, "httpService Port:" + WeiConstant.httpServicePort);
			nanoHTTPD = new NanoHTTPD(WeiConstant.httpServicePort);
			if (nanoHTTPD != null){
				nanoHTTPD.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//将预安装的apk的名称写入数据库,这个要反复验证是否会读写冲突
		addAppInfo(); 			
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		//开启服务登陆服务器
		if (isRegister()){
			WeiXmppManager.getInstance().login();
		} else {
			WeiXmppManager.getInstance().register();
		}
		//获取启动方式,其他应用启动级别高。。不重写
		if(WeiConstant.StartServiceMode.CURRENTMODE.equals(WeiConstant.StartServiceMode.OWN)){
			if (intent != null){
				String startMode = intent.getStringExtra("startmode");
				if (TextUtils.isEmpty(startMode)){
					WeiConstant.StartServiceMode.CURRENTMODE = startMode;
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroy()");
		if(mConnectionChangeReceiver != null){
			unregisterReceiver(mConnectionChangeReceiver);
		}
	 
		if(nanoHTTPD != null){
			nanoHTTPD.stop();
		}
	}

	
	/**
	 * 实例化设备数据库
	 */
	private void initDeviceDao(){
		String uuidStr = null;
		String memeberid = DeviceDao.getInstance().getMemberId();
		Log.i(TAG, "memeberid = " + memeberid);
		
		//判断当前用户是否生有用户号，如有，代表老用户，uuid置为空，否则生成uuid并保存数据库；
		if (TextUtils.isEmpty(memeberid)){
			UUID uuid = UUID.randomUUID();
			String arrayStr[] = uuid.toString().split("-");
			for(int i = 0; i<arrayStr.length; i++){
				uuidStr += arrayStr[i];
		    }	
			WeiQrDao.getInstance().updateUuid(uuidStr);
		}
	}
	
	/**
	 * 添加App信息
	 */
	public void addAppInfo(){
		try {
			ArrayList<AppInfo> systemAppList = CommonsFun.getSystemApp(this);
			if (AppInfoDao.getInstance().getAppCount() > 0){
				return ;
			}
			
			if(systemAppList == null){
				return;
			}
				
			for (AppInfo appInfo : systemAppList){
				AppInfoDao.getInstance().addAppInfo(appInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 判断当前用户是否注册
	 */
	private boolean isRegister(){
		boolean flag = false;
		String memberId = WeiXmppManager.getInstance().getMemberid();
		if (!TextUtils.isEmpty(memberId)){
			Log.i(TAG, "The memberId: " + memberId + " has been registered!");
			return true;
		}
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
			
			/**
			 * 注册返回设备id
			 */
			case CommandType.COMMAND_REGISTER: 
				Log.d(TAG, "=====================register successfully===============");
				//存储memberid到数据库
				String memberid = (String)this.getData();
				if (!TextUtils.isEmpty(memberid)){
					if (DeviceDao.getInstance().updateMemberId(memberid)){
						WeiXmppManager.getInstance().setMemberid(memberid);
						//注册成功并成功写入数据库后，直接登陆
						Log.d(TAG, "=====================start login===============");
						WeiXmppManager.getInstance().login();
					}
				}
				break;
				
			/**
			 * 登陆成功返回
			 */
			case CommandType.COMMAND_LOGIN: 
				Log.d(TAG, "=====================login  successfully===============");
				Toast.makeText(WeiXmppService.this, "WebChat Login Success", Toast.LENGTH_LONG).show();
				
				intent = new Intent(CommandBroadcast.LOGIN_SUCCESS);
				sendBroadcast(intent);
				//AIDL对外获取长连接回调			 
				if (WeiXmppManager.getInstance().getConnection()!=null && iCallback !=null){
					try {
						Object obj = WeiXmppManager.getInstance().getConnection();
						WeiConnection weiConnection = new WeiConnection(obj);
						iCallback.setConnection(weiConnection);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				 }
				 break;
				 
			/**
			 * 收到微信用户推送内容		
			 */
			case CommandType.COMMAND_GET_WEIXIN_MSG:
				Log.d(TAG, "=====================client received message ===============");
				WeiXinMsg weiXinMsg = (WeiXinMsg)msg.obj;
				intent = new Intent(CommandBroadcast.GET_WEIXIN_MSG);
				mBundle = new Bundle(); 
				mBundle.putSerializable("weiXinMsg", weiXinMsg);
				intent.putExtras(mBundle);
				sendBroadcast(intent);
				break;
				 
			/**
			 * 收到微信用户绑定、解绑定提醒
			 */
			case CommandType.COMMAND_GET_WEIXIN_NOTICE:
				 Log.d(TAG, "=====================client received Bind message ===============");
				 WeiNotice weiNotice  = (WeiNotice)this.getData();
				 intent = new Intent(CommandBroadcast.GET_WEIXIN_NOTICE);
				 mBundle = new Bundle(); 
				 mBundle.putSerializable("weiNotice", weiNotice);
				 intent.putExtras(mBundle);
				 sendBroadcast(intent);
				 break;
				 
			/**
			 * 收到控制消息
			 */
			case CommandType.COMMAND_GET_WEIXIN_CONTROL:
				 Log.d(TAG, "=====================client received control message ===============");
				 WeiXinMsg control  = (WeiXinMsg)this.getData();
				 intent = new Intent(CommandBroadcast.COMMAND_GET_WEIXIN_CONTROL);
				 mBundle = new Bundle(); 
				 mBundle.putSerializable("control", control);
				 intent.putExtras(mBundle);
				 sendBroadcast(intent);
				 break;
			
			case CommandType.COMMAND_GET_WEIXIN_APP:
				Log.d(TAG, "=====================client received APP message ===============");
				WeiXinMsg app  = (WeiXinMsg)this.getData();
				intent = new Intent(CommandBroadcast.COMMAND_GET_WEIXIN_APP);
				mBundle = new Bundle(); 
				mBundle.putSerializable("app", app);
				intent.putExtras(mBundle);
				sendBroadcast(intent);
				break;
				 
				 
			case CommandType.COMMAND_REMOTEBINDER:
				Log.d(TAG, "=====================client received remotebinder message ===============");
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
				Log.d(TAG, "=====================login timeout ===============");
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
