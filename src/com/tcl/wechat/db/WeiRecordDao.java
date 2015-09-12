/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tcl.wechat.modle.WeiXinMsg;

/**
 * @ClassName: WeiRecordDao
* @Description: 用于存储用户分享记录
 */

public class WeiRecordDao {
	
	private static final String TAG = WeiRecordDao.class.getSimpleName();
	
	private DBHelper mDbHelper;
	
	private static WeiRecordDao mInstance;
	
	private WeiRecordDao(Context context) {
		super();
		mDbHelper = new DBHelper(context);
		mDbHelper.getReadableDatabase();
	}

	public static void initWeiRecordDao(Context context){
		if (mInstance == null){
			mInstance = new WeiRecordDao(context);
		}
	}
	
	public static WeiRecordDao getInstance(){
		if (mInstance == null){
			throw new NullPointerException("WeiRecordDao is Null, " +
					"You should initialize WeiRecordDao first!!");
		}
		return mInstance;
	}
	
	
	
	public boolean addUserRecorder(WeiXinMsg recorder){
		
		
		
		
		return false;
	}
	
	
	public boolean save(WeiXinMsg msg)
	{
		// 如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		Log.i(TAG,"openid"+msg.getOpenid());
		Log.i(TAG,"getFileTime="+msg.getFileTime());
		
		db.execSQL("insert into weiuserrecord (openid,msgtype, content ,url,format,createtime,accesstoken,mediaid,thumbmediaid,expiretime,read,filename,filesize,filetime) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[] { msg.getOpenid(), msg.getMsgtype(),
					msg.getContent(), msg.getUrl() , msg.getFormat(), msg.getCreatetime(), msg.getAccesstoken(), msg.getMediaid(), msg.getThumbmediaid(), msg.getExpiretime(), "false",msg.getFileName()+"",msg.getFileSize()+"",msg.getFileTime()+""});	
		return true;
	}
	
	public List<WeiXinMsg> find(String openid) 
	{
		List<WeiXinMsg> msgList = new ArrayList<WeiXinMsg>();

		// 如果只对数据进行读取，建议使用此方法
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from weiuserrecord where openid=?",new String[] { openid });
		cursor.moveToLast();
		Log.i(TAG,"cursor.getCount()"+cursor.getCount());
		if (cursor.getCount() == 0 ){
			return null;
		}
		do{
			WeiXinMsg object = new WeiXinMsg();
			object.setOpenid(cursor.getString(cursor.getColumnIndex("openid")));
			object.setMsgtype(cursor.getString(cursor.getColumnIndex("msgtype")));
			object.setContent(cursor.getString(cursor.getColumnIndex("content")));
			object.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			object.setFormat(cursor.getString(cursor.getColumnIndex("format")));
			object.setCreatetime(cursor.getString(cursor.getColumnIndex("createtime")));
			object.setAccesstoken(cursor.getString(cursor.getColumnIndex("accesstoken")));
			object.setMediaid(cursor.getString(cursor.getColumnIndex("mediaid")));
			object.setThumbmediaid(cursor.getString(cursor.getColumnIndex("thumbmediaid")));
			object.setExpiretime(cursor.getString(cursor.getColumnIndex("expiretime")));
			object.setRead(cursor.getString(cursor.getColumnIndex("read")));
			object.setFileName(cursor.getString(cursor.getColumnIndex("filename")));
			object.setFileSize(cursor.getString(cursor.getColumnIndex("filesize")));
			object.setFileTime(cursor.getString(cursor.getColumnIndex("filetime")));
			msgList.add(object);
		
		} while (cursor.moveToPrevious());//!cursor.isFirst()

		cursor.close();
		return msgList;
	}
	public void delMsg(String openid,String createtime,String url)
	{
		Log.i("liyulin","1111delMsg-openid="+openid+";createtime="+createtime+";url="+url);
	
		// 如果只对数据进行读取，建议使用此方法
		SQLiteDatabase db = mDbHelper.getWritableDatabase();		
		db.delete("weiuserrecord", "openid=? and url=? and createtime=?",  new String[] { openid,url,createtime }) ;
		
	}
	//用户删除后，此用户的分享记录也要删除
	public void delMsg(String openid)
	{
		Log.i("liyulin","1111delMsg-openid="+openid);
	
		// 如果只对数据进行读取，建议使用此方法
		SQLiteDatabase db = mDbHelper.getWritableDatabase();		
		db.delete("weiuserrecord", "openid=?",  new String[] { openid }) ;
		
	}
	public boolean updateRead(String openid, boolean flag){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.execSQL("update weiuserrecord set read=? where openid=?",new Object[] { flag+"",openid });
		return true;
	}
	public boolean updateContent(String url,String saveurl){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		Log.i("liyulin", "updateContent:url="+url+";\nsaveurl="+saveurl);
		db.execSQL("update weiuserrecord set content=? where url=? or thumbmediaid=?",new Object[] {saveurl ,url ,url });
		return true;
	}
	public boolean updateFileName(String url,String filename){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.execSQL("update weiuserrecord set filename=? where url=?",new Object[] {filename ,url });
		return true;
	}
	public boolean updateFileSize(String url,int size){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.execSQL("update weiuserrecord set filesize=? where url=?",new Object[] {size ,url });
		return true;
	}
	public boolean updateFileTime(String url,int time){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.execSQL("update weiuserrecord set filetime=? where url=?",new Object[] {time ,url });
		return true;
	}
	public List<WeiXinMsg> find() 
	{
		List<WeiXinMsg> msgList = new ArrayList<WeiXinMsg>();

		// 如果只对数据进行读取，建议使用此方法
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from weiuserrecord",null);
		while (cursor.moveToNext())
		{
			WeiXinMsg object = new WeiXinMsg();
			object.setOpenid(cursor.getString(cursor.getColumnIndex("openid")));
			object.setMsgtype(cursor.getString(cursor.getColumnIndex("msgtype")));
			object.setContent(cursor.getString(cursor.getColumnIndex("content")));
			object.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			object.setFormat(cursor.getString(cursor.getColumnIndex("format")));
			object.setCreatetime(cursor.getString(cursor.getColumnIndex("createtime")));
			object.setAccesstoken(cursor.getString(cursor.getColumnIndex("accesstoken")));
			object.setMediaid(cursor.getString(cursor.getColumnIndex("mediaid")));
			object.setThumbmediaid(cursor.getString(cursor.getColumnIndex("thumbmediaid")));
			object.setExpiretime(cursor.getString(cursor.getColumnIndex("expiretime")));
			object.setRead(cursor.getString(cursor.getColumnIndex("read")));
			object.setFileName(cursor.getString(cursor.getColumnIndex("filename")));
			object.setFileSize(cursor.getString(cursor.getColumnIndex("filesize")));
			object.setFileTime(cursor.getString(cursor.getColumnIndex("filetime")));
			msgList.add(object);
		}
		cursor.close();
		return msgList;
	}
	public String  findOldRecord(){
		String filename = null;
		String msgtype = "video";
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from weiuserrecord where msgtype=? ORDER BY 0+createtime ASC ",new String[] { msgtype });
		cursor.moveToNext();
		
		filename = cursor.getString(cursor.getColumnIndex("filename"));
		Log.i("liyulin","filename="+filename+";time="+ cursor.getString(cursor.getColumnIndex("createtime")));
		//删除早期的记录
		String createtime = cursor.getString(cursor.getColumnIndex("createtime"));
		String openid =  cursor.getString(cursor.getColumnIndex("openid"));
		db.delete("weiuserrecord", "openid=? and filename=? and createtime=?",  new String[] { openid,filename,createtime }) ;
		
		return filename;

	}
}
