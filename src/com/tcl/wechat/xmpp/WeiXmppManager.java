package com.tcl.wechat.xmpp;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.IQTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.R;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.common.IConstant.EventReason;
import com.tcl.wechat.common.IConstant.EventType;
import com.tcl.wechat.common.IConstant.ReturnType;
import com.tcl.wechat.common.IConstant.SystemShared;
import com.tcl.wechat.database.DeviceDao;
import com.tcl.wechat.database.WeiQrDao;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.model.WeiNotice;
import com.tcl.wechat.model.WeiXinMsgRecorder;
import com.tcl.wechat.utils.NetWorkUtil;
import com.tcl.wechat.utils.SystemInfoUtil;
import com.tcl.wechat.utils.SystemShare.SharedEditer;
import com.tcl.wechat.utils.ToastUtil;

/**
 * Xmpp管理类
 * @author rex.lei
 *
 */
public class WeiXmppManager {
	
	private static final String TAG = "WeiXmppManager";
	
	private static final int CONNECT_TIME = 5;
	private static final int ACCESS_TIME  = 1;//考虑到负载均衡，一次没连上，就要重新获取AS的地址，重新AS
	private static int mAccessCount 	  = 0;//考虑到负载均衡，一次没连上，就要重新获取AS的地址，重新走登录流程不超过3次
	
	private Context mContext = null;
	/**
	 * 端口信息
	 */
	private String xmppHost1 = null;
	private int xmppPort1 = 0;
	private String curHost = null;
	private int curport = 0;
	private String password = "";
	private String memberid = "";
	private String token = "";
	private String deviceid = "";
	private String macAddress = "";
	private String uuid = "";
	
	private Thread reconnctionThtread = null;
	private XMPPConnection mXmppConnect = null;
	
	private XmppEventListener mEventListener;
	
	private boolean loginFlag = false;
	private boolean registerFlag = false;
	private Boolean reconnError = false;
	
	private static final class WeiXmppManagerInstance {
		private static WeiXmppManager mInstance = new WeiXmppManager();
	}

	private WeiXmppManager() {
		xmppHost1 = "124.251.36.70";
		curHost = xmppHost1;

		xmppPort1 = 5222;
		curport = xmppPort1;

		password = "6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2";

		mContext = WeApplication.getContext();
	}
	
	public static WeiXmppManager getInstance() {
		return WeiXmppManagerInstance.mInstance;
	}
	
	/**
	 * 判断是否已经建立链接
	 */
	public boolean isConnected() {
		return mXmppConnect != null && mXmppConnect.isConnected();
	}
	
	/**
	 * 设置XMPPConnection
	 * 
	 * @return connection
	 */
	public void setConnection(XMPPConnection connection) {
		if (mXmppConnect != null){
			mXmppConnect.disconnect();
		}
		mXmppConnect = connection;
		setReconnection();
	}
	
	/**
	 * 获取XMPPConnection
	 * @return
	 */
	public XMPPConnection getConnection() {
		return mXmppConnect;
	}
	
