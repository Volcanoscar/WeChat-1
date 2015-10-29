/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.tcl.wechat.model.DeviceInfo;


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
	 * 添加设备信息
	 * @param deviceInfo
	 * @return
	 */
	public boolean addDeviceInfo(DeviceInfo deviceInfo) {
		if (deviceInfo == null){
			return false;
		}
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_DEVICEID, deviceInfo.getDeviceId());
		values.put(Property.COLUMN_MAC, deviceInfo.getMacAddr());
		values.put(Property.COLUMN_MEMBERID, deviceInfo.getMemberId());
		if (db.insert(Property.TABLE_DEVICE, null, values) != -1){
			return true;
		}
		return false;
	}
	
	/**
	 * 获取设备信息
	 * @return
	 */
	public DeviceInfo getDeviceInfo(){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.query(Property.TABLE_DEVICE, null, null, null, null, null, null);
		if (cursor != null){
			DeviceInfo deviceInfo = new DeviceInfo(cursor.getString(cursor.getColumnIndex(Property.COLUMN_DEVICEID)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_MAC)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_MEMBERID)));
			cursor.close();
			return deviceInfo;
		}
		return null;
	}
	
	/**
	 * 获取MemberId
	 * @return
	 */
	public String getMemberId(){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.query(Property.TABLE_DEVICE, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			String memId = cursor.getString(cursor.getColumnIndex(Property.COLUMN_MEMBERID));
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
	
	/**
	 * 获取DeviceId
	 * @return
	 */
	public String getDeviceId(){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.query(Property.TABLE_DEVICE, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			String deviceId = cursor.getString(cursor.getColumnIndex(Property.COLUMN_DEVICEID));
			cursor.close();
			return deviceId;
		}
		return null;
	}
	
	/**
	 * 获取MemberId
	 * @return
	 */
	public String getMACAddr(){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.query(Property.TABLE_DEVICE, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			String macAddr = cursor.getString(cursor.getColumnIndex(Property.COLUMN_MAC));
			cursor.close();
			return macAddr;
		}
		return null;
	}
}
