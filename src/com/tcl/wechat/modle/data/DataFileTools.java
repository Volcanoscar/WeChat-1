package com.tcl.wechat.modle.data;

import java.io.File;

import android.os.Environment;

/**
 * 获取文件相关路径
 * @author rex.lei
 *
 */
public class DataFileTools {
	
	private static final String FOLDER_APP_LOCAL = "/WeChat";
	
	private static final String FOLDER_RECORD_SOUND = "/sound";
	
	private static final String FOLDER_RECORD_VIDEO = "/video";
 	
	
	private static class DataFileToolsInstance{
		private static final DataFileTools mInstance = new DataFileTools();
	}
	
	private DataFileTools() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 获取实例
	 * @return
	 */
	public static DataFileTools getInstance(){
		return DataFileToolsInstance.mInstance;
	}

	/**
	 * 获取存储器绝对地址
	 * @return
	 */
	public String getExternalStorageDirectory(){
		if (isSdCardExist()){
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return null;
	}
	
	/**
	 * 判断SDCard是否存在
	 * @return
	 */
	public boolean isSdCardExist() {
		  
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {//判断是否已经挂载
			return true;
		}
		return false;
	 }
	
	/**
	 * 获取录音文件存储路径 /wechat/sound
	 * @return
	 */
	public String getRecordSoundPath(){
		if (isSdCardExist()){
			return Environment.getExternalStorageDirectory() + 
					File.separator + FOLDER_APP_LOCAL +
					File.separator + FOLDER_RECORD_SOUND;
		}
		return null;
	}
	
	/**
	 * 获取录制视频存储路径 /wechat/video
	 * @return
	 */
	public String getRecordVideoPath(){
		if (isSdCardExist()){
			return Environment.getExternalStorageDirectory() + 
					File.separator + FOLDER_APP_LOCAL +
					File.separator + FOLDER_RECORD_VIDEO;
		}
		return null;
	}
	
	
}
