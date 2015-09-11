/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @ClassName: WeiQrDao
 * @Description: 该表用于存储二维码信�?
 */

public class WeiQrDao {
	private DBOpenHelper dbOpenHelper;

	public WeiQrDao(Context context)
	{
		this.dbOpenHelper = new DBOpenHelper(context);
	}
	
	public boolean update(String url) 
	{
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("update qrinfo set url=? ",new Object[] { url});
		return true;
	}
	
	public boolean update_uuid(String uuid) 
	{
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("update qrinfo set uuid=? ",new Object[] { uuid});
		return true;
	}
	
	public String find() 
	{
		String url ;
		// 如果只对数据进行读取，建议使用此方法
		try {
			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(
					"select url from qrinfo",null);
			if (cursor.moveToFirst())
			{
				url = cursor.getString(cursor.getColumnIndex("url"));
				cursor.close();
				return url;
			}
			cursor.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	public String find_uuid() 
	{
		String uuid ;
		// 如果只对数据进行读取，建议使用此方法
		try {
			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(
					"select uuid from qrinfo",null);
			if (cursor.moveToFirst())
			{
				uuid = cursor.getString(cursor.getColumnIndex("uuid"));
				cursor.close();
				return uuid;
			}
			cursor.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
}
