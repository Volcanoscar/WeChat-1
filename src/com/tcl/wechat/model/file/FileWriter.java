package com.tcl.wechat.model.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

import android.util.Log;

import com.tcl.wechat.common.IConstant.DownloadState;
import com.tcl.wechat.controller.WeiXinMsgManager;
import com.tcl.wechat.database.WeiRecordDao;
import com.tcl.wechat.utils.DataFileTools;
import com.tencent.wechat.AirKissHelper;

public class FileWriter {
	
	private static final String TAG = "TCLAirKiss3";
	
	// 下载状态：正常，暂停，下载中，已下载，排队中
	public static final int DOWNLOAD_STATE_NORMAL = 0x00;
	public static final int DOWNLOAD_STATE_PAUSE = 0x01;
	public static final int DOWNLOAD_STATE_DOWNLOADING = 0x02;
	public static final int DOWNLOAD_STATE_FINISH = 0x03;
	public static final int DOWNLOAD_STATE_WAITING = 0x04;
	
	private static FileWriter mInstance;

	//private DownloadFile mDownloadFile;
	
//	private HashMap<Long, DownloadFile> mDownLoadMap;

//	private File mSaveFile;

	//private long mDownSize;
	
	private FileWriter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static FileWriter getInstance(){
		
		if (mInstance == null){
			synchronized (FileWriter.class) {
				mInstance = new FileWriter();
			}
		}
		return mInstance;
	}
	
	public String createDownloadFile(DownloadFile file) {
		if (file == null){
			return null;
		}
		
		try {
			File dir = new File(DataFileTools.getRecordFilePath());
			if (!dir.exists()){
				dir.mkdirs();
			}
			File saveFile = new File(dir, file.getFileName());
			
			if (saveFile.exists()){//已经存在的文件，直接覆盖(MD5识别)
				saveFile = new File(dir, UUID.randomUUID() + file.getFileName());
			} 
			if (!saveFile.exists()){
				saveFile.createNewFile();
			}
			
			file.setSaveFile(saveFile);
			Log.i(TAG, "DownloadFile:" + file.toString());
			
			AirKissHelper.getInstance().addDownFile(file.getSessionid(), file);
			
			WeiXinMsgManager.getInstance().setMessageStatus(
					file.getDownloadId(), 
					DownloadState.STATE_START_DOWNLOAD);
			
			return saveFile.getAbsolutePath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 写入数据
	 * @param data
	 */
	public synchronized void write(long sessionId,byte[] data){
		
		DownloadFile downloadFile = AirKissHelper.getInstance().getDownFile(sessionId);
		
		if (downloadFile == null){
			return ;
		}
		
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(downloadFile.getSaveFile(), "rws");  
			raf.seek(downloadFile.getDownLoadSize()); 
			raf.write(data);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			long downloadSize = downloadFile.getDownLoadSize() + data.length;
			AirKissHelper.getInstance().updateDownLoadSize(sessionId, downloadSize);
			downloadFile.setDownLoadSize(downloadSize);
			update(downloadFile);
			try {
				if (raf != null){
					raf.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void update(DownloadFile downloadFile){
		if(downloadFile.getTotalSize() == downloadFile.getDownLoadSize()){
			
			//更新数据库，更新缓存
			WeiXinMsgManager.getInstance().setMessageStatus(
					downloadFile.getDownloadId(), 
					DownloadState.STATE_DOWNLOAD_CONPLETED);
			
			//更新文件路径（及状态）
			// WeiRecordDao.getInstance().updateFileName(
			// downloadFile.getDownloadId(),
			// downloadFile.getSaveFile().getAbsolutePath());
			AirKissHelper.getInstance().removeDownFile(downloadFile.getSessionid());
		}
	}
}
