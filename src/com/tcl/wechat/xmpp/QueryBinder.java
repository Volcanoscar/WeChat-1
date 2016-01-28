/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.xmpp;

import java.util.ArrayList;

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
import com.tcl.wechat.model.BindUser;

/**
 * 查询绑定用户
 * @author rex.lei
 *
 */
public class QueryBinder {
	
	private static final String TAG = QueryBinder.class.getSimpleName();
	
	private XMPPConnection mConnection = null;
	private XmppEventListener mListener = null;
	private PacketListener mPacketListener = null;
	
	public QueryBinder(XMPPConnection mConnection, XmppEventListener mListener) {
		super();
		this.mConnection = mConnection;
		this.mListener = mListener;
	}

	public void sentPacket(){
		
		new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				ProviderManager.getInstance().addIQProvider("querybinder", "tcl:hc:wechat", new QueryBinderProvider());
				PacketFilter filter = new PacketTypeFilter(QueryBinderResultIQ.class);
				if (mPacketListener != null){
					mConnection.removePacketListener(mPacketListener);
					mPacketListener = null;
				}
				mConnection.addPacketListener(mPacketListener = new PacketListener() {
					
					@Override
					public void processPacket(Packet packet) {
						IQ packetIq = (IQ) packet;
						Log.d(TAG, "QueryBinder:" + packetIq.toXML());
						if (packetIq instanceof QueryBinderResultIQ){
							QueryBinderResultIQ getQueryBinderResultIQ = (QueryBinderResultIQ) packet;
							String errorCode = getQueryBinderResultIQ.getErrorcode();
							ArrayList<BindUser> files = getQueryBinderResultIQ.getFiles();
							
							if (ReturnType.STATUS_SUCCESS.equals(errorCode)){
								if (mListener != null){
									mListener.onEvent(new XmppEvent(this, EventType.TYPE_GET_BINDUSER, 
											EventReason.REASON_COMMON_SUCCESS, files));
								}
							} else {
								if (mListener != null){
									mListener.onEvent(new XmppEvent(this, EventType.TYPE_GET_BINDUSER, 
											EventReason.REASON_COMMON_FAILED, null));
								}
							}
						}
					}
				}, filter);
				
				
				String content = "<querybinder xmlns=\"tcl:hc:wechat\">" + "</querybinder>";
				UserContentIQ userContentIQ = new UserContentIQ(content);
				userContentIQ.setType(IQ.Type.GET);
				mConnection.sendPacket(userContentIQ);
				Log.d(TAG, "send QueryBinder:" + userContentIQ.toXML());
				return null;
			}
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}
}
