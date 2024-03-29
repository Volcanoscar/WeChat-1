package com.tcl.wechat.common;


public interface IConstant {
	//5s超时时间
	public static final int LOGIN_TIME_OUT = 2 * 1000;
	
	//进入主界面
	public static final String ACTION_MAINVIEW = "android.appwidget.action.MAINVIEW";
	//进入聊天界面
	public static final String ACTION_CHATVIEW = "android.appwidget.action.CHATVIEW";
	//进入聊天界面
	public static final String ACTION_CHATVIEW2 = "android.appwidget.action.CHATVIEW2";
	//用户解绑事件
	public static final String ACTION_UNBIND_ENENT = "android.appwidget.action.UNBIND_ENENT";
	
	/**
	 * 媒体处理相关类
	 */
	//切换风格
	public static final String ACTION_STYLE_CHANGE = "android.appwidget.action.STYLE_CHANGE";
	//播放视频action
	public static final String ACTION_PLAY_VIDEO = "android.appwidget.action.PLAY_VIDEO";
	//播放音频action
	public static final String ACTION_PLAY_AUDIO = "android.appwidget.action.PLAY_AUDIO";
	//显示图片
	public static final String ACTION_SHOW_IMAGE = "android.appwidget.action.SHOW_IMAGE";
	//显示图片
	public static final String ACTION_SHOW_TEXT = "android.appwidget.action.SHOW_TEXT";
	//显示位置信息
	public static final String ACTION_SHOW_LOCATION = "android.appwidget.action.SHOW_LOCATION";
	//显示链接内容
	public static final String ACTION_SHOW_LINK = "android.appwidget.action.SHOW_LINK";
	//显示文件内容
	public static final String ACTION_SHOW_FILE = "android.appwidget.action.SHOW_FILE";
	//显示音乐内容
	public static final String ACTION_SHOW_MUSIC = "android.appwidget.action.SHOW_MUSIC";
	
	public static final String ACTION_UPDATE_AUDIO_ANMI = "android.appwidget.action.UPDATE_AUDIO_ANMI";
	
	//清除数据
	public static final String ACTION_DATA_CLEARED = "com.mediatek.intent.action.SETTINGS_PACKAGE_DATA_CLEARED";
	
	//重启服务
	public static final String ACTION_START_SERVICE = "com.mediatek.intent.action.START_SERVICE";
	
	//闹铃
	public static final String ACTION_ONLINE_ALARM = "com.tcl.wechat.action.user_online_monitor";
		
	/**
	 * Action 定义
	 * @author rex.lei
	 *
	 */
	public static final class CommandAction{
		public static final String ACTION_LOGIN_SUCCESS = "com.tcl.wechat.ACTION_LOGIN_SUCCESS";
		public static final String ACTION_RECEIVE_WEIXIN_MSG = "com.tcl.wechat.ACTION_RECEIVE_WEIXIN_MSG";
		public static final String ACTION_RECEIVE_WEIXIN_NOTICE = "com.tcl.wechat.ACTION_RECEIVE_WEIXIN_NOTICE";
		public static final String ACTION_WEIXIN_CONTROL = "com.tcl.wechat.ACTION_WEIXIN_CONTROL";
		public static final String ACTION_GET_WEIXIN_APP = "com.tcl.wechat.ACTION_GET_WEIXIN_APP";
		public static final String ACTION_REMOTEBINDER = "com.tcl.wechat.ACTION_REMOTEBINDER";
		
		/************************************************************************
		 * 媒体相关类
		 ************************************************************************/
		//播放视频action
		public static final String ACTION_PLAY_VIDEO = "com.tcl.action.play.video";
		//播放音频action
		public static final String ACTION_PLAY_SOUND = "com.tcl.action.play.sound";
		//显示图片
		public static final String ACTION_SHOW_PIC= "com.tcl.action.show.pic";
		
		
		/************************************************************************
		 * Launcher界面跳转逻辑处理
		 ************************************************************************/
		//更新绑定用户信息
		public static final String ACTION_UPDATE_BINDUSER = "com.tcl.wechat.UPDATE_BINDUSER";
		//更新系统用户信息
		public static final String ACTION_UPDATE_SYSTEMUSER = "com.tcl.wechat.UPDATE_SYSTEMUSER";
		
		public static final String ACTION_DOWNLOAD_SERVICE = "com.tcl.wechat.DOWNLOAD_SERVICE";
		
		public static final String ACTION_APPWIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";
		
		public static final String ACTION_MSG_UPDATE = "android.appwidget.action.WEIXIN_MSG_UPDATE";
		
		public static final String ACTION_MSG_USER = "android.appwidget.action.UPDATE_USER";
	}
	
	/**
	 * 事件类型
	 * @author rex.lei
	 *
	 */
	public static final class EventType{
		public static final int TYPE_REGISTER_SUCCESS 	= 0x01; //事件类型：注册成功
		public static final int TYPE_REGISTER_FAILED  	= 0x02; //事件类型：注册失败
		public static final int TYPE_LOGIN_SUCCESS    	= 0x03; //事件类型：登录成功
		public static final int TYPE_LOGINFAILED      	= 0x04; //事件类型：登录失败
		
