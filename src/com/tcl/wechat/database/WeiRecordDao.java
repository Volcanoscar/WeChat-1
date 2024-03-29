package com.tcl.wechat.database;

import java.util.ArrayList;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.WeApplication;
import com.tcl.wechat.common.IConstant.ChatMsgType;
import com.tcl.wechat.common.IConstant.DownloadState;
import com.tcl.wechat.controller.OnLineStatusMonitor;
import com.tcl.wechat.model.BindUser;
import com.tcl.wechat.model.WeiXinMessage;

/**
 * 微信消息记录类
 * @author rex.lei
 *
 */
public class WeiRecordDao {
	
	private static final String TAG = WeiRecordDao.class.getSimpleName();
	
	private DBHelper mDbHelper;
	
	private static WeiRecordDao mInstance;
	
	private WeiRecordDao(Context context) {
		super();
		mDbHelper = DBHelper.getInstance();
		mDbHelper.getReadableDatabase();
	}

	public static WeiRecordDao getInstance(){
		if (mInstance == null){
			synchronized (WeiRecordDao.class) {
				mInstance = new WeiRecordDao(WeApplication.getContext());
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
		BindUser user = WeiUserDao.getInstance().getUser(openid);
		if (user == null){
			BinderUserNotFoundException();
			return false ;
		}
		return true;
	}
	
	/**
	 * 判断用户是否在线
	 * @param openId
	 * @param lastChatTime：需求，如果用户在48小时内进行交谈，则表示用户在线
	 * @return true: 在线  false:离线
	 */
	@SuppressLint("SimpleDateFormat") 
	public boolean isUserOnLine(String openId, String lastChatTime){
		try {
			Long lastTime = Long.parseLong(lastChatTime);
			
			String timeStr = getLatestRecorderTime(openId); //获取的最新记录聊天时间
			Long latestTime = Long.parseLong(timeStr);
			Log.i(TAG, "latestTime:" + latestTime + ", lastTime:" + lastTime);
			if (latestTime - lastTime >= OnLineStatusMonitor.INTERVAL){//大于48小时，表示用户已经离线
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 获取聊天条目总数
	 * @return
	 */
	public int getRecorderCount(){
		int recorderCnt = 0;
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] columns = new String[]{Property.COLUMN_ID};
		Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, columns, null, null, null, null, null);
		if (cursor != null){
			recorderCnt = cursor.getCount();
			cursor.close();
		}
		return recorderCnt;
	}
	
	public boolean addRecorder(WeiXinMessage recorder){
		if (recorder == null){
			return false;
		}
		if (!bindUserIsExist(recorder.getOpenid())){
			Log.e(TAG, "BinderUser not exist!!");
			return false;
		}
		
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_OPENID, recorder.getOpenid());
		values.put(Property.COLUMN_TOOPENID, recorder.getToOpenid());
		values.put(Property.COLUMN_MSGTYPE, recorder.getMsgtype());
		values.put(Property.COLUMN_MSGID, recorder.getMsgid());
		values.put(Property.COLUMN_CONTENT, recorder.getContent());
		values.put(Property.COLUMN_URL, recorder.getUrl());
		values.put(Property.COLUMN_LOCATIONX, recorder.getLocation_x());
		values.put(Property.COLUMN_LOCATIONY, recorder.getLocation_y());
		values.put(Property.COLUMN_LABEL, recorder.getLabel());
		values.put(Property.COLUMN_TITLE, recorder.getTitle());
		values.put(Property.COLUMN_DESCRIPTION, recorder.getDescription());
		values.put(Property.COLUMN_FORMAT, recorder.getFormat());
		values.put(Property.COLUMN_MDDIAID, recorder.getMediaid());
		values.put(Property.COLUMN_THUMBMEDIAID, recorder.getThumbmediaid());
		values.put(Property.COLUMN_CREATE_TIME, recorder.getCreatetime());
		values.put(Property.COLUMN_READED, recorder.getReaded());
		values.put(Property.COLUMN_RECEIVED, recorder.getReceived());
		values.put(Property.COLUMN_FILENAME, recorder.getFileName());
		values.put(Property.COLUMN_FILESIZE, recorder.getFileSize());
		values.put(Property.COLUMN_FILETIME, recorder.getFileTime());
		values.put(Property.COLUMN_STATUS, recorder.getStatus());
		if (db.insert(Property.TABLE_USERMSGRECORD, null, values) != -1){
			return true;
		}
		return false;
	}
	
	/**
	 * 删除指定用户消息记录
	 * @param openid
	 * @return
	 */
	public boolean deleteUserRecorder(String openid){
		Log.d(TAG, "deleteUserRecorder openid:" + openid);
		if (!bindUserIsExist(openid)){
			Log.e(TAG, "BinderUser not exist!!");
			return false;
		}
		int bRetCode = -1;
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			String whereClause = Property.COLUMN_OPENID + "=? or " + Property.COLUMN_TOOPENID + "=?";
			String[] whereArgs = new String[]{openid, openid};
			bRetCode = db.delete(Property.TABLE_USERMSGRECORD, whereClause, whereArgs);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null){
				db.endTransaction();
			}
			if ( bRetCode > 0){
				return true;
			}
		}
		Log.w(TAG, "delete User Recorder failed!!");
		return false;
	}
	
