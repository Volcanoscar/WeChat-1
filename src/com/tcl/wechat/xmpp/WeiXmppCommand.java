package com.tcl.wechat.xmpp;

import java.util.Map;

import android.util.Log;

import com.tcl.wechat.common.IConstant.EventReason;
import com.tcl.wechat.common.IConstant.EventType;

/**
 * 协议接口控制类, 上报服务器事件
 * @author rex.lei
 *
 */
public class WeiXmppCommand {

	private static final String TAG = WeiXmppCommand.class.getSimpleName();
	
	private int mType;
	private Map<String, String> mValues;
	private XmppEventListener mListener;
	
	public WeiXmppCommand(int mType, Map<String, String> mValues,
			XmppEventListener mListener) {
		super();
		this.mType = mType;
		this.mValues = mValues;
		this.mListener = mListener;
	}

	public void execute(){
		if(!WeiXmppManager.getInstance().isConnected()){
			Log.d(TAG, "WeiXmppManager disConnect!!");
			if (mListener != null){
				mListener.onEvent(new XmppEvent(this, EventType.TYPE_NETWORK_ERROR, 
						EventReason.REASON_NETWORK_UNCONNECT, null));
			}
			return ;
		}
		
		Log.i(TAG, "mType:" + mType);
		switch (mType) {
		case EventType.TYPE_GET_QR: 
			 Getqr getqr = new Getqr(WeiXmppManager.getInstance().getConnection(), mListener);
			 getqr.sentPacket();
			break;

		case EventType.TYPE_GET_BINDUSER:
			QueryBinder queryBinder = new QueryBinder(WeiXmppManager.getInstance().getConnection(), mListener);
			queryBinder.sentPacket();
			break;
			
		case EventType.TYPE_UNBIND_EVENT:
			Unbind unbind = new Unbind(mValues, mListener);
			unbind.sentPacket();
			break;
			
		case EventType.TYPE_SEND_WEIXINMSG:
			ReplyMessage replyMessage = new ReplyMessage(mValues, mListener);
			replyMessage.sentPacket();
			break;
			
		case EventType.TYPE_RESPONSE_SERVER: //当收到服务器消息时，给服务器响应
			MsgResponse mMsgResponse = new MsgResponse(WeiXmppManager.getInstance().getConnection(), mValues);
			mMsgResponse.sentPacket();
			break;
			
		case EventType.TYPE_REPORT_DEVICEINFO:
			ReportDeviceInfo mReportDeviceInfo = new ReportDeviceInfo(WeiXmppManager.getInstance().getConnection(), mValues);
			mReportDeviceInfo.sentPacket();
			break;
			
		default:
			break;
		}
	}
}
