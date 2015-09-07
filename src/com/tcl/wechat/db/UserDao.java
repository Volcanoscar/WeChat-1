package com.tcl.wechat.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.db.interf.UserImpl;
import com.tcl.wechat.modle.User;

public class UserDao implements UserImpl{
	
	private static final String TAG = "UserDao";
	
	private static UserDao mInstance ;
	private static DBHelper dbHelper;
	private SQLiteDatabase db;
	
	private UserDao() {
		super();
	}

	public static UserDao getInstance(Context context){
		if (mInstance == null){
			synchronized (UserDao.class) {
				mInstance = new UserDao();
				dbHelper = new DBHelper(context);
			}
		}
		return mInstance;
	}
	
	@Override
	public int getUserCount() {
		int count = 0;
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(Property.TABLE_USER, null, null, null, null, null, null);
		if (cursor != null){
			count = cursor.getCount();
			cursor.close();
		}
		return count;
	}

	public ArrayList<User> getAllUsers(){
		ArrayList<User> users = new ArrayList<User>();
		
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(Property.TABLE_USER, null, null, null, null, null, null);
		if (cursor != null){
			while (cursor.moveToNext()) {
				User user = new User(cursor.getString(cursor.getColumnIndex(Property.COLUMN_OPENID)),
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_USERNAME)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_NICKNAME)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_REMARKNAME)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_USERSEX)),
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_HEADIMAGE_URL)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_SIGNATURE_INFO)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_NEWS_NUM)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS)));
				users.add(user);
			}
			cursor.close();
		}
		return users;
	}

	@Override
	public boolean addUser(User user) {
		if (user == null){
			return false;
		}
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_OPENID, user.getOpenId());
		values.put(Property.COLUMN_USERNAME, user.getUserName());
		values.put(Property.COLUMN_NICKNAME, user.getNickName());
		values.put(Property.COLUMN_REMARKNAME, user.getRemarkName());
		values.put(Property.COLUMN_USERSEX, user.getSex());
		values.put(Property.COLUMN_HEADIMAGE_URL, user.getHeadimageurl());
		values.put(Property.COLUMN_SIGNATURE_INFO, user.getSignature());
		values.put(Property.COLUMN_NEWS_NUM, user.getNewsNum());
		values.put(Property.COLUMN_STATUS, user.getStatus());
		if (db.insert(Property.TABLE_USER, null, values) != -1){
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteUser(String userName) {
		if (TextUtils.isEmpty(userName)){
			Log.e(TAG, "delete User is Exist or UserName is NULL!!");
			return false;
		}
		db = dbHelper.getWritableDatabase();
		
		String whereClause = Property.COLUMN_USERNAME + "=?";
		String[] whereArgs = {userName};
		if (db.delete(Property.TABLE_USER, whereClause, whereArgs) > 0){
			return true;
		}
		return false;
	}

	@Override
	public boolean updateNickName(String userName, String nickkName) {
		if (TextUtils.isEmpty(userName)){
			Log.e(TAG, "delete User is Exist or UserName is NULL!!");
			return false;
		}
		db = dbHelper.getWritableDatabase();
		String whereClause = Property.COLUMN_USERNAME + "=?"  ;
		String[] whereArgs = {userName};
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_NICKNAME, nickkName);
		if (db.update(Property.TABLE_USER, values, whereClause, whereArgs) > 0 ){
			return true;
		}
		return false;
	}

	@Override
	public boolean updateRemakName(String userName, String reMarkickName) {
		if (TextUtils.isEmpty(userName)){
			Log.e(TAG, "delete User is Exist or UserName is NULL!!");
			return false;
		}
		db = dbHelper.getWritableDatabase();
		String whereClause = Property.COLUMN_USERNAME + "=?"  ;
		String[] whereArgs = {userName};
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_REMARKNAME, reMarkickName);
		if (db.update(Property.TABLE_USER, values, whereClause, whereArgs) > 0 ){
			return true;
		}
		return false;
	}

	@Override
	public boolean updateSignatureInfo(String userName, String signatureInfo) {
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(userName)){
			Log.e(TAG, "delete User is Exist or UserName is NULL!!");
			return false;
		}
		db = dbHelper.getWritableDatabase();
		String whereClause = Property.COLUMN_USERNAME + "=?"  ;
		String[] whereArgs = {userName};
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_REMARKNAME, signatureInfo);
		if (db.update(Property.TABLE_USER, values, whereClause, whereArgs) > 0 ){
			return true;
		}
		return false;
	}

	@Override
	public boolean updateHeadimage(String userName, String headimageurl) {
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(userName)){
			Log.e(TAG, "delete User is Exist or UserName is NULL!!");
			return false;
		}
		db = dbHelper.getWritableDatabase();
		String whereClause = Property.COLUMN_USERNAME + "=?"  ;
		String[] whereArgs = {userName};
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_REMARKNAME, headimageurl);
		if (db.update(Property.TABLE_USER, values, whereClause, whereArgs) > 0 ){
			return true;
		}
		return false;
	}

}
