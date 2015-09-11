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

import android.util.Log;

import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.utils.BaseUIHandler;

/**
 * @ClassName: Getqr
 * @Description: 获取二维码信息
 */

public class Getqr {
	private String tag = "Getqr";
	private  XMPPConnection connection = null;
	private static BaseUIHandler mHandler = null;
	private static PacketListener packetListener = null;
	private IQ userContentIQ;
	
	public Getqr(XMPPConnection conn ,BaseUIHandler handler){
		this.connection = conn;
		this.mHandler = handler;
	}
	
	public static void initPacketListener() {
		packetListener = null;
	}

	public void sentPacket(){

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {

					ProviderManager.getInstance().addIQProvider("getqr", "tcl:hc:wechat", new GetQrProvider());
					PacketFilter filter = new PacketTypeFilter(GetQrResultIQ.class);//success		

					if (packetListener == null){
						connection.addPacketListener(packetListener = new PacketListener() {

							@Override
							public void processPacket(Packet p) {
				
								IQ myIQ = (IQ) p;
								Log.d(tag, "GetQrResultIQ返回结果:" + myIQ.toXML());

								try {

									if(p instanceof GetQrResultIQ){							
									
										GetQrResultIQ getQrResultIQ = (GetQrResultIQ) p;
										String err = getQrResultIQ.getErrorcode();
										String url = getQrResultIQ.getUrl();
										if (mHandler != null){
											mHandler.setData(url);
											mHandler.setStatus(err);
											mHandler.sendEmptyMessage(CommandType.COMMAND_GET_QR);
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, filter);	
					}
					
					String content = "<getqr xmlns=\"tcl:hc:wechat\">" + "</getqr>";
					userContentIQ = new UserContentIQ(content);
					userContentIQ.setType(IQ.Type.GET);
					connection.sendPacket(userContentIQ);
						
					Log.d(tag, "发送获取qr请求"+userContentIQ.toXML());
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
		
	}

}
