package com.tcl.wechat.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tcl.wechat.modle.User;
import com.tcl.wechat.modle.UserList;
import com.tcl.wechat.modle.IData.IData;
import com.tcl.wechat.modle.IData.ModelImpl;

/**
 * 用户信息获取帮助类
 * @author rex.lei
 *
 */
public class UserHelper {
	
	private static final String TAG = UserHelper.class.getSimpleName();
	
	private Context mContext;
	private UserHelper mInstance;
	
	private UserHelper(Context mContext) {
		super();
		this.mContext = mContext;
	}
	
	public UserHelper getInstance(Context context){
		if (mInstance == null){
			mInstance = new UserHelper(context);
		}
		return mInstance;
	}
	
	/**
	 * 获取所有用户信息
	 * @param context
	 * @return
	 */
	public static ArrayList<User> getAllUsers(Context context){
		ArrayList<User> users = new ArrayList<User>();
		
		String jsonData = getJsonData(context);
		if (TextUtils.isEmpty(jsonData)){
			Log.w(TAG, "user json data is NULL!!");
			return null;
		}
		
		ModelImpl mode = new ModelImpl(UserList.class);
		IData data = mode.doParser(jsonData);
		if (data instanceof UserList){
			users = ((UserList)data).getUserList();
		}
		return users;
	}
	
	/**
	 * 获取用户Json数据
	 * @return
	 */
	public static String getJsonData(Context context){
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					context.getAssets().open("userdata.json")));
			
			StringBuffer buffer = new StringBuffer();
			String strLen;
			while ((strLen = reader.readLine()) != null) {
				buffer.append(strLen);
			}
			if (buffer != null){
				Log.i(TAG, "buffer:" + buffer.toString());
				return buffer.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null){
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	
}
