package com.tcl.wechat.database;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.WeApplication;
import com.tcl.wechat.model.BindUser;

/**
 * 用户信息表
 * @author rex.lei
 *
 */
public class WeiUserDao {
	
	private static final String TAG = WeiUserDao.class.getSimpleName();
	
	private DBHelper mDbHelper;
	
	private static WeiUserDao mInstance;
	
	private WeiUserDao(Context context) {
		super();
		mDbHelper = DBHelper.getInstance();
		mDbHelper.getReadableDatabase();
	}

	public static WeiUserDao getInstance(){
		if (mInstance == null){
			synchronized (WeiUserDao.class) {
				mInstance = new WeiUserDao(WeApplication.getContext());
			}
		}
		return mInstance;
	}

	/**
	 * 判断用户是否存在
	 * @param openid
	 * @return
	 */
	public boolean bindUserIsExist(String openid){
		if (TextUtils.isEmpty(openid)){
			OpenidException();
			return false;
		}
		BindUser user = getUser(openid);
		if (user == null){
			return false ;
		}
		return true;
	}
	
	/**
	 * 判断用户是否已经绑定
	 * @return
	 */
	public boolean userIsBound(BindUser bindUser){
		if ("success".equals(bindUser.getStatus())){
			return true;
		} 
		return false;
	}
	
	/**
	 * 获取绑定用户数
	 * @return
	 */
	public int getBindUserCnt(){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] columns = new String[]{Property.COLUMN_OPENID};
		Cursor cursor = db.query(Property.TABLE_USER, columns, null, null, null, null, null);
		if (cursor != null){
			return cursor.getColumnCount();
		}
		
