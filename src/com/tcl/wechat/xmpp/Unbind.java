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

import android.os.Handler;
import android.util.Log;

import com.tcl.wechat.common.WeiConstant;
import com.tcl.wechat.common.WeiConstant.CommandType;
import com.tcl.wechat.modle.WeiNotice;
import com.tcl.wechat.utils.BaseUIHandler;
import com.tcl.wechat.utils.UIUtils;

/**
 * @ClassName: Unbind
 * @Description: 解绑定微信用户
 */

public class Unbind {
	private String tag = "Unbind";
	private  XMPPConnection connection = null;
	private  BaseUIHandler mHandler = null;
	private static PacketListener packetListener = null;
	private IQ userContentIQ;
	private String openid = null;
	private Handler timeHandler = new Handler();
	public Unbind(XMPPConnection conn ,BaseUIHandler handler){
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

					ProviderManager.getInstance().addIQProvider("unbind", "tcl:hc:wechat", new UnbindProvider());
					PacketFilter filter = new PacketTypeFilter(UnbindResultIQ.class);//success		

					if (packetListener == null){
						connection.addPacketListener(new PacketListener() {

							@Override
							public void processPacket(Packet p) {
				
								IQ myIQ = (IQ) p;
								Log.d(tag, "UnbindResultIQ返回结果:" + myIQ.toXML());
								timeHandler.removeCallbacks(mUpdateTimeTask);//移除超时处理
								try {

									if(p instanceof UnbindResultIQ){							
									
										UnbindResultIQ unbindResultIQ = (UnbindResultIQ) p;
										String err = unbindResultIQ.getErrorcode();
										String openid = unbindResultIQ.getopenid();
										if (mHandler != null){
											
											WeiNotice weiNotice = new WeiNotice();
											weiNotice.setOpenid(openid);
											mHandler.setStatus(err);
											mHandler.setData(weiNotice);
											mHandler.sendEmptyMessage(CommandType.COMMAND_UN_BINDER);
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}, filter);	
					}
					//超时处理
					timeHandler.removeCallbacks(mUpdateTimeTask);
					timeHandler.postDelayed(mUpdateTimeTask, WeiConstant.TIME_OUT);
					
					if(connection.isConnected()){
						String content = "<unbind xmlns=\"tcl:hc:wechat\">" 
								+ "<openid>" + UIUtils.inflterNull(openid) + "</openid>" 
								+ "<service>" + "tv" + "</service>" 
								+"</unbind>";
						userContentIQ = new UserContentIQ(content);
						userContentIQ.setType(IQ.Type.SET);
						connection.sendPacket(userContentIQ);						
						Log.d(tag, "发送获取unbind请求"+userContentIQ.toXML());
					}
					else{
						Log.d(tag, "unconnected when 发送获取unbind请求");
						if (mHandler != null){						
							mHandler.sendEmptyMessage(CommandType.COMMAND_UN_BINDER_ERROR);
						}
					}
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					Log.d(tag, "e.printStackTrace="+e);
					if (mHandler != null){						
						mHandler.sendEmptyMessage(CommandType.COMMAND_UN_BINDER_ERROR);
					}
				}
			}
		}).start();
		
	}
	/**
	 * 网络请求超时后跳转
	 */
	private Runnable mUpdateTimeTask = new Runnable() {     
		
		public void run() { 
			
			Log.d(tag, "解绑设备超时处理");
			if (mHandler != null){						
				mHandler.sendEmptyMessage(CommandType.COMMAND_UN_BINDER_ERROR);
			}
			
		}
	};


	/**
	 * @return the openid
	 */
	public String getOpenid() {
		return openid;
	}

	/**
	 * @param openid the openid to set
	 */
	public void setOpenid(String openid) {
		this.openid = openid;
	}

}