		public static final int TYPE_RESPONSE_SERVER    = 0x100;//事件类型：响应服务器事件
		public static final int TYPE_BIND_EVENT       	= 0x101;//事件类型：绑定事件
		public static final int TYPE_UNBIND_EVENT       = 0x102;//事件类型：解绑事件
		public static final int TYPE_REMOTE_BIND_EVENT 	= 0x103;//事件类型：远程绑定解绑
		public static final int TYPE_RECEIVE_WEIXINMSG 	= 0x104;//事件类型：接收微信消息
		public static final int TYPE_SEND_WEIXINMSG 	= 0x105;//事件类型：发送微信消息
		public static final int TYPE_GET_QR 			= 0x106;//事件类型：获取二维码
		public static final int TYPE_GET_BINDUSER       = 0x107;//事件类型：获取绑定用户
		public static final int TYPE_REPORT_DEVICEINFO  = 0x108;//事件类型：上报设备信息
		
		
		public static final int TYPE_NETWORK_ERROR      = 0x09;   //网络错误
	}
	
	/**
	 * 事件原因
	 * @author rex.lei
	 *
	 */
	public static final class EventReason{
		public static final int REASON_COMMON_SUCCESS 		= 0;
		public static final int REASON_COMMON_FAILED  		= 1;
		
		public static final int REASON_NETWORK_NOTAVAILABLE = 20000; //网络不可用
		public static final int REASON_NETWORK_UNCONNECT    = 20001; //网络未连接
		
		public static final int REASON_DEVICE_ERROR  		= 20101; //device为空或者不合法
		public static final int REASON_MAC_ERROR  	 		= 20102; //mac为空或者不合法
		public static final int REASON_UUID_ERROR  	 		= 20103; //uuid为空或者不合法
		public static final int REASON_CONNECT_FAILED	 	= 20104; //网络链接错误
		public static final int REASON_LOGIN_TIMEOUT	 	= 20105; //登录超时
		
	}
	
	/**
	 * xmpp请求参数key
	 *
	 */
	public final static class ParameterKey{
		public final static String PAGE = "page";
		public final static String STEP = "step";
		public final static String OPEN_ID = "openid";
	}
	
	/**
	 * 请求状态
	 */
	public static final class ReturnType{
		public static final String STATUS_SUCCESS = "0";
		public static final String STATUS_FAILED = "1";
	}
	
	//后台服务启动模式定义
	public static final class StartServiceMode{
		public static final String OWN = "own";//应用自身启动
		public static final String OTHERS = "others";//启动应用启动的，比如开机向导和家庭信箱
		public static  String CURRENTMODE = "own";//启动应用启动的，比如开机向导和家庭信箱
	}
	
	/**
	 * 消息来源
	 */
	public static final class ChatMsgSource{
		public static final String RECEIVEED = "0";
		public static final String SENDED = "1";
	}
	
	/**
	 * 消息发送状态
	 */
	public static final class ChatMsgStatus{
		public static final String SUCCESS = "0"; 	//发送成功
		public static final String FAILED =  "1";  	//发送失败
		public static final String SEND =    "2";  	//开始发送
		public static final String SENDING = "3";  	//发送中
	}
	
	/**
	 * 微信消息类
	 * @author rex.lei
	 *
	 */
	public static class ChatMsgType{
		
		public static final String IMAGE = "image";
		public static final String VIDEO = "video";
		public static final String VOICE = "voice";
		public static final String TEXT = "text";
		public static final String MAP = "location";
		public static final String WEB = "link";
		public static final String FILE = "file";
		public static final String MUSIC = "music";
		public static final String SHORTVIDEO = "shortvideo";
		public static final String BARRAGE = "barrage";
		public static final String NOTICE = "tvprogramnotice";
	}
	
	/**
	 * 下载状态
	 * @author rex.lei
	 *
	 */
	public static final class DownloadState{
		public static final String STATE_START_DOWNLOAD = "0";	//开始下载
		public static final String STATE_DOWNLOAD_PREGRESS = "1";//下载中
		public static final String STATE_DOWNLOAD_CONPLETED = "2";//下载完成
		public static final String STATE_DOWNLOAD_FAILED = "3";//下载失败
		public static final String STATE_FILE_NOTFOUND = "4";//文件未发现
		
		public static final String DOWNLOAD_COMPLETED = "String com.tcl.wechat.DOWNLOAD_COMPLETED";
	}
	
	/**
	 * SharedPreferences名称
	 * @author rex.lei
	 */
	public static final class SystemShared{
		public static final String DEFAULT_NAME = "detaultTypeValue";
		public static final String SHARE_TERMINAL_INFO = "terminal_info";
		
		//已经注册，用户等信息也保存完成
		public static final String KEY_REGISTENER_SUCCESS = "flag_registered";
		//是否已经进入聊天主界面
		public static final String KEY_FLAG_ENTER = "flag_enter";
		//最后一条消息记录
		public static final String SHARE_LAST_MSGTIME = "last_recorder";
	}
	
	//微信公众号ticket
	public static final String URL_TICKET = "http://file.api.weixin.qq.com/cgi-bin/media/upload?";
	

}
