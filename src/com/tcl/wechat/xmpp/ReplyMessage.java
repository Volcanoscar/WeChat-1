package com.tcl.wechat.xmpp;

import java.util.Map;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;

import android.os.AsyncTask;
import android.util.Log;

import com.tcl.wechat.WeApplication;
import com.tcl.wechat.common.IConstant.ChatMsgType;
import com.tcl.wechat.common.IConstant.EventReason;
import com.tcl.wechat.common.IConstant.EventType;
import com.tcl.wechat.common.IConstant.ReturnType;
import com.tcl.wechat.utils.NetWorkUtil;

/**
 * 消息回复
 * @author rex.lei
 *
 */
public class ReplyMessage {
	
	private static final String TAG = ReplyMessage.class.getSimpleName();
	
	private Map<String, String> values;
	private XmppEventListener mListener;
	private XMPPConnection mConnection = null;
	
	public ReplyMessage(Map<String, String> values, XmppEventListener mListener) {
		super();
		this.values = values;
		this.mListener = mListener;
		this.mConnection = WeiXmppManager.getInstance().getConnection();
	}
	

	/**
	 * 发送消息
	 */
	public void sentPacket(){
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				if (!NetWorkUtil.isNetworkAvailable()){
					Log.w(TAG, "Network is not available!!");
					return null;
				}
				
				if (mConnection != null && mConnection.isConnected()){
					//添加回复监听
					addReplyListener();
					
					//发送消息
					StringBuffer content = new StringBuffer();
					content.append("<tvreplymsg xmlns=\"tcl:hc:portal\">")
							.append("<tousername>").append( values.get("tousername")).append("</tousername>")
							.append("<fromusername>").append(values.get("fromusername")).append("</fromusername>")
							.append("<createtime>").append( values.get("createtime")).append("</createtime>")
							.append("<msgid>").append(values.get("msgid")).append("</msgid>")
							.append("<msgtype>").append( values.get("msgtype")).append("</msgtype>");
					if (ChatMsgType.TEXT.equals(values.get("msgtype"))){
						content.append("<content>").append( values.get("content")).append("</content>");
					} else {
						content.append("<mediaid>").append( values.get("mediaid")).append("</mediaid>");
					}
					content.append("</tvreplymsg>");	
					IQ userContentIQ = new UserContentIQ(content.toString());
					userContentIQ.setType(IQ.Type.SET);
					mConnection.sendPacket(userContentIQ);	
					Log.i(TAG, "userContentIQ:" + userContentIQ.toXML() );
				}
				return null;
			}
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}
	
	
	private void addReplyListener() {
		// TODO Auto-generated method stub
		ProviderManager.getInstance().addIQProvider("tvreplymsg", "tcl:hc:portal", new ReplyProvider());
		PacketFilter filter = new PacketTypeFilter(ReplyResultIQ.class);
		mConnection.addPacketListener(new PacketListener() {
			
			@Override
			public void processPacket(Packet packet) {
				IQ packetIq = (IQ) packet;
				Log.d(TAG, "Reply Result:" + packetIq.toXML());
				
				if(packetIq instanceof ReplyResultIQ){
					ReplyResultIQ replyIq = (ReplyResultIQ) packet;
					String errorCode = replyIq.getErrorcode();
					if (ReturnType.STATUS_SUCCESS.equals(errorCode)){
						ReplyResult result = new ReplyResult();
						result.setErrCode(replyIq.getErrorcode());
						result.setResult(replyIq.getResult());
						result.setMsgid(replyIq.getMsgid());
						
						if (mListener != null){
							mListener.onEvent(new XmppEvent(this, EventType.TYPE_SEND_WEIXINMSG, 
									EventReason.REASON_COMMON_SUCCESS, result));
						} 
					} else {
						if (mListener != null){
							mListener.onEvent(new XmppEvent(this, EventType.TYPE_SEND_WEIXINMSG, 
									EventReason.REASON_COMMON_FAILED, null));
						} 
					}
				}
			}
		}, filter);
	}
}