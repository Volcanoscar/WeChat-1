package com.tcl.wechat.xmpp;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.provider.ProviderManager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 初始化完成接�?
 * @author junjian
 *
 */
public class InitFinish {

	private String tag = "InitFinish";
	private XMPPConnection connection = null;
	private Handler mHandler = null;
	

	public InitFinish(XMPPConnection conn ,Handler handler){
		this.connection = conn;
		this.mHandler = handler;
	}

	/**
	 * @Description: 设置家庭云账号昵�?
	 */
	
	public void sentPacket(){

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
				
					String content = "<initfinish  xmlns=\"tcl:hc:portal\">" 
							+ "</initfinish >";
					IQ userContentIQ = new UserContentIQ(content);
					userContentIQ.setType(IQ.Type.SET);
					connection.sendPacket(userContentIQ);
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
		
	}


}
