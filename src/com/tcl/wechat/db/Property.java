package com.tcl.wechat.db;

public class Property {

	public static final String DATABASE_NAME = "wecaht.db";
	
	/**
	 * User数据库
	 */
	public static final String TABLE_USER = "user";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_OPENID = "openId";
	public static final String COLUMN_USERNAME = "userName";
	public static final String COLUMN_NICKNAME = "nickName";
	public static final String COLUMN_REMARKNAME = "remarkName";
	public static final String COLUMN_USERSEX = "sex";
	public static final String COLUMN_HEADIMAGE_URL = "headimageurl";
	public static final String COLUMN_SIGNATURE_INFO = "signature";
	public static final String COLUMN_NEWS_NUM = "newsNum";
	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_DATA1 = "data1";
	public static final String COLUMN_DATA2 = "data2";
	public static final String COLUMN_DATA3 = "data3";
	
	/**
	 * UserRecord数据库
	 */
	public static final String TABLE_USERRECORD = "user_record";
	public static final String COLUMN_ACCESS_TOKENS = "accessToken";
	public static final String COLUMN_MSGTYPE = "msgType";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_IMAGE_URL = "imageurl";
	public static final String COLUMN_FORMAT = "format";
	public static final String COLUMN_MDDIAID = "mediaId";
	public static final String COLUMN_HUMBMEDIAID = "humbmediaId";
	public static final String COLUMN_CREATE_TIME = "createTime";
	public static final String COLUMN_EXPIRETIME = "expireTime";
	public static final String COLUMN_READED = "readed";
	public static final String COLUMN_FILENAME = "fileName";
	public static final String COLUMN_FILESIZE = "fileSize";
	public static final String COLUMN_FILETIME = "fileTime";
	
	/**
	 * AppInfo数据库
	 */
	public static final String TABLE_APPINFO = "appinfo";
	public static final String COLUMN_PACKAGE_NAME = "packageName";
	public static final String COLUMN_VERSION_CODE = "versionCode";
	public static final String COLUMN_VERSION_NAME = "versionName";
	public static final String COLUMN_MD5 = "md5";
	
	/**
	 * device 数据库
	 */
	public static final String TABLE_DEVICE = "device";
	public static final String COLUMN_DEVICEID = "deviceId";
	public static final String COLUMN_DEVICE_NAME = "deviceName";
	
	
	/**
	 * QR二维码数据库
	 */
	public static final String TABLE_QR = "qrinfo";
	public static final String COLUMN_QR_URL = "url";
	public static final String COLUMN_UUID = "uuid";
	
	
	public static final String CREATE_TABLE_USER_SQL = 
			"CREATE TABLE IF NOT EXISTS " +
			Property.TABLE_USER + "(" +
			Property.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Property.COLUMN_OPENID + " VACHAR(100)," +
            Property.COLUMN_USERNAME + " VACHAR(100)," +
            Property.COLUMN_NICKNAME + " VACHAR(100)," +
            Property.COLUMN_REMARKNAME+ " VACHAR(100)," +
            Property.COLUMN_USERSEX + " VACHAR(50)," + 
            Property.COLUMN_HEADIMAGE_URL + " VACHAR(500)," +
            Property.COLUMN_SIGNATURE_INFO + " TEXT," +
            Property.COLUMN_NEWS_NUM + " VACHAR(100)," +
            Property.COLUMN_STATUS + " VACHAR(50)," +
            Property.COLUMN_DATA1 + " VACHAR(100)," +
            Property.COLUMN_DATA2 + " VACHAR(100)," +
            Property.COLUMN_DATA3 + " VACHAR(100)" + ")";
	
	public static final String CREATE_TABLE_USERRECORD_SQL = 
			"CREATE TABLE IF NOT EXISTS " +
			Property.TABLE_USERRECORD + "(" +
			Property.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Property.COLUMN_OPENID + " VACHAR(100)," +
            Property.COLUMN_ACCESS_TOKENS + " VACHAR(100)," +
            Property.COLUMN_MSGTYPE + " VACHAR(50)," +
            Property.COLUMN_CONTENT + " TEXT," +
            Property.COLUMN_IMAGE_URL + " TEXT," + 
            Property.COLUMN_FORMAT + " VACHAR(50)," +
            Property.COLUMN_MDDIAID + " VACHAR(100)," +
            Property.COLUMN_HUMBMEDIAID + " VACHAR(500)," +
            Property.COLUMN_CREATE_TIME + " REAL," +
            Property.COLUMN_EXPIRETIME + " REAL," +
            Property.COLUMN_READED + " VACHAR(20)," +
            Property.COLUMN_FILENAME + " TEXT," + 
            Property.COLUMN_FILESIZE + " INTEGER," +
            Property.COLUMN_FILETIME + " REAL," +
            Property.COLUMN_DATA1 + " VACHAR(100)," +
            Property.COLUMN_DATA2 + " VACHAR(100)," +
            Property.COLUMN_DATA3 + " VACHAR(100)" + ")";
	
	public static final String CREATE_TABLE_APPINFO_SQL = 
			"CREATE TABLE IF NOT EXISTS " +
			Property.TABLE_APPINFO + "(" +
			Property.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		    Property.COLUMN_PACKAGE_NAME + " VACHAR(100)," +
		    Property.COLUMN_VERSION_CODE + " VACHAR(100)," +
		    Property.COLUMN_VERSION_NAME + " VACHAR(50)," +
		    Property.COLUMN_MD5+ " TEXT," +
		    Property.COLUMN_DATA1 + " VACHAR(100)," +
		    Property.COLUMN_DATA2 + " VACHAR(100)," +
		    Property.COLUMN_DATA3 + " VACHAR(100)" + ")";
	
	public static final String CREATE_TABLE_DEVICE_SQL = 
			"CREATE TABLE IF NOT EXISTS " +
			Property.TABLE_DEVICE + "(" +
			Property.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		    Property.COLUMN_DEVICEID + " VACHAR(100)," +
		    Property.COLUMN_DEVICE_NAME + " VACHAR(100)" + ")";
	
	public static final String CREATE_TABLE_QR_SQL = 
			"CREATE TABLE IF NOT EXISTS " +
			Property.TABLE_QR + "(" +
			Property.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		    Property.COLUMN_QR_URL + " TEXT," +
		    Property.COLUMN_UUID + " VACHAR(100)," +
		    Property.COLUMN_DATA1 + " VACHAR(100)," +
		    Property.COLUMN_DATA2 + " VACHAR(100)," +
		    Property.COLUMN_DATA3 + " VACHAR(100)" + ")";
	
}
