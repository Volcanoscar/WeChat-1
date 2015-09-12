package com.tcl.wechat.db;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class MyContentProvider extends ContentProvider {
    private SQLiteDatabase     sqlDB;
    private DBHelper    dbHelper;

   /* private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //创建用于存储数据的表
        db.execSQL("CREATE TABLE IF NOT EXISTS qrinfo (_id integer primary key autoincrement, url varchar(500))");
       // db.execSQL("Create table " + MyUsers.User.QR_TABLE_NAME + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + MyUsers.User.QR_TABLE_NAME);
            onCreate(db);
        }
    }*/

    @Override
    public int delete(Uri uri, String s, String[] as) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentvalues) {
        sqlDB = dbHelper.getWritableDatabase();
        Log.i("liyulin", "@@@@@---uri="+uri);
        if(uri.equals(MyUsers.CONTENT_URI)){
	        long rowId = sqlDB.insert(MyUsers.QR_TABLE_NAME, "", contentvalues);
	        if (rowId > 0) {
	            Uri rowUri = ContentUris.appendId(MyUsers.CONTENT_URI.buildUpon(), rowId).build();
	            getContext().getContentResolver().notifyChange(rowUri, null);
	            return rowUri;
	        }
	        throw new SQLException("Failed to insert row into " + uri);
        }
        return null;
    }
    
    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return (dbHelper == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    	SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.i("liyulin", "@@@@@---uri="+uri);     
        if(uri.equals(MyUsers.CONTENT_URI)){
	        qb.setTables(MyUsers.QR_TABLE_NAME);
	        Cursor c = qb.query(db, projection, selection, null, null, null, sortOrder);
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;
        }else if(uri.equals(MyUsers.CONTENT_USER)){
        	qb.setTables(MyUsers.USER_TABLE_NAME);
	        Cursor c = qb.query(db, projection, selection, null, null, null, sortOrder);
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;
        }else if(uri.equals(MyUsers.CONTENT_RECORD)){
        	//修改权限
    	//	RootSeeker.exec("chmod -R 777 " + WeiConstant.DOWN_LOAD_FLASH_PATH);
          	
        	qb.setTables(MyUsers.RECORD_TABLE_NAME);
	        Cursor c = qb.query(db, projection, selection, null, null, null, sortOrder);
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        return c;
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues contentvalues, String s, String[] as) {
    	SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.update(MyUsers.RECORD_TABLE_NAME, contentvalues, s,as );
        return 0;
    }
}