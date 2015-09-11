package com.tcl.wechat.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.tcl.wechat.modle.BinderUser;
import com.tcl.wechat.modle.WeiXinMsg;


public class ProviderFun {
	private static String tag = "ProviderFun";
	
	//写到数据库，供外部进程访问
	 public static void insertRecord(Context mContext,String userName) {
	 
	        ContentValues values = new ContentValues();
	       
	        String[] str_user = { "id", MyUsers.QR_KEY };	      
	        try {
				Cursor c = mContext.getContentResolver().query(MyUsers.CONTENT_URI, str_user, null,
				     null, null);
				if (c == null || c.getCount() <= 0) {
					 values.put(MyUsers.QR_KEY, userName); 
					 mContext.getContentResolver().insert(MyUsers.CONTENT_URI, values);				 
				} else {
					Log.d("qr", "共享数据库已经存在，不再insert" );
				}

				if (c != null && !c.isClosed()) {
				   c.close();
				}
			} catch (SQLiteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
	        
	    }
	//可以提供给其他进程获取二维码的地址         
	 public static String getQR_url(Context mContext){
		   
			String[] str_user = { "id", MyUsers.QR_KEY };
			
		    String QRurl = "";
		    try {
				Cursor c = mContext.getContentResolver().query(MyUsers.CONTENT_URI, str_user, null,
				     null, null);
				if (c != null && c.getCount() > 0) {
				   c.moveToFirst();
				   QRurl = c.getString(c.getColumnIndex(MyUsers.QR_KEY )) == null ? ""
				        : c.getString(c.getColumnIndex(MyUsers.QR_KEY ));
				   Log.d("qr", "@@@get QRurl from db is :" + QRurl);
				} else {
					QRurl = "";
				}

				if (c != null && !c.isClosed()) {
				   c.close();
				}
			} catch (SQLiteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
		    return QRurl;
		}
	//可以提供给其他进程获取UUID         
		 public static String getuuid(Context mContext){
			   
				String[] str_user = { "id", MyUsers.UUID_KEY };
				
			    String uuid = "";
			    try {
					Cursor c = mContext.getContentResolver().query(MyUsers.CONTENT_URI, str_user, null,
					     null, null);
					if (c != null && c.getCount() > 0) {
					   c.moveToFirst();
					   uuid = c.getString(c.getColumnIndex(MyUsers.UUID_KEY )) == null ? ""
					        : c.getString(c.getColumnIndex(MyUsers.UUID_KEY ));
					   Log.d("qr", "@@@get uuid from db is :" + uuid);
					} else {
						uuid = "";
					}

					if (c != null && !c.isClosed()) {
					   c.close();
					}
				} catch (SQLiteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    
			    return uuid;
			}
	 public static List<BinderUser> getUserList(Context mContext){
		  List<BinderUser> cacheList = new ArrayList<BinderUser>();
		  try {
			Cursor c = mContext.getContentResolver().query(MyUsers.CONTENT_USER, null, null,
			     null, null);
			while (c.moveToNext())
			{
				BinderUser object = new BinderUser();
				object.setOpenid(c.getString(c.getColumnIndex("openid")));
				object.setNickname(c.getString(c.getColumnIndex("nickname")));
				object.setSex(c.getString(c.getColumnIndex("sex")));
				object.setHeadimgurl(c.getString(c.getColumnIndex("headimgurl")));
				object.setNewsnum(c.getString(c.getColumnIndex("newsnum")));
				String statusString = c.getString(c.getColumnIndex("status"));
				if(statusString==null||statusString.equals("success"))//绑定的status有的没有赋值，有的是success
					cacheList.add(object);
			}

			if (c != null && !c.isClosed()) {
			   c.close();
			}
		  } catch (SQLiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }    
	    return cacheList;
	}
	 public static List<WeiXinMsg> getRecordList(Context mContext) 
		{
			List<WeiXinMsg> msgList = new ArrayList<WeiXinMsg>();
			 try {
					Cursor c = mContext.getContentResolver().query(MyUsers.CONTENT_RECORD, null, null,
					     null, null);
					c.moveToLast();
					Log.i(tag,"cursor.getCount()"+c.getCount());
					if (c.getCount() == 0 ){
						return null;
					}
					do{
						WeiXinMsg object = new WeiXinMsg();
						object.setOpenid(c.getString(c.getColumnIndex("openid")));
						object.setMsgtype(c.getString(c.getColumnIndex("msgtype")));
						object.setContent(c.getString(c.getColumnIndex("content")));
						object.setUrl(c.getString(c.getColumnIndex("url")));
						object.setFormat(c.getString(c.getColumnIndex("format")));
						object.setCreatetime(c.getString(c.getColumnIndex("createtime")));
						object.setAccesstoken(c.getString(c.getColumnIndex("accesstoken")));
						object.setMediaid(c.getString(c.getColumnIndex("mediaid")));
						object.setThumbmediaid(c.getString(c.getColumnIndex("thumbmediaid")));
						object.setExpiretime(c.getString(c.getColumnIndex("expiretime")));
						object.setRead(c.getString(c.getColumnIndex("read")));
						object.setFileName(c.getString(c.getColumnIndex("filename")));
						object.setFileSize(c.getString(c.getColumnIndex("filesize")));
						object.setFileTime(c.getString(c.getColumnIndex("filetime")));
						msgList.add(object);
					
					} while (c.moveToPrevious());//!cursor.isFirst()

				if (c != null && !c.isClosed()) {
					   c.close();
					}
			 } catch (SQLiteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				  } 
			return msgList;
		}
}
