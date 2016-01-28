/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tcl.wechat.WeApplication;

/**
 * 微信数据库帮助类
 * @author rex.lei
 *
 */
public class DBHelper extends SQLiteOpenHelper{
	
	private static final String TAG = "DBHelper";
	
	private static final int VERSION = 2;
	
	private static class DBHelperInstance{
		private static final DBHelper mInstance = new DBHelper(WeApplication.getContext());
	}

	public DBHelper(Context context) {
		super(context, Property.DATABASE_NAME, null, VERSION);
	}
	
	public static DBHelper getInstance(){
		return DBHelperInstance.mInstance;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		try {
			db.execSQL(Property.CREATE_TABLE_USER_SQL);
			db.execSQL(Property.CREATE_TABLE_USERRECORD_SQL);
			db.execSQL(Property.CREATE_TABLE_APPINFO_SQL);
			db.execSQL(Property.CREATE_TABLE_DEVICE_SQL);
			db.execSQL(Property.CREATE_TABLE_QR_SQL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		// db.execSQL( "DROP TABLE IF EXISTS " + Property.TABLE_USER);
		// db.execSQL( "DROP TABLE IF EXISTS " + Property.TABLE_USERMSGRECORD);
		// db.execSQL( "DROP TABLE IF EXISTS " + Property.TABLE_APPINFO);
		// db.execSQL( "DROP TABLE IF EXISTS " + Property.TABLE_DEVICE);
		// db.execSQL( "DROP TABLE IF EXISTS " +  Property.TABLE_QR);
		
		Log.i(TAG, "oldVersion:" + oldVersion + ",newVersion:" + newVersion);
		
		//第一次升级，增加status字段
		if (oldVersion == 1 && newVersion == 2){
			Log.i(TAG, "start update database, newersion: " + newVersion);
			updateV1ToV2(db);
		}
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		super.onDowngrade(db, oldVersion, newVersion);
		
		//数据库降低
	}
	
	private void updateV1ToV2(SQLiteDatabase db) {

		boolean result = false;
		Log.i(TAG, "updateV1ToV2-->>");
		try {
			db.beginTransaction();
			// 1、查询是否存在字段
			// select * from sqlite_master where name = "messagedetail" and sql
			// like "%status%";
			String sql = "select * from sqlite_master where name = ?  and sql like ?";
			String[] selectionArgs = new String[] {
						Property.TABLE_USERMSGRECORD,
						"%" + Property.COLUMN_STATUS + "%" };
			Cursor cursor = db.rawQuery(sql, selectionArgs);
			result = (null != cursor && cursor.moveToFirst());
			Log.i(TAG, "result:" + result);
			if (result) { // 存在、返回
				return;
			}

			// 2、插入字段
			
			sql = "alter table " + Property.TABLE_USERMSGRECORD + " add "
					+ Property.COLUMN_STATUS + " vachar(32)";
			Log.i(TAG, "execSQL:" + sql);
			db.execSQL(sql);

			// 3、数据初始化
			sql = "update " + Property.TABLE_USERMSGRECORD + " set "
					+ Property.COLUMN_STATUS + "= \"false\"";
			Log.i(TAG, "execSQL:" + sql);
			db.execSQL(sql);

			db.setTransactionSuccessful();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.w(TAG, "Exception:" + e.getMessage());
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}
	
}
