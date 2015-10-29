package com.tcl.wechat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.tcl.wechat.model.AppInfo;

/**
 * AppInfo相关信息
 * @author rex.lei
 *
 */
public class AppInfoDao
{
	private DBHelper mDbHelper;
	private static AppInfoDao mInstance;

	private AppInfoDao(Context context){
		mDbHelper = new DBHelper(context);
		mDbHelper.getReadableDatabase();
	}
	
	public static void initAppDao(Context context){
		if (mInstance == null){
			mInstance = new AppInfoDao(context);
		}
	}
	
	public static AppInfoDao getInstance(){
		if (mInstance == null){
			throw new NullPointerException("AppDao is Null, You should initialize AppDao first!!");
		}
		return mInstance;
	}
	
	/**
	 * 获取App个数
	 * @return
	 */
	public int getAppCount(){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.query(Property.TABLE_APPINFO, null, null, null, null, null, null);
		if (cursor != null ){
			int count = cursor.getCount();
			cursor.close();
			return count;
		}
		return 0;
	}
	
	/**
	 * 添加AppInfo
	 * @param appinfo
	 * @return
	 */
	public boolean addAppInfo(AppInfo appinfo){
		if (appinfo == null){
			return false;
		}
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_APP_NAME, appinfo.getAppName());
		values.put(Property.COLUMN_PACKAGE_NAME, appinfo.getPackageName());
		values.put(Property.COLUMN_VERSION_CODE, appinfo.getVersionCode());
		values.put(Property.COLUMN_VERSION_NAME, appinfo.getVersionName());
		values.put(Property.COLUMN_MD5, appinfo.getMd5());
		if (db.insert(Property.TABLE_APPINFO, null, values ) > 0){
			return true;
		}
		return false;
	}
	
	public AppInfo getAppInfo(){
		AppInfo appInfo = null;
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.query(Property.TABLE_APPINFO, null, null, null, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			appInfo = new AppInfo(cursor.getString(cursor.getColumnIndex(Property.COLUMN_APP_NAME)),
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_PACKAGE_NAME)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_VERSION_CODE)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_VERSION_NAME)),
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_MD5)));
			cursor.close();
		}
		return appInfo;
	}
	
	/**
	 * 查询App名称
	 * @param packageName
	 * @return
	 */
	public String getAppName(String packageName){
		if (TextUtils.isEmpty(packageName)){
			return null;
		}
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String selection = Property.COLUMN_PACKAGE_NAME + "=?";
		String[] selectionArgs = new String[]{packageName};
		Cursor cursor = db.query(Property.TABLE_APPINFO, null, selection, selectionArgs, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			String appName = cursor.getString(cursor.getColumnIndex(Property.COLUMN_PACKAGE_NAME));
			cursor.close();
			return appName;
		}
		return null;
	}
}
