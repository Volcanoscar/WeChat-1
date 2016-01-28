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
import android.util.Log;

import com.tcl.wechat.WeApplication;
import com.tcl.wechat.model.QrInfo;

/**
 * 二维码信息
 * @author rex.lei
 *
 */
public class WeiQrDao {
	private static final String TAG = WeiQrDao.class.getSimpleName();
	
	private DBHelper mDbHelper;
	private static WeiQrDao mInstance;

	private WeiQrDao(Context context){
		mDbHelper = DBHelper.getInstance();
		mDbHelper.getReadableDatabase();
	}
	
	public static WeiQrDao getInstance(){
		if (mInstance == null){
			synchronized (WeiQrDao.class) {
				mInstance = new WeiQrDao(WeApplication.getContext());
			}
		}
		return mInstance;
	}
	
	/**
	 * 判断二维码是否已经存在
	 * @return
	 */
	public boolean isQrExist(){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.query(Property.TABLE_QR, null, null, null, null, null, null);
		if (cursor != null ){
			if (cursor.getCount() > 0){
				cursor.close();
				return true;
			}
			cursor.close();
		}
		return false;
	}
	
	/**
	 * 添加二维码
	 * @param qrInfo
	 * @return
	 */
	public boolean addQr(QrInfo qrInfo){
		if (qrInfo == null){
			Log.i(TAG, "qrInfo is NULL!!");
			return false;
		}
		if (isQrExist()){
			Log.i(TAG, "qrInfo is exist!!");
			return false;
		}
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_QR_URL, qrInfo.getUrl());
		values.put(Property.COLUMN_UUID, qrInfo.getUuid());
		if (db.insert(Property.TABLE_QR, null, values ) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 删除二维码
	 * @return
	 */
	public boolean deleteQr(){
		if (!isQrExist()){
			Log.e(TAG, "No Qr Exist!!");
		}
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		if (db.delete(Property.TABLE_QR, null, null) > 0 ){
			return true;
		}
		return false;
	}
	
	/**
	 * 查询二维码
	 * @return
	 */
	public QrInfo getQr(){
		if (!isQrExist()){
			return null;
		}
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		Cursor cursor = db.query(Property.TABLE_QR, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			QrInfo qrInfo = new QrInfo(cursor.getString(cursor.getColumnIndex(Property.COLUMN_QR_URL)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_UUID)));
			cursor.close();
			return qrInfo;
		}
		return null;
	}
	
	/**
	 * 获取二维码Url
	 * @return
	 */
	public String getQrUrl(){
		QrInfo qrInfo = getQr();
		if (qrInfo != null){
			return qrInfo.getUrl();
		}
		return null;
	}
	
	/**
	 * 更新二维码
	 * @param qrInfo
	 * @return
	 */
	public boolean updateQr(QrInfo qrInfo){
		if (qrInfo == null){
			return false;
		}
		deleteQr();
		return addQr(qrInfo);
	}
	
	/**
	 * 获取Url
	 * @return
	 */
	public String getUrl(){
		QrInfo qrInfo = getQr();
		if (qrInfo != null){
			return qrInfo.getUrl();
		}
		return null;
	}
	
	/**
	 * 获取uuid
	 * @return
	 */
	public String getUUID(){
		QrInfo qrInfo = getQr();
		if (qrInfo != null){
			return qrInfo.getUuid();
		}
		return null;
	}
	
	/**
	 * 跟新Url
	 * @param url
	 * @return
	 */
	public boolean updateUrl(String url){
		if (TextUtils.isEmpty(url)){
			return false;
		}
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_QR_URL, url);
		if (db.update(Property.TABLE_QR, values, null, null) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 更新uuid
	 * @param uuid
	 * @return
	 */
	public boolean updateUuid(String uuid){
		Log.i(TAG, "updateUuid:" + uuid);
		if (TextUtils.isEmpty(uuid)){
			return false;
		}
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_UUID, uuid);
		if (db.update(Property.TABLE_QR, values, null, null) > 0){
			return true;
		}
		return false;
	}
}
