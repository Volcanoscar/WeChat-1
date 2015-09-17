/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.xmpp;

import java.util.Map;

import android.app.Activity;
import android.util.Log;

import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.common.WeiConstant.ParameterKey;
import com.tcl.wechat.utils.BaseUIHandler;


/**
 * @ClassName: WeiXmppCommand
 * @Description: 协议接口控制类,上报给服务器的内容
 */

public class WeiXmppCommand {
	
	private String tag = "WeiXmppCommand";
	private int type;
	private Map<String, String> values;
	private BaseUIHandler<Object, Activity> uIHandler;
	
	public WeiXmppCommand(BaseUIHandler uiHandler, int type,
			Map<String, String> values)
	{
		this.type = type;
		if(uiHandler!=null)
			this.uIHandler = uiHandler;
		this.values = values;

	}
	
	
	/**
	 * 执行请求
	 */
	public void execute(){
		
		if(!WeiXmppManager.getInstance().isConnected()){
			Log.d(tag, "当前微信未连接");
			if(uIHandler==null)
				return;
			uIHandler.sendEmptyMessage(CommandType.COMMAND_NEWWORK_UNCONNECTED);
			if(this.type == CommandType.COMMAND_UN_BINDER)
				uIHandler.sendEmptyMessage(CommandType.COMMAND_UN_BINDER_ERROR);
			return ;
		}
		
		switch (this.type)
		{
			//获取二维码信息
			case CommandType.COMMAND_GET_QR:
				 Log.d(tag, "获取二维码信息");
				 Getqr getqr = new Getqr(WeiXmppManager.getInstance().getConnection(), uIHandler);
				 getqr.sentPacket();
				 break;
			//查询微信绑定用户	 
			case CommandType.COMMAND_GET_BINDER:
				 QueryBinder queryBinder = new QueryBinder(WeiXmppManager.getInstance().getConnection(), uIHandler);
				 queryBinder.sentPacket();
				 break;
			//解绑定用户	 
			case CommandType.COMMAND_UN_BINDER:
				 Unbind unbind = new Unbind(WeiXmppManager.getInstance().getConnection(), uIHandler);
				 if (values!=null){
					 unbind.setOpenid(values.get(ParameterKey.OPEN_ID));
				 }
				 unbind.sentPacket();
				 break;
			case CommandType.COMMAND_REPORT_DEVICEINFO:
				ReportDeviceInfo mReportDeviceInfo = new ReportDeviceInfo(WeiXmppManager.getInstance().getConnection(), values);
				mReportDeviceInfo.sentPacket();
				break;
			case CommandType.SEND_REMOTEBIND_RESPONSE:
				Remotebind mRemotebind = new Remotebind(WeiXmppManager.getInstance().getConnection(), values);
				mRemotebind.sentPacket();
				break;
			case CommandType.COMMAND_MSGRESPONSE:
				MsgResponse mMsgResponse = new MsgResponse(WeiXmppManager.getInstance().getConnection(), values);
				mMsgResponse.sentPacket();
				break;
			case CommandType.COMMAND_REPORTTVSTATUS:
				ReportTvstatus mReportTvstatus = new ReportTvstatus(WeiXmppManager.getInstance().getConnection(), values);
				mReportTvstatus.sentPacket();
				break;
			case CommandType.COMMAND_RESPONSETVSTATUS:
				ResponseTvstatus mResponseTvstatus = new ResponseTvstatus(WeiXmppManager.getInstance().getConnection(), values);
				mResponseTvstatus.sentPacket();
				break;	
			case CommandType.COMMAND_TVPROGRAMNOTICE:
				ResponSettvprogramnotice mResponSettvprogramnotice = new ResponSettvprogramnotice(WeiXmppManager.getInstance().getConnection(), values);
				mResponSettvprogramnotice.sentPacket();
			case CommandType.COMMAND_REPORTVIDEO:
				ReportVideo mReportVideo = new ReportVideo(WeiXmppManager.getInstance().getConnection(), values);
				mReportVideo.sentPacket();
			default:break;
		}
	}
	
	
}
