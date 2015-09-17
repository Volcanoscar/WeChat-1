package com.tcl.wechat.modle.data;

import java.io.File;
import java.io.FileNotFoundException;

import com.tcl.wechat.utils.ImageUtil;
import com.tcl.wechat.utils.MD5Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

/**
 * 获取文件相关路径
 * @author rex.lei
 *
 */
public class DataFileTools {
	
	private static final String TAG = DataFileTools.class.getSimpleName();
	
	private static final String FOLDER_APP_LOCAL = "WeChat";
	
	private static final String FOLDER_RECORD_SOUND = "sound";
	
	private static final String FOLDER_RECORD_VIDEO = "video";
	
	private static final String FOLDER_RECORD_IMAGE = "image";
	
	private static final String FOLDER_CACHE = "cache";
 	
	
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
	
	/**
	 * 获取图片存储路径 /wechat/image
	 * @return
	 */
	public String getRecordImagePath(){
		if (isSdCardExist()){
			return Environment.getExternalStorageDirectory() + 
					File.separator + FOLDER_APP_LOCAL +
					File.separator + FOLDER_RECORD_IMAGE;
		}
		return null;
	}
	
	/**
	 * 缓存存储路径 /wechat/cache
	 * @return
	 */
	public String getCachePath(){
		if (isSdCardExist()){
			return Environment.getExternalStorageDirectory() + 
					File.separator + FOLDER_APP_LOCAL +
					File.separator + FOLDER_CACHE;
		}
		return null;
	}
	
	
	/**
     * 获取用户头像
     * @param filePath
     * @return
     */
    public Bitmap getBindUserIcon(String fileName){
    	
    	String cachePath = getCachePath();
    	if (cachePath != null){
    		try {
    			String filePath = cachePath + File.separator + MD5Util.hashKeyForDisk(fileName);
    			Log.i(TAG, "filePath:" + filePath);
				return BitmapFactory.decodeFile(filePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return null;
    }
    
    /**
     * 获取用户圆形头像
     * @param fileName
     * @return
     */
    public Bitmap getBindUserCircleIcon(String fileName){
    	if (fileName == null && TextUtils.isEmpty(fileName)){
    		Log.w(TAG, "filePath is NULL!!");
    		return null;
    	}
    	return ImageUtil.getInstance().createCircleImage(getBindUserIcon(fileName));
    }
}
