package com.tcl.wechat.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tcl.wechat.db.interf.UserImpl;
import com.tcl.wechat.modle.User;

public class UserDao implements UserImpl{
	
	private static UserDao mInstance ;
	private static DBHelper dbHelper;
	private SQLiteDatabase db;
	
	private UserDao() {
		super();
	}

	public static UserDao getInstance(Context context){
		if (mInstance == null){
			mInstance = new UserDao();
			dbHelper = new DBHelper(context);
		}
		return mInstance;
	}
	
	public ArrayList<User> getAllUsers(){
		ArrayList<User> users = new ArrayList<User>();
		
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(Property.TABLE_USER, null, null, null, null, null, null);
		if (cursor != null){
			while (cursor.moveToNext()) {
				
			}
		}
		db.close();
		return users;
	}

	@Override
	public boolean addUser(User user) {
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		
		if (db.insert(Property.TABLE_USER, null, values) != -1){
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteUser(User user) {
		
		return false;
	}

	@Override
	public boolean updateUser(User user) {
		
		return false;
	}

	@Override
	public boolean updateRemakName(String userName, String reMaekickName) {
		String whereClause = "userName=?";
		String[] whereArgs = {reMaekickName};
		if ( db.update(Property.TABLE_USER, null, whereClause, whereArgs) > 0 ){
			return true;
		}
		return false;
	}
	
}
