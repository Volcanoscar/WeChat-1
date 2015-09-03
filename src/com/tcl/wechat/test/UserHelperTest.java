package com.tcl.wechat.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.tcl.wechat.modle.User;
import com.tcl.wechat.modle.UserList;
import com.tcl.wechat.modle.IData.IData;
import com.tcl.wechat.modle.IData.ModelImpl;

import android.test.AndroidTestCase;
import android.text.TextUtils;
import android.util.Log;

public class UserHelperTest extends AndroidTestCase {
	
	private static final String TAG = UserHelperTest.class.getSimpleName();
	
	public void test(){
		Log.i(TAG, "test sucessfull!!");
	}
	
	/**
	 * 获取所有用户信息
	 * @return
	 */
	public ArrayList<User> getAllUsers(){
		
		ArrayList<User> users = new ArrayList<User>();
		
		String jsonData = getJsonData();
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
	public String getJsonData(){
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					getContext().getAssets().open("userdata.json")));
			
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
