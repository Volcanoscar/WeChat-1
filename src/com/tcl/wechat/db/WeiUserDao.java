/* 
* -------------------------------------------------------------
 * Copyright (c) 2011 TCL, All Rights Reserved.
 * ---------------------------------------------------------
 * @author:zhangjunjian
 * @version V1.0
 */
package com.tcl.wechat.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tcl.wechat.modle.BinderUser;
import com.tcl.wechat.modle.NewsNum;

/**
 * @ClassName: WeiUserDao
* @Description: 用户信息表
 */

public class WeiUserDao {
	
	private DBOpenHelper dbOpenHelper;
	
	public WeiUserDao(Context context)
	{
		this.dbOpenHelper = new DBOpenHelper(context);
	}
	
	public boolean save(ArrayList<BinderUser> files)
	{
		//删除表所有记录之前，把用户新消息条数记录下来。
		 List<NewsNum> newsNums = findNews();
		// 如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();		
		db.execSQL("delete from weibinderuser");
		/*db.execSQL("CREATE TABLE IF NOT EXISTS weibinderuser (id integer primary key autoincrement, " +
				"openid varchar(100), nickname varchar(100), sex varchar(50),headimgurl varchar(100),newsnum varchar(100))");*/
		for (int i = 0 ;i < files.size();i++){
			db.execSQL("insert into weibinderuser (openid,nickname, sex ,headimgurl,newsnum) values(?,?,?,?,?)",
					new Object[] { files.get(i).getOpenid(), files.get(i).getNickname(),
							files.get(i).getSex(), files.get(i).getHeadimgurl(),"0" });	
		}
		//将新消息记录存进去
		for(int i=0;i<newsNums.size();i++){
			db.execSQL("update weibinderuser set newsnum=? where openid=?",
					new Object[] { newsNums.get(i).getNewsnum(),newsNums.get(i).getOpenid() });
		}
		return true;
	}
	public boolean save(BinderUser file)
	{
		if (file == null){
			return false;
		}
		String openid = file.getOpenid();
		List<BinderUser> cacheList = find(openid);
		//如果当前已经存在该绑定的用户则不再插入数据库
		if (cacheList != null && cacheList.size() > 0){
			return false;
		}
		// 如果要对数据进行更改，就调用此方法得到用于操作数据库的实例,该方法以读和写方式打开数据库
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("insert into weibinderuser (openid,nickname, sex ,headimgurl,status) values(?,?,?,?,?)",
					new Object[] { file.getOpenid(), file.getNickname(),
					file.getSex(), file.getHeadimgurl(),file.getstatus() });	
		
		return true;
	}
	public List<BinderUser> find(String openid) 
	{
		List<BinderUser> cacheList = new ArrayList<BinderUser>();

		// 如果只对数据进行读取，建议使用此方法
		try {
			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

			Cursor cursor = db.rawQuery("select * from weibinderuser where openid=?",new String[] { openid });
			while (cursor.moveToNext())
			{
				BinderUser object = new BinderUser();
				object.setOpenid(cursor.getString(cursor.getColumnIndex("openid")));
				object.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
				object.setSex(cursor.getString(cursor.getColumnIndex("sex")));
				object.setHeadimgurl(cursor.getString(cursor.getColumnIndex("headimgurl")));			
				cacheList.add(object);
			}
			cursor.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cacheList;

	}
	public List<BinderUser> find() 
	{
		List<BinderUser> cacheList = new ArrayList<BinderUser>();

		// 如果只对数据进行读取，建议使用此方法
		try {
			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

			Cursor cursor = db.rawQuery("select * from weibinderuser", null);
			while (cursor.moveToNext())
			{
				BinderUser object = new BinderUser();
				object.setOpenid(cursor.getString(cursor.getColumnIndex("openid")));
				object.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
				object.setSex(cursor.getString(cursor.getColumnIndex("sex")));
				object.setHeadimgurl(cursor.getString(cursor.getColumnIndex("headimgurl")));
				object.setNewsnum(cursor.getString(cursor.getColumnIndex("newsnum")));
				String statusString = cursor.getString(cursor.getColumnIndex("status"));
				if(statusString==null||statusString.equals("success"))//绑定的status有的没有赋值，有的是success
					cacheList.add(object);
			}
			cursor.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cacheList;

	}
	public List<NewsNum> findNews() 
	{
		List<NewsNum> cacheList = new ArrayList<NewsNum>();

		// 如果只对数据进行读取，建议使用此方法
		try {
			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

			Cursor cursor = db.rawQuery("select * from weibinderuser", null);
			while (cursor.moveToNext())
			{
				NewsNum object = new NewsNum();
				object.setOpenid(cursor.getString(cursor.getColumnIndex("openid")));
				object.setNewsnum(cursor.getString(cursor.getColumnIndex("newsnum")));
				cacheList.add(object);
			}
			cursor.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cacheList;

	}
	public boolean addNews(String openid) 
	{
		
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from weibinderuser where openid=?",new String[] { openid });
		String newsnumStr = "0";
		while (cursor.moveToNext())
		{
			newsnumStr = cursor.getString(cursor.getColumnIndex("newsnum"));			
		}
		if(newsnumStr==null)
			newsnumStr = "0";
		newsnumStr = String.valueOf((Integer.valueOf(newsnumStr)+1));		
		
		db.execSQL("update weibinderuser set newsnum=? where openid=?",
				new Object[] { newsnumStr,openid });
		
		return true;

	}
	public boolean reduceNews(String openid,int num) 
	{
		
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from weibinderuser where openid=?",new String[] { openid });
		String newsnumStr = "0";
		while (cursor.moveToNext())
		{
			newsnumStr = cursor.getString(cursor.getColumnIndex("newsnum"));			
		}
		if(newsnumStr==null)
			newsnumStr = "0";
		newsnumStr = String.valueOf((Integer.valueOf(newsnumStr)-num));		
		
		db.execSQL("update weibinderuser set newsnum=? where openid=?",
				new Object[] { newsnumStr,openid });
		
		return true;

	}
	public boolean setNews(String openid) 
	{
		
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();				
		db.execSQL("update weibinderuser set newsnum=? where openid=?",
				new Object[] { "0",openid });
		
		return true;

	}
	public boolean setStatus(String openid,String status) 
	{
		
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();				
		db.execSQL("update weibinderuser set status=? where openid=?",
				new Object[] { status,openid });
		
		return true;

	}
	public boolean delete(String openid){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL(
				"delete from weibinderuser where openid=?",
				new Object[] {  openid });
		return true;
	}
}
