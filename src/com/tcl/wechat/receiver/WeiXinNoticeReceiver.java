/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.db.WeiRecordDao;
import com.tcl.wechat.db.WeiUserDao;
import com.tcl.wechat.modle.BinderUser;
import com.tcl.wechat.modle.WeiNotice;
import com.tcl.wechat.utils.BaseUIHandler;
//import com.tcl.webchat.homepage.HomePageUIHandler;

/**
 * @ClassName: GetWeiXinNoticeReceiver
 */

public class WeiXinNoticeReceiver extends BroadcastReceiver{

	private static  BaseUIHandler mHandler = null;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		WeiUserDao weiUserDao = new WeiUserDao(context);
		WeiRecordDao weiRecordDao = new WeiRecordDao(context);
		//获取服务器推送绑定或解绑定的微信用户
		WeiNotice weiNotice = (WeiNotice)intent.getExtras().getSerializable("weiNotice");
		Log.d("GetWeiXinNoticeReceiver", "收到绑定通知,event:"+weiNotice.getEvent());
		
		String event  = weiNotice.getEvent();
		String openid = weiNotice.getOpenid();
		String nickName = weiNotice.getNickname();
		String sex = weiNotice.getSex();
		String headImageurl = weiNotice.getHeadimgurl();
		
		Log.d("GetWeiXinNoticeReceiver", "openid="+openid + "; event="+event + "; nickName=" + nickName + "; sex==" + sex + "; headImageurl=" + headImageurl);

		
		
		
		BinderUser binderUser = new BinderUser();
		binderUser.init();
		Log.d("liyulin", "openid="+openid);
		if (openid !=null){
			binderUser.setOpenid(openid);			
		}
		if (nickName !=null){
			binderUser.setNickname(nickName);
		}
		if (sex !=null){
			binderUser.setSex(sex);
		}
		if (headImageurl !=null){
			binderUser.setHeadimgurl(headImageurl);
		}
		//更新数据库微信用户
		if (event.equals("bind")){
			weiUserDao.save(binderUser);
			if (mHandler != null){
				Log.d("GetWeiXinNoticeReceiver", "send bind to to uihandle");
				mHandler.sendEmptyMessage(CommandType.COMMAND_BINDER_TOUI);
			}
		}else if (event.equals("unbind")){
			weiUserDao.delete(binderUser.getOpenid());
			//删除此用户分享的记录
			weiRecordDao.delMsg(binderUser.getOpenid());
			if (mHandler != null){
				mHandler.sendEmptyMessage(CommandType.COMMAND_UNBINDER_TOUI);
			}
		}
	}

	
	public static void setHandler(BaseUIHandler Handler){
		mHandler = (BaseUIHandler) Handler;
	}
}
