package com.tcl.wechat.db;

public class Property {

	public static final String DATABASE_NAME = "wechat.db";
	
	/**
	 *  BindUser数据库
	 */
	public static final String TABLE_USER = "bindUser";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_OPENID = "openid";
	public static final String COLUMN_NICKNAME = "nickname";
	public static final String COLUMN_REMARKNAME = "remarkname";
	public static final String COLUMN_USERSEX = "sex";
	public static final String COLUMN_HEADIMAGE_URL = "headimageurl";
	public static final String COLUMN_NEWS_NUM = "newsnum";
	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_REPLY = "reply";
	
	/**
	 * UserRecord数据库
	 */
	public static final String TABLE_USERMSGRECORD = "userMsgRecord";
	public static final String COLUMN_ACCESS_TOKENS = "accessToken";
	public static final String COLUMN_MSGTYPE = "msg_type";
	public static final String COLUMN_MSGID = "msgid";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_URL = "imageurl";
	public static final String COLUMN_FORMAT = "format";
	public static final String COLUMN_MDDIAID = "mediaid";
	public static final String COLUMN_THUMBMEDIAID = "thumbmediaid";
	public static final String COLUMN_CREATE_TIME = "create_time";
	public static final String COLUMN_EXPIRETIME = "expire_time";
	public static final String COLUMN_READED = "readed";
	public static final String COLUMN_FILENAME = "fileName";
	public static final String COLUMN_FILESIZE = "fileSize";
	public static final String COLUMN_FILETIME = "fileTime";
	
	/**
	 * AppInfo数据库
	 */
	public static final String TABLE_APPINFO = "appInfo";
	public static final String COLUMN_APP_NAME = "appname";
	public static final String COLUMN_PACKAGE_NAME = "packagename";
	public static final String COLUMN_VERSION_CODE = "versioncode";
	public static final String COLUMN_VERSION_NAME = "versionname";
	public static final String COLUMN_MD5 = "md5";
	
	/**
	 * device 数据库
	 */
	public static final String TABLE_DEVICE = "deviceInfo";
	public static final String COLUMN_DEVICEID = "deviceid";
	public static final String COLUMN_MAC = "mac";
	public static final String COLUMN_MEMBERID = "memberid";
	
	
	/**
	 * QR二维码数据库
	 */
	public static final String TABLE_QR = "qrInfo";
	public static final String COLUMN_QR_URL = "url";
	public static final String COLUMN_UUID = "uuid";
	
	
	public static final String CREATE_TABLE_USER_SQL = 
			"CREATE TABLE IF NOT EXISTS " +
			Property.TABLE_USER + "(" +
			Property.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Property.COLUMN_OPENID + " VACHAR(128)," +
            Property.COLUMN_NICKNAME + " VACHAR(128)," +
            Property.COLUMN_REMARKNAME+ " VACHAR(128)," +
            Property.COLUMN_USERSEX + " VACHAR(64)," + 
            Property.COLUMN_HEADIMAGE_URL + " TEXT," +
            Property.COLUMN_NEWS_NUM + " VACHAR(128)," +
            Property.COLUMN_STATUS + " VACHAR(64)," +
            Property.COLUMN_REPLY + " VACHAR(64)" + ")";
	
	public static final String CREATE_TABLE_USERRECORD_SQL = 
			"CREATE TABLE IF NOT EXISTS " +
			Property.TABLE_USERMSGRECORD + "(" +
			Property.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Property.COLUMN_OPENID + " VACHAR(128)," +
            Property.COLUMN_ACCESS_TOKENS + " VACHAR(128)," +
            Property.COLUMN_MSGID + " VACHAR(128)," +
            Property.COLUMN_MSGTYPE + " VACHAR(64)," +
            Property.COLUMN_CONTENT + " TEXT," +
            Property.COLUMN_URL + " TEXT," + 
            Property.COLUMN_FORMAT + " VACHAR(64)," +
            Property.COLUMN_MDDIAID + " VACHAR(128)," +
            Property.COLUMN_THUMBMEDIAID + " TEXT," +
            Property.COLUMN_CREATE_TIME + " VACHAR(32)," +
            Property.COLUMN_EXPIRETIME + " VACHAR(32)," +
            Property.COLUMN_READED + " VACHAR(32)," +
            Property.COLUMN_FILENAME + " TEXT," + 
            Property.COLUMN_FILESIZE + " INTEGER," +
            Property.COLUMN_FILETIME + " REAL" + ")";
	
	public static final String CREATE_TABLE_APPINFO_SQL = 
			"CREATE TABLE IF NOT EXISTS " +
			Property.TABLE_APPINFO + "(" +
			Property.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			Property.COLUMN_APP_NAME + " VACHAR(50)," +
		    Property.COLUMN_PACKAGE_NAME + " VACHAR(128)," +
		    Property.COLUMN_VERSION_CODE + " VACHAR(128)," +
		    Property.COLUMN_VERSION_NAME + " VACHAR(64)," +
		    Property.COLUMN_MD5 + " TEXT " + ")";
	
	public static final String CREATE_TABLE_DEVICE_SQL = 
			"CREATE TABLE IF NOT EXISTS " +
			Property.TABLE_DEVICE + "(" +
			Property.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			Property.COLUMN_DEVICEID + " VACHAR(128)," +
			Property.COLUMN_MAC + " VACHAR(64)," +
		    Property.COLUMN_MEMBERID + " VACHAR(128)" + ")";
	
	public static final String CREATE_TABLE_QR_SQL = 
			"CREATE TABLE IF NOT EXISTS " +
			Property.TABLE_QR + "(" +
			Property.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		    Property.COLUMN_QR_URL + " TEXT," +
		    Property.COLUMN_UUID + " VACHAR(128)" + ")";
	
}
