package com.tcl.wechat.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.android.http.RequestManager;
import com.android.http.RequestManager.RequestListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tcl.wechat.WeApplication;
import com.tcl.wechat.action.recorder.Recorder;
import com.tcl.wechat.common.Config;
import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.controller.listener.BindListener;
import com.tcl.wechat.controller.listener.LoginStateListener;
import com.tcl.wechat.controller.listener.NetChangedListener;
import com.tcl.wechat.controller.listener.NewMessageListener;
import com.tcl.wechat.controller.listener.OnLineChanagedListener;
import com.tcl.wechat.controller.listener.UploadListener;
import com.tcl.wechat.database.WeiMsgRecordDao;
import com.tcl.wechat.database.WeiUserDao;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.model.OnLineStatus;
import com.tcl.wechat.model.WeiNotice;
import com.tcl.wechat.model.WeiXinMsgRecorder;
import com.tcl.wechat.model.WeixinMsgInfo;
import com.tcl.wechat.utils.DataFileTools;
import com.tcl.wechat.utils.MD5Util;
import com.tcl.wechat.utils.http.HttpMultipartPost;
import com.tcl.wechat.xmpp.WeiXmppCommand;
import com.tcl.wechat.xmpp.XmppEventListener;

/***
 * 微信消息管理类
 * 		消息监听器必须要通过此类来实现
 * @author rex.lei
 *
 */
public class WeiXinMsgManager implements IConstant{

	private static final String TAG = WeiXinMsgManager.class.getSimpleName();
	
	/**
	 *	消息监听集合
	 */
	public static ArrayList<NewMessageListener> mNewMsgListeners = new ArrayList<NewMessageListener>();
	
	/**
	 * 网络变化监听集合
	 */
	public static ArrayList<NetChangedListener> mNetChangedListeners = new ArrayList<NetChangedListener>();
	
	/**
	 * 绑定解绑集合
	 */
	public static ArrayList<BindListener> mBindListeners = new ArrayList<BindListener>();
	
	/**
	 * 用户在线状态监听集合
	 */
	public static OnLineChanagedListener mOnLineChanagedListener;
	
	/**
	 * 登录状态监听器
	 */
	public static LoginStateListener mLoginStateListener;
	
	private WeiXinNotifier mWeiXinNotifier = WeiXinNotifier.getInstance();
	
	private DataFileTools mDataFileTools = DataFileTools.getInstance();
	
	private static class WeiXinMsgManagerInstance{
		private static final WeiXinMsgManager mInstance = new WeiXinMsgManager();
	}

	private WeiXinMsgManager() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public static WeiXinMsgManager getInstance(){
		return WeiXinMsgManagerInstance.mInstance;
	}
	
	public void initAllListener(){
		mNewMsgListeners.clear();
		mNetChangedListeners.clear();
		mBindListeners.clear();
	}
	
	public LoginStateListener getLoginStateListener() {
		return mLoginStateListener;
	}

	public void setLoginStateListener(LoginStateListener listener) {
		mLoginStateListener = listener;
	}
	
	/**
	 * 添加绑定解绑事件监听器
	 * @param listener 监听器
	 */
	public void addBindListener(BindListener listener) {
		if (mBindListeners.contains(listener)){
			mBindListeners.remove(listener);
		}
		mBindListeners.add(listener);
	}

	/**
	 * 添加绑定解绑事件监听器
	 * @param listener 监听器
	 */
	public void removeBindListener(BindListener listener){
		if (mBindListeners != null && mBindListeners.size() > 0){
			mBindListeners.remove(listener);
		}
	}
	
	/**
	 * 添加新消息监听器
	 * @param listener
	 */
	public void addNewMessageListener(NewMessageListener listener){
		if (mNewMsgListeners.contains(listener)){
			mNewMsgListeners.remove(listener);
		}
		mNewMsgListeners.add(listener);
	}
	
	/**
	 * 移除新消息监听器
	 * @param listener
	 */
	public void removeNewMessageListener(NewMessageListener listener){
		if (mNewMsgListeners != null && mNewMsgListeners.size() > 0){
			mNewMsgListeners.remove(listener);
		}
	}
	
