/**
 * -------------------------------------------------------------
   * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */

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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.db.WeiQrDao;
import com.tcl.wechat.modle.WeiNotice;
import com.tcl.wechat.modle.WeiRemoteBind;
import com.tcl.wechat.modle.WeiXinMsg;
import com.tcl.wechat.utils.BaseUIHandler;
import com.tcl.wechat.utils.CommonsFun;
import com.tcl.wechat.utils.UIUtils;
//import com.tcl.xian.StartandroidService.SqlCommon;

/**
 * @ClassName: WeiXmppManager
 * @Description: weixin协议管理类
 * tv-android-wechat（TV微信）
 *	tv-android-hc （TV家庭云）
 *	phone-android-hc (手机家庭云)


 */

public class WeiXmppManager {
	private static final String tag = "WeiXmppManager";
	
	private String TAG = this.getClass().getName();
	
	private static final int CONNECT_TIME = 5;
	private static final int ACCESS_TIME = 1;//考虑到负载均衡，一次没连上，就要重新获取AS的地址，重新AS
	private static  int GET_ACCESS_ADDR_TIME = 0;//考虑到负载均衡，一次没连上，就要重新获取AS的地址，重新走登录流程不超过3次
	public static WeiXmppManager weiXmppManager = null;
	private Context mContext = null;
	private String xmppHost1 = null;
	private String xmppHost2 = null;
	private String curHost = null;
	private int xmppPort1 = 0;
	private int xmppPort2 = 0;
	private int curport = 0;
	private String password = "";
	//private String deviceID = "";
	private String memberid = "senge";
	


	private String token = "";
	private  XMPPConnection  connection = null;
	private  BaseUIHandler mHandler = null;
	private  Boolean loginFlag = false;
	private  Boolean registerFlag = false;
	
	private static PacketListener authPacketListener=null,loginPacketListener = null,weiMsgPacketListener = null,
			weiNoticePacketListener = null,weiRemoteBindPacketListener = null,weiControlPacketListener=null,
			weiAppPacketListener = null,serviceMsgPacketListener;
	private Boolean reconnError  = false;
	private Thread reconnctionThtread = null;
	private Handler timeHandler = new Handler();
	public WeiXmppManager() {
		// xmppHost1 = "ls.wechat.dev.tventry.com";//正式地址
		// xmppHost2 = "ls2.wechat.dev.tventry.com";//南京移动网络80端口禁用情况,地址
		
//		xmppHost2 = "192.168.1.131";// 南京移动网络80端口禁用情况,地址
		xmppHost2 = "124.251.36.70";// 南京移动网络80端口禁用情况,地址

//		xmppHost1 = "124.251.36.70";// 正式地址
		xmppHost1 = "124.251.36.70";// 正式地址

		curHost = xmppHost1;
		// xmppPort1 = 80;
		xmppPort1 = 5222;
		xmppPort2 = 5222;// 南京移动网络80端口禁用情况
		// xmppPort2 = 8080;//南京移动网络80端口禁用情况
		curport = xmppPort1;
		password = "6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2";// ��������
		
	}
	
	public static WeiXmppManager getInstance() {
		if (null == weiXmppManager) {
			synchronized (WeiXmppManager.class) {
				weiXmppManager = new WeiXmppManager();
			}
			
		}
		return weiXmppManager;
	}
	
	/**
	 * 判断是否已经建立链接
	 */	
	public boolean isConnected() {
		
		return connection != null && connection.isConnected();
	}

	/**
	* @return connection
	*/
	
	public XMPPConnection getConnection() {
		return connection;
	}

