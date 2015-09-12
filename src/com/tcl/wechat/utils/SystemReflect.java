package com.tcl.wechat.utils;

import java.lang.reflect.Method;

/**
 * 通过发射机制设置/获取系统属性
 * @author rex.lei
 *
 */
public class SystemReflect {

	private static final String ClassName = "android.os.SystemProperties";

	private static Class<?> SystemPropertiesClass = null;

	private static final String GetMethodName = "get";

	private static final String setMethodName = "set";

	public static String getProperties(String key, String defaultvalue) {
		try {
			SystemPropertiesClass = Class.forName(ClassName);
			Class<?> getType[] = new Class[2];
			getType[0] = String.class;
			getType[1] = String.class;
			Method method = SystemPropertiesClass.getMethod(GetMethodName,
					getType);
			Object Value[] = new Object[2];
			Value[0] = key;
			Value[1] = defaultvalue;
			Object receiver = new Object();
			Object returnVulue = method.invoke(receiver, Value);
			if (returnVulue != null) {
				return (String) returnVulue;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void setProperties(String key, String value) {
		try {
			SystemPropertiesClass = Class.forName(ClassName);
			Class<?> getType[] = new Class[2];
			getType[0] = String.class;
			getType[1] = String.class;
			Method method = SystemPropertiesClass.getMethod(setMethodName,
					getType);
			Object object[] = new Object[2];
			object[0] = key;
			object[1] = value;
			Object receiver = new Object();
			Object obj = method.invoke(receiver, object);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
