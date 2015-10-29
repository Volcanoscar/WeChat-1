package com.tcl.wechat.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.database.sqlite.SQLiteDatabase.CursorFactory;


/**
 * 聊天数据缓存类
 * @author rex.lei
 *
 */
public class ChatDataCache {
	
	private String table = "recorder";
	
	public ChatDataCache() {
		super();
		SQLiteDatabase db = SQLiteDatabase.create(factory);
		
		db.query(table, null, null, null, null, null, null);
		
	}

	private CursorFactory factory = new CursorFactory() {
		
		@Override
		public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery,
				String editTable, SQLiteQuery query) {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	
	
	

}
