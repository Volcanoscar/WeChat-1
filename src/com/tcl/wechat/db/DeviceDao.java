/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;


/**
 * 设备表
 * @author rex.lei
 *
 */
public class DeviceDao {
	
	private DBHelper mDbHelper;
	private static DeviceDao mInstance;

	private DeviceDao(Context context){
		mDbHelper = new DBHelper(context);
		mDbHelper.getReadableDatabase();
	}
	
	public static void initDeviceDao(Context context){
		if (mInstance == null){
			mInstance = new DeviceDao(context);
		}
	}
	
	public static DeviceDao getInstance(){
		if (mInstance == null){
			throw new NullPointerException("DeviceDao is Null, You should initialize DeviceDao first!!");
		}
		return mInstance;
	}
	
	/**
	 * 获取MemberId
	 * @return
	 */
	public String getMemberId(){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.query(Property.TABLE_DEVICE, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			String memId = cursor.getColumnName(cursor.getColumnIndex(Property.COLUMN_MEMBERID));
			cursor.close();
			return memId;
		}
		return null;
	}
	
	/**
	 * 更新MemberId
	 * @return
	 */
	public boolean updateMemberId(String memId){
		if (TextUtils.isEmpty(memId)){
			return false;
		}
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_MEMBERID, memId);
		if (db.update(Property.TABLE_DEVICE, values, null, null) > 0){
			return true;
		}
		return false;
	}
}
