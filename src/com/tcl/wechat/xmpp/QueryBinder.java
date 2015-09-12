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

import android.util.Log;

import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.utils.BaseUIHandler;

/**
 * @ClassName: QueryBinder
 */

public class QueryBinder {
	private String tag = "QueryBinder";
	private  XMPPConnection connection = null;
	private  BaseUIHandler mHandler = null;
	private static PacketListener packetListener = null;
	private IQ userContentIQ;
	
	public QueryBinder(XMPPConnection conn ,BaseUIHandler handler){
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

					ProviderManager.getInstance().addIQProvider("querybinder", "tcl:hc:wechat", new QueryBinderProvider());
					PacketFilter filter = new PacketTypeFilter(QueryBinderResultIQ.class);//success		

					if (packetListener == null){
						connection.addPacketListener(new PacketListener() {

							@Override
							public void processPacket(Packet p) {
				
								IQ myIQ = (IQ) p;
								Log.d(tag, "QueryBinderResultIQ返回结果:" + myIQ.toXML());

								try {
									Log.i(tag,"000000000000000");
									if(p instanceof QueryBinderResultIQ){							
										Log.i(tag,"1111111111111111111");
										QueryBinderResultIQ getQueryBinderResultIQ = (QueryBinderResultIQ) p;
										String err = getQueryBinderResultIQ.getErrorcode();
										ArrayList<BindUser> files = getQueryBinderResultIQ.getFiles();
										Log.i(tag,"00000files.size="+files.size());
										if (mHandler != null){
											Log.i(tag,"222222222222222");
											Log.i(tag,"files.size="+files.size());
											mHandler.setData(files);
											mHandler.setStatus(err);
											mHandler.sendEmptyMessage(CommandType.COMMAND_GET_BINDER);
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, filter);	
					}
					
					String content = "<querybinder xmlns=\"tcl:hc:wechat\">" + "</querybinder>";
					userContentIQ = new UserContentIQ(content);
					userContentIQ.setType(IQ.Type.GET);
					connection.sendPacket(userContentIQ);
						
					Log.d(tag, "发送获取QueryBinder请求"+userContentIQ.toXML());
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
		
	}
}
