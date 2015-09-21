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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.R;
import com.tcl.wechat.WeChatApplication;
import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.db.DeviceDao;
import com.tcl.wechat.db.WeiQrDao;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.modle.UpLoadFileInfo;
import com.tcl.wechat.modle.WeiNotice;
import com.tcl.wechat.modle.WeiXinMsgRecorder;
import com.tcl.wechat.utils.BaseUIHandler;
import com.tcl.wechat.utils.HttpUtil;
import com.tcl.wechat.utils.SystemInfoUtil;
import com.tcl.wechat.utils.ToastUtil;
import com.tcl.wechat.utils.UIUtils;

/**
 * @ClassName: WeiXmppManager
 * @Description: weixin协议管理类 tv-android-wechat（TV微信） tv-android-hc （TV家庭云）
 *               phone-android-hc (手机家庭云)
 */
public class WeiXmppManager {

	private static final String TAG = WeiXmppManager.class.getSimpleName();

	private static final int CONNECT_TIME = 5;
	private static final int ACCESS_TIME = 1; // 考虑到负载均衡，一次没连上，就要重新获取AS的地址，重新AS
	private static int GET_ACCESS_ADDR_TIME = 0;// 考虑到负载均衡，一次没连上，就要重新获取AS的地址，重新走登录流程不超过3次

	private Context mContext = null;

	/**
	 * 端口信息
	 */
	private String xmppHost1 = null;
	private String xmppHost2 = null;
	private int xmppPort1 = 0;
	private int xmppPort2 = 0;
	private String curHost = null;
	private int curport = 0;
	private String password = "";
	private String memberid = "";
	private String token = "";

	private XMPPConnection mXmppConnect = null;
	private BaseUIHandler mHandler = null;
	private boolean loginFlag = false;
	private boolean registerFlag = false;

	private static PacketListener authPacketListener = null,
			loginPacketListener = null, weiMsgPacketListener = null,
			weiNoticePacketListener = null, weiRemoteBindPacketListener = null,
			weiControlPacketListener = null, weiAppPacketListener = null,
			serviceMsgPacketListener = null, weiunberUserListener = null,
			weiUploadListener = null;

	private Boolean reconnError = false;
	private Thread reconnctionThtread = null;
	private Handler timeHandler = new Handler();

	private static final class WeiXmppManagerInstance {
		private static WeiXmppManager mInstance = new WeiXmppManager();
	}