	/**
	 * 设置重连监听
	 */
	private void setReconnection() {
		if (reconnctionThtread != null && reconnctionThtread.isAlive()) {
			reconnctionThtread.interrupt();
			mXmppConnect.removeConnectionListener(connectionListener);
			reconnctionThtread = null;
		}

		reconnctionThtread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mXmppConnect.addConnectionListener(connectionListener);
			}
		});
		reconnctionThtread.start();
	}
	
	public Boolean getReconnError() {
		return reconnError;
	}

	public void setReconnError(Boolean reconnError) {
		this.reconnError = reconnError;
	}
	
	/**
	 * @Description:重连机制
	 */
	private ConnectionListener connectionListener = new ConnectionListener() {

		@Override
		public void reconnectionSuccessful() {
			Log.i(TAG, "reconnectionSuccessful");
			// relogin();
		}

		@Override
		public void reconnectionFailed(Exception arg0) {
			Log.i(TAG, "reconnectionFailed");
		}

		@Override
		public void reconnectingIn(int arg0) {
			Log.i(TAG, "reconnectingIn");
		}

		@Override
		public void connectionClosedOnError(Exception arg0) {
			Log.i(TAG, "connectionClosedOnError--Exception=" + arg0);
			reconnError = true;
			relogin();
		}

		@Override
		public void connectionClosed() {
			// TODO Auto-generated method stub
			Log.i(TAG, "connectionClosed");
			// reconnError = true;
			// relogin();
		}
	};
	
	/**
	 * ͣ关闭长连接
	 */
	public void disconnection() {

		if (mXmppConnect != null) {
			mXmppConnect.disconnect();
			mXmppConnect = null;
		}
	}
	
	/**
	 * 是否已经注册
	 * 
	 * @return
	 */
	public boolean isRegister() {
		boolean flag = false;
		String memberId = DeviceDao.getInstance().getMemberId();
		if (!TextUtils.isEmpty(memberId) && !memberId.trim().equals("")) {
			Log.i(TAG, "The memberId: " + memberId + " registered!");
			return true;
		}
		Log.i(TAG, "The memberId: " + memberId + " not registered!");
		return flag;
	}
	
	/**
	 * 网络是否可用
	 * @return
	 */
	private boolean isNetworkAvailable(){
		if (!NetWorkUtil.isWifiConnected()) {
			Log.i(TAG, "Network is not available!!");
			ToastUtil.showToastForced(R.string.notwork_not_available);
			if (mEventListener != null){
				mEventListener.onEvent(new XmppEvent(this, EventType.TYPE_REGISTER_FAILED, 
						EventReason.REASON_NETWORK_NOTAVAILABLE, null));
			}
			return false;
		}
		return true;
	}
	
	/**
	 * DeviceId是否合法
	 * @return
	 */
	private boolean isDeviceIdLegal(){
		deviceid = SystemInfoUtil.getDeviceId();
		Log.i(TAG, "DeviceId:" + deviceid);
		if (TextUtils.isEmpty(deviceid)) {
			Log.i(TAG, "DeviceId is not available!!");
			if (mEventListener != null){
				mEventListener.onEvent(new XmppEvent(this, EventType.TYPE_REGISTER_FAILED, 
						EventReason.REASON_DEVICE_ERROR, null));
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Mac地址是否合法
	 * @return
	 */
	private boolean isMacAddressLegal(){
		macAddress = SystemInfoUtil.getLocalMacAddress();
		Log.i(TAG, "macAddress:" + macAddress);
		if (TextUtils.isEmpty(macAddress)) {
			Log.e(TAG,"Get Mac Address Failed, You should init DeviceDB first!");
			if (mEventListener != null){
				mEventListener.onEvent(new XmppEvent(this, EventType.TYPE_REGISTER_FAILED, 
						EventReason.REASON_MAC_ERROR, null));
			}
			return false;
		}
		return true;
	}
	
	/**
	 * UUID是否合法
	 * @return
	 */
	private boolean isUUIDLegal(){
		uuid = WeiQrDao.getInstance().getUUID();
		Log.v(TAG, "uuid = " + uuid);
		if (TextUtils.isEmpty(uuid)) {
			Log.e(TAG, "Get uuid is NULL, You should init first!");
			if (mEventListener != null){
				mEventListener.onEvent(new XmppEvent(this, EventType.TYPE_REGISTER_FAILED, 
						EventReason.REASON_UUID_ERROR, null));
			}
			return false;
		}
		return true;
	}
	
	/**
	 * 获取XMPPConnection对象
	 * @param ipAddr ip地址
	 * @param port 端口
	 * @return
	 */
	private XMPPConnection getXMPPConnection(String ipAddr, int port) {

		try {
			ConnectionConfiguration config = null;
			config = new ConnectionConfiguration(ipAddr, port);
			config.setSecurityMode(SecurityMode.required);
			config.setSelfSignedCertificateEnabled(true);
			config.setSASLAuthenticationEnabled(true);
			config.setTruststorePassword("tclking");
			config.setTruststorePath("/system/etc/security/cacerts.bks");
			config.setTruststorePassword("changeit");
			config.setTruststoreType("bks");
			config.setReconnectionAllowed(false);
			config.setDebuggerEnabled(true);
			XMPPConnection connect = new XMPPConnection(config);
			return connect;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 客户端注册用户
	 */
	public void register(){
		Log.d(TAG, "=====================start register===============");
		if (!isNetworkAvailable()){
			Log.e(TAG, "register failed!!");
			ToastUtil.showToastForced(R.string.notwork_not_available);
			return ;
		}
		
		if (!isDeviceIdLegal() || !isMacAddressLegal() || !isUUIDLegal()){
			ToastUtil.showToastForced(R.string.device_info_error);
			return ;
		}
		
		new SharedEditer().putBoolean(SystemShared.KEY_REGISTENER_SUCCESS, false);
		if (registerFlag) {
			return;
		}
		
		ToastUtil.showToast(R.string.register);
		
		registerFlag = true;
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				SmackConfiguration.setPacketReplyTimeout(10 * 1000);
				for (int i = 0; i < CONNECT_TIME; i++) {
					XMPPConnection connect = null;
					try {
						connect = getXMPPConnection(curHost, curport);
						connect.connect();
						
						if (connect != null && connect.isConnected()){
							Log.d(TAG,"=====================connected===============");
							Log.i(TAG, "Thread:" + Thread.currentThread());
							
							addRegisterListener(connect);
							
							String deviceid_Mac = deviceid + macAddress;
							String content = "<addmaindevice xmlns=\"tcl:hc:login\">"
									+ "<deviceid>"
									//+ deviceid_Mac
									+ deviceid
									+ "</deviceid>"
									+ "<password>"
									+ password
									+ "</password>"
									+ "<resource>"
									+ "tv-android-wechat"
									+ "</resource>"
									+ "<clienttype>"
									+ SystemInfoUtil.getClienttype(mContext)
									+ "</clienttype>"
									+ "<uuid>"
									+ uuid
									+ "</uuid>"
									+ "<iqiyiid>"
									+ "12345"
									+ "</iqiyiid>"
									+ "<nick>"
									+ "TV"
									+ "</nick>"
									+ "<type>"
									+ "wechat" 
									+ "</type>" 
									+ "</addmaindevice>";
							IQ userContentIQ = new UserContentIQ(content);
							userContentIQ.setType(IQ.Type.SET);
							connect.sendPacket(userContentIQ);
							Log.i(TAG,"send register info:" + userContentIQ.toXML());
							return null;
						} else {
							registerFlag = false;
						}
					} catch (XMPPException e) {
						e.printStackTrace();
						Log.w(TAG, e.getMessage() + " ,ConnectCnt=" + (i + 1) + ",Host=" + curHost
								+ ", Port=" + curHost);
						registerFlag = false;
						connect.disconnect();
					}
				}
				return null;
			}
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}
	
	/**
	 * 客户端登录
	 */
	public void login(){
		
		Log.d(TAG, "=====================start login===============");
		Log.i(TAG, "login curThread:" + Thread.currentThread());
		if (!isNetworkAvailable()){
			return ;
		}
		
		if (loginFlag) {
			return;
		}
		
		if (isConnected()) {
			Log.d(TAG, "Long connection exists, do not need to login");
			return;
		}
		loginFlag = true;
		/**
		 * 登录过程：
		 * 1、获取接入列表 accessServer()
		 * 2、开始登陆           startLogin()
		 */
		accessServer();
	}
	
	/**
	 * @Description: 断开连接后重新登陆
	 */
	private void relogin() {
		Log.d(TAG, "开始重新login");

		if (!isNetworkAvailable()) {
			return;
		}
		
		if (loginFlag) {
			return;
		}
		
		if (isConnected()) {
			return;
		}

		// 断开连接后重新登陆
		WeiXmppManager.getInstance().login();
	}
	
	/**
	 * 接入服务器，获取接入令牌
	 */
	private void accessServer(){

		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				
				SmackConfiguration.setPacketReplyTimeout(10000);
				for (int i = 0; i < CONNECT_TIME; i++){
					XMPPConnection connect = null;
					try {
						connect = getXMPPConnection(curHost, curport);
						connect.connect();
						if (connect != null && connect.isConnected()){
							//设置接入成功的长链接
							setConnection(connect);
							
							addAccessListener(connect);
							
							memberid = DeviceDao.getInstance().getMemberId();
							Log.i(TAG, "memberid:" + memberid);
							
							String content = "<auth xmlns=\"tcl:hc:login\">"
									+ "<username>" + "#" + memberid
									+ "@wechat.big.tclclouds.com" + "</username>"
									+ "<password></password>" + "<digest></digest>"
									+ "<resource>tv-android-wechat</resource>"
									+ "</auth>";
							IQ userLoginIQ = new UserLoginIQ(content);
							userLoginIQ.setType(IQ.Type.GET);
							connect.sendPacket(userLoginIQ);
							Log.i(TAG, "send access info:" + userLoginIQ.toXML());
							return null;
						}
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						connect.disconnect();
						Log.w(TAG, e.getMessage() + " ,ConnectCnt=" + (i + 1) + ",Host=" + curHost
								+ ", Port=" + curHost);
					}
				}
				return null;
			} 
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}
	
	/**
	 * 开始登陆
	 */
	private void startLogin(){
		
		Log.d(TAG, "startLogin..........");
		Log.i(TAG, "startLogin curThread:" + Thread.currentThread());
		XMPPConnection connection = null;
		try {
			ConnectionConfiguration config = null;
			config = new ConnectionConfiguration(curHost,
					Integer.valueOf(curport));
			config.setSecurityMode(SecurityMode.required);
			config.setSASLAuthenticationEnabled(true);
			config.setCompressionEnabled(false);
			config.setDebuggerEnabled(true);
			config.setReconnectionAllowed(false);
			config.setDebuggerEnabled(true);
			connection = new XMPPConnection(config);
			connection.connect();
			
			if (connection != null && connection.isConnected()){
				Log.d(TAG, "startLogin connectted!!");
				setConnection(connection);
				
				addLoginListener(connection);
				
				String content = "<auth xmlns=\"tcl:hc:portal\">"
						+ "<username>"
						+ "#"
						+ memberid
						+ "</username>"
						+ "<resource>tv-android-wechat</resource>"
						+ "<token>"
						+ token
						+ "</token>"
						+ "</auth>";

				IQ userLoginIQ = new UserLoginIQ(content);
				userLoginIQ.setType(IQ.Type.SET);
				mXmppConnect.sendPacket(userLoginIQ);
				Log.d(TAG, "Sending an access request：" + content);
				return;
			}
		} catch (XMPPException e) {
			e.printStackTrace();
			Log.w(TAG, "connection error:" + e.getMessage());
			if (connection != null){
				connection.disconnect();
			}
		}
	}

	/**
	 * 客户端注册监听器
	 * @param connect
	 */
	private void addRegisterListener(final XMPPConnection connect) {
		ProviderManager.getInstance().addIQProvider("addmaindevice",
				"tcl:hc:login", new LSMemIDProvider());
		PacketFilter filter = new PacketTypeFilter(LSMemIDResultIQ.class);
		connect.addPacketListener(new PacketListener() {
			
			@Override
			public void processPacket(Packet packet) {
				IQ packageIq = (IQ) packet;
				Log.i(TAG, "Registration result:" + packageIq.toXML());
				Log.i(TAG, "Thread:" + Thread.currentThread());
				try {
					if (packageIq instanceof LSMemIDResultIQ){
						LSMemIDResultIQ iq = (LSMemIDResultIQ) packet;
						String memberId = iq.getMemberid();
						String errorCode = iq.getErrorcode();
						Log.i(TAG, "memberId=" + memberId + ",errorCode=" + errorCode);
						if (memberId != null && DeviceDao.getInstance().updateMemberId(memberId)) {
							
							if (connect != null) {
								Log.i(TAG, "Register finished, connect.disconnect");
								connect.disconnect();
							}
							
							// 注册成功
							Log.d(TAG,"=====================register successfully===============");
							if (mEventListener != null) {
								mEventListener.onEvent(new XmppEvent(this, EventType.TYPE_REGISTER_SUCCESS, 
										EventReason.REASON_COMMON_SUCCESS, null));
							}
						} else {
							Log.e(TAG, "updateMemberId Failed!!");
							if (mEventListener != null) {
								mEventListener.onEvent(new XmppEvent(this, EventType.TYPE_REGISTER_FAILED, 
										EventReason.REASON_COMMON_FAILED, errorCode));
							}
							registerFlag = false;
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} 
			}
		}, filter);
	}
	
	/**
	 * 客户端接入监听器
	 * @param connect
	 */
	private void addAccessListener(final XMPPConnection connect) {
		ProviderManager.getInstance().addIQProvider("auth", "tcl:hc:login",new LSLoginProvider());
		PacketFilter filter = new PacketTypeFilter(LSLoginResultIQ.class);
		connect.addPacketListener(new PacketListener() {
			
			@SuppressWarnings("unused")
			@Override
			public void processPacket(Packet packet) {
				LSLoginResultIQ loginResultIQ = (LSLoginResultIQ) packet;
				token = loginResultIQ.getToken();
				try {
					token = loginResultIQ.getToken();
					int size = loginResultIQ.getIpAndport().size();
					
					Log.i(TAG, "size:" + size);
					Log.i(TAG, "curThread:" + Thread.currentThread());
					
					if (size == 0) {    
						Log.e(TAG, "login Error, Access list size is NULL!!");
						return;
					}
					
					for (int i = 0 ;i < ACCESS_TIME ; i++){
						String str = loginResultIQ.getIpAndport().get(0);
						curHost = str.split(",")[0];
						curport = Integer.parseInt(str.split(",")[1]);
						Log.d(TAG, "curHost:" + curHost + ",curport:" + curport);
						
						if (connect != null){
							connect.disconnect();						
						}
						
						startLogin();
						return;
					}
					//一个AS IP连接五次都失败了，那就重新登录获取AS的地址
					Log.i(TAG, "负载均衡，AS没连接上，重新走登录流程，重新获取AS地址 mAccessCount:" + mAccessCount);
					mAccessCount++;
					loginFlag = false;
					if(mAccessCount <= 3){
						Thread.sleep(5000);
						relogin();
					} else{
						//不再登录获取，重置获取ls次数为0
						mAccessCount = 0;
						if (mEventListener != null){
							mEventListener.onEvent(new XmppEvent(this, EventType.TYPE_LOGINFAILED, 
									EventReason.REASON_LOGIN_TIMEOUT, null));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally{
					Log.i(TAG,"conn1.disconnect() 登录服务器被关闭：" + connect.isConnected());
//					if (connect != null){
//						connect.disconnect();						
//					}
				}
			}
		}, filter);
	}
	
	/**
	 * 登录监听器
	 * @param connection
	 */
	private void addLoginListener(final XMPPConnection connection) {
		// TODO Auto-generated method stub
		ProviderManager.getInstance().addIQProvider("auth", "tcl:hc:portal", new LoginProvider());
		PacketFilter filter = new PacketTypeFilter(LoginResultIQ.class);
		connection.addPacketListener(new PacketListener() {
			
			@Override
			public void processPacket(Packet packet) {
				// TODO Auto-generated method stub
				IQ packageIq = (IQ) packet;
				Log.d(TAG,"AS返回结果:" + packageIq.toXML());
				Log.i(TAG, "addLoginListener Thread:" + Thread.currentThread());
				if (packageIq instanceof LoginResultIQ){
					LoginResultIQ loginResultIQ = (LoginResultIQ) packet;
					String errorCode = loginResultIQ.getErrorcode();
					loginFlag = false;
					reconnError = false;
					mAccessCount = 0;
					
					Log.i(TAG, "errorCode:" + errorCode);
					
					if (ReturnType.STATUS_SUCCESS.equals(errorCode)){
						// 创建微信用户绑定解绑定监听
						addNoticeListener();
						// 创建微信用户远程绑定监听
						addRemoteBindListener();
						// 创建推送消息监听
						addWeiMsgListener();
						// 创建监听服务器发送离线确认消息
						addServiceMsglListener();
						
						//fix by rex 2015/10/5
						//reason:获取用户列表成功时，再通知服务器
						//初始化完成
						/*InitFinish initfinish = new InitFinish(connection, null);	
						initfinish.sentPacket();*/
						Log.i(TAG, "EventType: TYPE_LOGIN_SUCCESS ,EventReason: SUCCESS!!" );
						if (mEventListener != null){
							mEventListener.onEvent(new XmppEvent(this, EventType.TYPE_LOGIN_SUCCESS, 
									EventReason.REASON_COMMON_SUCCESS, null));	
						} else {
							Log.w(TAG, "EventListener is NULL!!!");
						}
					} else {// 服务器返回错误码
						Log.e(TAG, "disconnection!!,  errorInfo:" + loginResultIQ.toXML());
						disconnection();
					}
				}
			}
		}, filter);
	}
	
	/**
	 * 绑定解绑定监听
	 * @param connection
	 */
	private void addNoticeListener() {

		ProviderManager.getInstance().addIQProvider("notice", "tcl:hc:wechat", new WeiNoticeMsgProvider());
		PacketFilter filter = new PacketTypeFilter(WeiNoticegResultIQ.class);
		mXmppConnect.addPacketListener(new PacketListener() {
			
			@Override
			public void processPacket(Packet packet) {
				IQ packetIq = (IQ) packet;
				Log.d(TAG, "Receive Notice package:" + packetIq.toXML());
				if (packetIq instanceof WeiNoticegResultIQ){
					WeiNoticegResultIQ weiNoticeResultIQ = (WeiNoticegResultIQ) packet;
					WeiNotice weiNotice = weiNoticeResultIQ.getWeiNotice();
					Log.i(TAG, "weiNotice:" + weiNotice);
					if (mEventListener != null){
						mEventListener.onEvent(new XmppEvent(this, EventType.TYPE_BIND_EVENT, 
								0, weiNotice));
					}
				}
			}
		}, filter);
	}
	
	/**
	 * 创建远程绑定监听
	 */
	private void addRemoteBindListener(){
		ProviderManager.getInstance().addIQProvider("remotebind", "tcl:hc:wechat", new WeiRemoteBindProvider());
		PacketFilter filter = new PacketTypeFilter(WeiRemoteBindResultIQ.class);
		mXmppConnect.addPacketListener(new PacketListener() {
			
			@Override
			public void processPacket(Packet packet) {
				// TODO Auto-generated method stub
				IQ packetIq = (IQ) packet;
				Log.d(TAG, "Receive Remotebind package::" + packetIq.toXML());
				if (packetIq instanceof WeiRemoteBindResultIQ){
					WeiRemoteBindResultIQ weiRemoteBindResultIQ = (WeiRemoteBindResultIQ) packet;
					BindUser bindUser = weiRemoteBindResultIQ
							.getWeiRemoteBind();
					Log.i(TAG, "bindUser:" + bindUser);
					if (mEventListener != null){
						mEventListener.onEvent(new XmppEvent(this, EventType.TYPE_REMOTE_BIND_EVENT, 
								0, bindUser));
					}
				}
			}
		}, filter);
	}
	
	/**
	 * 创建终端接收推送监听
	 */
	private void addWeiMsgListener(){
		ProviderManager.getInstance().addIQProvider("msg", "tcl:hc:wechat", new WeiMsgProvider());
		PacketFilter filter = new PacketTypeFilter(WeiMsgResultIQ.class);
		mXmppConnect.addPacketListener(new PacketListener() {
			
			@Override
			public void processPacket(Packet packet) {
				IQ packetIq = (IQ) packet;
				Log.d(TAG, "Receive WeiMsg package:" + packetIq.toXML());
				Log.i(TAG, "addWeiMsgListener Thread:" + Thread.currentThread());
				if (packetIq instanceof WeiMsgResultIQ){
					WeiMsgResultIQ weiMsgResultIQ = (WeiMsgResultIQ) packet;
					WeiXinMsgRecorder weixinMsg = weiMsgResultIQ.getWeiXinMsg();
					
					if (mEventListener != null){
						mEventListener.onEvent(new XmppEvent(this, EventType.TYPE_RECEIVE_WEIXINMSG, 
								0, weixinMsg));
					}
				}
			}
		}, filter);
	}
	
	/**
	 * 创建服务器判断终端是否离线消息监听
	 */
	private void addServiceMsglListener() {
		PacketFilter filter = new AndFilter(new PacketTypeFilter(Ping.class), 
				new IQTypeFilter(Type.GET));
		mXmppConnect.addPacketListener(new PacketListener() {
			
			@Override
			public void processPacket(Packet packet) {
				// TODO Auto-generated method stub
				Pong pong = new Pong(packet);
				mXmppConnect.sendPacket(pong);
				Log.d(TAG, "Send a online message to the server");
			}
		}, filter);
		
	}
	
	
	/**
	 * 增加事件监听器
	 */
	public void addListener(XmppEventListener listener) {
		mEventListener = listener;
	}
}
