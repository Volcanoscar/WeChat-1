/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.receiver;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tcl.wechat.common.IConstant;
import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.db.WeiRecordDao;
import com.tcl.wechat.modle.WeiXinMsg;
import com.tcl.wechat.utils.CommonsFun;
import com.tcl.wechat.utils.SystemReflect;
import com.tcl.wechat.utils.UIUtils;
import com.tcl.wechat.xmpp.WeiXmppCommand;

/**
 * 微信消息接收器
 * @author rex.lei
 *
 */
public class WeiXinMsgReceiver extends BroadcastReceiver implements IConstant{
	
	private static final String TAG = WeiXinMsgReceiver.class.getSimpleName();

	private Context mContext = null;
	private WeiRecordDao mWeiRecordDao ;

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i(TAG, "Received push notification!");
		
		mContext = context;
		mWeiRecordDao = WeiRecordDao.getInstance();
		
		if (intent.getExtras() == null){
			Log.e(TAG, "intent.getExtras() is NULL!!");
			return;
		}
		
		//获取消息
		WeiXinMsg weiXinMsg = (WeiXinMsg)intent.getExtras().getSerializable("weiXinMsg");
		//消息为空，则返回
		if (weiXinMsg == null){
			Log.e(TAG, "weiXinMsg is NULL!!");
			return;
		}
		
		String type = weiXinMsg.getMsgtype();
		Log.i(TAG, "msgtype is:" + type);
		
		if (ChatMsgType.TEXT.equals(type)){    
			
			//接收文本消息
			receiveTextMsg(weiXinMsg);
		} else if (ChatMsgType.IMAGE.equals(type)){ 
			
			//接收图片消息
			receiveImageMsg(weiXinMsg);
		} else if (ChatMsgType.VOICE.equals(type)){
			
			//接收音频消息
			receiveVoiceMsg(weiXinMsg);
		} else if (ChatMsgType.VIDEO.equals(type)){
			
			//接收视频消息
			receiveVideoMsg(weiXinMsg);
		} else if (ChatMsgType.BARRAGE.equals(type)){
			
			//接收弹幕消息
			receiveBarrageMsg(weiXinMsg);
		} else if (ChatMsgType.NOTICE.equals(type)){
			
			//接收预约节目提醒
			receiveNoticeMsg(weiXinMsg);
		} else {
			Log.w(TAG, "Could not recognize message type:" + type);
		}
		
		//收到消息给服务器发送反馈		
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("openid",	weiXinMsg.getOpenid());
		hashMap.put("msgtype",	weiXinMsg.getMsgtype());
		hashMap.put("msgid", 	weiXinMsg.getmsgid());
		new WeiXmppCommand(null, CommandType.COMMAND_MSGRESPONSE, hashMap).execute();
	}
	
	/**
	 * 接收文本消息
	 * @param weiXinMsg
	 */
	private void receiveTextMsg(WeiXinMsg weiXinMsg){
		Log.i(TAG, "received TextMsg [msgtype=" + weiXinMsg.getMsgtype() + ", Text=" + weiXinMsg.getContent() + "]");
		
		if(!SystemReflect.getProperties("sys.scan.state", "off").equals("on")){
			
		}
	}
	
	/**
	 * 接收图片消息
	 * @param weiXinMsg
	 */
	private void receiveImageMsg(WeiXinMsg weiXinMsg){
		Log.i(TAG, "received ImageMsg [msgtype=" + weiXinMsg.getMsgtype() + ", ImgUrl=" + weiXinMsg.getUrl() + "]");
	}
	
	/**
	 * 接收音频消息
	 */
	private void receiveVoiceMsg(WeiXinMsg weiXinMsg){
		Log.i(TAG, "received VoiceMsg [msgtype=" + weiXinMsg.getMsgtype() + ", Recognition=" + weiXinMsg.getRecognition() + "]");
	}
	
	/**
	 * 接收视频消息
	 */
	private void receiveVideoMsg(WeiXinMsg weiXinMsg){
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
						String filenamedel = mWeiRecordDao.findOldRecord();
						CommonsFun.delSrcFile(filenamedel);
					}
				}	    	
			}else if(downPath.equals(WeiConstant.DOWN_LOAD_FLASH_PATH)){
			    //本地存储的视频文件大于400M。删除老文件和数据库		    
				Log.i(TAG,"UIUtils.getVideoFolderSize()="+UIUtils.getVideoFolderSize());
				if(UIUtils.getVideoFolderSize()>400){
					String filenamedel = mWeiRecordDao.findOldRecord();
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
	private void receiveBarrageMsg(WeiXinMsg weiXinMsg){
		Log.i(TAG, "received BarrageMsg [msgtype=" + weiXinMsg.getMsgtype() + ", Content=" + weiXinMsg.getContent() + "]");
		
		if(!SystemReflect.getProperties("sys.scan.state", "off").equals("on")
				&&!WeiConstant.WechatConfigure.CurConfigure.equals(WeiConstant.WechatConfigure.SimpleVer)){
		}
	}
	
	/**
	 * 接收预约节目提醒
	 * @param weiXinMsg
	 */
	private void receiveNoticeMsg(WeiXinMsg weiXinMsg){
		Log.i(TAG, "received NoticeMsg [msgtype=" + weiXinMsg.getMsgtype() + ", ChannelName=" + weiXinMsg.getchannelname()
				+ ", programName=" + weiXinMsg.getContent() + "]");
	}
}
