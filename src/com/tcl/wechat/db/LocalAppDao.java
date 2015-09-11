package com.tcl.wechat.db;



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.tcl.wechat.modle.AppInfo;



public class LocalAppDao
{
	private DBOpenHelper openHelper;

	public LocalAppDao(Context context)
	{
		openHelper = new DBOpenHelper(context);
	}

	public boolean saveAppInfo(AppInfo appInfo)
	{
		// 如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库
		try {
			SQLiteDatabase db = openHelper.getWritableDatabase();	

			db.execSQL("insert into appclass (packagename,appname) values(?,?)",
						new Object[] { appInfo.getPackageName(), appInfo.getappname()});
		} catch (SQLiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
			
	
		
		return true;
	}
	//返回应用名称
	public String findAppname(String packagename) 
	{
		// 如果只对数据进行读取，建议使用此方法
		String appname = null;
		try {
			SQLiteDatabase db = openHelper.getReadableDatabase();
			
			Cursor cursor = db.rawQuery("select * from appclass where packagename=?",new String[] { packagename });
			cursor.moveToLast();
			//Log.i(tag,"cursor.getCount()"+cursor.getCount());
			if (cursor.getCount() == 0 ){
				return null;
			}			
			appname = cursor.getString(cursor.getColumnIndex("appname"));							
			
			cursor.close();
		} catch (SQLiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return appname;
	}
	public int getsize() 
	{
		int i = 0;

		// 如果只对数据进行读取，建议使用此方法
		try {
			SQLiteDatabase db = openHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("select * from appclass",null);
			i = cursor.getCount();
			
			cursor.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("liyulin", "dblocalapp_size="+i);
		return i;
	}
}
