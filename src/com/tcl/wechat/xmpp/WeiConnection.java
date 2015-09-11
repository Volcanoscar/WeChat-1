/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.xmpp;


import java.util.List;

import org.jivesoftware.smack.XMPPConnection;



import android.os.Parcel;
import android.os.Parcelable;

/**
 * @ClassName: WeiConnection
 */

public class WeiConnection implements Parcelable  {
	private Object object;
	
	

	/**
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public WeiConnection(Object conn){
		object = conn;
	}
	public WeiConnection(Parcel source) {
		// TODO Auto-generated constructor stub
		object = source.readValue(Object.class.getClassLoader());
	}
	

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeValue(object);
	}

	 public static final Parcelable.Creator<WeiConnection> CREATOR = new Parcelable.Creator<WeiConnection>() {   
		//��дCreator
		  
		        public WeiConnection createFromParcel(Parcel source) {   
		            return new WeiConnection(source);   
		        }   
		  

		        public WeiConnection[] newArray(int size) {   
		            // TODO Auto-generated method stub   
		            return null;   
		        }   
		    };   
  
}
