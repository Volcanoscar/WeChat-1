package com.tcl.wechat.xmpp;

import java.util.EventObject;

/**
 * Xmpp事件类
 * @author rex.lei
 *
 */
public class XmppEvent extends EventObject{
	
	/**
	 * 自动生成的序列化ID
	 */
	private static final long serialVersionUID = 4115702312940717508L;

	private Object mSource = null;
	private int mType = -1;
	private int mReason = -1;
	private Object mEventData = null;
	
	public XmppEvent(Object source,int type,int reason,Object eventData) {
		super(source);
		mSource = source;
		mType = type;
		mReason = reason;
		mEventData = eventData;
	}

	/**
	 * 获取事件来源
	 * */
	public Object getSource(){return mSource;}
	
	/**
	 * 获取事件类型，取值见常量定义中的TYPE_***
	 * */
	public int getType(){return mType;}
	
	/**
	 * 获取事件原因，取值见常量定义中的REASON_***
	 * */
	public int getReason(){return mReason;}
	
	/**
	 * 获取事件数据，取值由REASON定义，见各种reason取值的注释说明
	 * */
	public Object getEventData(){return mEventData;}
}
