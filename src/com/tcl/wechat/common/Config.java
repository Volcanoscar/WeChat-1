package com.tcl.wechat.common;

public class Config {
	
	/** 家庭云端口号：8343 */
	public static final int httpServicePort = 8843;
	
	public static boolean bTestFlag = false;
	
	/** Xmpp服务器地址 */
	public static String serverAddr1 = "wechatmsg.big.tclclouds.com";//"124.251.36.108";  
	/** 测试服务器地址*/
	public static String serverAddr2 = "wechat.big.tclclouds.com";//"124.251.36.70";
    
    /** Xmpp端口号*/
	public static int serverPort1 = 5222;
	/** 测试服务器端口*/
    public static int serverPort2 = 5222;
    
    /** 密码*/
    public static String password = "6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2";
    
    /** 客户端平台类型*/
    public static String paltformType = "TCL_BIGPAD";
    
    /** 获取accesstoken地址*/
    public static String URL_ACCESS_TOKEN = "http://wechatmsg.big.tclclouds.com/ws_as/getAccessToken";
    public static String URL_ACCESS_TOKEN2 = "http://wechat.big.tclclouds.com/ws_as/getAccessToken";
    
    /** 获取媒体文件地址 */
    public static String URL_GET_MEDIA = "http://file.api.weixin.qq.com/cgi-bin/media/get";
    
    public static int mWidgetStyleIndex = 0;//Widget默认风格

}
