package com.tcl.wechat.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密类
 * @author rex.lei
 *
 */
public class MD5Util {
	
	/**
	 * 获取MD5加密字符串
	 * @param key
	 * @return
	 */
	 public static String hashKeyForDisk(String key){
		 if (key == null){
			 return "";
	     }

		 String cacheKey;
		 try{
			 final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			 mDigest.update(key.getBytes());
			 cacheKey = bytesToHexString(mDigest.digest());
		 } catch (NoSuchAlgorithmException e)  {
			 cacheKey = String.valueOf(key.hashCode());
		 }
		 return cacheKey;
	}
	
	private static String bytesToHexString(byte[] bytes){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1){
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
}
