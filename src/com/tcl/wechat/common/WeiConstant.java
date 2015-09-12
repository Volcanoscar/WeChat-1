/**
 * -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */

package com.tcl.wechat.common;

import android.os.Environment;

/**
 * @ClassName: WeiConstant
 * @Description: weixin静态类定义
 */

public class WeiConstant {

	//家庭云端口号：8348
	public static final int httpServicePort=8843;
	
	//广播定义
	public static final class CommandBroadcast{
		public static final String LOGIN_SUCCESS = "com.tcl.webchat.LOGIN_SUCCESS";
		public static final String GET_WEIXIN_MSG = "com.tcl.webchat.GET_WEIXIN_MSG";
		public static final String GET_WEIXIN_NOTICE = "com.tcl.webchat.GET_WEIXIN_NOTICE";
		public static final String COMMAND_GET_WEIXIN_CONTROL = "com.tcl.webchat.GET_WEIXIN_CONTROL";
		public static final String COMMAND_GET_WEIXIN_APP = "com.tcl.webchat.GET_WEIXIN_APP";
		public static final String COMMAND_REMOTEBINDER = "com.tcl.webchat.GET_WEIXIN_REMOTEBINDER";
	}
	
	//xmpp协议返回接口定义
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
	
	//xmpp请求参数key
	public final static class ParameterKey{
		
		public final static String PAGE = "page";
		public final static String STEP = "step";
		public final static String OPEN_ID = "openid";
	}
	
	public static final class CommandReturnType{
		
		public static final String STATUS_SUCCESS = "0";
		public static final String STATUS_FAIL = "1";
	}
	
	
	//for ui
	public static final int FOCUS_RESUME_TIME = 800;
	/** 获取本地应用列表的信息 */
	public static final int APPINFO_COMMAND_GET_LOCAL_APP_LIST_PAGE_INFO = 8;
	
	/**
	 * 动画执行时间
	 */
	public static final class AnimEexTime
	{
		/** 焦点移动时间 */
		public static final int FOCUS_MOVE_TIME = 100;
	}
	
	/**
	 * 设置接收分享消息的方式,默认是true，直接弹出播放，false则保存消息。
	 */	
	public static boolean SET_SHARE_STYLE = true;
	
	/**
	 * 设置弹幕显示开关，默认true,显示弹幕；false 不显示弹幕
	 */
	
	public static boolean SETUP_DANMU = true;
	
	
	public class Constant {
		public static final String TV_TUNE_CHANNEL = "012300";
		
	}
	
	public static String DOWN_LOAD_SDCARD_PATH = Environment.getExternalStorageDirectory()+"/"
			+"Android/data/com.tcl.wechat/cache";
	//视频文件存储路径。
	public static String DOWN_LOAD_FLASH_PATH = "/data/data/com.tcl.wechat"+"/"+"cache";

 

	public static String WEIXIN_WEB = "http://file.api.weixin.qq.com"; 
	
	public static final int LOG_IN_TIMEOUT = 10000;
	public static final int WAIT_LOGO_TIME = 1000;
	public static final int TIME_OUT = 10000;

	//广播定义
		public static final class PlayStyle{
			public static final String CLICK_TO_PLAY = "CLICK_TO_PLAY";
			public static final String REALTIME_SHARE_PLAY = "REALTIME_SHARE_PLAY";

		}
	//后台服务启动模式定义
	public static final class StartServiceMode{
		public static final String OWN = "own";//应用自身启动
		public static final String OTHERS = "others";//启动应用启动的，比如开机向导和家庭信箱
		public static  String CURRENTMODE = "own";//启动应用启动的，比如开机向导和家庭信箱
	}
	public static String MAC = null;
	
	//根据系统内存大小配置全功能版或简化版（简化版就是去掉了讯飞的智控和识别，在微信应用里面对应，语音，直播换台，识别服务）
	public static final class WechatConfigure{
		public static String CurConfigure = "1";//全功能版本
		public static final String DefaultVer = "1";//全功能版本
		public static final String SimpleVer = "0";//简化版本
	}
	
	//CFROM：表示扫码来源（两个扫码来源come from，1是开机向导，2是微信互联），值有：1,2 。
	public static final class CFROM{
		public static final String Guide = "1";//全功能版本
		public static final String WeiXin = "2";//简化版本
	}
		
	//公众号的ticket
	public static final String ticket="http://we.qq.com/d/AQCvqyC3kjCTqtsDD9oU6mKorecLxifqDhpMvrno";
	
	public static String UUID = null;
}
