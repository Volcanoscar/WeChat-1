/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.xmpp;

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

/**
 * 获取二维码信息
 * @author rex.lei
 *
 */
public class Getqr {
	
	private static final String TAG = Getqr.class.getSimpleName();
	
	private XMPPConnection mConnection = null;
	private XmppEventListener mListener = null;
	private PacketListener mPacketListener = null;
	

	public Getqr(XMPPConnection mConnection, XmppEventListener mListener) {
		super();
		this.mConnection = mConnection;
		this.mListener = mListener;
	}

	public void sentPacket(){
		
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				ProviderManager.getInstance().addIQProvider("getqr", "tcl:hc:wechat", new GetQrProvider());
				PacketFilter filter = new PacketTypeFilter(GetQrResultIQ.class);
				if (mPacketListener != null){
					mConnection.removePacketListener(mPacketListener);
					mPacketListener = null;
				}
				mConnection.addPacketListener(mPacketListener = new PacketListener() {
					
					@Override
					public void processPacket(Packet packet) {
						IQ packetIq = (IQ) packet;
						Log.d(TAG, "GetQr ResultIQ:" + packetIq.toXML());
						if (packetIq instanceof GetQrResultIQ){
							GetQrResultIQ getQrResultIQ = (GetQrResultIQ) packet;
							String errorCode = getQrResultIQ.getErrorcode();
							String url = getQrResultIQ.getUrl();
							
							if (ReturnType.STATUS_SUCCESS.equals(errorCode)){
								if (mListener != null){
									mListener.onEvent(new XmppEvent(this, EventType.TYPE_GET_QR, 
											EventReason.REASON_COMMON_SUCCESS, url));
								}
							} else {
								if (mListener != null){
									mListener.onEvent(new XmppEvent(this, EventType.TYPE_GET_QR, 
											EventReason.REASON_COMMON_FAILED, null));
								}
							}
						}
					}
				}, filter);
				
				
				String content = "<getqr xmlns=\"tcl:hc:wechat\">" + "</getqr>";
				UserContentIQ userContentIQ = new UserContentIQ(content);
				userContentIQ.setType(IQ.Type.GET);
				mConnection.sendPacket(userContentIQ);
				Log.d(TAG, "Send Qr Request:" + userContentIQ.toXML());
				return null;
			}
		}.executeOnExecutor(WeApplication.getExecutorPool());
	}
}
