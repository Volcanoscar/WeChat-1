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
}
