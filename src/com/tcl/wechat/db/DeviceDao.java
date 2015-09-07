package com.tcl.wechat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

public class DeviceDao {

	private DBHelper mDbHelper;

	public DeviceDao(Context context) {
		mDbHelper = new DBHelper(context);
	}
	
	/**
	 * 插入一条设备信息
	 */
	public void insert(String memberid){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_DEVICEID, memberid);
		values.put(Property.COLUMN_DEVICE_NAME, Build.MODEL);
		db.insert(Property.TABLE_DEVICE, null, values);
	}
	
	/**
	 * 获取设备ID
	 * @return
	 */
	public String getDeviceId(){
		String deviceId = null;
		Cursor cursor = null;
		try {
			SQLiteDatabase db = mDbHelper.getReadableDatabase();
			cursor = db.query(Property.TABLE_DEVICE, null, null, null, null, null, null);
			if (cursor != null && cursor.moveToFirst()){
				deviceId = cursor.getString(cursor.getColumnIndex(Property.COLUMN_DEVICEID));
				return deviceId;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (cursor != null){
				cursor.close();
			}
		}
		return null;
	}
}
