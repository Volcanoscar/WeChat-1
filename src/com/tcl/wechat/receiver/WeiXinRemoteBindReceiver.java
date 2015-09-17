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
import com.tcl.wechat.db.WeiUserDao;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.utils.BaseUIHandler;

/**
 * 绑定用户监听器
 * @author rex.lei
 *
 */
public class WeiXinRemoteBindReceiver extends BroadcastReceiver{

	private static final String TAG = WeiXinRemoteBindReceiver.class.getSimpleName();
	
	private static  BaseUIHandler mHandler = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.i(TAG, "Received WeiXinRemoteBindUser");
		
		//获取远程绑定消息
		BindUser bindUser = intent.getExtras().getParcelable("bindUser");
		
		String openid = bindUser.getOpenId();
		String nickName = bindUser.getNickName();
		String sex = bindUser.getSex();
		String headImageurl = bindUser.getHeadImageUrl();
		//两类消息1、远程绑定请求 2、远程绑定成功回复，只有openid，其他是null
		if(nickName == null && sex == null && headImageurl == null){
			//收到服务器回复之后才显示提示“加入成功”
			if(bindUser.getReply().equals("disallow")){
				return;
			}
			WeiUserDao.getInstance().updateStatus(openid, "success");
			if (mHandler != null){
				Log.d("GetWeiXinRemoteBindReceiver", "send bind to to uihandle");
				mHandler.sendEmptyMessage(CommandType.COMMAND_BINDER_TOUI);
			}
		}else{
			BindUser binderUser = new BindUser();
			if (openid !=null){
				binderUser.setOpenId(openid);
			}
			if (nickName !=null){
				binderUser.setNickName(nickName);
			}
			if (sex !=null){
				binderUser.setSex(sex);
			}
			if (headImageurl !=null){
				binderUser.setHeadImageUrl(headImageurl);
				Log.i("GetWeiXinRemoteBindReceiver","headImageurl="+headImageurl);
			}
			binderUser.setStatus("wait");
		}
	}
	
	public static void setHandler(BaseUIHandler Handler){
		mHandler = (BaseUIHandler) Handler;
	}
}
