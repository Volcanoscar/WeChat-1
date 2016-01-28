package com.tcl.wechat.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.tcl.wechat.WeApplication;

import android.content.ContentResolver;
import android.widget.Toast;

/**
 * 日期工具类
 * 
 * @author rex.lei
 * 
 */
public class DateUtils {
	
	
	private static String mTimeFormat = "HH:mm";
	
	private static SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm");

	public static void initDataFormat() {
		ContentResolver cv = WeApplication.getContext().getContentResolver();
		String strTimeFormat = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.TIME_12_24);

		if (strTimeFormat.equals("24")){
			mTimeFormat = "HH:mm";
			mFormat = new SimpleDateFormat("HH:mm"); 
		} else {
			mTimeFormat = "hh:mm";
			mFormat = new SimpleDateFormat("hh:mm");
		}
	}

	public static String getTimeShort(String time) {
		try {
			return mFormat.format(new Date(Long.parseLong(time)));
		} catch (Exception e){
			e.printStackTrace();
			return "";
		}
	}
	   

	public static String getTimestampString(Date paramDate) {
		String str = null;
		long l = paramDate.getTime();
		if (isSameDay(l)) {
			Calendar localCalendar = GregorianCalendar.getInstance();
			localCalendar.setTime(paramDate);
			int i = localCalendar.get(11);
			if (i > 17)
				str = "晚上 " + mTimeFormat;
			else if ((i >= 0) && (i <= 6))
				str = "凌晨 " + mTimeFormat;
			else if ((i > 11) && (i <= 17))
				str = "下午 " + mTimeFormat;
			else
				str = "上午 " + mTimeFormat;
		} else if (isYesterday(l)) {
			str = "昨天 " + mTimeFormat;
		} else {
			str = "M月d日 " + mTimeFormat;
		}
		return new SimpleDateFormat(str, Locale.CHINA).format(paramDate);
	}

	public static boolean isCloseEnough(long paramLong1, long paramLong2) {
		long l = paramLong1 - paramLong2;
		if (l < 0L)
			l = -l;
		return l < 30000L;
	}

	private static boolean isSameDay(long paramLong) {
		TimeInfo localTimeInfo = getTodayStartAndEndTime();
		return (paramLong > localTimeInfo.getStartTime())
				&& (paramLong < localTimeInfo.getEndTime());
	}

	private static boolean isYesterday(long paramLong) {
		TimeInfo localTimeInfo = getYesterdayStartAndEndTime();
		return (paramLong > localTimeInfo.getStartTime())
				&& (paramLong < localTimeInfo.getEndTime());
	}

	public static Date StringToDate(String paramString1, String paramString2) {
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				paramString2);
		Date localDate = null;
		try {
			localDate = localSimpleDateFormat.parse(paramString1);
		} catch (ParseException localParseException) {
			localParseException.printStackTrace();
		}
		return localDate;
	}

	public static String toTime(int paramInt) {
		paramInt /= 1000;
		int i = paramInt / 60;
		int j = 0;
		if (i >= 60) {
			j = i / 60;
			i %= 60;
		}
		int k = paramInt % 60;
		return String.format("%02d:%02d", new Object[] { Integer.valueOf(i),
				Integer.valueOf(k) });
	}

	public static String toTimeBySecond(int paramInt) {
		int i = paramInt / 60;
		int j = 0;
		if (i >= 60) {
			j = i / 60;
			i %= 60;
		}
		int k = paramInt % 60;
		return String.format("%02d:%02d", new Object[] { Integer.valueOf(i),
				Integer.valueOf(k) });
	}

	public static TimeInfo getYesterdayStartAndEndTime() {
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.add(5, -1);
		localCalendar1.set(11, 0);
		localCalendar1.set(12, 0);
		localCalendar1.set(13, 0);
		localCalendar1.set(14, 0);
		Date localDate1 = localCalendar1.getTime();
		long l1 = localDate1.getTime();
		Calendar localCalendar2 = Calendar.getInstance();
		localCalendar2.add(5, -1);
		localCalendar2.set(11, 23);
		localCalendar2.set(12, 59);
		localCalendar2.set(13, 59);
		localCalendar2.set(14, 999);
		Date localDate2 = localCalendar2.getTime();
		long l2 = localDate2.getTime();
		TimeInfo localTimeInfo = new TimeInfo();
		localTimeInfo.setStartTime(l1);
		localTimeInfo.setEndTime(l2);
		return localTimeInfo;
	}

	public static TimeInfo getTodayStartAndEndTime() {
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.set(11, 0);
		localCalendar1.set(12, 0);
		localCalendar1.set(13, 0);
		localCalendar1.set(14, 0);
		Date localDate1 = localCalendar1.getTime();
		long l1 = localDate1.getTime();
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss S");
		Calendar localCalendar2 = Calendar.getInstance();
		localCalendar2.set(11, 23);
		localCalendar2.set(12, 59);
		localCalendar2.set(13, 59);
		localCalendar2.set(14, 999);
		Date localDate2 = localCalendar2.getTime();
		long l2 = localDate2.getTime();
		TimeInfo localTimeInfo = new TimeInfo();
		localTimeInfo.setStartTime(l1);
		localTimeInfo.setEndTime(l2);
		return localTimeInfo;
	}

	public static TimeInfo getBeforeYesterdayStartAndEndTime() {
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.add(5, -2);
		localCalendar1.set(11, 0);
		localCalendar1.set(12, 0);
		localCalendar1.set(13, 0);
		localCalendar1.set(14, 0);
		Date localDate1 = localCalendar1.getTime();
		long l1 = localDate1.getTime();
		Calendar localCalendar2 = Calendar.getInstance();
		localCalendar2.add(5, -2);
		localCalendar2.set(11, 23);
		localCalendar2.set(12, 59);
		localCalendar2.set(13, 59);
		localCalendar2.set(14, 999);
		Date localDate2 = localCalendar2.getTime();
		long l2 = localDate2.getTime();
		TimeInfo localTimeInfo = new TimeInfo();
		localTimeInfo.setStartTime(l1);
		localTimeInfo.setEndTime(l2);
		return localTimeInfo;
	}

	public static TimeInfo getCurrentMonthStartAndEndTime() {
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.set(5, 1);
		localCalendar1.set(11, 0);
		localCalendar1.set(12, 0);
		localCalendar1.set(13, 0);
		localCalendar1.set(14, 0);
		Date localDate1 = localCalendar1.getTime();
		long l1 = localDate1.getTime();
		Calendar localCalendar2 = Calendar.getInstance();
		Date localDate2 = localCalendar2.getTime();
		long l2 = localDate2.getTime();
		TimeInfo localTimeInfo = new TimeInfo();
		localTimeInfo.setStartTime(l1);
		localTimeInfo.setEndTime(l2);
		return localTimeInfo;
	}

	public static TimeInfo getLastMonthStartAndEndTime() {
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.add(2, -1);
		localCalendar1.set(5, 1);
		localCalendar1.set(11, 0);
		localCalendar1.set(12, 0);
		localCalendar1.set(13, 0);
		localCalendar1.set(14, 0);
		Date localDate1 = localCalendar1.getTime();
		long l1 = localDate1.getTime();
		Calendar localCalendar2 = Calendar.getInstance();
		localCalendar2.add(2, -1);
		localCalendar2.set(5, 1);
		localCalendar2.set(11, 23);
		localCalendar2.set(12, 59);
		localCalendar2.set(13, 59);
		localCalendar2.set(14, 999);
		localCalendar2.roll(5, -1);
		Date localDate2 = localCalendar2.getTime();
		long l2 = localDate2.getTime();
		TimeInfo localTimeInfo = new TimeInfo();
		localTimeInfo.setStartTime(l1);
		localTimeInfo.setEndTime(l2);
		return localTimeInfo;
	}

	public static String getTimestampStr() {
		return Long.toString(System.currentTimeMillis());
	}
}

/*
 * Location: C:\Users\senge.zhu\Desktop\ Qualified Name:
 * com.easemob.util.DateUtils JD-Core Version: 0.6.1
 */