	private WeiXmppManager() {
		xmppHost1 = "124.251.36.70";
		xmppHost2 = "124.251.36.70";
		curHost = xmppHost1;

		xmppPort1 = 5222;
		xmppPort2 = 5222;
		curport = xmppPort1;

		password = "6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2";

		mContext = WeChatApplication.gContext;
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
	 * 获取XMPPConnection对象
	 * 
	 * @return connection
	 */
	public XMPPConnection getConnection() {
		return mXmppConnect;
	}

	/**
	 * 设置XMPPConnection
	 * 
	 * @return connection
	 */
	public void setConnection(XMPPConnection connection) {
		mXmppConnect = connection;
		setReconnection();
	}

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
	 * @return mContext
	 */

	public Context getmContext() {
		return mContext;
	}

	/**
	 * @return mContext
	 */

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * 是否已经注册
	 * 
	 * @return
	 */
	public boolean isRegister() {
		boolean flag = false;
		String memberId = DeviceDao.getInstance().getMemberId();
		Log.i(TAG, "memberId:" + memberId);
		if (!TextUtils.isEmpty(memberId) && !memberId.trim().equals("")) {
			Log.i(TAG, "The memberId: " + memberId + " has been registered!");
			return true;
		}
		return flag;
	}

	/**
	 * 初始化监听器
	 */
	void initListener() {

		Log.d(TAG, "initListener");

		authPacketListener = null;
		loginPacketListener = null;
		weiMsgPacketListener = null;
		weiNoticePacketListener = null;
		weiRemoteBindPacketListener = null;
		weiControlPacketListener = null;
		weiAppPacketListener = null;
		serviceMsgPacketListener = null;
		weiunberUserListener = null;
		weiUploadListener = null;
		Getqr.initPacketListener();
		QueryBinder.initPacketListener();
		Unbind.initPacketListener();
	}

	/**
	 * @return the mHandler
	 */
	public Handler getmHandler() {
		return mHandler;
	}

	/**
	 * @param mHandler
	 *            the mHandler to set
	 */
	public void setmHandler(BaseUIHandler mHandler) {
		this.mHandler = mHandler;
	}

	/**
	 * @return the reconnError
	 */
	public Boolean getReconnError() {
		return reconnError;
	}

	/**
	 * @param reconnError
	 *            the reconnError to set
	 */
	public void setReconnError(Boolean reconnError) {
		this.reconnError = reconnError;
	}

	/**
	 * @Description:重连机制
	 */

	ConnectionListener connectionListener = new ConnectionListener() {

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

	/**
	 * 获取XMPPConnection对象
	 * 
	 * @return
	 */
	private XMPPConnection getXMPPConnection() {

//		String clientkeystore = "data/data/com.tcl.music/keystore";
//		String clienttruststore = "data/data/com.tcl.music/truststore";

		try {
			ConnectionConfiguration config = null;
			config = new ConnectionConfiguration(curHost, curport);
//			config.setKeystorePath(clientkeystore);
			config.setSecurityMode(SecurityMode.required);
			config.setSelfSignedCertificateEnabled(true);
			config.setSASLAuthenticationEnabled(true);
			config.setTruststorePassword("tclking");
//			config.setTruststorePath(clienttruststore);
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
	 * 客户端注册服务器函数 注册
	 */
	public void register() {

		Log.d(TAG, "=====================start register===============");

		if (!UIUtils.isNetworkAvailable()) {
			Log.i(TAG, "Network is not available!!");
			ToastUtil.showToastForced(R.string.notwork_not_available);
			return;
		}

		Log.d(TAG, "registerFlag =  " + registerFlag);
		if (registerFlag) {
			Log.d(TAG, "Has been registered successfully! ");
			return;
		}

		registerFlag = true;
		ToastUtil.showToast("Start to register");
		Log.d(TAG, "Start to register !");

		initListener();

		new Thread(new Runnable() {

			@Override
			public void run() {

				String deviceid = SystemInfoUtil.getDeviceId();
				if (TextUtils.isEmpty(deviceid)) {
					if (mHandler != null) {
						mHandler.sendEmptyMessage(CommandType.COMMAND_DEVICEIDNULL);
					}
					return;
				}

				String macAddr = SystemInfoUtil.getMacAddr();
				if (TextUtils.isEmpty(macAddr)) {
					Log.e(TAG,
							"Get Mac Address Failed, You should init DeviceDB first!");
					if (mHandler != null) {
						mHandler.sendEmptyMessage(CommandType.COMMAND_MACNULL);
					}
					return;
				}

				String uuid = WeiQrDao.getInstance().getUUID();
				Log.v(TAG, "uuid = " + uuid);
				if (TextUtils.isEmpty(uuid)) {
					Log.e(TAG, "Get uuid is NULL, You should init first!");
					return;
				}

				SmackConfiguration.setPacketReplyTimeout(10000);

				for (int i = 0; i < CONNECT_TIME; i++) {
					XMPPConnection connect = getXMPPConnection();
					try {
						Log.d(TAG,
								"=====================connecting...===============");
						Log.i(TAG, "ConnectCnt=" + (i + 1) + ",Host=" + curHost
								+ ", Port=" + curHost);
						connect.connect();
					} catch (XMPPException e) {
						Log.d(TAG,
								"==================connect failed!!===============");
						registerFlag = false;
						connect.disconnect();
						if (i == 1) { // 尝试两次都失败就换个域名和端口
							curport = xmppPort2;// 异常后就尝试另外一个端口注册
							curHost = xmppHost2;
						}
						e.printStackTrace();
					}
					// 连接登录服务器
					if (connect.isConnected()) {
						Log.d(TAG,
								"=====================connected===============");
						Log.d(TAG, "Server has been connected!");

						addRegisterListener(connect);
						// deviceID
						String content = "";

						String deviceidmac_id = deviceid + macAddr;
						// deviceidmac_id=
						// "6c99e7943d15d7cb6325c79d0837b3bf65344d77";
						content = "<addmaindevice xmlns=\"tcl:hc:login\">"
								+ "<deviceid>"
								+ deviceidmac_id
								+ "</deviceid>" // deviceid
												// 3xj53egiejw5tk1232abc34na123rfrer9q1aaa8
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
								+ "wechat" + "</type>" + "</addmaindevice>";
						IQ userContentIQ = new UserContentIQ(content);
						userContentIQ.setType(IQ.Type.SET);
						connect.sendPacket(userContentIQ);
						Log.i(TAG,
								"send register info:" + userContentIQ.toXML());
						return;
					}
				}
			}
		}).start();
	}

	/**
	 * 添加注册用户监听器
	 * 
	 * @param connection
	 */
	private void addRegisterListener(final XMPPConnection connection) {
		// 创建获取mumid监听
		ProviderManager.getInstance().addIQProvider("addmaindevice",
				"tcl:hc:login", new LSMemIDProvider());
		PacketFilter midfilter = new PacketTypeFilter(LSMemIDResultIQ.class);

		connection.addPacketListener(new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				IQ packageIq = (IQ) packet;
				Log.i(TAG, "Registration result:" + packageIq.toXML());

				try {
					if (packageIq instanceof LSMemIDResultIQ) {

						LSMemIDResultIQ lSMemIDResultIQ = (LSMemIDResultIQ) packet;
						String memberId = lSMemIDResultIQ.getMemberid();
						String errorCode = lSMemIDResultIQ.getErrorcode();
						Log.i(TAG, "memberId=" + memberId + ",errorCode="
								+ errorCode);

						if (memberId != null
								&& DeviceDao.getInstance().updateMemberId(
										memberId)) {
							// 注册成功
							Log.d(TAG,
									"=====================register successfully===============");
							ToastUtil.showToast("register successfully");
						} else {
							Log.e(TAG, "updateMemberId Failed!!");
							registerFlag = false;
						}

						if (mHandler != null && errorCode != null
								&& errorCode.equalsIgnoreCase("0")) {
							mHandler.sendEmptyMessage(CommandType.COMMAND_REGISTER);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}, midfilter);
	}

	/**
	 * 客户端登陆服务器函数
	 */
	public void login() {

		Log.d(TAG, "=====================start login===============");
		if (!UIUtils.isNetworkAvailable()) {
			Log.i(TAG, "Network is not available!!");
			ToastUtil.showToastForced("Network is not available");
			return;
		}

		if (loginFlag) {
			ToastUtil.showToast("Login successfully");
			return;
		}

		if (isConnected()) {
			Log.d(TAG, "Long connection exists, do not need to login");
			return;
		}

		ToastUtil.showToast("start login");

		loginFlag = true;
		timeHandler.removeCallbacks(mUpdateTimeTask);
		timeHandler.postDelayed(mUpdateTimeTask, WeiConstant.LOG_IN_TIMEOUT);

		initListener();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				SmackConfiguration.setPacketReplyTimeout(10000);
				for (int i = 0; i < CONNECT_TIME; i++) {
					XMPPConnection connect = getXMPPConnection();
					try {
						connect.connect();
					} catch (XMPPException e) {
						e.printStackTrace();
						connect.disconnect();
						Log.d(TAG,
								"==================connect failed!!===============");
						Log.i(TAG, "ConnectCnt=" + (i + 1) + ",Host=" + curHost
								+ ", Port=" + curHost);
						if (i == 1) {
							curport = xmppPort2;
							curHost = xmppHost2;
						}
					}
					// 连接登录服务
					if (connect.isConnected()) {

						Log.d(TAG,
								"=====================connected===============");
						Log.d(TAG, "Server has been connected!");

						addLoginListener(connect);

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
						Log.i(TAG, "send Login info:" + userLoginIQ.toXML());
						return;
					}
				}
			}
		}).start();
	}

	/**
	 * 添加登录监听器
	 * 
	 * @param connect
	 */
	private void addLoginListener(final XMPPConnection connect) {

		ProviderManager.getInstance().addIQProvider("auth", "tcl:hc:login",new LSLoginProvider());
		PacketFilter filter = new PacketTypeFilter(LSLoginResultIQ.class);
		connect.addPacketListener(authPacketListener = new PacketListener() {

			@Override
			public void processPacket(Packet packet) {
				IQ loginIq = (IQ) packet;
				Log.d(TAG, "Login Result:" + loginIq.toXML());
				try {
					if (packet instanceof LSLoginResultIQ) {
						LSLoginResultIQ loginResultIQ = (LSLoginResultIQ) packet;
						token = loginResultIQ.getToken();

						Log.d(TAG, "接入列表大小=" + loginResultIQ.getIpAndport().size());
						for (int i = 0; i < ACCESS_TIME; i++) {
							String asIp = "124.251.36.70";
							String asPort = "5223";
							try {
								ConnectionConfiguration config = null;
								config = new ConnectionConfiguration(asIp,
										Integer.valueOf(asPort));
								config.setSecurityMode(SecurityMode.required);
								config.setSASLAuthenticationEnabled(true);
								config.setCompressionEnabled(false);
								config.setDebuggerEnabled(true);// //////////////////////////
								config.setReconnectionAllowed(false);
								config.setDebuggerEnabled(true);
								XMPPConnection conn = new XMPPConnection(config);

								Log.d(TAG, "正在介入服务   asIp=" + asIp + ",asPort="
										+ asPort);

								conn.connect();

								Log.i(TAG, "接入地址连接是否成功:" + conn.isConnected());
								if (conn.isConnected()) {
									// 设置接入成功的长链接
									setConnection(conn);
									Log.d(TAG, "--接入地址成功，准备接入！");
									ProviderManager.getInstance().addIQProvider("auth", "tcl:hc:portal", new LoginProvider());
									PacketFilter loginFilter = new PacketTypeFilter(LoginResultIQ.class);// success
									if (loginPacketListener == null) {
										mXmppConnect.addPacketListener(loginPacketListener = new PacketListener() {

											@Override
											public void processPacket(Packet p) {

												IQ myIQ = (IQ) p;
												Log.d(TAG,"AS返回结果:" + myIQ.toXML());
												try {
													if (p instanceof LoginResultIQ) {

														LoginResultIQ loginResultIQ = (LoginResultIQ) p;
														String errorCode = loginResultIQ.getErrorcode();
														loginFlag = false;
														reconnError = false;
														GET_ACCESS_ADDR_TIME = 0;
														timeHandler.removeCallbacks(mUpdateTimeTask);
														if (errorCode.equalsIgnoreCase("0")) {
															// 创建微信用户绑定解绑定监听
															setNoticeListener();
															// 创建微信用户远程绑定监听
															setRemoteBindListener();
															// 创建推送消息监听
															setWeiMsgListener();
															// 创建遥控器监听
															setWeiControlListener();
															// 创建监听服务器发送离线确认消息
															setServiceMsglListener();
															// 创建监听获取accesstoken监听器
															setUploadFileListener();
															
															if (mHandler != null) {
																mHandler.sendEmptyMessage(CommandType.COMMAND_LOGIN);
															}
														} else {// 服务器返回错误码
															disconnection();
														}
													}
												} catch (Exception e) {
													e.printStackTrace();
												}
											}

										}, loginFilter);
									}

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
									Log.d(TAG, "Sending an access request："
											+ content);
									return;
								}

							} catch (XMPPException e1) {
								Log.i(TAG, "XPPP ERROR：" + e1.toString());
								e1.printStackTrace();
								disconnection();
							}
						}

						// 一个AS IP连接五次都失败了，那就重新登录获取AS的地址
						Log.i(TAG, "负载均衡，AS没连接上，重新走登录流程，重新获取AS地址-GET_ACCESS_ADDR_TIME=" + GET_ACCESS_ADDR_TIME);
						loginFlag = false;
						GET_ACCESS_ADDR_TIME++;
						if (GET_ACCESS_ADDR_TIME <= 3) {
							Thread.sleep(5000);
							relogin();
						} else {
							// 不再登录获取，重置获取ls次数为0
							GET_ACCESS_ADDR_TIME = 0;
							if (mHandler != null) {
								Message msg = mHandler.obtainMessage();
								msg.what = WeiConstant.LOG_IN_TIMEOUT;
								mHandler.sendMessage(msg);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Log.d(TAG, "server has been shut down!");
					connect.disconnect();
				}
			}
		}, filter);
	}

	/**
	 * @Description: 断开连接后重新登陆
	 */

	void relogin() {

		Log.d(TAG, "开始重新login");

		if (!UIUtils.isNetworkAvailable()) {
			Log.d(TAG, "网络不可以用");
			return;
		}
		if (loginFlag) {
			Log.d(TAG, "当前正在登陆");
			return;
		}
		if (isConnected()) {
			Log.d(TAG, "长连接存在，不用登陆");
			return;
		}

		initListener();
		// 断开连接后重新登陆
		WeiXmppManager.getInstance().login();
	}

	
	
	/**
	 * 创建绑定解绑定监听
	 */
	private void setNoticeListener() {

		ProviderManager.getInstance().addIQProvider("notice", "tcl:hc:wechat",
				new WeiNoticeMsgProvider());
		PacketFilter authFilter = new PacketTypeFilter(WeiNoticegResultIQ.class);// success
		if (weiNoticePacketListener == null) {
			mXmppConnect.addPacketListener(
					weiNoticePacketListener = new PacketListener() {

						@Override
						public void processPacket(Packet p) {

							IQ myIQ = (IQ) p;
							Log.d(TAG, "收到weixin用户绑定通知:" + myIQ.toXML());

							try {

								if (p instanceof WeiNoticegResultIQ) {
									WeiNoticegResultIQ weiNoticeResultIQ = (WeiNoticegResultIQ) p;
									WeiNotice weiNotice = weiNoticeResultIQ
											.getWeiNotice();
									if (mHandler != null && weiNotice != null) {
										mHandler.setData(weiNotice);
										mHandler.sendEmptyMessage(CommandType.COMMAND_GET_WEIXIN_NOTICE);
									}
								}

							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}, authFilter);
		}

	}

	/**
	 * 创建远程绑定监听
	 */
	private void setRemoteBindListener() {

		ProviderManager.getInstance().addIQProvider("remotebind",
				"tcl:hc:wechat", new WeiRemoteBindProvider());
		PacketFilter authFilter = new PacketTypeFilter(
				WeiRemoteBindResultIQ.class);// success
		if (weiRemoteBindPacketListener == null) {
			mXmppConnect.addPacketListener(
					weiRemoteBindPacketListener = new PacketListener() {

						@Override
						public void processPacket(Packet p) {

							IQ myIQ = (IQ) p;
							Log.d(TAG, "收到weixin用户远程绑定请求:" + myIQ.toXML());

							try {

								if (p instanceof WeiRemoteBindResultIQ) {
									WeiRemoteBindResultIQ weiRemoteBindResultIQ = (WeiRemoteBindResultIQ) p;
									BindUser bindUser = weiRemoteBindResultIQ
											.getWeiRemoteBind();

									// WeiRemoteBind weiRemoteBind =
									// weiRemoteBindResultIQ.getWeiRemoteBind();
									if (mHandler != null && bindUser != null) {
										mHandler.setData(bindUser);
										mHandler.sendEmptyMessage(CommandType.COMMAND_REMOTEBINDER);
									}
								}

							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}, authFilter);
		}

	}

	/**
	 * 创建终端接收推送监听
	 */
	private void setWeiMsgListener() {
		/**
		 * XML文本解析器，将信息转换成IQ
		 */
		ProviderManager.getInstance().addIQProvider("msg", "tcl:hc:wechat",
				new WeiMsgProvider());
		PacketFilter authFilter = new PacketTypeFilter(WeiMsgResultIQ.class);// success

		if (weiMsgPacketListener == null) {
			mXmppConnect.addPacketListener(
					weiMsgPacketListener = new PacketListener() {

						@Override
						public void processPacket(Packet p) {

							IQ myIQ = (IQ) p;
							Log.d(TAG, "收到weixin用户推送内容:" + myIQ.toXML());
							try {
								if (p instanceof WeiMsgResultIQ) {
									WeiMsgResultIQ weiMsgResultIQ = (WeiMsgResultIQ) p;
									WeiXinMsgRecorder weiXinMsg = weiMsgResultIQ
											.getWeiXinMsg();

									if (mHandler != null && weiXinMsg != null) {
										Log.i(TAG,
												"消息内容：" + weiXinMsg.toString());
										Message msg = new Message();
										msg.what = CommandType.COMMAND_GET_WEIXIN_MSG;
										Bundle b = new Bundle();
										b.putParcelable("weiXinMsg", weiXinMsg);
										msg.obj = b;
										mHandler.sendMessage(msg); // 向Handler发送消息,更新UI
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, authFilter);
		}
	}

	/**
	 * 创建终端接收推送监听
	 */
	private void setWeiControlListener() {

		ProviderManager.getInstance().addIQProvider("control", "tcl:hc:wechat",
				new WeiControlProvider());
		PacketFilter authFilter = new PacketTypeFilter(WeiControlResultIQ.class);// success
		if (weiControlPacketListener == null) {
			mXmppConnect.addPacketListener(
					weiControlPacketListener = new PacketListener() {

						@Override
						public void processPacket(Packet p) {

							IQ myIQ = (IQ) p;
							Log.d(TAG, "收到weixin用户推送内容:" + myIQ.toXML());

							try {

								if (p instanceof WeiControlResultIQ) {
									WeiControlResultIQ weiControlResultIQ = (WeiControlResultIQ) p;
									WeiXinMsgRecorder weiXinMsg = weiControlResultIQ
											.getWeiXinMsg();
									if (mHandler != null && weiXinMsg != null) {
										mHandler.setData(weiXinMsg);
										mHandler.sendEmptyMessage(CommandType.COMMAND_GET_WEIXIN_CONTROL);
									}
								}

							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}, authFilter);
		}
	}

	/**
	 * 创建终端接收应用、游戏安装、卸载或打开命令监听
	 */
	private void setWeiAppListener() {
		ProviderManager.getInstance().addIQProvider("app", "tcl:hc:wechat",
				new WeiControlProvider());
	}

	/**
	 * 创建服务器判断终端是否离线消息监听
	 */
	private void setServiceMsglListener() {
		PacketFilter PING_PACKET_FILTER = new AndFilter(new PacketTypeFilter(
				Ping.class), new IQTypeFilter(Type.GET));

		if (serviceMsgPacketListener == null) {
			mXmppConnect.addPacketListener(new PacketListener() {
				// Send a Pong for every Ping
				@Override
				public void processPacket(Packet packet) {
					Pong pong = new Pong(packet);
					mXmppConnect.sendPacket(pong);
					Log.i(TAG, "setServiceMsglListener发送确认在线的消息到服务器");
				}
			}, PING_PACKET_FILTER);
		}
	}

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			loginFlag = false;
			/*
			 * if (mHandler != null){ Message msg = mHandler.obtainMessage();
			 * msg.what = WeiConstant.LOG_IN_TIMEOUT; mHandler.sendMessage(msg);
			 * }
			 */
		}
	};
	
	private UpLoadFileInfo mUpLoadFileInfo;
	
	public UpLoadFileInfo getmUpLoadFileInfo() {
		return mUpLoadFileInfo;
	}

	public void setmUpLoadFileInfo(UpLoadFileInfo mUpLoadFileInfo) {
		this.mUpLoadFileInfo = mUpLoadFileInfo;
	}

	/**
	 * 上传文件至服务器
	 * @param type
	 * @param recorder
	 */
	public void upload(){
		Log.d(TAG, "=====================start upload===============");

		if (!UIUtils.isNetworkAvailable()) {
			Log.i(TAG, "Network is not available!!");
			ToastUtil.showToastForced("Network is not available");
			return;
		}

		ToastUtil.showToast("start upload");

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				SmackConfiguration.setPacketReplyTimeout(10000);

				XMPPConnection connect = getXMPPConnection();
				try {
					connect.connect();
				} catch (XMPPException e) {
					e.printStackTrace();
					connect.disconnect();
					Log.d(TAG,"==================connect failed!!===============");
				}
				// 连接登录服务
				if (connect.isConnected()) {

					Log.d(TAG, "=====================connected===============");
					Log.d(TAG, "Server has been connected!");

					String content = "<getaccesstoken xmlns=\"tcl:hc:portal\">"
							+ "</getaccesstoken>";

					IQ tokenIQ = new LSAccessTokenIQ(content);
					tokenIQ.setType(IQ.Type.SET);
					connect.sendPacket(tokenIQ);
					Log.d(TAG, "Sending an access request：" + content);
					return;
				}
			}
		}).start();
	}
	
	private void setUploadFileListener() {
		// TODO Auto-generated method stub
		/**
		 * XML文本解析器，将信息转换成IQ
		 */
		ProviderManager.getInstance().addIQProvider("getaccesstoken", "tcl:hc:portal", new AccessTokenProvider());
		PacketFilter midfilter = new PacketTypeFilter(LSAccessTokenIQ.class);

		if (weiUploadListener == null) {
			mXmppConnect.addPacketListener(weiUploadListener = new PacketListener() {

				@Override
				public void processPacket(Packet packet) {
					
					LSAccessTokenIQ tokenIQ = (LSAccessTokenIQ) packet;
					
					if (tokenIQ != null){
						Log.i(TAG, "tokenIQ:" + tokenIQ.toXML());
						
//						String errorCode = tokenIQ.getErrorcode();
//						String accessToken = tokenIQ.getAccesstoken();
//						UpLoadFileInfo fileInfo = getmUpLoadFileInfo();
						
//						HttpUtil.upload(fileInfo.getAccesstoken(), fileInfo.getType(), fileInfo.getFilePath());
						/*fileInfo.setAccesstoken(accessToken);
						if (mHandler != null && errorCode != null
								&& errorCode.equalsIgnoreCase("0")) {
							Message msg = mHandler.obtainMessage();
							msg.what = CommandType.COMMAND_SET_WEIXIN_MSG;
							msg.obj = fileInfo;
							mHandler.sendMessage(msg);
						}*/
					}
				}
			}, midfilter);
		}
	
	}
	
	/**
	 * 客户端解绑操作
	 */
	public void unbind(final BindUser bindUser) {
		Log.d(TAG, "=====================start unbind===============");
		if (bindUser == null) {
			Log.w(TAG, "unbind user is NULL!!");
			return;
		}
		if (!UIUtils.isNetworkAvailable()) {
			Log.i(TAG, "Network is not available!!");
			ToastUtil.showToastForced("Network is not available");
			return;
		}

		ToastUtil.showToast("start unbind");

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				SmackConfiguration.setPacketReplyTimeout(10000);

				XMPPConnection connect = getXMPPConnection();
				try {
					connect.connect();
				} catch (XMPPException e) {
					e.printStackTrace();
					connect.disconnect();
					Log.d(TAG,
							"==================connect failed!!===============");
				}
				// 连接登录服务
				if (connect.isConnected()) {

					Log.d(TAG, "=====================connected===============");
					Log.d(TAG, "Server has been connected!");

					addUnbindListener(connect, bindUser);

					String content = "<unbind xmlns=\"tcl:hc:wechat\">"
							+ "<openid>" + bindUser.getOpenId() + "</openid>"
							+ "<deviceid>"
							+ DeviceDao.getInstance().getDeviceId()
							+ "</deviceid>" + "</unbind>";

					IQ userUnbindIQ = new UnbindResultIQ(content);
					userUnbindIQ.setType(IQ.Type.SET);

					mXmppConnect.sendPacket(userUnbindIQ);
					Log.d(TAG, "Sending an access request：" + content);
					return;
				}
			}
		}).start();
	}

	private void addUnbindListener(final XMPPConnection connection,
			final BindUser bindUser) {
		ProviderManager.getInstance().addIQProvider("unbind", "tcl:hc:wechat",
				new UnbindProvider());
		PacketFilter filter = new PacketTypeFilter(LSLoginResultIQ.class);
		connection.addPacketListener(
				weiunberUserListener = new PacketListener() {

					@Override
					public void processPacket(Packet packet) {
						UnbindResultIQ packageIq = (UnbindResultIQ) packet;
						Log.i(TAG, "Registration result:" + packageIq.toXML());

						try {
							if (packageIq instanceof UnbindResultIQ) {

								UnbindResultIQ unbindResultIQ = (UnbindResultIQ) packet;
								String openId = unbindResultIQ.getopenid();
								String errorCode = unbindResultIQ
										.getErrorcode();
								Log.i(TAG, "openId=" + openId + ",errorCode="
										+ errorCode);

								if (openId != null
										&& openId.equals(bindUser.getOpenId())) {
									// 注册成功
									Log.d(TAG,
											"=====================Unbind successfully===============");
									ToastUtil.showToast("Unbind successfully");

								} else {
									Log.e(TAG, "updateMemberId Failed!!");
									mHandler.sendEmptyMessage(CommandType.COMMAND_UN_BINDER_ERROR);
									return;
								}

								if (mHandler != null && errorCode != null
										&& errorCode.equalsIgnoreCase("0")) {
									Message msg = mHandler.obtainMessage();
									msg.what = CommandType.COMMAND_UN_BINDER;
									msg.obj = openId;
									mHandler.sendMessage(msg);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (connection != null) {
								connection.disconnect();
							}
						}
					}
				}, filter);

	}
}
