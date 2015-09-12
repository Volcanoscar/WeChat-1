package com.tcl.wechat.test;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.db.DBHelper;
import com.tcl.wechat.db.Property;
import com.tcl.wechat.modle.UserRecord;
import com.tcl.wechat.test.interf.RecordImpl;

/**
 * 用户消息记录类
 * @author rex.lei
 *
 */
public class RecordDao implements RecordImpl{

	private static final String TAG = "RecordDao";
	
	private static RecordDao mInstance ;
	private static DBHelper dbHelper;
	private SQLiteDatabase db;
	
	private RecordDao() {
		super();
	}
	
	public static RecordDao getInstance(Context context){
		if (mInstance == null){
			synchronized (RecordDao.class) {
				mInstance = new RecordDao();
				dbHelper = new DBHelper(context);
			}
		}
		return mInstance;
	}

	@Override
	public int getRecordCount(String openId) {
		int count = 0;
		db = dbHelper.getReadableDatabase();
		String selection = Property.COLUMN_OPENID + "=?";
		String[] selectionArgs = new String[]{openId};
		Cursor cursor = db.query(Property.TABLE_USERRECORD, null, selection, selectionArgs, null, null, null);
		if (cursor != null){
			count = cursor.getCount();
			cursor.close();
		}
		return count;
	}

	@Override
	public boolean addRecord(UserRecord record) {
		db = dbHelper.getWritableDatabase();
		if (record == null || TextUtils.isEmpty(record.getOpenId())){
			Log.e(TAG, "The Record is NULL or openId is NULL!!");
			return false ;
		}
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_OPENID, record.getOpenId());
		values.put(Property.COLUMN_ACCESS_TOKENS, record.getAccessToken());
		values.put(Property.COLUMN_MSGTYPE, record.getMsgType());
		values.put(Property.COLUMN_CONTENT, record.getContent());
		values.put(Property.COLUMN_IMAGE_URL, record.getImageurl());
		values.put(Property.COLUMN_FORMAT, record.getFormat());
		values.put(Property.COLUMN_MDDIAID, record.getMediaId());
		values.put(Property.COLUMN_HUMBMEDIAID, record.getHumbmediaId());
		values.put(Property.COLUMN_CREATE_TIME, record.getCreateTime());
		values.put(Property.COLUMN_EXPIRETIME, record.getExpireTime());
		values.put(Property.COLUMN_READED, record.getReaded());
		values.put(Property.COLUMN_FILENAME, record.getFileName());
		values.put(Property.COLUMN_FILESIZE, record.getFileSize());
		values.put(Property.COLUMN_FILETIME, record.getFileTime());
		if (db.insert(Property.TABLE_USERRECORD, "", values) > 0){
			return true;
		}
		return false;
	}

	@Override
	public ArrayList<UserRecord> getAllUserRecord(String openId) {
		if (TextUtils.isEmpty(openId)){
			return null;
		}
		ArrayList<UserRecord> records = new ArrayList<UserRecord>();
		db = dbHelper.getReadableDatabase();
		String selection = Property.COLUMN_OPENID + "=?";
		String[] selectionArgs = new String[]{openId};
		Cursor cursor = db.query(Property.TABLE_USERRECORD, null, selection, selectionArgs, null, null, null);
		if (cursor != null){
			while (cursor.moveToNext()) {
				UserRecord record = new UserRecord(
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_OPENID)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_ACCESS_TOKENS)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGTYPE)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_CONTENT)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_IMAGE_URL)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_FORMAT)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_MDDIAID)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_HUMBMEDIAID)), 
						cursor.getLong(cursor.getColumnIndex(Property.COLUMN_CREATE_TIME)), 
						cursor.getLong(cursor.getColumnIndex(Property.COLUMN_EXPIRETIME)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_READED)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILENAME)), 
						cursor.getInt(cursor.getColumnIndex(Property.COLUMN_FILESIZE)), 
						cursor.getLong(cursor.getColumnIndex(Property.COLUMN_FILETIME)));
				records.add(record);
			}
			cursor.close();
		}
		db.close();
		return records;
	}

	@Override
	public boolean deleteRecord(String openId) {
		if (TextUtils.isEmpty(openId)){
			Log.e(TAG, "delete User is Exist or openId is NULL!!");
			return false;
		}
		db = dbHelper.getWritableDatabase();
		
		String whereClause = Property.COLUMN_OPENID + "=?";
		String[] whereArgs = {openId};
		if (db.delete(Property.TABLE_USERRECORD, whereClause, whereArgs) > 0){
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteRecord(String openId, long createTime) {
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(openId)){
			Log.e(TAG, "delete User is Exist or openId is NULL!!");
			return false;
		}
		db = dbHelper.getWritableDatabase();
		String whereClause = Property.COLUMN_OPENID + "=? and " 
		+ Property.COLUMN_CREATE_TIME + " >?";
		String[] whereArgs = {openId, "" + createTime};
		if (db.delete(Property.TABLE_USERRECORD, whereClause, whereArgs) > 0) {
			return true;
		}
		return false;
	}
	
	
}
