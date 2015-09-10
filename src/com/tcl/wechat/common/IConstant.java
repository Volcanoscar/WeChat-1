package com.tcl.wechat.common;


public interface IConstant {

	/**
	 * Launcher界面跳转逻辑处理
	 */
	//进入主界面
	public static final String ACTION_MAINVIEW = "com.tcl.action.MAINVIEW";
	//进入用户信息界面
	public static final String ACTION_USERINFO = "com.tcl.action.USERINFO";
	//进入聊天界面
	public static final String ACTION_CHATVIEW = "com.tcl.action.CHATVIEW";
	
	/**
	 * 媒体处理相关类
	 */
	//播放视频action
	public static final String ACTION_PLAY_VIDEO = "com.tcl.action.play.video";
	//播放音频action
	public static final String ACTION_PLAY_SOUND = "com.tcl.action.play.sound";
	//显示图片
	public static final String ACTION_SHOW_PIC= "com.tcl.action.show.pic";
	/**
	 * xmpp协议返回接口定义
	 * @author rex.lei
	 *
	 */
	public static final class CommandType{
		public static final int COMMAND_NEWWORK_NOT_AVAILABLE  = 0;
		public static final int COMMAND_NEWWORK_UNCONNECTED = 1;
		public static final int COMMAND_NEWWORK_TIME_OUT = 2;
		public static final int COMMAND_PLAY_VIEWO = 20;
		public static final int COMMAND_VIEWO_DOWNING = 21;
		public static final int COMMAND_VIEWO_FINISH = 22;
		public static final int COMMAND_GET_ICON_PIC = 100;
		public static final int COMMAND_REGISTER = 101;
		public static final int COMMAND_LOGIN = 102;
		public static final int COMMAND_GET_QR = 103;
		public static final int COMMAND_GET_BINDER = 104;
		public static final int COMMAND_UN_BINDER = 105;
		public static final int COMMAND_GET_WEIXIN_MSG = 106;
		public static final int COMMAND_GET_WEIXIN_NOTICE = 107;
		public static final int COMMAND_GET_WEIXIN_CONTROL = 108;
		public static final int COMMAND_BINDER_TOUI = 109;
		public static final int COMMAND_UNBINDER_TOUI = 110;
		public static final int COMMAND_GET_TVSTATUS = 111;
		
		public static final int LOGIN_GETDATA_SUCCESS = 112;		
		public static final int FRESH_NEWS = 113;
		public static final int LONGCONNECTION_EXIST = 114;
		public static final int COMMAND_REPORT_DEVICEINFO = 115;
		public static final int COMMAND_REMOTEBINDER = 116;
		public static final int SEND_REMOTEBIND_RESPONSE = 117;
		public static final int COMMAND_MSGRESPONSE = 118;
		public static final int COMMAND_REPORTTVSTATUS = 119;
		public static final int COMMAND_RESPONSETVSTATUS = 120;
		public static final int COMMAND_TVPROGRAMNOTICE = 121;
		public static final int COMMAND_RESPONSEBARRRAGE = 122;
		public static final int COMMAND_REPORTVIDEO = 123;
		
		
		public static final int NOTFINDVIDEOFILE = 124;
		
		public static final int COMMAND_UN_BINDER_ERROR = 125;
		
		public static final int COMMAND_DEVICEIDNULL = 126;
		public static final int COMMAND_MACNULL = 127;
		
		public static final int COMMAND_GET_WEIXIN_APP = 128;
	}
	
	//后台服务启动模式定义
	public static final class StartServiceMode{
		public static final String OWN = "own";//应用自身启动
		public static final String OTHERS = "others";//启动应用启动的，比如开机向导和家庭信箱
		public static  String CURRENTMODE = "own";//启动应用启动的，比如开机向导和家庭信箱
	}
}
