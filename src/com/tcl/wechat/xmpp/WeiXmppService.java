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
import com.tcl.wechat.common.WeiConstant.CommandAction;
import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.db.AppInfoDao;
import com.tcl.wechat.db.DeviceDao;
import com.tcl.wechat.db.WeiMsgRecordDao;
import com.tcl.wechat.db.WeiQrDao;
import com.tcl.wechat.db.WeiUserDao;
import com.tcl.wechat.modle.AppInfo;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.modle.DeviceInfo;
import com.tcl.wechat.modle.QrInfo;
import com.tcl.wechat.modle.WeiNotice;
import com.tcl.wechat.receiver.ConnectionChangeReceiver;
import com.tcl.wechat.utils.BaseUIHandler;
import com.tcl.wechat.utils.NanoHTTPD;
import com.tcl.wechat.utils.SystemInfoUtil;
import com.tcl.wechat.utils.ToastUtil;

/**
 * @ClassName: WeiXmppService
 * @Description: weibo服务类，接收服务器推送处理*/

public class WeiXmppService extends Service{
	
	
	private static final String TAG = WeiXmppService.class.getSimpleName();

	
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
		
		initDao();
		
		//获取当前系统内存配置
		SystemInfoUtil.getConfigure();
	
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
		if (WeiXmppManager.getInstance().isRegister()){
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
	
	/**
	 * 实例化数据库
	 */
	private void initDao(){
		if (!WeiXmppManager.getInstance().isRegister()){
			
			//初始化二维码数据库
			String uuidStr = WeiQrDao.getInstance().getUUID();
			if (TextUtils.isEmpty(uuidStr) || uuidStr.equalsIgnoreCase("")){
				uuidStr = "";
				UUID uuid = UUID.randomUUID();
				String arrayStr[] = uuid.toString().split("-");
			    for(int i = 0; i < arrayStr.length; i++){
			    	uuidStr = uuidStr + arrayStr[i];
			    }	
			    QrInfo qrInfo = new QrInfo("", uuidStr);
			    if (!WeiQrDao.getInstance().addQr(qrInfo)){
			    	Log.e(TAG, "updateUuid ERROR!!");
			    }
			} 
			
			//初始化设备数据库
			String macAddr = DeviceDao.getInstance().getMACAddr();
			String deviceId = DeviceDao.getInstance().getDeviceId();
			if (TextUtils.isEmpty(macAddr) || macAddr.equalsIgnoreCase("") ||
					TextUtils.isEmpty(deviceId) || deviceId.equalsIgnoreCase("")){
				macAddr = SystemInfoUtil.getMacAddr();
				deviceId = SystemInfoUtil.getDeviceId();
				DeviceInfo deviceInfo = new DeviceInfo(deviceId, macAddr, null);
				if (!DeviceDao.getInstance().addDeviceInfo(deviceInfo)){
					Log.e(TAG, "addDeviceInfo ERROR!!");
				}
			}
		}
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
	 * 添加App信息
	 */
	public void addAppInfo(){
		try {
			ArrayList<AppInfo> systemAppList = SystemInfoUtil.getSystemApp(this);
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
			 * 注册成功
			 */
			case CommandType.COMMAND_REGISTER: 
				WeiXmppManager.getInstance().login();
				break;
				
			/**
			 * 登陆成功
			 */
			case CommandType.COMMAND_LOGIN: 
				Log.d(TAG, "=====================login  successfully===============");
				ToastUtil.showToast("login  successfully");
				
				intent = new Intent(CommandAction.ACTION_LOGIN_SUCCESS);
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
//				Bundle b = (Bundle) msg.obj;
//				WeiXinMsg weiXinMsg = b.getParcelable("weiXinMsg");
				intent = new Intent(CommandAction.ACTION_RECEIVE_WEIXIN_MSG);
//				mBundle = new Bundle(); 
//				mBundle.putParcelable("weiXinMsg", weiXinMsg);
				intent.putExtras((Bundle) msg.obj);
				sendBroadcast(intent);
				break;
				 
			/**
			 * 收到微信用户绑定、解绑定提醒
			 */
			case CommandType.COMMAND_GET_WEIXIN_NOTICE:
				 Log.d(TAG, "=====================client received Bind message ===============");
				 WeiNotice weiNotice  = (WeiNotice)this.getData();
				 intent = new Intent(CommandAction.ACTION_RECEIVE_WEIXIN_NOTICE);
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
//				 WeiXinMsg control  = (WeiXinMsg)this.getData();
//				 intent = new Intent(CommandAction.ACTION_WEIXIN_CONTROL);
//				 mBundle = new Bundle(); 
//				 mBundle.putSerializable("control", control);
//				 intent.putExtras(mBundle);
//				 sendBroadcast(intent);
				 break;
			
			case CommandType.COMMAND_GET_WEIXIN_APP:
				Log.d(TAG, "=====================client received APP message ===============");
//				WeiXinMsg app  = (WeiXinMsg)this.getData();
//				intent = new Intent(CommandAction.ACTION_GET_WEIXIN_APP);
//				mBundle = new Bundle(); 
//				mBundle.putSerializable("app", app);
//				intent.putExtras(mBundle);
//				sendBroadcast(intent);
				break;
				 
				 
			case CommandType.COMMAND_REMOTEBINDER:
				Log.d(TAG, "=============client received remotebinder message ===============");
				
				BindUser bindUser = (BindUser)this.getData();
				Log.d(TAG, "=============bindUser：" + bindUser);
				intent = new Intent(CommandAction.ACTION_REMOTEBINDER);
				mBundle = new Bundle(); 
				mBundle.putParcelable("bindUser", mBundle);
				intent.putExtras(mBundle);
				sendBroadcast(intent);
				break;
				
			case CommandType.COMMAND_UN_BINDER:
				String openId = (String) msg.obj;
				WeiUserDao.getInstance().deleteUser(openId);
				WeiMsgRecordDao.getInstance().deleteUserRecorder(openId);
				intent = new Intent(CommandAction.ACTION_UPDATE_BINDUSER);
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
