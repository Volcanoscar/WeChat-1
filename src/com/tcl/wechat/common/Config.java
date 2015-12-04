package com.tcl.wechat.common;

public class Config {
	
	/** 家庭云端口号：8343 */
	public static final int httpServicePort = 8843;
	
	/** Xmpp服务器地址 */
	public static String serverAddr1 = /*"wechatmsg.big.tclclouds.com";*/"124.251.36.108";  
	//for test :"wechat.big.tclclouds.com";//"124.251.36.70";
	public static String serverAddr2 = /*"wechatmsg.big.tclclouds.com";*/"124.251.36.108";
	//for test :"wechat.big.tclclouds.com";//"124.251.36.70";
    
    /** Xmpp端口号*/
	public static int serverPort1 = 5222;
    public static int serverPort2 = 5222;
    
    /** 密码*/
    public static String password = "6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2";
    
    /** 客户端平台类型*/
    public static String paltformType = "TCL_BIGPAD";
    
    /** 获取accesstoken地址*/
    public static String URL_ACCESS_TOKEN = "http://wechatmsg.big.tclclouds.com/ws_as/getAccessToken";
    //for test: public static String URL_ACCESS_TOKEN = "http://wechat.big.tclclouds.com/ws_as/getAccessToken";
    
    /** 获取媒体文件地址 */
    public static String URL_GET_MEDIA = "http://file.api.weixin.qq.com/cgi-bin/media/get";

}
