package com.tcl.wechat.xmpp;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;

import android.os.Handler;

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
