package com.tcl.wechat.xmpp;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
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
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.WeApplication;
import com.tcl.wechat.common.Config;
import com.tcl.wechat.common.IConstant.EventReason;
import com.tcl.wechat.common.IConstant.EventType;
import com.tcl.wechat.common.IConstant.ReturnType;
import com.tcl.wechat.database.DeviceDao;
import com.tcl.wechat.database.WeiQrDao;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.model.WeiNotice;
import com.tcl.wechat.model.WeiXinMessage;
import com.tcl.wechat.utils.NetWorkUtil;
import com.tcl.wechat.utils.SystemInfoUtil;
import com.tencent.wechat.AirKissHelper;

/**
 * Xmpp管理类
 * 
 * @author rex.lei
 * 
 */
public class WeiXmppManager_bak {

	private static final String TAG = "WeiXmppManager";

	private Context mContext = null;

	private static int packetReplyTimeout = 5000;

	private static final int CONNECT_TIME = 5;
	private static final int ACCESS_TIME = 1;// 考虑到负载均衡，一次没连上，就要重新获取AS的地址，重新AS
	private static int mAccessCount = 0;// 考虑到负载均衡，一次没连上，就要重新获取AS的地址，重新走登录流程不超过3次
	/**
	 * 端口信息
	 */
	private int curport;
	private String curHost;
	private String password = "";
	private String memberid = "";
	private String token = "";
	private String deviceid = "";
	private String macAddress = "";
	private String uuid = "";

	private Thread reconnctionThtread = null;
	private XMPPConnection mXmppConnect = null;

	private XmppEventListener mEventListener;
	
	private PacketListener mAuthListener;
	private PacketListener mLoginListener;
	private PacketListener mNoticeListener;
	private PacketListener mRemoteBindListener;
	private PacketListener mWeiMsgListener;
	private PacketListener mServiceMsgListener;

	private boolean loginFlag = false;
	private boolean registerFlag = false;
	private boolean reconnError = false;
	private boolean authenticated = false;

	private static final class WeiXmppManagerInstance {
		private static WeiXmppManager_bak mInstance = new WeiXmppManager_bak();
	}

	private WeiXmppManager_bak() {
		curHost = Config.serverAddr1;

		curport = Config.serverPort1;

		password = "6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2";

		mContext = WeApplication.getContext();
		
		reconnctionThtread = new ReconnectionThread();
	}

	public static WeiXmppManager_bak getInstance() {
		return WeiXmppManagerInstance.mInstance;
	}
	
	/**
	 * 判断是否已经建立链接
	 */
	public boolean isConnected() {
		return mXmppConnect != null && mXmppConnect.isConnected();
	}
	
	public boolean isAuthenticated(){
		return authenticated;
	}

	/**
	 * 设置XMPPConnection
	 * 
	 * @return connection
	 */
	public void setConnection(XMPPConnection connection) {
		if (mXmppConnect != null) {
			mXmppConnect.disconnect();
		}
		mXmppConnect = connection;
		
		if (mXmppConnect != null){
			mXmppConnect.removeConnectionListener(connectionListener);
			mXmppConnect.addConnectionListener(connectionListener);
		}
	}

	/**
	 * 获取XMPPConnection
	 * 
	 * @return
	 */
	public XMPPConnection getConnection() {
		return mXmppConnect;
	}

	/**
	 * 设置重连监听
	 */
	private void setReconnection() {
		synchronized (reconnctionThtread) {
			if (reconnctionThtread == null || !reconnctionThtread.isAlive()) {
				reconnctionThtread = new ReconnectionThread();
				reconnctionThtread.setName("Xmpp Reconnection Thread");
				reconnctionThtread.start();
			}
		}
	}
	