	public void removeNewMessageListener(){
		if (mNewMsgListeners != null && mNewMsgListeners.size() > 0){
			mNewMsgListeners.clear();
		}
	}
	
	/**
	 * 设置在线状态监听器
	 * @param listener
	 */
	public void addOnLineStatusListener(OnLineChanagedListener listener){
		mOnLineChanagedListener = listener;
	}
	
	/**
	 * 接收notice消息
	 * @param weiNotice
	 */
	public void receiveNoticeMsg(WeiNotice weiNotice){
		String event = weiNotice.getEvent();
		BindUser bindUser = new BindUser(weiNotice.getOpenId(), 
				weiNotice.getNickName(), 
				weiNotice.getNickName(), weiNotice.getSex(), 
				weiNotice.getHeadImageUrl(), "0", "false", "");
		
		int size = WeiXinMsgManager.mBindListeners.size();
		
		Log.d(TAG, "receive Notice Event:" + event);
		if (event.equals("bind")){
			if (!WeiUserDao.getInstance().addUser(bindUser)){
				Log.e(TAG, "addUser ERROR!!");
			}
			for (int i = 0; i < size; i++) {
				WeiXinMsgManager.mBindListeners.get(i).onBind(bindUser.getOpenId(), 0);
			}
		}else if (event.equals("unbind")){
			/**
			 * 清除用户数据
			 */
			String openId = weiNotice.getOpenId();
			if (WeiUserDao.getInstance().getUser(openId) == null){
				Log.w(TAG, "BindUser :" + openId + " not exist!!");
				return ;
			}
			//1、发送解绑通知,更新界面
			for (int i = 0; i < size; i++) {
				WeiXinMsgManager.mBindListeners.get(i).onUnbind(openId);
			}
			
			//2、清除用户状态检测
			OnLineStatusMonitor.getInstance().stopMonitor(openId);
			
			//3、清除数据
			clearUserData(openId);
		}
	}
	
	/**
	 * 发送微信消息
	 * @param msgInfo
	 */
	public void sendWeiXinMsg(WeixinMsgInfo msgInfo, XmppEventListener eventListener){
		
		if (msgInfo == null){
			return ;
		}
		send(msgInfo, eventListener);
	}
	
	/**
	 * 通知接收消息
	 * @param weiXinMsg
	 */
	public void receiveWeiXinMsg(WeiXinMsgRecorder recorder){
		
		String msgType = recorder.getMsgtype();
		/*if (ChatMsgType.IMAGE.equals(msgType)){
			//视频文件,图片，先下载后通知
			// TODO 后续优化
			Log.d(TAG, "Download file:" + recorder.getUrl());
			download(recorder, recorder.getUrl());
		} else*/ if (ChatMsgType.VOICE.equals(msgType) || ChatMsgType.VIDEO.equals(msgType) 
				|| ChatMsgType.SHORTVIDEO.equals(msgType)){
			
			//音频文件，需要先获取accesstoken，生成对应的url，再获取
			obtainMeidaUrl(recorder);
		}  else {
			notifyUserReceive(recorder);
		}
	}
	
	/**
	 * 通知用户接收消息
	 * @param weiXinMsg
	 */
	public void notifyUserReceive(WeiXinMsgRecorder weiXinMsg){
		
			//1、保存消息记录
		if (WeiMsgRecordDao.getInstance().addRecorder(weiXinMsg, "")){
			//2、消息提示
			mWeiXinNotifier.notify(weiXinMsg);
			notifyAppWidget(weiXinMsg);
			
			//3、通知接收
			notifyMsgReceive(weiXinMsg);
		} else {
			Log.e(TAG, "addRecorder ERROR!!");
		}
	}
	
	/**
	 * 删除一条微信消息
	 * @param openid
	 * @param msgid
	 */
	public void deleteWeiXinMsg(WeiXinMsgRecorder recorder){
		
		deleteRecorder(recorder);
	}
	
	/**
	 * 删除用户所有微信所有消息
	 * @param recorders
	 */
	public void deleteWeiXinMsg(final ArrayList<WeiXinMsgRecorder> recorders){
		
		if (recorders == null || recorders.isEmpty()){
			return ;
		}
		
		int size = recorders.size();
		for (int i = 0; i < size; i++) {
			deleteRecorder(recorders.get(i));
		}
	}
	
