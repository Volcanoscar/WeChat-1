package com.tcl.wechat.xmpp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.tcl.wechat.WeApplication;
import com.tcl.wechat.common.Config;
import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.common.IConstant.SystemShared;
import com.tcl.wechat.database.WeiUserDao;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.model.WeiNotice;
import com.tcl.wechat.model.WeiXinMsgRecorder;
import com.tcl.wechat.utils.NanoHTTPD;
import com.tcl.wechat.utils.SystemShare.SharedEditer;

/**
 * Xmpp服务类
 * @author rex.lei
 *
 */
public class WeiXmppService extends Service implements IConstant{

	private static final String TAG = WeiXmppService.class.getSimpleName();
	
	private ICallback iCallback = null;
	private NanoHTTPD nanoHTTPD=null;
	
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
		
		//添加Xmpp事件监听器
		WeiXmppManager.getInstance().addListener(eventListener);
		
		//启动httpserver
		try {
			Log.i(TAG, "httpService Port:" + Config.httpServicePort);
//			nanoHTTPD = new NanoHTTPD(Config.httpServicePort);
//			if (nanoHTTPD != null){
//				nanoHTTPD.start();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		
		//开启服务登陆服务器，在主线程中操作，注意网络请求
		if (WeiXmppManager.getInstance().isRegister()){
			WeiXmppManager.getInstance().login();
		} else {
			WeiXmppManager.getInstance().register();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	private XmppEventListener eventListener = new XmppEventListener() {
		
		@Override
		public void onEvent(XmppEvent event) {
			
			int eventType = event.getType();
			Log.i(TAG, "eventType:" + eventType);
			switch (eventType) {
			case EventType.TYPE_REGISTER_SUCCESS:
				WeiXmppManager.getInstance().login();
				break;
				
			case EventType.TYPE_REGISTER_FAILED:
				break;
				
			case EventType.TYPE_LOGIN_SUCCESS:
				//if (new SharedEditer().getBoolean(SystemShared.KEY_REGISTENER_SUCCESS, false)){
					//1、登录成功
					WeApplication.bLoginSuccess = true;
				//} else {
					//2、后台去更新用户列表
					Intent intent = new Intent();
					intent.setAction(CommandAction.ACTION_LOGIN_SUCCESS);
					sendBroadcast(intent);
				//}
				
				//AIDL对外获取长连接回调			 
//				if (WeiXmppManager.getInstance().getConnection() != null && iCallback != null){
//					try {
//						Object obj = WeiXmppManager.getInstance().getConnection();
//						WeiConnection weiConnection = new WeiConnection(obj);
//						iCallback.setConnection(weiConnection);
//					} catch (RemoteException e) {
//						e.printStackTrace();
//					}
//				 }
				break;
				
			case EventType.TYPE_LOGINFAILED:
				int reason = event.getReason();
				switch (reason) {
				case EventReason.REASON_DEVICE_ERROR:
				case EventReason.REASON_MAC_ERROR:
				case EventReason.REASON_UUID_ERROR:
					break;
					
				case EventReason.REASON_LOGIN_TIMEOUT:
					break;

				default:
					break;
				}
				break;
				
			case EventType.TYPE_BIND_EVENT:
				WeiNotice weiNotice  = (WeiNotice)event.getEventData();
				Intent noticeIntent = new Intent(CommandAction.ACTION_RECEIVE_WEIXIN_NOTICE);
				noticeIntent.putExtra("weiNotice", weiNotice);
				sendBroadcast(noticeIntent);
				break;
				
			case EventType.TYPE_UNBIND_EVENT:
				String openid = (String) event.getEventData();
				BindUser unBindUser = WeiUserDao.getInstance().getUser(openid);
				Intent unBindIntent = new Intent(CommandAction.ACTION_UPDATE_BINDUSER);
				unBindIntent.putExtra("bindUser", unBindUser);
				sendBroadcast(unBindIntent);
				break;
				
			case EventType.TYPE_REMOTE_BIND_EVENT:
				BindUser bindUser = (BindUser)event.getEventData();
				Intent bindIntent = new Intent(CommandAction.ACTION_REMOTEBINDER);
				bindIntent.putExtra("bindUser", bindUser);
				sendBroadcast(bindIntent);
				break;
				
			case EventType.TYPE_RECEIVE_WEIXINMSG:
				WeiXinMsgRecorder recorder = (WeiXinMsgRecorder) event.getEventData();
				Intent recorderIntent = new Intent(CommandAction.ACTION_RECEIVE_WEIXIN_MSG);
				recorderIntent.putExtra("weiXinMsg", recorder);
				sendBroadcast(recorderIntent);
				break;

			default:
				break;
			}
			
		}
	};
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	 
		if(nanoHTTPD != null){
			nanoHTTPD.stop();
		}
	}
}
