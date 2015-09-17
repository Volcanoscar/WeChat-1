/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.db;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.modle.BindUser;
import com.tcl.wechat.modle.WeiXinMsgRecorder;

/**
 * 微信消息记录类
 * @author rex.lei
 *
 */
public class WeiMsgRecordDao {
	
	private static final String TAG = WeiMsgRecordDao.class.getSimpleName();
	
	private DBHelper mDbHelper;
	
	private static WeiMsgRecordDao mInstance;
	
	private WeiMsgRecordDao(Context context) {
		super();
		mDbHelper = new DBHelper(context);
		mDbHelper.getReadableDatabase();
	}

	public static void initWeiRecordDao(Context context){
		if (mInstance == null){
			mInstance = new WeiMsgRecordDao(context);
		}
	}
	
	public static WeiMsgRecordDao getInstance(){
		if (mInstance == null){
			throw new NullPointerException("WeiRecordDao is Null, " +
					"You should initialize WeiRecordDao first!!");
		}
		return mInstance;
	}
	
	/**
	 * 判断用户是否存在
	 * @param openid
	 * @return
	 */
	public boolean bindUserIsExist(String openid){
		if (TextUtils.isEmpty(openid)){
			OpenidException();
			return false;
		}
		BindUser user = WeiUserDao.getInstance().getUser(openid);
		if (user == null){
			BinderUserNotFoundException();
			return false ;
		}
		return true;
	}
	
	public boolean addRecorder(WeiXinMsgRecorder recorder){
		if (recorder == null){
			return false;
		}
		if (!bindUserIsExist(recorder.getOpenid())){
			Log.e(TAG, "BinderUser not exist!!");
			return false;
		}
		
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_OPENID, recorder.getOpenid());
		values.put(Property.COLUMN_ACCESS_TOKENS, recorder.getAccesstoken());
		values.put(Property.COLUMN_MSGTYPE, recorder.getMsgtype());
		values.put(Property.COLUMN_MSGID, recorder.getMsgid());
		values.put(Property.COLUMN_CONTENT, recorder.getContent());
		values.put(Property.COLUMN_URL, recorder.getUrl());
		values.put(Property.COLUMN_FORMAT, recorder.getFormat());
		values.put(Property.COLUMN_MDDIAID, recorder.getMediaid());
		values.put(Property.COLUMN_THUMBMEDIAID, recorder.getThumbmediaid());
		values.put(Property.COLUMN_CREATE_TIME, recorder.getCreatetime());
		values.put(Property.COLUMN_EXPIRETIME, recorder.getExpiretime());
		values.put(Property.COLUMN_READED, recorder.getRead());
		values.put(Property.COLUMN_FILENAME, recorder.getFileName());
		values.put(Property.COLUMN_FILESIZE, recorder.getFileSize());
		values.put(Property.COLUMN_FILETIME, recorder.getFileTime());
		if (db.insert(Property.TABLE_USERMSGRECORD, null, values) != -1){
			return true;
		}
		return false;
	}
	
	/**
	 * 删除指定用户消息记录
	 * @param openid
	 * @return
	 */
	public boolean deleteUserRecorder(String openid){
		if (bindUserIsExist(openid)){
			Log.e(TAG, "BinderUser not exist!!");
			return false;
		}
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		String whereClause = Property.COLUMN_OPENID + "=?";
		String[] whereArgs = new String[]{openid};
		if (db.delete(Property.TABLE_USERMSGRECORD, whereClause, whereArgs) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 删除所用用户消息记录
	 * @return
	 */
	public boolean deleteAllUserRecorder(){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		if (db.delete(Property.TABLE_USERMSGRECORD, null, null) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 删除某条消息记录
	 * @param openid
	 * @param msgId
	 * @return
	 */
	public boolean deleteRecorder(String openid, String msgId){
		if (bindUserIsExist(openid)){
			Log.e(TAG, "BinderUser not exist!!");
			return false;
		}
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		String whereClause = Property.COLUMN_OPENID + "=? " + Property.COLUMN_MSGID + "=?";
		String[] whereArgs = new String[]{openid, msgId};
		if (db.delete(Property.TABLE_USERMSGRECORD, whereClause, whereArgs) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 获取用户所用消息记录 //TODO 开发接口，获取多少条？？
	 * @param openid
	 * @return
	 */
	public ArrayList<WeiXinMsgRecorder> getUserRecorder(String openid){
		if (bindUserIsExist(openid)){
			Log.e(TAG, "BinderUser not exist!!");
			return null;
		}
		
		ArrayList<WeiXinMsgRecorder> recorders = new ArrayList<WeiXinMsgRecorder>();
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		
		String selection = Property.COLUMN_OPENID + "=? ";
		String[] selectionArgs = new String[]{openid};
		String orderBy = Property.COLUMN_CREATE_TIME;
		Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, null, selection, selectionArgs, null, null, orderBy);
		if (cursor != null){
			while (cursor.moveToNext()) {
				WeiXinMsgRecorder recorder = new WeiXinMsgRecorder(openid, 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_ACCESS_TOKENS)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGTYPE)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGID)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_CONTENT)) ,
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_URL)) , 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_FORMAT)) , 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_CREATE_TIME)) , 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_EXPIRETIME)) , 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_MDDIAID)) , 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_THUMBMEDIAID)) ,
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_READED)) , 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILENAME)) , 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILESIZE)) , 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILETIME)) , 
						"");
				recorders.add(recorder);
			}
			cursor.close();
		}
		return recorders;
	}
	
	/**
	 * 获取所有用户消息记录
	 * @return
	 */
	public ArrayList<WeiXinMsgRecorder> getAllUserRecorder(){
		ArrayList<WeiXinMsgRecorder> recorders = new ArrayList<WeiXinMsgRecorder>();
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String orderBy = Property.COLUMN_CREATE_TIME;
		Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, null, null, null, null, null, orderBy );
		if (cursor != null){
			while (cursor.moveToNext()){
				WeiXinMsgRecorder recorder = new WeiXinMsgRecorder(
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_OPENID)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_ACCESS_TOKENS)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGTYPE)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGID)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_CONTENT)) ,
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_URL)) , 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_FORMAT)) , 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_CREATE_TIME)) , 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_EXPIRETIME)) , 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_MDDIAID)) , 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_THUMBMEDIAID)) ,
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_READED)) , 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILENAME)) , 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILESIZE)) , 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILETIME)) , 
						"");
				recorders.add(recorder);
			}
			cursor.close();
		}
		return recorders;
	}
	
	
	/**
	 * openid错误异常
	 */
	private void OpenidException(){
		try {
			throw new Exception("openid is NULL");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 用户不存在异常
	 */
	public void BinderUserNotFoundException(){
		try {
			throw new Exception("Users do not exist!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
