/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.modle.BindUser;

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
		mDbHelper = new DBHelper(context);
		mDbHelper.getReadableDatabase();
	}

	public static void initWeiUserDao(Context context){
		if (mInstance == null){
			mInstance = new WeiUserDao(context);
		}
	}
	
	public static WeiUserDao getInstance(){
		if (mInstance == null){
			throw new NullPointerException("WeiUserDao is Null, You should initialize WeiUserDao first");
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
			BinderUserNotFoundException();
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
	public int getBindUserNum(){
		return getAllUsers() == null ? 0 :
			getAllUsers().size();
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
		//如果当前已经存在该绑定的用户则不再插入数据库
		if (bindUserIsExist(bindUser.getOpenId())){
			Log.e(TAG, "User already exist!");
			return false;
		}
		
		//用户未绑定，不能添加数据可
		if (!userIsBound(bindUser)){
			Log.e(TAG, "User is not bound!!");
//			return false;
		}
		
		//插入新用户
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_OPENID, bindUser.getOpenId());
		values.put(Property.COLUMN_NICKNAME, bindUser.getNickName());
		values.put(Property.COLUMN_REMARKNAME, bindUser.getRemarkName());
		values.put(Property.COLUMN_USERSEX, bindUser.getSex());
		values.put(Property.COLUMN_HEADIMAGE_URL, bindUser.getHeadImageUrl());
		values.put(Property.COLUMN_NEWS_NUM, bindUser.getNewsNum());
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
	public Boolean addUserList(ArrayList<BindUser> userList){
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
		for (int i = 0; i < userList.size(); i++) {
			BindUser bindUser = userList.get(i);
			
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
			
			//用户未绑定，不能添加数据可
			if (!userIsBound(bindUser)){
				Log.e(TAG, "User is not bound!! openId = " + bindUser.getOpenId());
//				continue;
			}
			
			ContentValues values = new ContentValues();
			values.put(Property.COLUMN_OPENID, bindUser.getOpenId());
			values.put(Property.COLUMN_NICKNAME, bindUser.getNickName());
			values.put(Property.COLUMN_REMARKNAME, bindUser.getRemarkName());
			values.put(Property.COLUMN_USERSEX, bindUser.getSex());
			values.put(Property.COLUMN_HEADIMAGE_URL, bindUser.getHeadImageUrl());
			values.put(Property.COLUMN_NEWS_NUM, bindUser.getNewsNum());
			values.put(Property.COLUMN_STATUS, bindUser.getStatus());
			values.put(Property.COLUMN_REPLY, bindUser.getReply());
			if (db.insert(Property.TABLE_USER, null, values) != -1){
				addedUserCnt ++;
				bRet &= true;
			} else {
				bRet &= false;
			}
		}
		Log.i(TAG, "success:" + addedUserCnt + ",failure:" + (userList.size() - addedUserCnt));
		
		//提交事务
		db.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
		db.endTransaction(); 		   // 处理完成
		db.close();
		
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
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_NEWS_NUM)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS)),
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_REPLY)));
			cursor.close();
		}
		return user;
	}
	
	
	/**
	 * 获取所用用户信息
	 * @return 所用用户列表
	 */
	public ArrayList<BindUser> getAllUsers(){
		ArrayList<BindUser> userList = new ArrayList<BindUser>();
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.query(Property.TABLE_USER, null, null, null, null, null, null);
		if (cursor != null){
			while (cursor.moveToNext()) {
				BindUser user = new BindUser(cursor.getString(cursor.getColumnIndex(Property.COLUMN_OPENID)),
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_NICKNAME)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_REMARKNAME)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_USERSEX)),
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_HEADIMAGE_URL)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_NEWS_NUM)), 
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS)),
						cursor.getString(cursor.getColumnIndex(Property.COLUMN_REPLY)));
				userList.add(user);
			}
			cursor.close();
		}
		return userList;
	}
	
	/**
	 * 删除用户
	 * @param openId
	 * @return
	 */
	public boolean deleteUser(String openId){
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
	 * 删除用户
	 * @param user
	 * @return
	 */
	public boolean deleteUser(BindUser user){
		if (user == null || !bindUserIsExist(user.getOpenId())){
			return false ;
		}
		
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		String whereClause = Property.COLUMN_OPENID + "=?";
		String[] whereArgs = new String[]{user.getOpenId()};
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

		if (bindUserIsExist(bindUser.getOpenId())){
			if (bindUser.getRemarkName() == null){
				bindUser.setRemarkName(bindUser.getNickName());
			} 
		}
		
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_OPENID, bindUser.getOpenId());
		values.put(Property.COLUMN_NICKNAME, bindUser.getNickName());
		values.put(Property.COLUMN_REMARKNAME, bindUser.getRemarkName());
		values.put(Property.COLUMN_USERSEX, bindUser.getSex());
		values.put(Property.COLUMN_HEADIMAGE_URL, bindUser.getHeadImageUrl());
		values.put(Property.COLUMN_NEWS_NUM, bindUser.getNewsNum());
		values.put(Property.COLUMN_STATUS, bindUser.getStatus());
		values.put(Property.COLUMN_REPLY, bindUser.getReply());
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
	 * 获取消息个数
	 * @param openid
	 * @return
	 */
	public int getNewsNum(String openid){
		if (TextUtils.isEmpty(openid)){
			return 0;
		}
		BindUser user = getUser(openid);
		if (user != null){
			String allNewsNum = user.getNewsNum();
			if (allNewsNum != null){
				return Integer.parseInt(allNewsNum);
			}
		}
		return 0;
	}
	
	
	/**
	 * 增加一条消息
	 * @param openid
	 * @return
	 */
	public boolean addOneNews(String openid){
		if (bindUserIsExist(openid)){
			BindUser user = getUser(openid);
			return updateNewsNum(openid, increaseNewsNum(user.getNewsNum())) ;
		}
		return false;
	}
	
	/**
	 * 删除一条消息
	 * @param openid
	 * @return
	 */
	public boolean deleteOneNews(String openid){
		if (bindUserIsExist(openid)){
			BindUser user = getUser(openid);
			return updateNewsNum(openid, reduceNewsNum(user.getNewsNum())) ;
		}
		return false;
	}
	
	/**
	 * 更新消息个数
	 * @param openid
	 * @param newsNum
	 * @return
	 */
	public boolean updateNewsNum(String openid, String newsNum){
		BindUser user = getUser(openid);
		if (user != null){
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(Property.COLUMN_NEWS_NUM, newsNum);
			String whereClause = Property.COLUMN_OPENID + "=?";
			String[] whereArgs = new String[]{openid};
			if (db.update(Property.TABLE_USER, values, whereClause, whereArgs) > 0){
				return true;
			}
		}
		return false ;
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
	 * 消息数 + 1
	 * @param newsNum
	 */
	private String increaseNewsNum(String newsNum){
		if (TextUtils.isEmpty(newsNum)){
			newsNum = "0";
		}
		return String.valueOf((Integer.valueOf(newsNum)+1));
	}
	
	/**
	 * 消息数 - 1
	 * @param newsNum
	 * @return
	 */
	private String reduceNewsNum(String newsNum){
		if (TextUtils.isEmpty(newsNum) || Integer.valueOf(newsNum) <= 1){
			newsNum = "0";
		}
		return String.valueOf((Integer.valueOf(newsNum) - 1));
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
