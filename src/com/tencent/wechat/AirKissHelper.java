package com.tencent.wechat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.tcl.wechat.model.WeiXinMessage;
import com.tcl.wechat.model.file.DownloadFile;

public class AirKissHelper {
	
	private static long mDdeviceHandle;
	
	private HashMap<Long, WeiXinMessage> mReceivedMap;
	
	private HashMap<Long, DownloadFile> mDownloadFileMap;
	
	private static class AirKissHelperInstance{
		private static final AirKissHelper mInstance = new AirKissHelper();
	}

	@SuppressLint("UseSparseArrays")
	private AirKissHelper() {
		super();
		// TODO Auto-generated constructor stub
		mReceivedMap = new HashMap<Long, WeiXinMessage>();
		mDownloadFileMap = new HashMap<Long, DownloadFile>();
	}
	
	public static AirKissHelper getInstance(){
		return AirKissHelperInstance.mInstance;
	}
	
	/**
	 * 设备进行注册（Airkiss模式）
	 * @param memberId
	 */
	public void start(String memberId){
		
		if (!TextUtils.isEmpty(memberId)){
			mDdeviceHandle = AirKiss3.deviceInit(
	    			"gh_93cf5ac1fca7",  //设备型号
	    			memberId,			//设备id
	    			"AirKiss3_DEVICE_NAME",
	    			AirKiss3.AK3_SERVICE_WECHAT_MUSIC | AirKiss3.AK3_SERVICE_WECHAT_FILE,
	    			(short)15601);
	    	AirKiss3.deviceStart(mDdeviceHandle);
		}
	}
	
	/**
	 * 释放资源
	 */
	public void destory(){
		AirKiss3.deviceStop(mDdeviceHandle);
		AirKiss3.deviceFree(mDdeviceHandle);
	}
	
	public void addMessage(long  sessionId, WeiXinMessage message){
		if (mReceivedMap != null){
			mReceivedMap.put(sessionId, message);
		}
	}
	
	public WeiXinMessage getMessage(long  sessionId){
		if (mReceivedMap.containsKey(sessionId)){
			return mReceivedMap.get(sessionId);
		}
		return null;
	}
	
	public void removeMessage(long sessionId){
		if (mReceivedMap.containsKey(sessionId)){
			mReceivedMap.remove(sessionId);
		}
	}
	
	public void addDownFile(long sessionId, DownloadFile downloadFile){
		
		if (mDownloadFileMap != null ){
			mDownloadFileMap.put(sessionId, downloadFile);
		}
	}
	
	public DownloadFile getDownFile(long sessionId){
		if (mDownloadFileMap.containsKey(sessionId)){
			return mDownloadFileMap.get(sessionId);
		}
		return null;
	}
	
	public DownloadFile getDownFile(String downloadId){
		Iterator<Entry<Long, DownloadFile>> iterator = mDownloadFileMap.entrySet().iterator();
		
		while (iterator.hasNext()) {
			DownloadFile downloadFile = iterator.next().getValue();
			if (downloadId.equals(downloadFile.getDownloadId())){
				return downloadFile;
			}
		}
		return null;
	}
	
	public void removeDownFile(long sessionId){
		if (mDownloadFileMap.containsKey(sessionId)){
			mDownloadFileMap.remove(sessionId);
		}
	}
	
	public void updateDownLoadSize(long sessionId, long downLoadSize ){
		if (mDownloadFileMap.containsKey(sessionId)){
			mDownloadFileMap.get(sessionId).setDownLoadSize(downLoadSize);
		}
	}
}
