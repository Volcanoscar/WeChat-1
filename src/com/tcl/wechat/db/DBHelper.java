package com.tcl.wechat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库帮助类
 * @author rex.lei
 *
 */
public class DBHelper extends SQLiteOpenHelper {

	private static final int VERSION = 1;
	
	public DBHelper(Context context) {
		super(context, Property.DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Property.CREATE_TABLE_USER_SQL);
		db.execSQL(Property.CREATE_TABLE_USERRECORD_SQL);
		db.execSQL(Property.CREATE_TABLE_APPINFO_SQL);
		db.execSQL(Property.CREATE_TABLE_DEVICE_SQL);
		db.execSQL(Property.CREATE_TABLE_QR_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL( "DROP TABLE IF EXISTS " +  Property.TABLE_USER);
		db.execSQL( "DROP TABLE IF EXISTS " +  Property.TABLE_USERRECORD);
		db.execSQL( "DROP TABLE IF EXISTS " +  Property.TABLE_APPINFO);
		db.execSQL( "DROP TABLE IF EXISTS " +  Property.TABLE_DEVICE);
		db.execSQL( "DROP TABLE IF EXISTS " +  Property.TABLE_QR);
	}

}
