package com.tcl.wechat.action.player.interf;

/**
 * 播放接口类
 * @author rex.lei
 *
 */
public interface MediaPlayerImpl {
	
	/**
	 * 播放
	 * @param filePath 播放音频文件路径
	 */
	public void play(String filePath);
	
	/**
	 * 暂停播放
	 */
	public void pause();
	
	/**
	 * 停止播放
	 */
	public void stop();
	
	/**
	 * 释放资源
	 */
	public void release();

}
