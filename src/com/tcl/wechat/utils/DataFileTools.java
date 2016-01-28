package com.tcl.wechat.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.common.IConstant.ChatMsgType;

/**
 * 获取文件相关路径
 * @author rex.lei
 *
 */
public class DataFileTools {
	
	private static final String TAG = DataFileTools.class.getSimpleName();
	
	private static final String FOLDER_APP_LOCAL = "WeChat";
	
	private static final String FOLDER_RECORD_AUDIO = "audio"; //音频文件路径
	
	private static final String FOLDER_RECORD_VIDEO = "video"; //视频文件路径
	
	private static final String FOLDER_RECORD_IMAGE = "image"; //图片文件路径
	
	private static final String FOLDER_RECORD_FILE = "file"; 	//文件路径
	
	private static final String FOLDER_TEMP = "temp";   		//临时缓存文件
	
	private static final String FOLDER_CACHE = "cache";			//缓存文件路径
 	
	
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
	public static String getExternalStorageDirectory(){
		if (isSdCardExist()){
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return null;
	}
	
	/**
	 * 判断SDCard是否存在
	 * @return
	 */
	public static boolean isSdCardExist() {
		  
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {//判断是否已经挂载
			return true;
		}
		return false;
	 }
	
	/**
	 * 获取录音文件存储路径 /wechat/cache/audio
	 * @return
	 */
	public static String getRecordAudioPath(){
		if (isSdCardExist()){
			return Environment.getExternalStorageDirectory() + 
					File.separator + FOLDER_APP_LOCAL +
					File.separator + FOLDER_CACHE +
					File.separator + FOLDER_RECORD_AUDIO;
		}
		return null;
	}
	
	/**
	 * 获取录制视频存储路径 /wechat/cache/video
	 * @return
	 */
	public static String getRecordVideoPath(){
		if (isSdCardExist()){
			return Environment.getExternalStorageDirectory() + 
					File.separator + FOLDER_APP_LOCAL +
					File.separator + FOLDER_CACHE +
					File.separator + FOLDER_RECORD_VIDEO;
		}
		return null;
	}
	
	/**
	 * 获取图像文件存储路径 /wechat/cache/image
	 * @return
	 */
	public static String getRecordImagePath(){
		if (isSdCardExist()){
			return Environment.getExternalStorageDirectory() + 
					File.separator + FOLDER_APP_LOCAL +
					File.separator + FOLDER_CACHE +
					File.separator + FOLDER_RECORD_IMAGE;
		}
		return null;
	}
	
	/**
	 * 获取图像文件存储路径 /wechat/cache/file
	 * @return
	 */
	public static String getRecordFilePath(){
		if (isSdCardExist()){
			return Environment.getExternalStorageDirectory() + 
					File.separator + FOLDER_APP_LOCAL +
					File.separator + FOLDER_CACHE +
					File.separator + FOLDER_RECORD_FILE;
		}
		return null;
	}
	
	/**
	 * 缓存存储路径 /wechat/cache
	 * @return
	 */
	public static String getCachePath(){
		if (isSdCardExist()){
			return Environment.getExternalStorageDirectory() + 
					File.separator + FOLDER_APP_LOCAL +
					File.separator + FOLDER_CACHE;
		}
		return null;
	}
	
	/**
	 * 获取临时文件路径/wechat/temp
	 * @return
	 */
	public static String getTempPath(){
		if (isSdCardExist()){
			return Environment.getExternalStorageDirectory() + 
					File.separator + FOLDER_APP_LOCAL +
					File.separator + FOLDER_TEMP;
		}
		return null;
	}
	
	/**
	 * 获取缓存文件绝对路径
	 * @param fileName
	 * @return
	 */
	public static String getCacheFilePath(String fileName){
		if (TextUtils.isEmpty(fileName)){
			return null;
		}
		String cachePath = getCachePath();
		String filePath = MD5Util.hashKeyForDisk(fileName);
		File dirFile = new File(cachePath, filePath);
		if (dirFile != null && dirFile.exists()){
			return dirFile.getAbsolutePath();
		}
		return null;
	}
	
	/**
	 * 获取图像文件绝对路径
	 * @param fileName
	 * @return
	 */
	public static String getImageFilePath(String url){
		if (TextUtils.isEmpty(url)){
			return null;
		}
		String fileName = getCacheImageFileName(url);
		String cachePath = getRecordImagePath();
		File file = new File(cachePath, fileName);
		if (file != null && file.exists()){
			return file.getAbsolutePath();
		}
		return null;
	}
	
	/**
	 * 获取音频文件绝对路径
	 * @param fileName
	 * @return
	 */
	public static String getAudioFilePath(String fileName){
		if (TextUtils.isEmpty(fileName)){
			return null;
		}
		String cachePath = getRecordAudioPath();
		File file = new File(cachePath, fileName);
		if (file != null && file.exists()){
			return file.getAbsolutePath();
		}
		return null;
	}
	
	/**
	 * 获取视频文件绝对路径
	 * @param fileName
	 * @return
	 */
	public static String getVideoFilePath(String url){
		if (TextUtils.isEmpty(url)){
			return null;
		}
		String fileName = MD5Util.hashKeyForDisk(url) + ".mp4";
		String cachePath = getRecordVideoPath();
		File file = new File(cachePath, fileName);
		if (file != null && file.exists()){
			return file.getAbsolutePath();
		}
		return null;
	}
	
	/**
	 * 获取文件绝对路径
	 * @param fileName
	 * @return
	 */
	public static String getFilePath(String fileName){
		if (TextUtils.isEmpty(fileName)){
			return null;
		}
		String cachePath = getRecordFilePath();
		File file = new File(cachePath, fileName);
		if (file != null && file.exists()){
			return file.getAbsolutePath();
		}
		return null;
	}
	
	/**
	 * 获取用户头像路径
	 * @param fileName
	 * @return
	 */
	public static String getBindUserIconPath(String fileName){
		if (TextUtils.isEmpty(fileName)){
			return null;
		}
		String cachePath = getRecordImagePath();
		return cachePath + File.separator + getCacheImageFileName(fileName);
	}
    
	/**
	 * 获取需要保存的文件
	 * @param type
	 * @param fileName
	 * @return
	 */
	public static File getSaveFilePath(String type, String fileName){
		String dirPath = null, filePath = null;
		if (ChatMsgType.VOICE.equals(type)){
			dirPath = getRecordAudioPath();
			filePath = MD5Util.hashKeyForDisk(fileName) + ".amr";
			
		} else if (ChatMsgType.VIDEO.equals(type)){
			dirPath = getRecordVideoPath();
			filePath = MD5Util.hashKeyForDisk(fileName) + ".mp4";
		}
		File dirFile = new File(dirPath);
		if (!dirFile.exists()){
			dirFile.mkdirs();
		}
		return new File(dirFile, filePath);
	}
	
	
	
	/**
     * 获取用户头像
     * @param filePath
     * @return
     */
    public static Bitmap getBindUserIcon(String fileName){
    	
    	String cachePath = getRecordImagePath();
    	if (cachePath != null){
    		try {
    			String filePath = cachePath + File.separator + getCacheImageFileName(fileName);
    			Log.i(TAG, "filePath:" + filePath);
				return BitmapFactory.decodeFile(filePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return null;
    }
    
    /**
     * 获取二维码图片
     * @param fileName
     * @return
     */
    public static Bitmap getQrBitmap(String fileName){
    	String imageCachePath = getRecordImagePath();
    	if (imageCachePath != null){
    		String filePath = imageCachePath + File.separator + 
    				getCacheImageFileName(imageCachePath);
    		Log.i(TAG, "filePath:" + filePath);
    		return BitmapFactory.decodeFile(filePath);
    	}
    	return null;
    }
    
    /**
     * 获取用户圆形头像
     * @param fileName
     * @return
     */
    public Bitmap getBindUserCircleIcon(String fileName){
    	if (TextUtils.isEmpty(fileName)){
    		Log.w(TAG, "filePath is NULL!!");
    		return null;
    	}
    	return ImageUtil.getInstance().createCircleImage(getBindUserIcon(fileName));
    }
    
    /**
     * 获取聊天缓存图片
     * @param fileName
     * @return
     */
    public Bitmap getChatImageIcon(String fileName){
    	String imageCachePath = getRecordImagePath();
    	if (imageCachePath != null){
    		String filePath = imageCachePath + File.separator + getCacheImageFileName(fileName);
    		Log.i(TAG, "filePath:" + filePath);
    		return BitmapFactory.decodeFile(filePath);
    	}
    	return null;
    }
    
    public static boolean fileExist(String dir, String fileName){
    	File dirFile = new File(dir);
    	if (dirFile == null || !dirFile.exists()) {
    		return false;
    	}
    	File file = new File(dirFile, fileName);
    	if (file != null && file.exists()) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * 获取缓存中图片的名称
     * @note 该名称有Volley开源架构生成
     * @param fileName 图片url作为文件名称来存储
     * @return
     */
    public static String getCacheImageFileName(String fileName){
    	//volley规则
    	return MD5Util.hashKeyForDisk(getCacheKey(fileName)) + ".0";
    }
    
    public static String getCacheImageFileName(String fileName, int maxWidth, int maxHeight){
    	//volley规则
    	return MD5Util.hashKeyForDisk(getCacheKey(fileName, maxWidth, maxHeight)) + ".0";
    }
    
    public static String getCacheKey(String url){
    	return getCacheKey(url, 0, 0);
    }
    
    public static String getCacheKey(String url, int maxWidth, int maxHeight) {
        return new StringBuilder(url.length() + 12).append("#W").append(maxWidth)
                .append("#H").append(maxHeight).append(url).toString();
    }
    
    
    /**
     * 清除用户图像缓存数据
     * @param fileName
     */
    public void clearBindUerIconCache(String fileName){
    	String cachePath = getRecordImagePath();
    	File file = new File(cachePath, fileName);
    	if (file != null && file.exists()){
    		file.delete();
    	}
    }
    
    /**
     * 清除图片缓存
     * @param fileName
     */
    public void clearImageRecorderCache(String path){
    	Log.i(TAG, "clear file:" + path);
    	if (TextUtils.isEmpty(path)){
    		return ;
    	}
    	String cachePath = getRecordImagePath();
    	
    	//大图原图
    	String fileName1 = getCacheImageFileName(path);
    	Log.i(TAG, "fileName:" + fileName1);
    	File file1 = new File(cachePath, fileName1);
    	if (file1 != null){
    		Log.i(TAG, "file.exists():" + file1.exists());
    		file1.delete();
    	}
    	//缩略图
    	String fileName2 = getCacheImageFileName(path, 400,400);
    	Log.i(TAG, "fileName:" + fileName2);
    	File file2 = new File(cachePath, fileName2);
    	if (file2 != null){
    		Log.i(TAG, "file.exists():" + file2.exists());
    		file2.delete();
    	}
    }
    
    /**
     * 清除语音缓存
     * @param fileName
     */
    public void clearAudioRecorderCache(String fileName){
    	String cachePath = getRecordVideoPath();
    	File file = new File(cachePath, fileName);
    	if (file != null && file.exists()){
    		file.delete();
    	}
    }
    
    /**
     * 清除视频缓存
     * @param fileName
     */
    public void clearVideoRecorderCache(String fileName){
    	String cachePath = getRecordVideoPath();
    	File file = new File(cachePath, fileName);
    	if (file != null && file.exists()){
    		file.delete();
    	}
    }
    
    /**
     * 清除所有图片缓存文件
     */
    public static void clearAllIamgeCache(){
    	try {
			String cachePath = getRecordImagePath();
			File file = new File(cachePath);
			if (file != null && file.exists()){
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(cachePath + File.separator + filelist[i]);
					delfile.delete();
				}
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 清除所有音频缓存文件
     */
    public static void clearAllAudioCache(){
    	try {
			String cachePath = getRecordAudioPath();
			File file = new File(cachePath);
			if (file != null && file.exists()){
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(cachePath + File.separator + filelist[i]);
					delfile.delete();
				}
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 清除所有视频缓存文件
     */
    public static void clearAllVideoCache(){
    	try {
			String cachePath = getRecordVideoPath();
			File file = new File(cachePath);
			if (file != null && file.exists()){
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(cachePath + File.separator + filelist[i]);
					delfile.delete();
				}
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 拷贝文件
     * @param from 原文件 
     * @param to 目标文件
     * @return true:拷贝成功 false:拷贝失败
     */
    public boolean copySystemDir(File from, File to) {

        try {
            FileUtils.copyDirectory(from, to);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * 拷贝文件
     * @param from
     * @param to
     * @return
     */
    public boolean copySystemDir(String from, String to) {
        return copySystemDir(new File(from), new File(to));
    }
    
    public boolean cleanDirectory(File dir) {
        try {
            FileUtils.cleanDirectory(dir);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean copyFile(String from,String to){
        return copyFile(new File(from),new File(to));
    }
    
    public boolean copyFile(File from, File to) {

        try {
            FileUtils.copyFile(from, to);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
    
}
