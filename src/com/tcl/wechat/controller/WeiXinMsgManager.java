package com.tcl.wechat.controller;

import java.util.ArrayList;

import android.util.Log;

import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.db.WeiRecordDao;
import com.tcl.wechat.modle.WeiXinMsg;
import com.tcl.wechat.utils.CommonsFun;
import com.tcl.wechat.utils.SystemReflect;
import com.tcl.wechat.utils.UIUtils;

/***
 * 微信消息管理类
 * @author rex.lei
 *
 */
public class WeiXinMsgManager {

	/**
	 *	消息监听集合
	 */
	public static ArrayList<NewMessageListener> newMsgListeners = new ArrayList<NewMessageListener>();
	
	/**
	 * 用户监听集合
	 */
	public static ArrayList<NewUserRecordListener> newRecordListeners = new ArrayList<NewUserRecordListener>();
	
	/**
	 * 网络变化监听集合
	 */
	public static ArrayList<NetChangedListener> netChangedListeners = new ArrayList<NetChangedListener>();
	
	/**
	 * 绑定解绑集合
	 */
	public static ArrayList<BindListener> bindListeners = new ArrayList<BindListener>();
	
	private static final String TAG = WeiXinMsgManager.class.getSimpleName();
	
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
	

	/**
	 * 接收文本消息
	 * @param weiXinMsg
	 */
	public void receiveTextMsg(WeiXinMsg weiXinMsg){
		Log.i(TAG, "received TextMsg [msgtype=" + weiXinMsg.getMsgtype() + ", Text=" + weiXinMsg.getContent() + "]");
		
		if(!SystemReflect.getProperties("sys.scan.state", "off").equals("on")){
			
		}
	}
	
	/**
	 * 接收图片消息
	 * @param weiXinMsg
	 */
	public void receiveImageMsg(WeiXinMsg weiXinMsg){
		Log.i(TAG, "received ImageMsg [msgtype=" + weiXinMsg.getMsgtype() + ", ImgUrl=" + weiXinMsg.getUrl() + "]");
	}
	
	/**
	 * 接收音频消息
	 */
	public void receiveVoiceMsg(WeiXinMsg weiXinMsg){
		Log.i(TAG, "received VoiceMsg [msgtype=" + weiXinMsg.getMsgtype() + ", Recognition=" + weiXinMsg.getRecognition() + "]");
	}
	
	/**
	 * 接收视频消息
	 */
	public void receiveVideoMsg(WeiXinMsg weiXinMsg){
		Log.i(TAG, "received VideoMsg [msgtype=" + weiXinMsg.getMsgtype() + ", Recognition=" + weiXinMsg.getRecognition() + "]");
		/**
		 * 下载之前要判断是否有SD卡，如果有优先存储到sd卡，
		 * 			如果没有判断flash中已有视频是否大于400M.		
		 */
		String downPath = UIUtils.getDownLoadPath();	
	    try {
			if(downPath.equals(WeiConstant.DOWN_LOAD_SDCARD_PATH)){
				Log.i(TAG,"UIUtils.getSDFreeSize()="+UIUtils.getSDFreeSize());
				if (UIUtils.getSDFreeSize()<20){
					Log.i(TAG,"有SD卡，但是SD卡剩余空间少于20M，将视频存储到flash中");
					downPath = WeiConstant.DOWN_LOAD_FLASH_PATH;
					if(UIUtils.getVideoFolderSize() > 400){
						String filenamedel = WeiRecordDao.getInstance().findOldRecord();
						CommonsFun.delSrcFile(filenamedel);
					}
				}	    	
			}else if(downPath.equals(WeiConstant.DOWN_LOAD_FLASH_PATH)){
			    //本地存储的视频文件大于400M。删除老文件和数据库		    
				Log.i(TAG,"UIUtils.getVideoFolderSize()="+UIUtils.getVideoFolderSize());
				if(UIUtils.getVideoFolderSize()>400){
					String filenamedel = WeiRecordDao.getInstance().findOldRecord();
					CommonsFun.delSrcFile(filenamedel);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	    
	    /**
	     * 开始下载视频
	     */
	    //TODO
	    
	}
	
	/**
	 * 接收弹幕消息
	 * @param weiXinMsg
	 */
	public void receiveBarrageMsg(WeiXinMsg weiXinMsg){
		Log.i(TAG, "received BarrageMsg [msgtype=" + weiXinMsg.getMsgtype() + ", Content=" + weiXinMsg.getContent() + "]");
		
		if(!SystemReflect.getProperties("sys.scan.state", "off").equals("on")
				&&!WeiConstant.WechatConfigure.CurConfigure.equals(WeiConstant.WechatConfigure.SimpleVer)){
		}
	}
	
	/**
	 * 接收预约节目提醒
	 * @param weiXinMsg
	 */
	public void receiveNoticeMsg(WeiXinMsg weiXinMsg){
		Log.i(TAG, "received NoticeMsg [msgtype=" + weiXinMsg.getMsgtype() + ", ChannelName=" + weiXinMsg.getchannelname()
				+ ", programName=" + weiXinMsg.getContent() + "]");
	}
}
