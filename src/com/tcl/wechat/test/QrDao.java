package com.tcl.wechat.test;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tcl.wechat.db.DBHelper;
import com.tcl.wechat.db.Property;
import com.tcl.wechat.modle.QrInfo;

public class QrDao {
	
	private DBHelper mDbHelper;

	public QrDao(Context context) {
		mDbHelper = new DBHelper(context);
	}
	
	/**
	 * 插入数据
	 * @param qrInfo
	 */
	public void insert(QrInfo qrInfo){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_QR_URL, qrInfo.getUrl());
		values.put(Property.COLUMN_UUID, qrInfo.getUuid());
		db.insert(Property.TABLE_QR, null, values);
		
	}
	
	/**
	 * 根据uuid获取Qr信息
	 * @param uuid
	 * @return
	 */
	public QrInfo getQrInfoByUUID(String uuid){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor  cursor = db.query(Property.TABLE_QR, null, Property.COLUMN_UUID + "=" + uuid, null, null, null, null);
		if (cursor != null) {
			String url = cursor.getString(cursor.getColumnIndex(uuid));
			return new QrInfo(url, uuid);
		}
		return null;
	}
	
	/**
	 * 根据uuid获取url
	 * @param uuid
	 * @return
	 */
	public String getQrUrl(String uuid){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor  cursor = db.query(Property.TABLE_QR, null, Property.COLUMN_UUID + "=" + uuid, null, null, null, null);
		if (cursor != null) {
			return cursor.getString(cursor.getColumnIndex(uuid));
		}
		return null;
	}
	
	/**
	 * 获取uuid
	 * @return
	 */
	public String getQrUUID(){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.query(Property.TABLE_QR, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			return cursor.getString(cursor.getColumnIndex(Property.COLUMN_UUID));
		}
		return null;
	}
	
	/**
	 * 更新url
	 * @param uuid
	 * @param url
	 */
	public void updateUrl(String uuid,String url){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_QR_URL, url);
		db.update(Property.TABLE_QR, values, Property.COLUMN_UUID + "=" + uuid, null);
	}
	
	/**
	 * 更新uuid
	 * @param uuid
	 */
	public void updateUUID(String uuid){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_UUID, uuid);
		db.update(Property.TABLE_QR, values, Property.COLUMN_UUID + "=" + uuid, null);
	}
	
	
	/**
	 * 删除数据
	 * @param uuid
	 */
	public void delete(String uuid){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.delete(Property.TABLE_QR, Property.COLUMN_UUID + "=" + uuid, null);
	}
}
