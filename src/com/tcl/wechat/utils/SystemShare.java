package com.tcl.wechat.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 本地数据存储帮助类
 * @author rex.lei
 *
 */
public class SystemShare {
	
	private static class SystemShareInstance{
		private static final SystemShare mInstanace = new SystemShare();
	}
	
	private SystemShare() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public static SystemShare getInstance(){
		return SystemShareInstance.mInstanace;
	}
	
	public static class SharedEditer {
		
		private SharedPreferences mPreferences = null;
		private SharedPreferences.Editor mEditor = null;
		private static final String shareFildName = "detaultPreferences";
		
		private SharedEditer() {
			super();
			// TODO Auto-generated constructor stub
		}

		public SharedEditer(Context context){
			this(context, shareFildName);
		}
		
		public SharedEditer(Context context, String fileName) {
			super();
			mPreferences = context.getSharedPreferences(fileName, 
					Context.MODE_PRIVATE);
			mEditor = mPreferences.edit();
		}

		public SharedEditer putString(String key, String value){
			mEditor.putString(key, value);
			mEditor.commit();
			return this;
		}
		
		public SharedEditer putInt(String key, int value){
			mEditor.putInt(key, value);
			mEditor.commit();
			return this;
			
		}
		
		public SharedEditer putLong(String key, long value){
			mEditor.putLong(key, value);
			mEditor.commit();
			return this;
		}
		
		public SharedEditer putFloat(String key, float value){
			mEditor.putFloat(key, value);
			mEditor.commit();
			return this;
		}
		
		public SharedEditer putBoolean(String key, boolean value){
			mEditor.putBoolean(key, value);
			mEditor.commit();
			return this;
		}
		
		public String getString(String key, String defValue){
			return mPreferences.getString(key, defValue);
		}
		
		public int getInt(String key, int defValue){
			return mPreferences.getInt(key, defValue);
		}
		
		public long getLong(String key, long defValue){
			return mPreferences.getLong(key, defValue);
		}
		
		public float getFloat(String key, float defValue){
			return mPreferences.getFloat(key, defValue);
		}
		
		public boolean getBoolean(String key, boolean defValue){
			return mPreferences.getBoolean(key, defValue);
		}
		
		public boolean contains(String key){
			return mPreferences.contains(key);
		}
		
		public void remove(String key){
			mEditor.remove(key);
			mEditor.commit();
		}
	}
}