	/**
	 * Sets the number of milliseconds to wait for a response from
     * the server.
	 * @param timeout
	 */
	private void setPacketReplyTimeout(int timeout){
		if (timeout < 5000) {
            packetReplyTimeout = 5000;
        }
		packetReplyTimeout = timeout;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(packetReplyTimeout);
					if (loginFlag){
						loginFlag = false;
						relogin();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
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
			setReconnection();
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
			setReconnection();
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
		Log.i(TAG, "disconnection...");
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
	 * 
	 * @return
	 */
	private boolean isNetworkAvailable() {
		if (!NetWorkUtil.isWifiConnected()) {
			Log.i(TAG, "Network is not available!!");
			if (mEventListener != null) {
				mEventListener.onEvent(new XmppEvent(this,
						EventType.TYPE_REGISTER_FAILED,
						EventReason.REASON_NETWORK_NOTAVAILABLE, null));
			}
			return false;
		}
		return true;
	}

	/**
	 * DeviceId是否合法
	 * 
	 * @return
	 */
	private boolean isDeviceIdLegal() {
		deviceid = SystemInfoUtil.getSerialNumber();
		Log.i(TAG, "DeviceId:" + deviceid);
		if (TextUtils.isEmpty(deviceid)) {
			Log.i(TAG, "DeviceId is not available!!");
			if (mEventListener != null) {
				mEventListener.onEvent(new XmppEvent(this,
						EventType.TYPE_REGISTER_FAILED,
						EventReason.REASON_DEVICE_ERROR, null));
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Mac地址是否合法
	 * 
	 * @return
	 */
	private boolean isMacAddressLegal() {
		macAddress = SystemInfoUtil.getLocalMacAddress();
		Log.i(TAG, "macAddress:" + macAddress);
		if (TextUtils.isEmpty(macAddress)) {
			Log.e(TAG,"Get Mac Address Failed, You should init DeviceDB first!");
			if (mEventListener != null) {
				mEventListener.onEvent(new XmppEvent(this,
						EventType.TYPE_REGISTER_FAILED,
						EventReason.REASON_MAC_ERROR, null));
			}
			return false;
		}
		return true;
	}

	/**
	 * UUID是否合法
	 * 
	 * @return
	 */
	private boolean isUUIDLegal() {
		uuid = WeiQrDao.getInstance().getUUID();
		Log.v(TAG, "uuid = " + uuid);
		if (TextUtils.isEmpty(uuid)) {
			Log.e(TAG, "Get uuid is NULL, You should init first!");
			if (mEventListener != null) {
				mEventListener.onEvent(new XmppEvent(this,
						EventType.TYPE_REGISTER_FAILED,
						EventReason.REASON_UUID_ERROR, null));
			}
			return false;
		}
		return true;
	}

	/**
	 * 获取XMPPConnection对象
	 * 
	 * @param ipAddr
	 *            ip地址
	 * @param port
	 *            端口
	 * @return
	 */
	private XMPPConnection getXMPPConnection(String ipAddr, int port) {

			Log.i(TAG, "ipAddr:" + ipAddr + ",port:" + port);
			ConnectionConfiguration config = null;
			config = new ConnectionConfiguration(ipAddr, port);
			config.setSecurityMode(SecurityMode.required);
			config.setSelfSignedCertificateEnabled(true);
			config.setSASLAuthenticationEnabled(true);
			config.setReconnectionAllowed(true);
			config.setDebuggerEnabled(true);
			XMPPConnection.DEBUG_ENABLED = true;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				config.setTruststoreType("AndroidCAStore");
				config.setTruststorePassword(null);
				config.setTruststorePath(null);
			} else {
				config.setTruststoreType("BKS");
				config.setTruststorePath("/system/etc/security/cacerts.bks");
			}

			XMPPConnection connect = new XMPPConnection(config);
			return connect;
	}

	/**
	 * 客户端注册用户
	 */
	public void register() {
		Log.d(TAG, "=====================start register===============");
		if (!isNetworkAvailable()) {
			Log.e(TAG, "register failed!!");
			return;
		}

		if (!isDeviceIdLegal() /*|| !isMacAddressLegal() || !isUUIDLegal()*/) {
			return; 
		}
		
		registerFlag = false;
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				SmackConfiguration.setPacketReplyTimeout(10 * 1000);
				for (int i = 0; i < CONNECT_TIME; i++) {
					XMPPConnection connect = null;
					try {
						connect = getXMPPConnection(curHost, curport);
						if (connect == null){
							return null;
						}
						Log.d(TAG, "start connect:" + System.currentTimeMillis());
						connect.connect();
						Log.d(TAG, "Connect finished:" + System.currentTimeMillis());
						
						if (connect != null && connect.isConnected() && !registerFlag) {

							addRegisterListener(connect);

							String content = "<addmaindevice xmlns=\"tcl:hc:login\">"
									+ "<deviceid>"
									+ /*deviceid*/  "TCLTESTREX23456"
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
									+ "</type>" + "</addmaindevice>";
							IQ userContentIQ = new UserContentIQ(content);
							userContentIQ.setType(IQ.Type.SET);
							connect.sendPacket(userContentIQ);
							Log.i(TAG, "send register info:" + userContentIQ.toXML());
							registerFlag = true;
							return null;
						} else {
							registerFlag = false;
						}
					} catch (Exception e) {
						e.printStackTrace();
						Log.w(TAG, e.getMessage() + " ,ConnectCnt=" + (i + 1)
								+ ",Host=" + curHost + ", Port=" + curport);
						registerFlag = false;
						if (connect != null){
							connect.disconnect();
						}
					}
				}
				return null;
			}
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}

	/**
	 * 客户端登录
	 */
	public void login() {

		Log.d(TAG, "=====================start login===============");
		if (!isNetworkAvailable()) {
			return;
		}
		
		if (loginFlag) {
			return;
		}

		if (isConnected()) {
			Log.d(TAG, "Long connection exists, do not need to login");
			return;
		}
		
		loginFlag = true;
		authenticated = false;
		/**
		 * 登录过程： 1、获取接入列表 accessServer() 2、开始登陆 startLogin()
		 */
		accessServer();
	}

	/**
	 * @Description: 断开连接后重新登陆
	 */
	private void relogin() {
		Log.d(TAG, "开始重新login");

		if (!isNetworkAvailable()) {
			Log.w(TAG, "network is unavaiable!!");
			return;
		}

		// 断开连接后重新登陆
		login();
	}

	/**
	 * 接入服务器，获取接入令牌
	 */
	private void accessServer() {

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				//SmackConfiguration.setPacketReplyTimeout(10000);
				setPacketReplyTimeout(5000);
				
				for (int i = 0; i < CONNECT_TIME; i++) {
					XMPPConnection connect = null;
					try {
						curHost = Config.serverAddr1;
						curport = Config.serverPort1;
						connect = getXMPPConnection(curHost, curport);
						
						Log.d(TAG, "start connect:" + System.currentTimeMillis());
						connect.connect();
						Log.d(TAG, "Connect finished:" + System.currentTimeMillis());
						
						if (connect != null && connect.isConnected()) {

							addAccessListener(connect);

							memberid = DeviceDao.getInstance().getMemberId();
							Log.i(TAG, "memberid:" + memberid);

							String content = "<auth xmlns=\"tcl:hc:login\">"
									+ "<username>" 
									+ "#" + memberid
									+ "@wechatmsg.big.tclclouds.com"
									+ "</username>" 
									+ "<password>"
									+ "</password>"
									+ "<digest></digest>"
									+ "<resource>tv-android-wechat</resource>"
									+ "</auth>";
							IQ userLoginIQ = new UserLoginIQ(content);
							userLoginIQ.setType(IQ.Type.GET);
							connect.sendPacket(userLoginIQ);
							Log.i(TAG,"send access time:" + System.currentTimeMillis() 
									+ ",info:" + userLoginIQ.toXML());
							return null;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.w(TAG, e.getMessage() + " ,ConnectCnt=" + (i + 1)
								+ ",Host=" + curHost + ", Port=" + curport);
						connect.disconnect();
						setReconnection();
					}
				}
				return null;
			}
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}

	/**
	 * 开始登陆
	 */
	private void startLogin() {

		Log.d(TAG, "start login..........");
		XMPPConnection connection = null;
		try {
			connection = getXMPPConnection(curHost, curport);
			
			Log.d(TAG, "start connect:" + System.currentTimeMillis());
			connection.connect();
			Log.d(TAG, "connect finish:" + System.currentTimeMillis());

			if (connection != null && connection.isConnected()) {
				
				setConnection(connection);
				
				addLoginListener(connection);

				String content = "<auth xmlns=\"tcl:hc:portal\">"
						+ "<username>" 
						+ "#" + memberid 
						+ "</username>"
						+ "<resource>tv-android-wechat</resource>" 
						+ "<token>"
						+ token 
						+ "</token>" 
						+ "</auth>";

				IQ userLoginIQ = new UserLoginIQ(content);
				userLoginIQ.setType(IQ.Type.SET);
				mXmppConnect.sendPacket(userLoginIQ);
				Log.d(TAG, "send login request：" + content);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.w(TAG, "connection error:" + e.getMessage());
			if (connection != null) {
				connection.disconnect();
			}
			setReconnection();
		}
	}

	/**
	 * 客户端注册监听器
	 * 
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
					if (packageIq instanceof LSMemIDResultIQ) {
						LSMemIDResultIQ iq = (LSMemIDResultIQ) packet;
						String memberId = iq.getMemberid();
						String errorCode = iq.getErrorcode();
						Log.i(TAG, "memberId=" + memberId + ",errorCode="
								+ errorCode);
						if (memberId != null && DeviceDao.getInstance().updateMemberId(memberId)) {
							
							//Airkiss模式设备注册
							AirKissHelper.getInstance().start(memberId);
							
							// 注册成功
							Log.d(TAG,"=====================register successfully===============");
							if (mEventListener != null) {
								mEventListener.onEvent(new XmppEvent(this,
												EventType.TYPE_REGISTER_SUCCESS,
												EventReason.REASON_COMMON_SUCCESS,
												null));
							}
						} else {
							Log.e(TAG, "updateMemberId Failed!!");
							if (mEventListener != null) {
								mEventListener.onEvent(new XmppEvent(this,
										EventType.TYPE_REGISTER_FAILED,
										EventReason.REASON_COMMON_FAILED,
										errorCode));
							}
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}  finally {
					if (connect != null) {
						Log.i(TAG,"Register finished, connect.disconnect");
						connect.disconnect();
					}
				}
			}
		}, filter);
	}

	/**
	 * 客户端接入监听器
	 * 
	 * @param connect
	 */
	private void addAccessListener(final XMPPConnection connection) {
		ProviderManager.getInstance().addIQProvider("auth", "tcl:hc:login",
				new LSLoginProvider());
		PacketFilter filter = new PacketTypeFilter(LSLoginResultIQ.class);
		if (mAuthListener != null){
			connection.removePacketListener(mAuthListener);
			mAuthListener = null;
		}
		connection.addPacketListener(mAuthListener = new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				LSLoginResultIQ loginResultIQ = (LSLoginResultIQ) packet;
				token = loginResultIQ.getToken();
				try {
					token = loginResultIQ.getToken();
					int size = loginResultIQ.getIpAndport().size();
					Log.d(TAG, "get access packet:" + packet.toXML());

					if (size == 0) {
						Log.e(TAG, "login Error, Access list size is NULL!!");
						return;
					}

					for (int i = 0; i < ACCESS_TIME; i++) {
						String str = loginResultIQ.getIpAndport().get(0);
						curHost = str.split(",")[0];
						curport = Integer.parseInt(str.split(",")[1]);
						Log.d(TAG, "curHost:" + curHost + ",curport:" + curport);
						loginFlag = false;
						startLogin();
						return;
					}
					// 一个AS IP连接五次都失败了，那就重新登录获取AS的地址
					Log.i(TAG, "负载均衡，AS没连接上，重新走登录流程，重新获取AS地址 mAccessCount:"
							+ mAccessCount);
					mAccessCount++;
					loginFlag = false;
					if (mAccessCount <= 3) {
						Thread.sleep(5000);
						relogin();
					} else {
						// 不再登录获取，重置获取ls次数为0
						mAccessCount = 0;
						if (mEventListener != null) {
							mEventListener.onEvent(new XmppEvent(this,
									EventType.TYPE_LOGINFAILED,
									EventReason.REASON_LOGIN_TIMEOUT, null));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Log.i(TAG,
							"conn1.disconnect() 登录服务器被关闭："
									+ connection.isConnected());
					 if (connection != null){
						 connection.disconnect();
					 }
				}
			}
		}, filter);
	}

	/**
	 * 登录监听器
	 * 
	 * @param connection
	 */
	private void addLoginListener(final XMPPConnection connection) {
		// TODO Auto-generated method stub
		ProviderManager.getInstance().addIQProvider("auth", "tcl:hc:portal",
				new LoginProvider());
		PacketFilter filter = new PacketTypeFilter(LoginResultIQ.class);
		
		if (mLoginListener != null){
			connection.removePacketListener(mLoginListener);
			mLoginListener = null;
		}
		connection.addPacketListener(mLoginListener = new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				// TODO Auto-generated method stub
				IQ packageIq = (IQ) packet;
				Log.d(TAG, "AS返回结果:" + packageIq.toXML());
				Log.i(TAG, "addLoginListener Thread:" + Thread.currentThread());
				if (packageIq instanceof LoginResultIQ) {
					LoginResultIQ loginResultIQ = (LoginResultIQ) packet;
					String errorCode = loginResultIQ.getErrorcode();
					loginFlag = false;
					reconnError = false;
					mAccessCount = 0;

					Log.i(TAG, "errorCode:" + errorCode);

					if (ReturnType.STATUS_SUCCESS.equals(errorCode)) {
						// 创建微信用户绑定解绑定监听
						addNoticeListener();
						// 创建微信用户远程绑定监听
						addRemoteBindListener();
						// 创建推送消息监听
						addWeiMsgListener();
						// 创建监听服务器发送离线确认消息
						addServiceMsglListener();
						//离线消息初始化
						InitFinish initfinish = new InitFinish(connection,  null); 
						initfinish.sentPacket();
						 
						authenticated = true;
						Log.i(TAG,"EventType: TYPE_LOGIN_SUCCESS ,EventReason: SUCCESS!!");
						if (mEventListener != null) {
							mEventListener.onEvent(new XmppEvent(this,
									EventType.TYPE_LOGIN_SUCCESS,
									EventReason.REASON_COMMON_SUCCESS, null));
						} else {
							Log.w(TAG, "EventListener is NULL!!!");
						}
					} else {// 服务器返回错误码
						Log.e(TAG, "disconnection!!,  errorInfo:"
								+ loginResultIQ.toXML());
						disconnection();
					}
				}
			}
		}, filter);
	}

	/**
	 * 绑定解绑定监听
	 * 
	 * @param connection
	 */
	private void addNoticeListener() {

		ProviderManager.getInstance().addIQProvider("notice", "tcl:hc:wechat",
				new WeiNoticeMsgProvider());
		PacketFilter filter = new PacketTypeFilter(WeiNoticegResultIQ.class);
		if (mNoticeListener != null){
			mXmppConnect.removePacketListener(mNoticeListener);
			mNoticeListener = null;
		}
		mXmppConnect.addPacketListener(mNoticeListener = new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				IQ packetIq = (IQ) packet;
				Log.d(TAG, "Receive Notice package:" + packetIq.toXML());
				if (packetIq instanceof WeiNoticegResultIQ) {
					WeiNoticegResultIQ weiNoticeResultIQ = (WeiNoticegResultIQ) packet;
					WeiNotice weiNotice = weiNoticeResultIQ.getWeiNotice();
					Log.i(TAG, "weiNotice:" + weiNotice);
					if (mEventListener != null) {
						mEventListener.onEvent(new XmppEvent(this,
								EventType.TYPE_BIND_EVENT, 0, weiNotice));
					}
				}
			}
		}, filter);
	}

