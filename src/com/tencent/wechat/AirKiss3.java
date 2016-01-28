package com.tencent.wechat;

import java.io.UnsupportedEncodingException;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.common.IConstant.ChatMsgType;
import com.tcl.wechat.common.IConstant.DownloadState;
import com.tcl.wechat.controller.WeiXinMsgControl;
import com.tcl.wechat.model.WeiXinMessage;
import com.tcl.wechat.model.file.DownloadFile;
import com.tcl.wechat.model.file.FileInfo;
import com.tcl.wechat.model.file.FileParser;
import com.tcl.wechat.model.file.FileWriter;
import com.tcl.wechat.model.file.MusicFileInfo;
import com.tcl.wechat.model.file.WXMsgFile;
import com.tcl.wechat.model.file.WXMsgMusicFile;

/**
 * 微信Airkiss模式接收文件
 * @author rex.lei
 *
 */
public class AirKiss3 {

	private static final String TAG = "TCLAirKiss3";
	
	public static final int AK3_SERVICE_WECHAT_MUSIC = 0x01;
	public static final int AK3_SERVICE_WECHAT_FILE = 0x02;
	
	public static final int AK3_EVENT_TCP_DISCONNECTED = -2;	
	public static final int AK3_EVENT_ERROR = -1;
	public static final int AK3_EVENT_WECHAT_MUSIC = 1;
	public static final int AK3_EVENT_WECHAT_FILE = 2;
	public static final int AK3_EVENT_WECHAT_FILE_DATA = 3;
	
	/*AK3_EVENT_ERROR 错误码arg1*/
	/*对于ERROR_CREATE_*错误，底层会stop设备，上层需要调用ak3_device_start重试，或ak3_device_free释放资源*/
	public static final int ERROR_CREATE_EVENT_BASE = -1;
	public static final int ERROR_CREATE_TCP_SOCKET = -2;
	public static final int ERROR_CREATE_UDP_SOCKET = -3;
	public static final int ERROR_NOT_JSON_DATA = -4;
	public static final int ERROR_DEV_NOT_SUPPORT_SERVICE = -5;
	
	public interface AirKissListener{
		public void onDeviceCallback(long deviceHandle, int event, int error, byte[] data);
	}
	
	/*
	 * 初始化一个设备，成功则返回设备句柄，失败则返回0
	 * services:
	 * 		设备支持的service，取值为AK3_SERVICE_*，如设备支持多个service可以用|来组合
	 * port:
	 * 		设备所监听的服务端口
	 */
	public static native long deviceInit(String deviceType, String deviceId, String deviceName, int services, short port);
	
	/*
	 * 释放设备句柄和相关资源（如果设备服务未停止，内部会调用deviceStop()停止服务）
	 */
	public static native void deviceFree(long deviceHandle);
	
	/*
	 * 启动设备服务，成功则返回0，失败则返回-1（成功调用后会启动一个服务线程）
	 */
	public static native int deviceStart(long deviceHandle);
	
	/*
	 * 停止设备服务
	 */
	public static native void deviceStop(long deviceHandle);
	
	/*
	 * 设备发送通知数据给手机
	 */
	public static native void deviceNotify(long deviceHandle, String notifyData);
	
