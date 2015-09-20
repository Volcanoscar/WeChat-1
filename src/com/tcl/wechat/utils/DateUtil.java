package com.tcl.wechat.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	private static String format = "yyyy-MM-dd";
	
	
	public DateUtil() {
		super();
	}

	public DateUtil(String format) {
		super();
		this.format = format;
	}
	
	
	/**
	 * 日期转为字符串  
	 * @param date
	 * @return
	 */
    public static String ConverToString(Date date){  
        DateFormat df = new SimpleDateFormat(format);  
          
        return df.format(date);  
    }  
    /**
     * 字符串转为日期  
     * @param strDate
     * @return
     */
    public static Date ConverToDate(String strDate) {  
        try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
			return df.parse(strDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        return null;
    }  
    
    public static String getTime(String time){
    	int start = time.lastIndexOf(":");
		int end = time.length();
		String second = time.substring(start + 1, end);
		time = time.substring(0, start);
		start = time.lastIndexOf(":");
		end = time.length();
		String min = time.substring(start + 1, end);
		return min + ":" + second;
    }

}
