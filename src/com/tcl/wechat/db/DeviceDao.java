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
 * @ClassName: DeviceDao
 * @Description: 设备表
 */

public class DeviceDao {
	
	private DBOpenHelper dbOpenHelper;

	public DeviceDao(Context context)
	{
		this.dbOpenHelper = new DBOpenHelper(context);
	}
	
	public boolean update(String memberid) 
	{
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("update tcl_weixin_device set memberid=? ",new Object[] { memberid});
		return true;
	}
	
	public String find() 
	{
		String memberid ;
		// 如果只对数据进行读取，建议使用此方法
		try {
			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(
					"select memberid from tcl_weixin_device",null);
			if (cursor.moveToFirst())
			{
				memberid = cursor.getString(cursor.getColumnIndex("memberid"));
				cursor.close();
				return memberid;
			}
			cursor.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
}