	/**
	 * 解绑用户，清除用户的所有信息(用户信息，聊天信息，图片，音频，视频等缓存)
	 * @param openId
	 */
	public void clearUserData(final String openId){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean unbindRet = true;
				Log.d(TAG, "start to clear user cache data!!");
				/**
				 * 1、清除缓存数据：图片、语音、视频、用会员头像
				 * 2、清除数据库聊天信息
				 * 3、清除数据库用户信息
				 */
				Log.d(TAG, "openId:" + openId);
				//保存用户数据信息
				ArrayList<WeiXinMsgRecorder> mAllRecorders = 
						WeiMsgRecordDao.getInstance().getUserRecorder(openId);
				
				//清除头像
				BindUser delUser = WeiUserDao.getInstance().getUser(openId);
				if (delUser != null){
					mDataFileTools.clearImageRecorderCache(delUser.getHeadImageUrl());
				}
				
				//清除缓存数据
				deleteWeiXinMsg(mAllRecorders);
				
				//删除用户信息及消息记录数据库
				unbindRet &= WeiMsgRecordDao.getInstance().deleteUserRecorder(openId)
						& WeiUserDao.getInstance().deleteUser(openId);
				if (!unbindRet){
					Log.e(TAG, "clear user history failed!!, opendId:" + openId);
				}
				//通知AppWidget更新
				Log.d(TAG, "clear user cache data finished!!");
				
				//通知更新AppWidget
				Intent intent = new Intent();
				intent.setAction(ACTION_UNBIND_ENENT);
				intent.putExtra("BindUser", delUser);
				WeApplication.getContext().sendBroadcast(intent);
			}
		}).start();
	}
	
	/**
	 * 通知用户状态改变
	 * @param status 用户在线状态信息
	 */
	public void notifyUserStatusChaned(OnLineStatus status){
		if (status == null){
			Log.w(TAG, "OnLineStatus is NULL!!");
			return ;
		}
		Log.d(TAG, "OnLineStatus:" + status);
		/**
		 * 判断用户状态
		 * 1、用户还在线，重新获取最新消息时间，加上48小时，重新设置检测
		 * 2、用户已经离线，则不用检测。  在收到消息后，再启动检测。
		 */
		String openId = status.getOpenid();
		if (WeiMsgRecordDao.getInstance().isUserOnLine(openId, status.getTriggerTime())){
			//设置最新时间
			status.setTriggerTime(WeiMsgRecordDao.getInstance().getLatestRecorderTime(openId));
			OnLineStatusMonitor.getInstance().startMonitor(status);
		} else {
			//用户离线，则不再对其进行检测，收到新消息后，会添加监听器
			OnLineStatusMonitor.getInstance().stopMonitor(openId);
			//改变用户状态
			if (mOnLineChanagedListener != null){
				mOnLineChanagedListener.onStatusChanged(openId, false);
			}
		}
	}
	
	/**
	 * 通知AppWidget
	 */
	private void notifyAppWidget(WeiXinMsgRecorder weiXinMsg){
		Intent intent = new Intent();
		intent.setAction(CommandAction.ACTION_MSG_UPDATE);
		intent.putExtra("WeiXinMsgRecorder", weiXinMsg);
		WeApplication.getContext().sendBroadcast(intent);
	}
	
	
	/**
	 * 通知用户接收消息
	 * @param recorder
	 */
	public void notifyMsgReceive(WeiXinMsgRecorder recorder){
		
		if (recorder == null){
			return ;
		}
		
		//通知用户
		for (NewMessageListener listener : mNewMsgListeners) {
			listener.onNewMessage(recorder);
		}
	}
	
	/**
	 * 通知用户更新消息
	 * @param recorder 删除
	 */
	public void notifyMsgUpdate(WeiXinMsgRecorder recorder){
		
		if (recorder == null){
			return ;
		}
		/**
		 * 判断数据库是否还有该用户的聊天记录
		 */
		WeiXinMsgRecorder lastRecorder = WeiMsgRecordDao.getInstance().getLatestRecorder(recorder.getOpenid());
		if (lastRecorder == null){
			lastRecorder = new WeiXinMsgRecorder();
			lastRecorder.setOpenid(recorder.getOpenid());
			lastRecorder.setMsgtype(ChatMsgType.TEXT);
			lastRecorder.setMsgid("-1");
		} 
		//通知更新AppWidget
		Intent intent = new Intent();
		intent.setAction(CommandAction.ACTION_MSG_UPDATE);
		WeApplication.getContext().sendBroadcast(intent);
		
		//通知用户
		for (NewMessageListener listener : mNewMsgListeners) {
			listener.onNewMessage(lastRecorder);
		}
	}
	
	/**
	 * 发送媒体文件至微信客户端
	 * @param weixinMsgInfo 媒体消息内容
	 * @param eventListener Xmpp事件监听器 
	 */
	private void send(final WeixinMsgInfo weixinMsgInfo, 
			final XmppEventListener eventListener){
		
		/**
		 * 1、请求accesstoken的方法
		 * 2、上传文件  -----  回调监听
		 * 3、获取mediaid
		 * 4、发送消息   -----  回调监听
		 */
		
		RequestManager.getInstance().get(Config.URL_ACCESS_TOKEN, new RequestListener() {
			
			@Override
			public void onSuccess(String response, Map<String, String> arg1, String arg2,
					int arg3) {
				// TODO Auto-generated method stub
				String accesstoken = response;
				Log.i(TAG, "accesstoken:" + accesstoken);
				if (!TextUtils.isEmpty(accesstoken)){
					upload(accesstoken, weixinMsgInfo, eventListener);
				}
			}
			
			@Override
			public void onRequest() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(String arg0, String arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
		}, 0);
	}
	
	/**
	 * 获取音频文件链接地址
	 * @note 针对音频问题，先要获取接入令牌accesstoken，生成对应的url，然后再向微信服务器请求文件
	 * @param weiXinMsgRecorder
	 */
	private void obtainMeidaUrl(final WeiXinMsgRecorder weiXinMsgRecorder) {
		// TODO Auto-generated method stub
		
		if (weiXinMsgRecorder == null || TextUtils.isEmpty(weiXinMsgRecorder.getMediaid())){
			Log.w(TAG, "recorder is NULL or MeidaId is NULL!!");
			return ;
		}
		RequestManager.getInstance().get(Config.URL_ACCESS_TOKEN, new RequestListener() {
			
			@Override
			public void onSuccess(String response, Map<String, String> arg1, String arg2,
					int arg3) {
				
				String accesstoken = response;
				Log.i(TAG, "accesstoken:" + accesstoken);
				if (!TextUtils.isEmpty(accesstoken)){

					WeiXinMsgRecorder recorder = weiXinMsgRecorder;
					
					if (ChatMsgType.VIDEO.equals(recorder.getMsgtype()) 
							|| ChatMsgType.SHORTVIDEO.equals(recorder.getMsgtype())){ //同步下载缩略图
						StringBuffer thumbmeUrl = new StringBuffer(Config.URL_GET_MEDIA);
						thumbmeUrl.append("?access_token=").append(accesstoken)
							.append("&media_id=").append(recorder.getThumbmediaid());
						//保存url，用于取出缩略图
						recorder.setUrl(thumbmeUrl.toString());
						WeiMsgRecordDao.getInstance().updateRecorderUrl(recorder.getMsgid(), thumbmeUrl.toString());
						//下载缩略图资源
						Log.i(TAG, "thumbmeUrl:" + thumbmeUrl.toString());
						WeApplication.getImageLoader().get(thumbmeUrl.toString(), mImageListener);
						
						//下载媒体资源文件
						StringBuffer url = new StringBuffer(Config.URL_GET_MEDIA);
						url.append("?access_token=").append(accesstoken)
							.append("&media_id=").append(recorder.getMediaid());
						
						download(recorder, url.toString());
						
					} else {
						StringBuffer url = new StringBuffer(Config.URL_GET_MEDIA);
						url.append("?access_token=").append(accesstoken)
							.append("&media_id=").append(recorder.getMediaid());
						recorder.setUrl(url.toString());
						
						download(recorder, url.toString());
					}
				}
			}
			
			@Override
			public void onRequest() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onError(String arg0, String arg1, int arg2) {
				// TODO Auto-generated method stub
			}
		}, 0);
	}
	
	/**
	 * 下载媒体类文件
	 * @param recorder
	 */
	private void download(final WeiXinMsgRecorder recorder, String url){
		
		if (TextUtils.isEmpty(url)){
			return ;
		}
		Log.d(TAG, "url:" + url);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new AsyncHttpResponseHandler(){
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,
	  				byte[] binaryData) {
				// TODO Auto-generated method stub
				Log.i(TAG, "onSuccess statusCode:" + statusCode);
				saveRecorder(recorder, binaryData);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
	  				byte[] binaryData, Throwable error) {
				// TODO Auto-generated method stub
				Log.i(TAG, "onFailure statusCode:" + statusCode);
			}
		});
	}
	
	/**
	 * 上传文件至服务器
	 * @param accesstoken
	 * @param msgInfo
	 * @param handler
	 * @note 增加同步锁操作
	 */
	private synchronized void upload(final String accesstoken, 
			final WeixinMsgInfo msgInfo, 
			final XmppEventListener eventListener){
		
		Recorder recorder = msgInfo.getRecorder();
		if (recorder == null){
			return ;
		}
		
		String filePath = recorder.getFileName();
		File uploadFile = new File(filePath);
		
		//如果文件写入未完成，则进行等待.
		//TODO 后续考虑增加上传超时操作
		while (!uploadFile.exists()) {
			try {
				Log.i(TAG, "filePath:" + filePath);
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(URL_TICKET).append("access_token=").append(accesstoken)
							.append("&type=").append(msgInfo.getMsgtype());
		Log.i(TAG, "Url:" + buffer.toString());
		
		/**
		 * 此处上传文件操作，可返回上传结果  以及 进度的更新。
		 */
		HttpMultipartPost post = new HttpMultipartPost(uploadFile.getAbsolutePath(), 
				new UploadResult(msgInfo, eventListener));
		post.executeOnExecutor(WeApplication.getExecutorPool(), buffer.toString());
		
//		RequestMap params = new RequestMap();
//		params.put("uploadedfile", uploadFile);
//		
//		RequestManager.getInstance().post(buffer.toString(), params, 
//				new RequestData(listener, msgInfo), 0);
	}
	

	/**
	 * 删除用户所有信息
	 */
	private void deleteRecorder(WeiXinMsgRecorder recorder){
		
		if (recorder == null){
			return ;
		}
		String msgType = recorder.getMsgtype();
		if (ChatMsgType.IMAGE.equals(msgType)){
			mDataFileTools.clearImageRecorderCache(recorder.getUrl());
		} else if (ChatMsgType.VIDEO.equals(msgType)){
			mDataFileTools.clearVideoRecorderCache(recorder.getFileName());
		} else if (ChatMsgType.VOICE.equals(msgType)){
			mDataFileTools.clearAudioRecorderCache(recorder.getFileName());
		}
	}
	
	/**
	 * 应答服务器
	 * @param msgInfo
	 * @param handler
	 */
	private void replyMessage(XmppEventListener listener, WeixinMsgInfo msgInfo){
		Map<String, String> valus = new HashMap<String, String>();
		valus.put("msgid", msgInfo.getMessageid());
		valus.put("tousername", msgInfo.getTousername());
		valus.put("fromusername", msgInfo.getFromusername());
		valus.put("createtime", String.valueOf(System.currentTimeMillis()));
		valus.put("msgtype", msgInfo.getMsgtype());
		valus.put("mediaid", msgInfo.getMediaid());
		new WeiXmppCommand(EventType.TYPE_SEND_WEIXINMSG, valus, listener).execute();
	}
	
	/**
	 * 保存用户微信消息
	 * @param weiXinMsg
	 * @return
	 */
	private boolean saveRecorder(WeiXinMsgRecorder recorder, byte[] binaryData){
		if (recorder == null || binaryData == null){
			return false;
		}
		String cachePath = null;
		File savePath = null;
		String msgType = recorder.getMsgtype();
		if (ChatMsgType.VOICE.equals(msgType)){
			cachePath = mDataFileTools.getRecordAudioPath();
		} else if (ChatMsgType.IMAGE.equals(msgType)){
			cachePath = mDataFileTools.getRecordImagePath();
		} else if (ChatMsgType.VIDEO.equals(msgType) || ChatMsgType.SHORTVIDEO.equals(msgType)){
			cachePath = mDataFileTools.getRecordVideoPath();
		} else {
			cachePath = mDataFileTools.getCachePath();
		}
		
		File file = new File(cachePath);
		if (file != null && !file.exists()){
			file.mkdirs();
		}
		
		if (ChatMsgType.VOICE.equals(msgType)){
			savePath = new File(file, MD5Util.hashKeyForDisk(recorder.getUrl()) + ".amr");
		} else if (ChatMsgType.IMAGE.equals(msgType)){
			savePath = new File(file, DataFileTools.getCacheImageFileName(recorder.getUrl()));
		} else if (ChatMsgType.VIDEO.equals(msgType) || ChatMsgType.SHORTVIDEO.equals(msgType)){
			savePath = new File(file, MD5Util.hashKeyForDisk(recorder.getMediaid()) + ".mp4");
		}
		return save(recorder, savePath, binaryData);
	}
	
	/**
	 * 保存文件
	 * @param recorder
	 * @param savePath
	 * @param binaryData
	 * @return
	 */
	private boolean save(WeiXinMsgRecorder recorder, File savePath, byte[] binaryData){
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(savePath);
			fos.write(binaryData);
			fos.flush();
			recorder.setFileName(savePath.getAbsolutePath());
			notifyUserReceive(recorder);
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (fos != null){
					fos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private class UploadResult implements UploadListener{

		private XmppEventListener mEventListener;
		
		private WeixinMsgInfo mWeixinMsgInfo;
		
		public UploadResult(WeixinMsgInfo mWeixinMsgInfo, XmppEventListener eventListener) {
			super();
			this.mWeixinMsgInfo = mWeixinMsgInfo;
			this.mEventListener = eventListener;
		}
		
		@Override
		public void onResult(String result) {
			// TODO Auto-generated method stub
			//服务器返回结果
			Log.d(TAG, "Upload Result:" + result);
			try {
				if (!TextUtils.isEmpty(result)){
					JSONObject object = new JSONObject(result);
					String mediaid = (String)object.get("media_id");
					mWeixinMsgInfo.setMediaid(mediaid);
					replyMessage(mEventListener, mWeixinMsgInfo);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onProgressUpdate(int progress) {
			// TODO Auto-generated method stub
			//更新进度
		}

		@Override
		public void onError(int errorCode) {
			// TODO Auto-generated method stub
			//服务器返回错误结果
		}
	}
	
	private class RequestData implements RequestListener{
		
		private XmppEventListener mListener;
		private WeixinMsgInfo mWeixinMsgInfo;
		
		public RequestData(XmppEventListener listener, WeixinMsgInfo mWeixinMsgInfo) {
			super();
			this.mListener = listener;
			this.mWeixinMsgInfo = mWeixinMsgInfo;
		}

		@Override
		public void onError(String errorMsg, String url, int actionId) {
			Log.i(TAG, "actionId:" + actionId + ", onError!\n" + errorMsg);
		}

		@Override
		public void onRequest() {
			Log.i(TAG, "request send...");
		}

		@Override
		public void onSuccess(String response, Map<String, String> headers,
				String url, int actionId) {
			Log.i(TAG,"actionId:" + actionId + ", OnSucess!\n" + ", response:" + response);
			try {
				if (!TextUtils.isEmpty(response)){
					JSONObject object = new JSONObject(response);
					String mediaid = (String)object.get("media_id");
					mWeixinMsgInfo.setMediaid(mediaid);
					replyMessage(mListener, mWeixinMsgInfo);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 图片下载监听器
	 */
	private ImageListener mImageListener = new ImageListener() {
		
		@Override
		public void onErrorResponse(VolleyError arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onResponse(ImageContainer response, boolean isImmediate) {
			// TODO Auto-generated method stub
			Log.i(TAG, "RequestUrl:" + response.getRequestUrl());
			Log.i(TAG, "RequestData:" + response.getBitmap());
		}
	};
}