	/*
	 * 设备callback函数，注意此函数由运行于JNI层的线程来调用，建议此callback中不要直接处理业务，而是将数据发送给工作线程来处理
	 * 
	 * event:
	 * 		 K3_EVENT_TCP_DISCONNECTED：
	 *          arg0:无意义
	 * 			arg1:错误代码
	 * 			sessionId：会话Id
	 * 		AK3_EVENT_ERROR:
	 * 			error:错误代码
	 * 			data :无意义
	 *          sessionId:无意义
	 * 		AK3_EVENT_WECHAT_MUSIC:
	 * 			error:无意义
	 * 			data :MUSIC数据(UTF-8编码的字符串，JSON格式）
	 *          sessionId：会话Id
	 * 		AK3_EVENT_WECHAT_FILE:
	 * 			error:无意义
	 * 			data :FILE数据(UTF-8编码的字符串，JSON格式）
	 *          sessionId：会话Id
	 * 		AK3_EVENT_WECHAT_FILE_DATA:
	 * 			error:无意义
	 * 			data :FILE数据(二进制）
	 *          sessionId：会话Id
	 */
	public static void onDeviceCallback(long deviceHandle, int event, int error, byte[] data, long sessionId) {
		
		
		String jsonData = null;
		try {
			jsonData = new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (TextUtils.isEmpty(jsonData)){
			Log.w(TAG, "Json data is empty!!");
			return  ;
		}
		
		switch (event) {
		case AK3_EVENT_ERROR:
			
			break;
			
		case AK3_EVENT_WECHAT_MUSIC:
			Log.d(TAG, "[AK3_EVENT_WECHAT_MUSIC]deviceHandle:" + deviceHandle + ",event:" + event + ", errorCode:" + error + 
					"sessionId:" + sessionId + "\n data:" + jsonData);
			MusicFileInfo musicInfo = FileParser.parseMusicFile(jsonData);
			if (musicInfo != null){
				Log.d(TAG, "musicInfo:" + musicInfo.toString());
				Message message = Message.obtain();
				message.what = AK3_SERVICE_WECHAT_MUSIC;
				message.obj = musicConversionToMessage(musicInfo);
				mHandler.sendMessage(message);
			}
			break;
			
		case AK3_EVENT_WECHAT_FILE:
			Log.d(TAG, "[AK3_EVENT_WECHAT_FILE]deviceHandle:" + deviceHandle + ",event:" + event + ", errorCode:" + error + 
					"sessionId:" + sessionId + "\n data:" + jsonData);
			FileInfo fileInfo = FileParser.parseFile(jsonData);
			
			if (fileInfo != null){
				Log.d(TAG, "fileInfo:" + fileInfo.toString());
				WeiXinMessage weiXinMessage = fileConversionToMessage(fileInfo);
				if (weiXinMessage != null){
					DownloadFile downloadFile = new DownloadFile();
					downloadFile.setSessionid(sessionId);
					downloadFile.setFileName(weiXinMessage.getContent());
					downloadFile.setDownloadId(weiXinMessage.getMsgid());
					downloadFile.setTotalSize(Long.valueOf(weiXinMessage.getFileSize()));
					downloadFile.setDownLoadState(FileWriter.DOWNLOAD_STATE_DOWNLOADING);
					downloadFile.setMd5(weiXinMessage.getDescription());
					weiXinMessage.setFileName(FileWriter.getInstance().createDownloadFile(downloadFile));
				}
				
				if (ChatMsgType.IMAGE.equals(weiXinMessage.getMsgtype())){
					AirKissHelper.getInstance().addMessage(sessionId, weiXinMessage);
				} else {
					Message message = Message.obtain();
					message.what = AK3_SERVICE_WECHAT_FILE;
					message.obj = weiXinMessage;
					mHandler.sendMessage(message);
				}
			}
			break;
			
		case AK3_EVENT_WECHAT_FILE_DATA:
			Log.d(TAG, "[AK3_EVENT_WECHAT_FILE_DATA]deviceHandle:" + deviceHandle + ",event:" + event + ", errorCode:" + error + 
					"sessionId:" + sessionId);
			Log.d(TAG, "Receive file data: [" + data.length + "] bytes" + ",Thread:" + Thread.currentThread());
			FileWriter.getInstance().write(sessionId, data); 
			WeiXinMessage weiXinMessage = AirKissHelper.getInstance().getMessage(sessionId);
			if (weiXinMessage != null && 
					ChatMsgType.IMAGE.equals(weiXinMessage.getMsgtype())){
				weiXinMessage.setFileName(weiXinMessage.getFileName());
				Message message = Message.obtain();
				message.what = AK3_SERVICE_WECHAT_FILE;
				message.obj = weiXinMessage;
				mHandler.sendMessage(message);
				AirKissHelper.getInstance().removeMessage(sessionId);
			}
			break;
		case AK3_EVENT_TCP_DISCONNECTED:
			break;
			
		default:
			break;
		}
	}
	
	private static Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Log.i(TAG, "handleMessage:" + msg.what);
			switch (msg.what) {
			case AK3_SERVICE_WECHAT_MUSIC:
				WeiXinMessage musicMessage = (WeiXinMessage) msg.obj;
				WeiXinMsgControl.getInstance().receiveWeiXinMsg(musicMessage);
				break;
				
			case AK3_SERVICE_WECHAT_FILE:
				WeiXinMessage fileMessage = (WeiXinMessage) msg.obj;
				Log.i(TAG, "WeiXinMessage:" + fileMessage.toString());
				WeiXinMsgControl.getInstance().receiveWeiXinMsg(fileMessage);
				break;

			default:
				break;
			}
		};
	};
	
	
	
	private static WeiXinMessage fileConversionToMessage(FileInfo fileInfo){
		WeiXinMessage weiXinMessage = null;
		WXMsgFile msgFile = fileInfo.getServices().getWxmsg_file();
		if (msgFile != null){
			weiXinMessage = new WeiXinMessage();
			String createtime = String.valueOf(System.currentTimeMillis());
			//生成默认用户
			weiXinMessage.setOpenid(fileInfo.getUser());
			weiXinMessage.setMsgid(String.valueOf(fileInfo.getMsg_id()));
			weiXinMessage.setFormat(fileInfo.getMsg_type());
			weiXinMessage.setContent(msgFile.getName());//文件名称
			weiXinMessage.setFileSize(String.valueOf(msgFile.getSize()));
			weiXinMessage.setDescription(msgFile.getMd5());
			weiXinMessage.setCreatetime(createtime);
			weiXinMessage.setLabel(msgFile.getType());//不同文件类型标识
			weiXinMessage.setStatus(String.valueOf(DownloadState.STATE_START_DOWNLOAD));
			if ("png".equals(msgFile.getType()) 
					|| "jpg".equals(msgFile.getType())
					|| "jpeg".equals(msgFile.getType())){
				weiXinMessage.setMsgtype(ChatMsgType.IMAGE);
			} else {
				weiXinMessage.setMsgtype(ChatMsgType.FILE);
			}
		}
		return weiXinMessage ;
	}
	
	private static WeiXinMessage musicConversionToMessage(MusicFileInfo musicFileInfo){
		WeiXinMessage weiXinMessage = null;
		WXMsgMusicFile msgMusicFile = musicFileInfo.getServices().getWxmsg_music();
		if (msgMusicFile != null){
			weiXinMessage = new WeiXinMessage();
			String createtime = String.valueOf(System.currentTimeMillis());
			
			//生成默认用户
			weiXinMessage.setOpenid(musicFileInfo.getUser());
			weiXinMessage.setMsgid(String.valueOf(musicFileInfo.getMsg_id()));
			weiXinMessage.setContent(msgMusicFile.getTitle());
			weiXinMessage.setLabel(msgMusicFile.getArtist());
			weiXinMessage.setUrl(msgMusicFile.getUrl());
			weiXinMessage.setTitle(msgMusicFile.getData_url());
			weiXinMessage.setDescription(msgMusicFile.getFrom_appname());
			weiXinMessage.setCreatetime(createtime);
			weiXinMessage.setMsgtype(ChatMsgType.MUSIC);
		}
		return weiXinMessage ;
	}
}
