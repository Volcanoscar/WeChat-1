/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.db;


 

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @ClassName: DBOpenHelper
 * @Description: weixin数据库
 */

public class DBOpenHelper extends SQLiteOpenHelper{
	
	private static final String DATABASENAME = "tcl_weixin.db"; // 数据库名
	private static final int DATABASEVERSION = 05;// 数据库版本
	private String tag = "DBOpenHelper";

	/** */
	Context mContext;

	public DBOpenHelper(Context context)
	{
		super(context, DATABASENAME, null, DATABASEVERSION);
		mContext = context;
	}

	private static SQLiteDatabase writeabledb;
	private static SQLiteDatabase readabledb;

	@Override
	public synchronized SQLiteDatabase getWritableDatabase()
	{
		if (writeabledb != null)
		{
			return writeabledb;
		}
		else
		{
			writeabledb = super.getWritableDatabase();
			return writeabledb;
		}
	}

	@Override
	public synchronized SQLiteDatabase getReadableDatabase()
	{
		return getWritableDatabase();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		try
		{			
			db.execSQL("CREATE TABLE IF NOT EXISTS tcl_weixin_device (id integer primary key autoincrement, memberid varchar(100))");
			db.execSQL("CREATE TABLE IF NOT EXISTS weibinderuser (id integer primary key autoincrement, openid varchar(100), nickname varchar(100), sex varchar(50),headimgurl varchar(500),newsnum varchar(100),status varchar(50))");
			db.execSQL("CREATE TABLE IF NOT EXISTS weiuserrecord (id integer primary key autoincrement, openid varchar(100), msgtype varchar(50), content varchar(100),url varchar(500),format varchar(50),createtime varchar(50),accesstoken varchar(50),mediaid varchar(100),thumbmediaid varchar(500),expiretime varchar(50),read varchar(20),filename varchar(200),filesize varchar(50),filetime varchar(50))");
			db.execSQL("CREATE TABLE IF NOT EXISTS qrinfo (id integer primary key autoincrement, url varchar(500),uuid varchar(100))");
			
			db.execSQL("insert into qrinfo(url,uuid) values('','');");
			db.execSQL("insert into tcl_weixin_device(memberid) values('');");		
			db.execSQL("CREATE TABLE IF NOT EXISTS appclass(id integer primary key autoincrement,packagename varchar(100),appname varchar(100));");
			/** insert system app info */
			//insertSysAppInfo();
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		/*db.execSQL("DROP TABLE IF EXISTS tcl_weixin_device");
		db.execSQL("DROP TABLE IF EXISTS weibinderuser");
		db.execSQL("DROP TABLE IF EXISTS weiuserrecord");
		db.execSQL("DROP TABLE IF EXISTS appclass");*/
		db.execSQL("DROP TABLE IF EXISTS qrinfo");
		onCreate(db);
	}
	
	
	
	
}
