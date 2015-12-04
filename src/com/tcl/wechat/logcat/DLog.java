package com.tcl.wechat.logcat;

import android.util.Log;



/**
 * 打印信息统一管理类
 * @author rex.lei
 *
 */
public class DLog {
	
	public static int LOG_LEVEL = 0;
	
	/**
     * Priority constant for the println method; use DLog.v.
     */
	public static int VERBOSE = 1;
	
	/**
     * Priority constant for the println method; use DLog.d.
     */
	public static int DEBUG = 2;
	
	 /**
     * Priority constant for the println method; use DLog.i.
     */
	public static int INFO = 3;
	
	/**
     * Priority constant for the println method; use DLog.w.
     */
	public static int WARN = 4;
	
	
	/**
     * Priority constant for the println method; use DLog.e.
     */
	public static int ERROR = 5;
	
	private DLog() {
    }
	
	/**
     * Send a {@link #VERBOSE} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
	public static void v(String tag,String msg){
		if(VERBOSE > LOG_LEVEL){
			Log.v(tag, msg);
		}
	}
	

	/**
     * Send a {@link #DEBUG} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
	public static void d(String tag,String msg){
		if(DEBUG > LOG_LEVEL)
			Log.d(tag, msg);
	}
	
	/**
     * Send an {@link #INFO} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
	public static void i(String tag,String msg){
		if(INFO > LOG_LEVEL){
			Log.i(tag, msg);
		}
	}

	/**
     * Send a {@link #WARN} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
	public static void w(String tag,String msg){
		if(WARN > LOG_LEVEL){
			Log.w(tag, msg);
		}
	}
	
	/**
     * Send an {@link #ERROR} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
	public static void e(String tag,String msg){
		if(ERROR > LOG_LEVEL ){
			Log.e(tag, msg);
		}
	 }
}
