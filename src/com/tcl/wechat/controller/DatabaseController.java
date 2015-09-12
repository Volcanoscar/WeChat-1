package com.tcl.wechat.controller;

import java.sql.Date;

import android.content.Context;
import android.util.Log;

import com.tcl.wechat.db.AppInfoDao;
import com.tcl.wechat.db.DeviceDao;
import com.tcl.wechat.db.WeiQrDao;
import com.tcl.wechat.db.WeiRecordDao;
import com.tcl.wechat.db.WeiUserDao;

/**
 * 数据库Controller
 * @author rex.lei
 *
 */
public class DatabaseController {
	
	private static final String TAG = DatabaseController.class.getSimpleName();

	private Context mContext;
	private static DatabaseController mController;
	
	private DatabaseController(Context context) {
		super();
		mContext = context;
	}
	
	/**
	 * 获取DatabaseController单例
	 * @param context
	 * @return
	 */
	public static DatabaseController getController(Context context){
		if (mController == null){
			synchronized (DatabaseController.class) {
				if (mController == null){
					mController = new DatabaseController(context);
				}
			}
		}
		return mController;
	}
	
	/**
	 * 初始化数据库
	 */
	public void initDataBase(){
		long start = 0, end = 0;
		start = System.currentTimeMillis();
		Log.d(TAG, "initDataBase start :" + new Date(start));
		
		AppInfoDao.initAppDao(mContext);
		DeviceDao.initDeviceDao(mContext);
		WeiQrDao.initWeiUserDao(mContext);
		WeiUserDao.initWeiUserDao(mContext);
		WeiRecordDao.initWeiRecordDao(mContext);
		
		end = System.currentTimeMillis();
		Log.d(TAG, "initDataBase end :" + new Date(end) + ",Take time：" + (end - start));
	}
		
	
}