		return 0;
	}
	
	/**
	 * 获取用户在线状态
	 * @param openId
	 * @return
	 */
	public String getUserStatus(String openId){
		String status = "false";
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] columns = new String[]{Property.COLUMN_STATUS};
		String selection = Property.COLUMN_OPENID + "=?";
		String[] selectionArgs = new String[]{openId};
		Cursor cursor = db.query(Property.TABLE_USER, columns, selection, selectionArgs, null, null, null);
		if (cursor != null){
			status = cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS));
		}
		cursor.close();
		return status == null ? "false" : "true";
	}
	
	/**
	 * 添加单个用户
	 * @param user 用户信息
	 * @return true: success
	 * 		   false: failed
	 * @exception 以下情况时添加用户失败
	 * 			  1)用户数据为空
	 * 			  2)用户数据库已经存在
	 * 			  3)用户未绑定
	 */
	public boolean addUser(BindUser bindUser){	
		if (bindUser == null){
			return false;
		}
		Log.i(TAG, "bindUser:" + bindUser.toString());
		//如果当前已经存在,则删除重新插入
		if (bindUserIsExist(bindUser.getOpenId())){
			Log.d(TAG, "Update bindUser:" + bindUser);
			return updateUser(bindUser);
		}
		
//		//用户未绑定，不能添加数据可
//		if (!userIsBound(bindUser)){
//			Log.e(TAG, "User is not bound!!");
////			return false;
//		}
		
		//插入新用户
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_OPENID, bindUser.getOpenId());
		values.put(Property.COLUMN_NICKNAME, bindUser.getNickName());
		values.put(Property.COLUMN_REMARKNAME, bindUser.getRemarkName());
		values.put(Property.COLUMN_USERSEX, bindUser.getSex());
		values.put(Property.COLUMN_HEADIMAGE_URL, bindUser.getHeadImageUrl());
		values.put(Property.COLUMN_STATUS, bindUser.getStatus());
		if (db.insert(Property.TABLE_USER, null, values) != -1){
			return true;
		}
		return false;
	}
	
	/**
	 * 批量添加数据
	 * @param userList
	 * @return
	 * @ 
	 * Notice:批量添加数据，会清空原有的所有数据
	 */
	public boolean addUserList(ArrayList<BindUser> userList){
		if (userList == null || userList.isEmpty()){
			Log.i(TAG, "userList is NULL!!");
			return false ;
		}
		boolean bRet = true;
		int addedUserCnt = 0;
		//插入新用户
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		//开始事务
		db.beginTransaction();
		try {
			for (int i = 0; i < userList.size(); i++) {
				BindUser bindUser = userList.get(i);
				
				//异常则不进行更新
				if (TextUtils.isEmpty(bindUser.getOpenId()) || 
						TextUtils.isEmpty(bindUser.getNickName()) 
						/*TextUtils.isEmpty(bindUser.getHeadImageUrl())*/){
					continue;
				}
				
				//如果当前已经存在则直接更新即可
				if (bindUserIsExist(bindUser.getOpenId())){
					Log.e(TAG, "User already exist! openId = " + bindUser.getOpenId());
					
					if (updateUser(bindUser)){
						addedUserCnt ++;
						bRet &= true;
					} else {
						bRet &= false;
					}
					continue;
				}
				//用户不存在，第一次添加状态为false
				bindUser.setStatus("false");

				ContentValues values = new ContentValues();
				values.put(Property.COLUMN_OPENID, bindUser.getOpenId());
				values.put(Property.COLUMN_NICKNAME, bindUser.getNickName());
				values.put(Property.COLUMN_REMARKNAME, bindUser.getRemarkName());
				values.put(Property.COLUMN_USERSEX, bindUser.getSex());
				values.put(Property.COLUMN_HEADIMAGE_URL, bindUser.getHeadImageUrl());
				values.put(Property.COLUMN_STATUS, bindUser.getStatus());
				if (db.insert(Property.TABLE_USER, null, values) != -1){
					addedUserCnt ++;
					bRet &= true;
				} else {
					bRet &= false;
				}
			}
			db.setTransactionSuccessful();
			Log.i(TAG, "success:" + addedUserCnt + ",failure:" + (userList.size() - addedUserCnt));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null){
				db.endTransaction();
			}
		}
		return bRet;
	}
	
	/**
	 * 根据openid获取用户信息
	 * @param openId 
	 * @return 用户信息
	 */
	public BindUser getUser(String openid){
		if (TextUtils.isEmpty(openid)){
			OpenidException();
			return null;
		}
		
		BindUser user = null;
		try {
			SQLiteDatabase db = mDbHelper.getReadableDatabase();
			String selection = Property.COLUMN_OPENID + "=?";
			String[] selectionArgs = new String[]{openid};
			String orderBy = Property.COLUMN_OPENID;
			Cursor cursor = db.query(Property.TABLE_USER, null, selection, selectionArgs , null, null, orderBy );
			if (cursor != null && cursor.moveToFirst()){
				user = new BindUser(openid,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_NICKNAME)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_REMARKNAME)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_USERSEX)),
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_HEADIMAGE_URL)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS)));
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return user;
	}
	
	/**
	 * 获取系统用户信息
	 * @return
	 */
	public BindUser getSystemUser(){
		BindUser user = null;
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String selection = Property.COLUMN_USERSEX + "=?";
		String[] selectionArgs = new String[]{"-1"};
		Cursor cursor = db.query(Property.TABLE_USER, null, selection, selectionArgs, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			user = new BindUser(cursor.getString(cursor.getColumnIndex(Property.COLUMN_OPENID)),
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_NICKNAME)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_REMARKNAME)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_USERSEX)),
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_HEADIMAGE_URL)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS)));
			cursor.close(); 
		}
		return user;
	}
	
	public BindUser getLastBindUser(){
		BindUser user = null;
		BindUser systemUser = getSystemUser();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String selection = Property.COLUMN_OPENID + "!= ?";
		String[] selectionArgs = new String[]{systemUser.getOpenId()};
		String limit = " 1 ";
		Cursor cursor = db.query(Property.TABLE_USER, null, selection, selectionArgs, null, null, null, limit );
		if (cursor != null && cursor.moveToFirst()){
			user = new BindUser(cursor.getString(cursor.getColumnIndex(Property.COLUMN_OPENID)),
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_NICKNAME)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_REMARKNAME)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_USERSEX)),
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_HEADIMAGE_URL)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS)));
			cursor.close(); 
		}
		return user;
	}
	
	/**
	 * 获取所用用户信息
	 * @return 所用用户列表
	 */
	public LinkedList<BindUser> getAllUsers(){
		LinkedList<BindUser> userList = new LinkedList<BindUser>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		db.beginTransaction();
		try {
			Cursor cursor = db.query(Property.TABLE_USER, null, null, null, null, null, null);
			if (cursor != null){
				while (cursor.moveToNext()) {
					BindUser user = new BindUser(cursor.getString(cursor.getColumnIndex(Property.COLUMN_OPENID)),
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_NICKNAME)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_REMARKNAME)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_USERSEX)),
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_HEADIMAGE_URL)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS)));
					userList.add(user);
				}
				db.setTransactionSuccessful();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null){
				db.endTransaction();
			}
		}
		return userList;
	}
	
	/**
	 * 获取所用绑定用户
	 * @return 所用用户列表
	 */
	public LinkedList<BindUser> getBindUsers(){
		LinkedList<BindUser> userList = new LinkedList<BindUser>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		/*BindUser systemUser = getSystemUser();
		if (systemUser == null){
			return userList;
		}*/
		db.beginTransaction();
		try {
			String selection = Property.COLUMN_USERSEX + "!=?";
			String[] selectionArgs = new String[]{"-1"};
			String orderBy = Property.COLUMN_ID + " DESC ";
			Cursor cursor = db.query(Property.TABLE_USER, null, selection, selectionArgs , null, null, orderBy);
			if (cursor != null){
				while (cursor.moveToNext()) {
					BindUser user = new BindUser(cursor.getString(cursor.getColumnIndex(Property.COLUMN_OPENID)),
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_NICKNAME)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_REMARKNAME)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_USERSEX)),
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_HEADIMAGE_URL)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS)));
					userList.add(user);
				}
				db.setTransactionSuccessful();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null){
				db.endTransaction();
			}
		}
		return userList;
	}
	
	/**
	 * 获取备注名称
	 * @param openid
	 * @return
	 */
	public String getRemarkName(String openid){
		String remarkName = null;
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
 		String selection = Property.COLUMN_OPENID + "=?";
		String[] selectionArgs = new String[]{openid};
		String[] columns = new String[]{Property.COLUMN_REMARKNAME};
		Cursor cursor = db.query(Property.TABLE_USER, columns, selection, selectionArgs, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			remarkName = cursor.getString(cursor.getColumnIndex(Property.COLUMN_REMARKNAME));
			cursor.close();
		}
		return remarkName;
	}
	
	/**
	 * 删除用户
	 * @param openId
	 * @return
	 */
	public boolean deleteUser(String openId){
		Log.d(TAG, "deleteUser openId:" + openId);
		if (!bindUserIsExist(openId)){
			return false ;
		}
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		String whereClause = Property.COLUMN_OPENID + "=?";
		String[] whereArgs = new String[]{openId};
		if (db.delete(Property.TABLE_USER, whereClause, whereArgs) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 删除所有用户
	 * @return
	 */
	public boolean deleteAllUser(){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		if (db.delete(Property.TABLE_USER, null, null) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 更新用户信息
	 * @param user
	 * @return
	 */
	public boolean updateUser(BindUser bindUser){
		if (bindUser == null || !bindUserIsExist(bindUser.getOpenId())){
			return false;
		}

		bindUser.setRemarkName(bindUser.getNickName());
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_OPENID, bindUser.getOpenId());
		values.put(Property.COLUMN_NICKNAME, bindUser.getNickName());
		//values.put(Property.COLUMN_REMARKNAME, bindUser.getRemarkName()); 不更新备注名称
		values.put(Property.COLUMN_USERSEX, bindUser.getSex());
		values.put(Property.COLUMN_HEADIMAGE_URL, bindUser.getHeadImageUrl());
		String whereClause = Property.COLUMN_OPENID + "=?";
		String[] whereArgs = new String[]{bindUser.getOpenId()};
		if (db.update(Property.TABLE_USER, values, whereClause, whereArgs) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 更新用户备注名称
	 * @param openid
	 * @param remarkName
	 * @return
	 */
	public boolean updateRemarkName(String openid, String remarkName){
		if (remarkName == null || !bindUserIsExist(openid)){
			return false;
		}
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_REMARKNAME, remarkName);
		String whereClause = Property.COLUMN_OPENID + "=?";
		String[] whereArgs = new String[]{openid};
		if (db.update(Property.TABLE_USER, values, whereClause, whereArgs) > 0){
			return true;
		}
		return false;
	}

	/**
	 * 获取状态
	 * @param openid
	 * @return
	 */
	public String getStatus(String openid){
		if (TextUtils.isEmpty(openid)){
			OpenidException();
			return null;
		}
	
		BindUser user = getUser(openid);
		if (user == null){
			BinderUserNotFoundException();
			return null;
		}
		return user.getStatus();
	}
	
	/**
	 * 更新状态
	 * @param openid
	 * @return
	 */
	public boolean updateStatus(String openid, String status){
		
		//openid 是否合法
		if (TextUtils.isEmpty(openid)){
			OpenidException();
			return false;
		}
	
		//Binduser是否存在
		BindUser user = getUser(openid);
		if (user == null){
			BinderUserNotFoundException();
			return false;
		}
		
		//更新状态
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_STATUS, status);
		String whereClause = Property.COLUMN_OPENID + "=?";
		String[] whereArgs = new String[]{openid};
		if (db.update(Property.TABLE_USER, values, whereClause, whereArgs) > 0 ){
			return true;
		}
		return false;
	}
	
	/**
	 * openid错误异常
	 */
	private void OpenidException(){
		try {
			throw new Exception("openid is NULL");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 用户不存在异常
	 */
	public void BinderUserNotFoundException(){
		try {
			throw new Exception("Users do not exist!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