	/**
	* @return connection
	*/
	
	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
		setReconnection();
	}
	
	/**
	 * ͣ关闭长连接
	 */	

	public void  disconnection() {

		if (connection!=null){
			connection.disconnect();
			connection = null;
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
	 * 初始化监听器
	 */
	void initListener(){
		
		Log.d(TAG, "initListener");
		
		authPacketListener = null;
		loginPacketListener = null;
		weiMsgPacketListener = null;
		weiNoticePacketListener = null;
		weiRemoteBindPacketListener = null;
		weiControlPacketListener=null;
		weiAppPacketListener = null;
		serviceMsgPacketListener=null;
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
	 * @param mHandler the mHandler to set
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
	 * @param reconnError the reconnError to set
	 */
	public void setReconnError(Boolean reconnError) {
		this.reconnError = reconnError;
	}
	
	public String getMemberid() {
		return "senge";
//		return memberid;
	}

	public void setMemberid(String memberid) {
		this.memberid = "senge";
//		this.memberid = memberid;
	}

	/**
	 * @Description:重连机制
	 */
	
    ConnectionListener connectionListener = new ConnectionListener() {  
    	  
        @Override  
        public void reconnectionSuccessful() {  
            Log.i(tag, "reconnectionSuccessful");  
            //relogin();       
        }  
  
        @Override  
        public void reconnectionFailed(Exception arg0) {  
            Log.i(tag, "reconnectionFailed");  
        }  
  
        @Override  
        public void reconnectingIn(int arg0) {  
            Log.i(tag, "reconnectingIn");  
        }  
  
        @Override  
        public void connectionClosedOnError(Exception arg0) {  
            Log.i(tag, "connectionClosedOnError--Exception="+arg0);  
            reconnError = true;
            relogin(); 
        }

		@Override
		public void connectionClosed() {
			// TODO Auto-generated method stub
			 Log.i(tag, "connectionClosed"); 
			// reconnError = true;
			// relogin(); 
		}  
    };
    
    /**
     * 设置重连监听
     */
    private void setReconnection(){
    	if (reconnctionThtread != null && reconnctionThtread.isAlive()){
			reconnctionThtread.interrupt();
			connection.removeConnectionListener(connectionListener);
			reconnctionThtread = null;	
		}

		reconnctionThtread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				connection.addConnectionListener(connectionListener); 
			}
		});
		reconnctionThtread.start();
    }
    
	/**
	 * 客户端注册服务器函数 注册
	 */
	public void register() {

		Log.d(tag, "webchat-tct -------------register");

		if (!UIUtils.isNetworkAvailable(mContext)) {
			Log.d(tag, "网络不可以用");
			Toast.makeText(mContext, "no network",Toast.LENGTH_LONG).show();
			return;
		}
		if (registerFlag){
			Log.d(tag, "注册标示为true,当前正在注册");
			return;
		}
		registerFlag = true;
		Log.d(tag, "--准备注册");
		initListener();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
	
				// TODO Auto-generated method stub
				//deviceid为空，不发送注册				
				String deviceid = CommonsFun.getDeviceId(mContext);
				if (deviceid == null || deviceid.trim() == null
						|| deviceid.trim().length() <= 0) {
					Log.d(tag, "webchat-tct -------------no deviceid");
					Log.d(tag, "机器没有deviceid");
					if (mHandler != null){
						mHandler.sendEmptyMessage(CommandType.COMMAND_DEVICEIDNULL);
					}
					return;
				}
				String mac = CommonsFun.getMAC();
				if(mac==null){
					Log.d(tag, "add on获取mac地址为空");
					if (mHandler != null){
						mHandler.sendEmptyMessage(CommandType.COMMAND_MACNULL);
					}
					return;
				}
				
				WeiQrDao weiQrDao = new WeiQrDao(mContext);
				String uuid = weiQrDao.find_uuid();
				Log.v(tag, "uuid = " + uuid);
					final String clientkeystore  = "data/data/com.tcl.music/keystore";
					final String clienttruststore  = "data/data/com.tcl.music/truststore";
		
					SmackConfiguration.setPacketReplyTimeout(10000);
					for (int i = 0 ;i < CONNECT_TIME ; i ++){
						ConnectionConfiguration config = null;					
						config = new ConnectionConfiguration(curHost, curport);
						config.setKeystorePath(clientkeystore);
		
						config.setSecurityMode(SecurityMode.required);
						config.setSelfSignedCertificateEnabled(true);
						config.setSASLAuthenticationEnabled(true);
						config.setTruststorePassword("tclking");
						config.setTruststorePath(clienttruststore);
						config.setTruststoreType("bks");
						config.setDebuggerEnabled(true);
						XMPPConnection.DEBUG_ENABLED = true;
						
						XMPPConnection con = new XMPPConnection(config);
						try {
							con.connect();
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							registerFlag = false;
							con.disconnect();
							e.printStackTrace();
							Log.i(tag,"第"+i+"次注册异常："+e);
							if(i==1){ //尝试两次都失败就换个域名和端口
								 curport = xmppPort2;//异常后就尝试另外一个端口注册
								 curHost = xmppHost2;
							 }
							
						}//连接登录服务器
						if(con.isConnected()){
						Log.d(tag, "webchat-tct -------------con.isConnected()");
						Log.d(tag, "LS接入成功");
						setAddMainDeviceListener(con);
						// deviceID
						String content = "";

						String deviceidmac_id = deviceid + mac;
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
								+ CommonsFun.getClienttype(mContext)
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
						con.sendPacket(userContentIQ);
						Log.d(tag, "发送注册请求" + userContentIQ.toXML());
						return;
					}
				}
			}
		}).start();
		
		
	}
	/**
	 * @param 创建注册监听
	 */
	private void setAddMainDeviceListener(final XMPPConnection conn){
		Log.d(tag, " setAddMainDeviceListener ------------------");
		//创建获取mumid监听
		ProviderManager.getInstance().addIQProvider("addmaindevice", "tcl:hc:login", new LSMemIDProvider());
		PacketFilter midfilter = new PacketTypeFilter(LSMemIDResultIQ.class);//success				
		conn.addPacketListener(new PacketListener() {

			@Override
			public void processPacket(Packet p) {
				Log.d(tag, " setAddMainDeviceListener -----processPacket-------------");

				IQ myIQ = (IQ) p;
				Log.d(tag, "addmaindevice获取memID返回结果:" + myIQ.toXML());
				
				try {

					if(p instanceof LSMemIDResultIQ){							
					

						LSMemIDResultIQ lSMemIDResultIQ = (LSMemIDResultIQ) p;
						String id = lSMemIDResultIQ.getMemberid();
						String error = lSMemIDResultIQ.getErrorcode();
						if (id != null){
							//setMemberid(id);//成功写入数据库之后才set到内存
							Log.d(tag, "注册memberid="+id);
						}else{
							registerFlag = false;//如果服务器返回id不对，点击icon可以再次注册
						}
						if (mHandler != null && error != null && error.equalsIgnoreCase("0")){
							mHandler.setData(lSMemIDResultIQ.getMemberid());
							mHandler.sendEmptyMessage(CommandType.COMMAND_REGISTER);	
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					Log.i(tag," 注册后主动关闭连接");	
					conn.disconnect();
				}

			}
		}, midfilter);
	}

	/**
	 * 客户端登陆服务器函数
	 */	
	public void login(){

		if (!UIUtils.isNetworkAvailable(mContext)){
			Toast.makeText(mContext, "no network",Toast.LENGTH_LONG).show();
			Log.d(tag, "网络不可以用");
			return;
		}
		if (loginFlag){
			Toast.makeText(mContext, "当前正在登陆",Toast.LENGTH_LONG).show();
			Log.d(tag, "当前正在登陆");
			return;
		}
		if (isConnected()){
			Log.d(tag, "长连接存在，不用登陆");
			return;
		}
		loginFlag = true;
		timeHandler.removeCallbacks(mUpdateTimeTask);
		timeHandler.postDelayed(mUpdateTimeTask, WeiConstant.LOG_IN_TIMEOUT);
		
		initListener();
		
		new Thread(new Runnable() { 
			
			@Override
			public void run() {
				// TODO Auto-generated method stub				
					final String clientkeystore  = "data/data/com.tcl.music/keystore";
					final String clienttruststore  = "data/data/com.tcl.music/truststore";
					SmackConfiguration.setPacketReplyTimeout(10000);
					
					for (int i = 0 ;i < CONNECT_TIME ; i ++){
						ConnectionConfiguration config = null;
						config = new ConnectionConfiguration(curHost, curport);
						config.setKeystorePath(clientkeystore);
						config.setSecurityMode(SecurityMode.required);
						config.setSelfSignedCertificateEnabled(true);
						config.setSASLAuthenticationEnabled(true);
						config.setTruststorePassword("tclking");
						config.setTruststorePath(clienttruststore);
						config.setTruststoreType("bks");
						config.setReconnectionAllowed(false);
						config.setDebuggerEnabled(true);
						XMPPConnection con = new XMPPConnection(config);	
						try {
							con.connect();
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							con.disconnect();
							Log.d(tag,"第"+i+ "次登录过程XMPPException"+e);
							if(i==1){// 尝试两次都失败就换个域名和端口
								curport =  xmppPort2;//异常后就尝试另外一个端口登陆
								curHost =  xmppHost2;
							}
							
						}//连接登录服务
						if (con.isConnected()){
							setLoginListener(con);
							String content = "<auth xmlns=\"tcl:hc:login\">" 
								+ "<username>" + "#" + memberid + "@wechat.big.tclclouds.com"
								+ "</username>" + "<password></password>"
										+ "<digest></digest>" 
										+ "<resource>tv-android-wechat</resource>" 
										+ "</auth>";
							IQ userLoginIQ = new UserLoginIQ(content);
							userLoginIQ.setType(IQ.Type.GET);
							con.sendPacket(userLoginIQ);
							Log.d(tag,"conn="+con.isConnected()+ "发送LS请求="+userLoginIQ.toXML());
							return;
						}else{
							//Log.d(tag,"第"+i+ "次登录过程connect失败");
						}
					}
				}
			
		}).start();
	}
	
	/**
	 * @Description: 设置登陆监听器
	 */
	
	private void setLoginListener(final XMPPConnection conn1){
		
	
		ProviderManager.getInstance().addIQProvider("auth", "tcl:hc:login", new LSLoginProvider());
		PacketFilter filter = new PacketTypeFilter(LSLoginResultIQ.class);//success		
		//if (authPacketListener == null){
			conn1.addPacketListener(authPacketListener = new PacketListener() {

				@Override
				public void processPacket(Packet p) {
					IQ myIQ = (IQ) p;
					Log.d(tag, "LS返回结果:" + myIQ.toXML());
					try {
						if(p instanceof LSLoginResultIQ){							
						
							LSLoginResultIQ loginResultIQ = (LSLoginResultIQ) p;
							//String strTid = loginResultIQ.getTid();							
							token = loginResultIQ.getToken();
							int size = loginResultIQ.getIpAndport().size();
							Log.d(tag, "接入列表大小="+size);
//						if (size == 0) {    dzm
//							Log.d(tag, "weixin登陆失败");
//							return;
//						}
							//尝试1次链接，否则重新获取AS地址。重走登录流程
							for (int i = 0 ;i < ACCESS_TIME ; i ++){
//								String str = loginResultIQ.getIpAndport().get(0);
								//for(String str : loginResultIQ.getIpAndport()){
//									String asIp = str.split(",")[0];
									String asIp;
									asIp = "124.251.36.70";
									//asIp = "54.223.144.236";
//									String asPort = str.split(",")[1];	
									String asPort;
							asPort = "5223";
									try{
										ConnectionConfiguration config = null;
										config = new ConnectionConfiguration(asIp, Integer.valueOf(asPort));									
										config.setSecurityMode(SecurityMode.required);
										config.setSASLAuthenticationEnabled(true);
										config.setCompressionEnabled(false);
										config.setDebuggerEnabled(true);////////////////////////////
										config.setReconnectionAllowed(false);
										config.setDebuggerEnabled(true);
										XMPPConnection conn = new XMPPConnection(config);
										
										Log.d(tag, "正在介入服务"+asIp + "," + asPort);
									
										conn.connect();//连接接入服务�?
										Log.d(tag, "接入地址连接是否成功"+conn.isConnected());
										boolean flag = conn.isConnected();
										if(flag){
											//设置接入成功的长链接
											setConnection(conn);
											Log.d(tag, "--接入地址成功，准备接入！");
											ProviderManager.getInstance().addIQProvider("auth", "tcl:hc:portal", new LoginProvider());
											PacketFilter loginFilter = new PacketTypeFilter(LoginResultIQ.class);//success	
											if (loginPacketListener == null){
												connection.addPacketListener(loginPacketListener = new PacketListener() {

													@Override
													public void processPacket(Packet p) {
										
														IQ myIQ = (IQ) p;
														Log.d(tag, "AS返回结果:" + myIQ.toXML());														
														try {

															if(p instanceof LoginResultIQ){							
																
																LoginResultIQ loginResultIQ = (LoginResultIQ) p;
																String err = loginResultIQ.getErrorcode();
																loginFlag = false;
																reconnError = false;
																GET_ACCESS_ADDR_TIME = 0;
																timeHandler.removeCallbacks(mUpdateTimeTask);
																if (err.equalsIgnoreCase("0")){
																	//创建微信用户绑定解绑定监听
																	setNoticeListener();
																	//创建微信用户远程绑定监听
																	setRemoteBindListener();
																	//创建推送消息监听
																	setWeiMsgListener();
																	//创建遥控器监听
																	setWeiControlListener();
																	//创建监听服务器发送离线确认消息
																	setServiceMsglListener();
																	/*//发送接收离线消息
																	InitFinish initfinish = new InitFinish(connection, null);	
																	initfinish.sentPacket();*/
																	if (mHandler != null){
																		mHandler.sendEmptyMessage(CommandType.COMMAND_LOGIN);
																	}
																}else{//服务器返回错误码
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
													+ "<username>" + "#" + memberid +"</username>" 
													+ "<resource>tv-android-wechat</resource>" 
													+ "<token>" + token + "</token>" + "</auth>";
											IQ userLoginIQ = new UserLoginIQ(content);
											userLoginIQ.setType(IQ.Type.SET);
											connection.sendPacket(userLoginIQ);
											Log.d(tag, "发送接入请求");
											return;
										}
					
									}catch( XMPPException e1){
										Log.i(tag,"XPPP异常："+e1);
										e1.printStackTrace();
										disconnection();
										
									}
								//}
							}
							//一个AS IP连接五次都失败了，那就重新登录获取AS的地址
							Log.i(tag,"负载均衡，AS没连接上，重新走登录流程，重新获取AS地址-GET_ACCESS_ADDR_TIME="+GET_ACCESS_ADDR_TIME);
							loginFlag = false;
							GET_ACCESS_ADDR_TIME++;
							if(GET_ACCESS_ADDR_TIME<=3){
								Thread.sleep(5000);
								relogin();
							}
							else{
								//不再登录获取，重置获取ls次数为0
								GET_ACCESS_ADDR_TIME = 0;
								if (mHandler != null){
									Message msg = mHandler.obtainMessage(); 
									msg.what = WeiConstant.LOG_IN_TIMEOUT;
									mHandler.sendMessage(msg);	
								}
							}
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						Log.i(tag,"conn1.disconnect() 登录服务器被关闭："+conn1.isConnected());	
						conn1.disconnect();						
					}
				}
			}, filter);
		//}
	}
	/**
	 * @Description: 断开连接后重新登陆
	 */
	
	void relogin(){
		
		Log.d(tag, "开始重新login");
		
		if (!UIUtils.isNetworkAvailable(mContext)){
			Log.d(tag, "网络不可以用");
			return;
		}
		if (loginFlag){
			Log.d(tag, "当前正在登陆");
			return;
		}
		if (isConnected()){
			Log.d(tag, "长连接存在，不用登陆");
			return;
		}
		
		initListener();
		// 断开连接后重新登陆
		WeiXmppManager.getInstance().login();
		/*new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
							
				String content = "<auth xmlns=\"tcl:hc:portal\">" 
						+ "<username>" + "#" + memberid +"</username>" 
						+ "<resource>tv-android-wechat</resource>" 
						+ "<token>" + token + "</token>" + "</auth>";
				IQ userLoginIQ = new UserLoginIQ(content);
				userLoginIQ.setType(IQ.Type.SET);
				connection.sendPacket(userLoginIQ);
			}
		}).start();*/
	}

	
	/**
	 * 创建绑定解绑定监听
	 */
	private void setNoticeListener(){

		ProviderManager.getInstance().addIQProvider("notice", "tcl:hc:wechat", new WeiNoticeMsgProvider());
		PacketFilter authFilter = new PacketTypeFilter(WeiNoticegResultIQ.class);//success
		if(weiNoticePacketListener == null){
			connection.addPacketListener(weiNoticePacketListener = new PacketListener() {

				@Override
				public void processPacket(Packet p) {
	
					IQ myIQ = (IQ) p;
					Log.d(tag, "收到weixin用户绑定通知:" + myIQ.toXML());
					
					try {

						if(p instanceof WeiNoticegResultIQ){							
							WeiNoticegResultIQ weiNoticeResultIQ = (WeiNoticegResultIQ) p;
							WeiNotice weiNotice = weiNoticeResultIQ.getWeiNotice();
							if (mHandler != null && weiNotice != null){
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
	private void setRemoteBindListener(){

		ProviderManager.getInstance().addIQProvider("remotebind", "tcl:hc:wechat", new WeiRemoteBindProvider());
		PacketFilter authFilter = new PacketTypeFilter(WeiRemoteBindResultIQ.class);//success
		if(weiRemoteBindPacketListener == null){
			connection.addPacketListener(weiRemoteBindPacketListener = new PacketListener() {

				@Override
				public void processPacket(Packet p) {
	
					IQ myIQ = (IQ) p;
					Log.d(tag, "收到weixin用户远程绑定请求:" + myIQ.toXML());
					
					try {

						if(p instanceof WeiRemoteBindResultIQ){							
							WeiRemoteBindResultIQ weiRemoteBindResultIQ = (WeiRemoteBindResultIQ) p;
							WeiRemoteBind weiRemoteBind = weiRemoteBindResultIQ.getWeiRemoteBind();
							if (mHandler != null && weiRemoteBind != null){
								mHandler.setData(weiRemoteBind);
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
	private void setWeiMsgListener(){
		
		ProviderManager.getInstance().addIQProvider("msg", "tcl:hc:wechat", new WeiMsgProvider());
		PacketFilter authFilter = new PacketTypeFilter(WeiMsgResultIQ.class);//success
		Log.d(tag, "setWeiMsgListener -----------------------------------------------------");
		if(weiMsgPacketListener == null){
			connection.addPacketListener(weiMsgPacketListener = new PacketListener() {

				@Override
				public void processPacket(Packet p) {
	
					IQ myIQ = (IQ) p;
					Log.d(tag, "收到weixin用户推送内容:" + myIQ.toXML());
					
					try {

						if(p instanceof WeiMsgResultIQ){							
							WeiMsgResultIQ weiMsgResultIQ = (WeiMsgResultIQ) p;
							WeiXinMsg weiXinMsg = weiMsgResultIQ.getWeiXinMsg();
							if (mHandler != null && weiXinMsg != null){
								/*mHandler.setData(weiXinMsg);
								mHandler.sendEmptyMessage(CommandType.COMMAND_GET_WEIXIN_MSG);*/
								Log.i(tag,"weiXinMsg.getUrl()="+weiXinMsg.getUrl());
								Message msg = new Message();
								msg.what = CommandType.COMMAND_GET_WEIXIN_MSG;
								msg.obj = weiXinMsg;								
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
	private void setWeiControlListener(){
		
		ProviderManager.getInstance().addIQProvider("control", "tcl:hc:wechat", new WeiControlProvider());
		PacketFilter authFilter = new PacketTypeFilter(WeiControlResultIQ.class);//success
		if(weiControlPacketListener == null){
			connection.addPacketListener(weiControlPacketListener = new PacketListener() {

				@Override
				public void processPacket(Packet p) {
	
					IQ myIQ = (IQ) p;
					Log.d(tag, "收到weixin用户推送内容:" + myIQ.toXML());
					
					try {

						if(p instanceof WeiControlResultIQ){							
							WeiControlResultIQ weiControlResultIQ = (WeiControlResultIQ) p;
							WeiXinMsg weiXinMsg = weiControlResultIQ.getWeiXinMsg();
							if (mHandler != null && weiXinMsg != null){
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
	private void setWeiAppListener(){
		ProviderManager.getInstance().addIQProvider("app", "tcl:hc:wechat", new WeiControlProvider());
	}
	/**
	 * 创建服务器判断终端是否离线消息监听
	 */
	private void setServiceMsglListener(){
	    PacketFilter PING_PACKET_FILTER = new AndFilter(
                new PacketTypeFilter(Ping.class), new IQTypeFilter(Type.GET));
		
		if(serviceMsgPacketListener == null){
			connection.addPacketListener(new PacketListener() {
	            // Send a Pong for every Ping
	            @Override
	            public void processPacket(Packet packet)  {
	                Pong pong = new Pong(packet);
	                connection.sendPacket(pong);
	                Log.i(tag, "setServiceMsglListener发送确认在线的消息到服务器");
	            }
	        }, PING_PACKET_FILTER);
		}
	}
	
	 private Runnable mUpdateTimeTask = new Runnable() {     
			
			public void run() { 
				loginFlag = false;
				/*if (mHandler != null){
					Message msg = mHandler.obtainMessage(); 
					msg.what = WeiConstant.LOG_IN_TIMEOUT;
					mHandler.sendMessage(msg);	
				}*/
			}
		};
	
}
