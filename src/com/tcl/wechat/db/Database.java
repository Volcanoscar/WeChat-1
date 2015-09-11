package com.tcl.wechat.db;

import android.net.Uri;

public class Database {

    public static final Uri CONTENT_URI  = Uri.parse("content://com.tcl.webchat.MyContentProvider/QR_URL");
    public static final Uri CONTENT_USER  = Uri.parse("content://com.tcl.webchat.MyContentProvider/USER");
    public static final Uri CONTENT_RECORD  = Uri.parse("content://com.tcl.webchat.MyContentProvider/RECORD");
   //三个表名
    public static final String QR_TABLE_NAME = "qrinfo";
    public static final String USER_TABLE_NAME = "weibinderuser";
    public static final String RECORD_TABLE_NAME = "weiuserrecord";
    // 表数据列
    public static final String  QR_KEY  = "url";
    public static final String  UUID_KEY  = "uuid";
	
}
