package com.tcl.wechat.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Http帮助类
 * @author Administrator
 *
 */
public class HttpUtil {
	
	 private static final int TIME_OUT = 8000;// ms

	 private static final int BUFFER_SIZE = 1024;

    /**
     * 获取下载文件的InputStream对象
     * @param urlStr 下载文件的地址
     * @return 返回InputStream文件对象
     */
	 public static InputStream GetInputStream(String urlStr){
		 try {
			 URL url = new URL(urlStr);
			 HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			 httpConn.setConnectTimeout(TIME_OUT);
			 httpConn.setReadTimeout(TIME_OUT);
			 httpConn.setRequestMethod("GET");
			 httpConn.setDoInput(true);
			 httpConn.setRequestProperty("Accept-Encoding", "identity");
			 InputStream inputStream = httpConn.getInputStream();
			 return inputStream;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return null;
	 }
	
	/**
	 * 下载数据
	 * @param urlStr
	 */
	public static void downloadData(String urlStr){
		
	}

	public static void downloadAndSaveFle(String urlStr, String savePath){
		
	}
}
