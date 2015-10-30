package com.tcl.wechat.common;

public class Config {
	
	/** 家庭云端口号：8348 */
	public static final int httpServicePort = 8843;
	
	/** Xmpp服务器地址 */
    protected static String serverAddr1 = "124.251.36.70";
    protected static String serverAddr2 = "124.251.36.70";
    
    /** Xmpp端口号*/
    protected static int serverPort1 = 5222;
    protected static int serverPort2 = 5222;
    
    /** 密码*/
    protected static String password = "6216f8a75fd5bb3d5f22b6f9958cdede3fc086c2";
    
    /** 客户端平台类型*/
    protected static String paltformType = "TCL_BIGPAD";
    
    /** 获取accesstoken地址*/
    public static String URL_ACCESS_TOKEN = "http://124.251.36.70/ws_as/getAccessToken";
    
    /** 获取媒体文件地址 */
    public static String URL_GET_MEDIA = "http://file.api.weixin.qq.com/cgi-bin/media/get";

}
