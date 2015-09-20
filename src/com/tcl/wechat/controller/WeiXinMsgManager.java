package com.tcl.wechat.controller;

import java.util.ArrayList;

import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.action.player.DownloadManager;
import com.tcl.wechat.action.player.listener.DownloadStateListener;
import com.tcl.wechat.common.IConstant.ChatMsgType;
import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.controller.listener.BindListener;
import com.tcl.wechat.controller.listener.LoginStateListener;
import com.tcl.wechat.controller.listener.NetChangedListener;
import com.tcl.wechat.controller.listener.NewMessageListener;
import com.tcl.wechat.db.WeiMsgRecordDao;
import com.tcl.wechat.modle.WeiXinMsgRecorder;
import com.tcl.wechat.utils.SystemReflect;

/***
 * 微信消息管理类
 * 		消息监听器必须要通过此类来实现
 * @author rex.lei
 *
 */
public class WeiXinMsgManager {

	private static final String TAG = WeiXinMsgManager.class.getSimpleName();
	
	private static final String SYS_SCAN_STATE = "sys.scan.state";	
	
	/**
	 *	消息监听集合
	 */
	public static ArrayList<NewMessageListener> newMsgListeners = new ArrayList<NewMessageListener>();
	
	/**
	 * 网络变化监听集合
	 */
	public static ArrayList<NetChangedListener> netChangedListeners = new ArrayList<NetChangedListener>();
	
	/**
	 * 绑定解绑集合
	 */
	public static ArrayList<BindListener> bindListeners = new ArrayList<BindListener>();
	
	/**
	 * 登录状态监听器
	 */
	private static LoginStateListener mLoginStateListener;
	
	
	private WeiXinNotifier mWeiXinNotifier = WeiXinNotifier.getInstance();
	
	private DownloadManager downloadManager = DownloadManager.getInstace();
	
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
	
	public LoginStateListener getLoginStateListener() {
		return mLoginStateListener;
	}

	public void setLoginStateListener(LoginStateListener mListener) {
		mLoginStateListener = mListener;
	}

	public void addNewMessageListener(NewMessageListener listener){
		newMsgListeners.add(listener);
	}

	/**
	 * 通知接收消息
	 * @param weiXinMsg
	 */
	public void receiveWeiXinMsg(WeiXinMsgRecorder weiXinMsg){
		
		//1、消息提示
		mWeiXinNotifier.notify(weiXinMsg);
		
		//2、接收消息
		receive(weiXinMsg);
		
		//3、 保存消息至数据库
		save(weiXinMsg);
	}
	
	/**
	 * 通知用户接收消息
	 * @param recorder
	 */
	private void receive(WeiXinMsgRecorder recorder){
		
		if (recorder == null){
			return ;
		}
		
		Log.d(TAG, "WeiXinMsgRecorder:" + recorder.toString());
		
		/*if(!SystemReflect.getProperties(SYS_SCAN_STATE, "off").equals("on")){
			
		}*/
		//1、下载数据
		if (isNeedDownload(recorder)){
			download(recorder);
		}
		
		//2、通知用户
		for (NewMessageListener listener : newMsgListeners) {
			listener.onNewMessage(recorder);
		}
	}
	
	private boolean isNeedDownload(WeiXinMsgRecorder recorder){
		if (recorder == null || TextUtils.isEmpty(recorder.getMsgtype())){
			return false;
		}
		String type = recorder.getMsgtype();
		if (ChatMsgType.IMAGE.equals(type) || 
				ChatMsgType.VIDEO.equals(type) || 
				ChatMsgType.VOICE.equals(type)){
			return true;
		}
		return false;
	}
	
	/**
	 * 下载文件
	 */
	private void download(WeiXinMsgRecorder recorder){
		
		String urlStr = recorder.getUrl();
		
		if (TextUtils.isEmpty(urlStr)){
			return ;
		}
		
		String type = recorder.getMsgtype();
		
		downloadManager.setDownloadStateListener(mListener);
		downloadManager.startToDownload(urlStr, type);
		
	}	
	/**
	 * 保存用户微信消息
	 * @param weiXinMsg
	 * @return
	 */
	private boolean save(WeiXinMsgRecorder recorder){
		if (recorder == null){
			return false;
		}
		Log.i(TAG, "saveWeiXinMsg:" + recorder.toString());
		
		recorder.setRead("false");
		
		if (!WeiMsgRecordDao.getInstance().addRecorder(recorder)){
			Log.e(TAG, "saveWeiXinMsg ERROR!!");
			return false ;
		}
		return true;
	}
	
	
	/**
	 * 接收文本消息
	 * @param weiXinMsg
	 */
	private void receiveTextMsg(WeiXinMsgRecorder weiXinMsg){
		Log.i(TAG, "received TextMsg [msgtype=" + weiXinMsg.getMsgtype() + ", Text=" + weiXinMsg.getContent() + "]");
		
		for (NewMessageListener listener : newMsgListeners) {
			listener.onNewMessage(weiXinMsg);
		}
		
		if(!SystemReflect.getProperties(SYS_SCAN_STATE, "off").equals("on")){
			
		}
	}
	
	/**
	 * 接收图片消息
	 * @param weiXinMsg
	 */
	private void receiveImageMsg(WeiXinMsgRecorder weiXinMsg){
		Log.i(TAG, "received ImageMsg [msgtype=" + weiXinMsg.getMsgtype() + ", ImgUrl=" + weiXinMsg.getContent() + "]");
	}
	
	/**
	 * 接收音频消息
	 */
	private void receiveVoiceMsg(WeiXinMsgRecorder weiXinMsg){
		Log.i(TAG, "received VoiceMsg [msgtype=" + weiXinMsg.getMsgtype() + ", Recognition=" + weiXinMsg.getContent() + "]");
	}
	
	/**
	 * 接收视频消息
	 */
	private void receiveVideoMsg(WeiXinMsgRecorder weiXinMsg){
		Log.i(TAG, "received VideoMsg [msgtype=" + weiXinMsg.getMsgtype() + ", Recognition=" + weiXinMsg.getContent() + "]");

		/**
		 * 1、检查存储情况
		 */
	    /**
	     * 2、开始下载视频
	     */
	    //TODO
		/**
		 * 3、 保存消息
		 */
	}
	
	/**
	 * 接收弹幕消息
	 * @param weiXinMsg
	 */
	private void receiveBarrageMsg(WeiXinMsgRecorder weiXinMsg){
		Log.i(TAG, "received BarrageMsg [msgtype=" + weiXinMsg.getMsgtype() + ", Content=" + weiXinMsg.getContent() + "]");
		
		if(!SystemReflect.getProperties(SYS_SCAN_STATE, "off").equals("on")
				&&!WeiConstant.WechatConfigure.CurConfigure.equals(WeiConstant.WechatConfigure.SimpleVer)){
		}
	}
	
	/**
	 * 接收预约节目提醒
	 * @param weiXinMsg
	 */
	private void receiveNoticeMsg(WeiXinMsgRecorder weiXinMsg){
		Log.i(TAG, "received NoticeMsg");
	}
	
	private DownloadStateListener mListener = new DownloadStateListener() {
		
		@Override
		public void startDownLoad() {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProgressUpdate(int progress) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onDownLoadError(int errorCode) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onDownLoadCompleted() {
			// TODO Auto-generated method stub
			
		}
	};
}