	/**
	 * 创建远程绑定监听
	 */
	private void addRemoteBindListener() {
		ProviderManager.getInstance().addIQProvider("remotebind",
				"tcl:hc:wechat", new WeiRemoteBindProvider());
		PacketFilter filter = new PacketTypeFilter(WeiRemoteBindResultIQ.class);
		if (mRemoteBindListener != null){
			mXmppConnect.removePacketListener(mRemoteBindListener);
			mRemoteBindListener = null;
		}
		mXmppConnect.addPacketListener(mRemoteBindListener = new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				// TODO Auto-generated method stub
				IQ packetIq = (IQ) packet;
				Log.d(TAG, "Receive Remotebind package::" + packetIq.toXML());
				if (packetIq instanceof WeiRemoteBindResultIQ) {
					WeiRemoteBindResultIQ weiRemoteBindResultIQ = (WeiRemoteBindResultIQ) packet;
					BindUser bindUser = weiRemoteBindResultIQ
							.getWeiRemoteBind();
					Log.i(TAG, "bindUser:" + bindUser);
					if (mEventListener != null) {
						mEventListener.onEvent(new XmppEvent(this,
								EventType.TYPE_REMOTE_BIND_EVENT, 0, bindUser));
					}
				}
			}
		}, filter);
	}

	/**
	 * 创建终端接收推送监听
	 */
	private void addWeiMsgListener() {
		ProviderManager.getInstance().addIQProvider("msg", "tcl:hc:wechat",
				new WeiMsgProvider());
		PacketFilter filter = new PacketTypeFilter(WeiMsgResultIQ.class);
		if (mWeiMsgListener != null){
			mXmppConnect.removePacketListener(mWeiMsgListener);
			mWeiMsgListener = null;
		}
		mXmppConnect.addPacketListener(mWeiMsgListener = new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				IQ packetIq = (IQ) packet;
				Log.d(TAG, "Receive WeiMsg package:" + packetIq.toXML());
				Log.i(TAG, "WeiMsgListener Thread:" + Thread.currentThread());
				if (packetIq instanceof WeiMsgResultIQ) {
					WeiMsgResultIQ weiMsgResultIQ = (WeiMsgResultIQ) packet;
					WeiXinMessage weixinMsg = weiMsgResultIQ.getWeiXinMsg();

					if (mEventListener != null) {
						mEventListener.onEvent(new XmppEvent(this,
										EventType.TYPE_RECEIVE_WEIXINMSG, 0,
										weixinMsg));
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
		if (mServiceMsgListener != null){
			mXmppConnect.removePacketListener(mServiceMsgListener);
			mServiceMsgListener = null;
		}
		mXmppConnect.addPacketListener(mServiceMsgListener = new PacketListener() {

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
