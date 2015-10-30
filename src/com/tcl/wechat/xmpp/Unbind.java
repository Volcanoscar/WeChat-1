/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
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
import com.tcl.wechat.common.IConstant.EventReason;
import com.tcl.wechat.common.IConstant.EventType;
import com.tcl.wechat.common.IConstant.ReturnType;
import com.tcl.wechat.database.Property;

/**
 *  解绑定微信用户
 * @author rex.lei
 *
 */
public class Unbind {
	
	private static final String TAG = Unbind.class.getSimpleName();
	
	private Map<String, String> mValues;
	private XmppEventListener mListener;
	private XMPPConnection mXmppConnection;
	
	public Unbind(Map<String, String> mValues, XmppEventListener mListener) {
		super();
		this.mValues = mValues;
		this.mListener = mListener;
		this.mXmppConnection = WeiXmppManager.getInstance().getConnection();
	}

	public void sentPacket(){
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				
				if (mXmppConnection == null){
					Log.w(TAG, "disConnection!!");
					if (mListener != null){
						mListener.onEvent(new XmppEvent(this, EventType.TYPE_UNBIND_EVENT, 
								EventReason.REASON_CONNECT_FAILED, null));
					}
					return null;
				}
				
				if(mXmppConnection != null && mXmppConnection.isConnected()){
					
					addUnbindListener();
					
					String openid = mValues.get(Property.COLUMN_OPENID);
					String deviceid = mValues.get(Property.COLUMN_DEVICEID);
					
					StringBuffer content = new StringBuffer("<unbind xmlns=\"tcl:hc:wechat\">");
					content.append("<openid>").append(openid).append("</openid>")
							.append("<deviceid>").append(deviceid).append("</deviceid>")
							.append("</unbind>");
					
					UserContentIQ userContentIQ = new UserContentIQ(content.toString());
					userContentIQ.setType(IQ.Type.SET);
					mXmppConnection.sendPacket(userContentIQ);						
					Log.d(TAG, "Send unbind event:"+userContentIQ.toXML());
				} 
				return null;
			}
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}

	private void addUnbindListener() {
		ProviderManager.getInstance().addIQProvider("unbind", "tcl:hc:wechat", new UnbindProvider());
		PacketFilter filter = new PacketTypeFilter(UnbindResultIQ.class);
		
		mXmppConnection.addPacketListener(new PacketListener() {
			
			@Override
			public void processPacket(Packet packet) {
				IQ packetIq = (IQ) packet;
				Log.d(TAG, "Receive Unbind Packet:" + packetIq.toXML());
				if (packetIq instanceof UnbindResultIQ){
					UnbindResultIQ unbindResultIQ = (UnbindResultIQ) packet;
					String errorCode = unbindResultIQ.getErrorcode();
					String openid = unbindResultIQ.getopenid();
					if (ReturnType.STATUS_SUCCESS.equals(errorCode)){
						if (mListener != null){
							mListener.onEvent(new XmppEvent(this, EventType.TYPE_UNBIND_EVENT, 
									EventReason.REASON_COMMON_SUCCESS, openid));
						}
					} else {
						if (mListener != null){
							mListener.onEvent(new XmppEvent(this, EventType.TYPE_UNBIND_EVENT, 
									EventReason.REASON_COMMON_FAILED, openid));
						}
					}
				}
			}
		}, filter);
	}
}