	/**
	 * 删除所用用户消息记录
	 * @return
	 */
	public boolean deleteAllUserRecorder(){
		int bRetCode = -1;
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			bRetCode = db.delete(Property.TABLE_USERMSGRECORD, null, null);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null){
				db.endTransaction();
			}
			if (bRetCode > 0){
				return true;
			}
		}
		Log.w(TAG, "delete all userRecorder failed!!");
		return false;
	}
	
	/**
	 * 删除某条消息记录
	 * @param openid
	 * @param msgId
	 * @return
	 */
	public boolean deleteRecorder(String msgId){

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		String whereClause = Property.COLUMN_MSGID + "=?";
		String[] whereArgs = new String[]{msgId};
		if (db.delete(Property.TABLE_USERMSGRECORD, whereClause, whereArgs) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 获取系统用户和另一指定用户聊天记录
	 * @param userOpenid
	 * @param sysOpenid
	 * @TODO 该部分需要优化，实现分页读取
	 * @return
	 */
	public ArrayList<WeiXinMessage> getSystmAndUserRecorder(String userOpenid, String sysOpenid){
		if (!bindUserIsExist(userOpenid) || !bindUserIsExist(sysOpenid)){
			Log.e(TAG, "BinderUser not exist!!");
			return null;
		}
		
		ArrayList<WeiXinMessage> recorders = new ArrayList<WeiXinMessage>();
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		db.beginTransaction();
		try {
			String selection = Property.COLUMN_OPENID + "=? or " + Property.COLUMN_OPENID + "=?";
			String[] selectionArgs = new String[]{sysOpenid, userOpenid};
			String orderBy = Property.COLUMN_ID + " asc ";
			Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, null, selection, selectionArgs, null, null, orderBy, null);
			if (cursor != null ){
				while (cursor.moveToNext()) {
					WeiXinMessage recorder = new WeiXinMessage(
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_OPENID)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_TOOPENID)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGTYPE)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGID)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_CONTENT)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_URL)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONX)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONY)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LABEL)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_TITLE)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_DESCRIPTION)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FORMAT)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_CREATE_TIME)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MDDIAID)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_THUMBMEDIAID)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_READED)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_RECEIVED)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILENAME)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILESIZE)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILETIME)) , 
							"");
					recorders.add(recorder);
				}
				cursor.close();
				db.setTransactionSuccessful();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null){
				db.endTransaction();
			}
		}
		return recorders;
	}
	
	/**
	 * 获取最新消息的msgid
	 * @return
	 */
	public String getLatestRecorderId(String openid){
		String msgId = "";
		//select msgid from messagedetail where  _id=(select max(_id) from messagedetail where received="0")
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] columns = new String[]{Property.COLUMN_MSGID};
		String selection = "_id=(select max(_id) from messagedetail where received=\"0\" and " + 
						Property.COLUMN_OPENID + " =? " +  ")";
		String[] selectionArgs = new String[]{openid};
		Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, columns , selection, selectionArgs, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			msgId = cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGID));
			cursor.close();
		}
		return msgId;
	}
	
	/**
	 * 获取数据库最新消息记录
	 * @return
	 */
	public WeiXinMessage getLatestRecorder(){
		
		WeiXinMessage recorder = null;
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		//select * from messagedetail where received = "0" order by createtime DESC LIMIT 1
		String selection = Property.COLUMN_RECEIVED + " =? " ;
		String[] selectionArgs = new String[]{"0"};
		String orderBy = Property.COLUMN_CREATE_TIME + " DESC ";
		String limit = "1";
		Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, null, selection, selectionArgs, null, null, orderBy, limit);
		if (cursor != null && cursor.moveToFirst()){
			recorder = new WeiXinMessage(
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_OPENID)), 
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_TOOPENID)), 
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGTYPE)), 
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGID)), 
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_CONTENT)) ,
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_URL)) , 
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONX)) ,
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONY)) ,
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_LABEL)) ,
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_TITLE)) ,
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_DESCRIPTION)) ,
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_FORMAT)) , 
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_CREATE_TIME)) , 
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_MDDIAID)) , 
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_THUMBMEDIAID)) ,
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_READED)) ,
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_RECEIVED)) ,
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILENAME)) , 
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILESIZE)) , 
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILETIME)) , 
				cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS)));
		}
		cursor.close();
		return recorder;
	}
	
	/**
	 * 获取用户最近一次聊天记录信息
	 * @param openId
	 * @return
	 */
	public WeiXinMessage getLatestRecorder(String openId){
		WeiXinMessage recorder = null;
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		//select * from messagedetail where  fromuseropenid ="asdfadsf" order by createtime DESC LIMIT 1
		String selection = Property.COLUMN_OPENID + " =? ";
		String[] selectionArgs = new String[]{openId};
		String orderBy = Property.COLUMN_CREATE_TIME + " DESC ";
		String limit = "1";
		Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, null, selection, selectionArgs, null, null, orderBy, limit);
		if (cursor != null && cursor.moveToFirst()){
			recorder = new WeiXinMessage(
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_OPENID)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_TOOPENID)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGTYPE)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGID)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_CONTENT)) ,
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_URL)) , 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONX)) ,
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONY)) ,
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_LABEL)) ,
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_TITLE)) ,
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_DESCRIPTION)) ,
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_FORMAT)) , 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_CREATE_TIME)) , 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_MDDIAID)) , 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_THUMBMEDIAID)) ,
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_READED)) , 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_RECEIVED)) ,
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILENAME)) , 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILESIZE)) , 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILETIME)) , 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS)));
		}
		cursor.close();
		return recorder;
	}
	
	/**
	 * 获取用户最近一次聊天记录信息
	 * @param msgid
	 * @return
	 */
	public WeiXinMessage getRecorder(String msgid){
		WeiXinMessage recorder = null;
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		
		String selection = Property.COLUMN_MSGID + "=? ";
		String[] selectionArgs = new String[]{msgid};
		Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, null, selection, selectionArgs, null, null, null, null);
		if (cursor != null && cursor.moveToLast()){
			recorder = new WeiXinMessage(
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_OPENID)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_TOOPENID)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGTYPE)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGID)), 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_CONTENT)) ,
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_URL)) , 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONX)) ,
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONY)) ,
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_LABEL)) ,
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_TITLE)) ,
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_DESCRIPTION)) ,
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_FORMAT)) , 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_CREATE_TIME)) , 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_MDDIAID)) , 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_THUMBMEDIAID)) ,
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_READED)) , 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_RECEIVED)) ,
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILENAME)) , 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILESIZE)) , 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILETIME)) , 
					cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS)));
		}
		cursor.close();
		return recorder;
	}
	
	/**
	 * 获取所有聊天用户的id
	 * @return
	 */
	public LinkedList<String> getAllRecorderUserId(){
		//select fromuseropenid from messagedetail where fromuseropenid != "gh_93cf5ac1fca7"  and _id in(select max(_id) FROM messagedetail group by fromuseropenid)
		LinkedList<String> userIds = new LinkedList<String>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		BindUser systemUser = WeiUserDao.getInstance().getSystemUser();
		db.beginTransaction();
		try {
			String[] columns = new String[]{Property.COLUMN_OPENID};
			String selection = Property.COLUMN_OPENID + "!=? and _id in(select max(_id) from messagedetail group by fromuseropenid)" ;
			String[] selectionArgs = new String[]{systemUser.getOpenId()};
			Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, columns, selection, selectionArgs, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					userIds.addFirst(cursor.getString(cursor.getColumnIndex(Property.COLUMN_OPENID)));
				}
				cursor.close();
				db.setTransactionSuccessful();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null){
				db.endTransaction();
			}
		}
		return userIds;
	}
	
	/**
	 * 获取所有用户的最新聊天记录
	 * @return
	 */
	public LinkedList<WeiXinMessage> getLastRecorder(){
		LinkedList<WeiXinMessage> allLastRecorder = new LinkedList<WeiXinMessage>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		BindUser systemUser = WeiUserDao.getInstance().getSystemUser();
		if (systemUser == null){
			return allLastRecorder;
		}
		db.beginTransaction();
		try {
			//select * from messagedetail where fromuseropenid !="gh_93cf5ac1fca7" and  _id in(select max(_id) FROM messagedetail group by fromuseropenid)
			String selection = Property.COLUMN_OPENID + "!=? and  _id in(select max(_id) from messagedetail group by fromuseropenid)";
			String[] selectionArgs = new String[]{systemUser.getOpenId()};
			Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, null, selection, selectionArgs, null, null, null, null);
			if (cursor != null){
				while (cursor.moveToNext()) {
					WeiXinMessage recorder = new WeiXinMessage(
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_OPENID)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_TOOPENID)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGTYPE)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGID)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_CONTENT)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_URL)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONX)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONY)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LABEL)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_TITLE)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_DESCRIPTION)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FORMAT)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_CREATE_TIME)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MDDIAID)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_THUMBMEDIAID)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_READED)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_RECEIVED)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILENAME)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILESIZE)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILETIME)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS)));
					allLastRecorder.addFirst(recorder);
				}
				cursor.close();
				db.setTransactionSuccessful();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (db != null){
				db.endTransaction();
			}
		}
		return allLastRecorder;
	}
	
	/**
	 * 获取用户所用消息记录 //TODO 开发接口，获取多少条？？
	 * @param openid
	 * @return
	 */
	public ArrayList<WeiXinMessage> getUserRecorder(String openid){
		if (!bindUserIsExist(openid)){
			Log.e(TAG, "BinderUser not exist!!");
			return null;
		}
		
		ArrayList<WeiXinMessage> recorders = null;
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		db.beginTransaction();
		try {
			String selection = Property.COLUMN_OPENID + "=? ";
			String[] selectionArgs = new String[]{openid};
			String orderBy = Property.COLUMN_CREATE_TIME;
			Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, null, selection, selectionArgs, null, null, orderBy);
			if (cursor != null){
				recorders = new ArrayList<WeiXinMessage>();
				while (cursor.moveToNext()) {
					WeiXinMessage recorder = new WeiXinMessage(openid, 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_TOOPENID)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGTYPE)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGID)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_CONTENT)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_URL)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONX)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONY)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LABEL)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_TITLE)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_DESCRIPTION)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FORMAT)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_CREATE_TIME)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MDDIAID)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_THUMBMEDIAID)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_READED)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_RECEIVED)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILENAME)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILESIZE)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILETIME)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS)));
					recorders.add(recorder);
				}
				cursor.close();
				db.setTransactionSuccessful();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null){
				db.endTransaction();
			}
		}
		return recorders;
	}
	
	/**
	 * 分页查询
	 * @param pageIndex
	 * @param openid
	 * @return
	 */
	public LinkedList<WeiXinMessage> getUserRecorder(int startIndex, String openid){
		LinkedList<WeiXinMessage> recorders = null;
		
		//SELECT * from userMsgRecord where _id order by create_time DESC LIMIT 0,10
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		db.beginTransaction();
		try {
			String selection = Property.COLUMN_ID + " and " + Property.COLUMN_OPENID + "=? or " 
							 + Property.COLUMN_TOOPENID + "=?";
			String[] selectionArgs = new String[]{openid, openid};
			String orderBy = Property.COLUMN_ID + " DESC ";
			String limit = String.valueOf(startIndex) + "," + " 15";
			Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, null, selection, selectionArgs, null, null, orderBy, limit);
			if (cursor != null){
				recorders = new LinkedList<WeiXinMessage>();
				while (cursor.moveToNext()){
					WeiXinMessage recorder = new WeiXinMessage(
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_OPENID)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_TOOPENID)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGTYPE)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGID)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_CONTENT)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_URL)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONX)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONY)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LABEL)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_TITLE)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_DESCRIPTION)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FORMAT)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_CREATE_TIME)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MDDIAID)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_THUMBMEDIAID)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_READED)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_RECEIVED)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILENAME)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILESIZE)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILETIME)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS)));
					recorders.addFirst(recorder);
				}
				db.setTransactionSuccessful();
				cursor.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (db != null){
				db.endTransaction();
			}
		}
		return recorders;
	}
	
	/**
	 * 获取所有未读消息
	 * @param startTime
	 * @param openid
	 * @return
	 */
	public LinkedList<WeiXinMessage> getUnreadRecorder(String startTime, String openid){
		LinkedList<WeiXinMessage> recorders = new LinkedList<WeiXinMessage>();
		//select * from messagedetail where fromuseropenid = "oOAgYvwgM3Rvifhru2jk60IOg__w" and createtime > "1451977835829";
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		db.beginTransaction();
		try {
			String selection = "(" + Property.COLUMN_OPENID + " =? or "+ Property.COLUMN_TOOPENID + " =? ) and " + Property.COLUMN_CREATE_TIME + " >? ";
			String[] selectionArgs = new String[]{openid, openid, startTime};
			String orderBy = Property.COLUMN_ID + " DESC ";;
			Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, null, selection, selectionArgs, null, null, orderBy);
			if (cursor != null){
				while (cursor.moveToNext()) {
					WeiXinMessage recorder = new WeiXinMessage(
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_OPENID)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_TOOPENID)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGTYPE)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGID)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_CONTENT)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_URL)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONX)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONY)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LABEL)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_TITLE)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_DESCRIPTION)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FORMAT)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_CREATE_TIME)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MDDIAID)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_THUMBMEDIAID)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_READED)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_RECEIVED)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILENAME)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILESIZE)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILETIME)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS)));
					recorders.addFirst(recorder);
				}
				db.setTransactionSuccessful();
				cursor.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (db != null){
				db.endTransaction();
			}
		}
		return recorders;
	}
	
	/**
	 * 获取所有用户消息记录
	 * @return
	 */
	public ArrayList<WeiXinMessage> getAllUserRecorder(){
		ArrayList<WeiXinMessage> recorders = new ArrayList<WeiXinMessage>();
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		db.beginTransaction();
		try {
			Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, null, null, null, null, null, null);
			
			if (cursor != null){
				while (cursor.moveToNext()){
					WeiXinMessage recorder = new WeiXinMessage(
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_OPENID)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_TOOPENID)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGTYPE)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MSGID)), 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_CONTENT)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_URL)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONX)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LOCATIONY)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_LABEL)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_TITLE)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_DESCRIPTION)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FORMAT)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_CREATE_TIME)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_MDDIAID)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_THUMBMEDIAID)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_READED)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_RECEIVED)) ,
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILENAME)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILESIZE)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILETIME)) , 
							cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS)));
					recorders.add(recorder);
				}
				cursor.close();
				db.setTransactionSuccessful();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null){
				db.endTransaction();
			}
		}
		return recorders;
	}
	
	/**
	 * 获取用户最近一次聊天时间
	 * @param openId
	 * @return
	 */
	public String getLatestRecorderTime(String openId){
		String latestTime = null ;
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] columns = new String[]{Property.COLUMN_CREATE_TIME};
		String selection = Property.COLUMN_OPENID + "=?";
		String[] selectionArgs = new String[]{openId};
		//SELECT create_time from userMsgRecord where openid="gh_93cf5ac1fca7" order by create_time DESC LIMIT 0,1
		Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, columns, selection, selectionArgs, null, null, null);
		if (cursor != null && cursor.moveToLast()){
			latestTime = cursor.getString(cursor.getColumnIndex(Property.COLUMN_CREATE_TIME));
			cursor.close();
		}
		return latestTime;
	}
	
	/**
	 * 获取Url
	 * @param messageid
	 * @return
	 */
	public String getRecorderUrl(String messageid){
		String url = null;
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] columns = new String[]{Property.COLUMN_URL};
		String selection = Property.COLUMN_MSGID + "=?";
		String[] selectionArgs = new String[]{messageid};
		Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, columns, selection, selectionArgs, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			url = cursor.getString(cursor.getColumnIndex(Property.COLUMN_URL));
			cursor.close();
		}
		return url;
	}
	
	/**
	 * 获取用户所有的图片url
	 * @param openid 
	 * @param toOpenid 
	 * @return
	 */
	public ArrayList<String> getAllRecorderUrl(String openid) {
		ArrayList<String> allUrls = new ArrayList<String>();
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String selection = Property.COLUMN_OPENID + "=? or " + Property.COLUMN_TOOPENID + 
				" =? and " + Property.COLUMN_MSGTYPE + "=?";
		String[] selectionArgs = new String[]{openid, openid, ChatMsgType.IMAGE};
		String orderBy = Property.COLUMN_ID;
		String[] columns = new String[]{Property.COLUMN_URL};
		Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, columns , selection, selectionArgs, null, null, orderBy);
		if (cursor != null ) {
			while (cursor.moveToNext()) {
				String url = cursor.getString(cursor.getColumnIndex(Property.COLUMN_URL));
				if (!TextUtils.isEmpty(url)) {
					allUrls.add(url);
				}
			}
			cursor.close();
		}
		return allUrls;
	}
	
	/**
	 * 更新Url信息
	 * @param messageid 
	 * @param url
	 * @return
	 */
	public boolean updateRecorderUrl(String messageid, String url){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_URL, url);
		String whereClause = Property.COLUMN_MSGID + "=?";
		String[] whereArgs = new String[]{messageid};
		if (db.update(Property.TABLE_USERMSGRECORD, values, whereClause, whereArgs) > 0 ){
			return true;
		}
		return false;
	}
	
	/**
	 * 更新Url信息
	 * @param messageid 
	 * @param url
	 * @return
	 */
	public boolean updateFileName(String messageid, String filePath){
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_FILENAME, filePath);
		values.put(Property.COLUMN_STATUS, DownloadState.DOWNLOAD_COMPLETED);
		String whereClause = Property.COLUMN_MSGID + "=?";
		String[] whereArgs = new String[]{messageid};
		if (db.update(Property.TABLE_USERMSGRECORD, values, whereClause, whereArgs) > 0 ){
			return true;
		}
		return false;
	}
	
	/**
	 * 获取用户未读消息个数
	 * @param openid
	 * @return
	 */
	public int getAllUnreadedMsgCnt(String openid){
		int unreadedCnt = 0;
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] columns = new String[]{Property.COLUMN_READED};
		String selection = Property.COLUMN_OPENID + " =? and " + Property.COLUMN_READED + " =? ";
		String[] selectionArgs = new String[]{openid, "0"};
		Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, columns, selection, selectionArgs, null, null, null);
		if (cursor != null){
			unreadedCnt = cursor.getCount();
			cursor.close();
		}
		return unreadedCnt;
	}
	
	/**
	 * 查看消息状态
	 * @param msgid
	 * @return
	 */
	public String getMessageStatue(String msgid){
		String statue = "1";
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		
		String[] columns = new String[]{Property.COLUMN_READED};
		String selection = Property.COLUMN_MSGID + "=? and " + Property.COLUMN_READED + "=?";
		String[] selectionArgs = new String[]{msgid, "0"};
		Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, columns, selection, selectionArgs, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			statue = cursor.getString(cursor.getColumnIndex(Property.COLUMN_READED));
			cursor.close();
		}
		return statue;
	}
	
	/**
	 * 更新消息状态
	 * @param msgid
	 * @return
	 */
	public boolean updateMessageReadState(String msgid, String status){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_READED, status);
		String whereClause = Property.COLUMN_MSGID + "=?";
		String[] whereArgs = new String[]{msgid};
		if (db.update(Property.TABLE_USERMSGRECORD, values, whereClause, whereArgs) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 设置消息已读状态
	 * @param msgid
	 * @return
	 */
	public boolean setMessageReaded(String openid){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_READED, "1");
		String whereClause = Property.COLUMN_OPENID + "=? or " + Property.COLUMN_TOOPENID + "=?";
		String[] whereArgs = new String[]{openid, openid};
		if (db.update(Property.TABLE_USERMSGRECORD, values, whereClause, whereArgs) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 更新播放状态
	 * @param msgid
	 * @return
	 */
	public boolean updateMessagePlayState(String msgid){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_READED, "2");
		String whereClause = Property.COLUMN_MSGID + "=?";
		String[] whereArgs = new String[]{msgid};
		if (db.update(Property.TABLE_USERMSGRECORD, values, whereClause, whereArgs) > 0){
			return true;
		}
		return false;
	}
	
	/**
	 * 更新该用户的所有消息状态 （进入聊天界面更新）
	 * @param openid
	 * @return
	 */
	public boolean updateAllMessageReadState(String openid){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_READED, "1");
		String whereClause = Property.COLUMN_OPENID + "=?";
		String[] whereArgs = new String[]{openid};
		if (db.update(Property.TABLE_USERMSGRECORD, values, whereClause, whereArgs) > 0){
			return true;
		}
		return false ;
	}
	
	/**
	 * 获取消息状态
	 * @param msgId
	 * @return
	 */
	public String getMessageStatus(String msgId){
		
		String status = null;
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] columns = new String[]{Property.COLUMN_STATUS};
		String selection = Property.COLUMN_MSGID + "=?";
		String[] selectionArgs = new String[]{msgId};
		Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, columns, selection, selectionArgs, null, null, null);
		if (cursor != null){
			status = cursor.getString(cursor.getColumnIndex(Property.COLUMN_STATUS));
			cursor.close();
		}
		return status;
	}
	
	/**
	 * 获取文件名称
	 * @param msgId
	 * @return
	 */
	public String getFileName(String msgId){
		String fileName = null;
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] columns = new String[]{Property.COLUMN_FILENAME};
		String selection = Property.COLUMN_MSGID + "=?";
		String[] selectionArgs = new String[]{msgId};
		Cursor cursor = db.query(Property.TABLE_USERMSGRECORD, columns, selection, selectionArgs, null, null, null);
		if (cursor != null && cursor.moveToFirst()){
			fileName = cursor.getString(cursor.getColumnIndex(Property.COLUMN_FILENAME));
			cursor.close();
		}
		return fileName;
	}
	/**
	 * 更新消息状态：
	 * 发送成功、失败、发送中
	 * @return
	 */
	public boolean updateMessageState(String msgid, String state){
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(Property.COLUMN_STATUS, state);
		String whereClause = Property.COLUMN_MSGID + "=?";
		String[] whereArgs = new String[]{msgid};
		if (db.update(Property.TABLE_USERMSGRECORD, values, whereClause, whereArgs) > 0){
			return true;
		}
		return false ;
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
