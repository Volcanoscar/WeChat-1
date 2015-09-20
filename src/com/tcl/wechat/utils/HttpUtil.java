package com.tcl.wechat.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.util.HttpURLConnection;

import android.text.TextUtils;



/**
 * Http帮助类
 * @author Administrator
 *
 */
public class HttpUtil {
	
	/**
	 * 下载文件
	 * @param downloadUrl 下载文件url
	 * @param path 文件存储路径
	 * @return
	 */
	public static boolean download(String downloadUrl, String downPath){
		if (TextUtils.isEmpty(downloadUrl) || TextUtils.isEmpty(downPath)){
			return false;
		}
		
		File file = new File(downPath);
		if (file != null && !file.exists()){
			file.mkdirs();
		}
		
		// 根据地址创建URL对象(网络访问的url) 
		InputStream is = null;
		FileOutputStream fos = null;
		
        try {
			URL url = new URL(downloadUrl);
			
			// url.openConnection()打开网络链接  
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();  
            urlConnection.setRequestMethod("GET");// 设置请求的方式  
            urlConnection.setReadTimeout(5000);// 设置超时的时间  
            urlConnection.setConnectTimeout(5000);// 设置链接超时的时间  
            // 设置请求的头  
            urlConnection.setRequestProperty("User-Agent",  
                            "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
            // 获取响应的状态码 404 200 505 302  
           if (urlConnection.getResponseCode() == 200){
        	   // 获取响应的输入流对象  
               is = urlConnection.getInputStream();  
 
               // 创建字节输出流对象  
               fos = new FileOutputStream(file);
               // 定义读取的长度  
               int len = 0;  
               // 定义缓冲区  
               byte buffer[] = new byte[1024];  
               // 按照缓冲区的大小，循环读取  
               while ((len = is.read(buffer)) != -1) {  
                   // 根据读取的长度写入到os对象中  
            	   fos.write(buffer, 0, len);  
               }   
               fos.flush();
               return true;
           }
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			try {
				if (fos != null){
					fos.close();
				}
				if (is != null){
					is.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * 上传文件
	 * @param fileName 文件名称，绝对路径
	 * @param serverUrl 上传服务器地址
	 * @return
	 */
	public static boolean upload(String fileName, String serverUrl){
		if (TextUtils.isEmpty(fileName)){
			return false;
		}
		
		String end = "/r/n";
		String Hyphens = "--";
		String boundary = "*****";
		DataOutputStream ds = null;
		FileInputStream fStream = null;
		InputStream is = null;
		try {
			URL url = new URL(serverUrl);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			httpConn.setDoInput(true);
			httpConn.setDoOutput(true);
			httpConn.setUseCaches(false);
			/* 设定传送的method=POST */
			httpConn.setRequestMethod("POST");
			 /* setRequestProperty */
			httpConn.setRequestProperty("Connection", "Keep-Alive");
			httpConn.setRequestProperty("Charset", "UTF-8");
			httpConn.setRequestProperty("Content-Type",
		          "multipart/form-data;boundary=" + boundary);
			
			/* 设定DataOutputStream */
		    ds = new DataOutputStream(httpConn.getOutputStream());
		    ds.writeBytes(Hyphens + boundary + end);
		    
		    ds.writeBytes("Content-Disposition: form-data; name=\"" + 
		            fileName + "\"; filename=\"" + fileName + "\"" + end);
		    ds.writeBytes(end);
		    /* 取得文件的FileInputStream */
		    fStream = new FileInputStream(fileName);
		    /* 设定每次写入1024bytes */
		    int bufferSize = 1024;
		    byte[] buffer = new byte[bufferSize];
		    int length = -1;
		    /* 从文件读取数据到缓冲区 */
		    while ((length = fStream.read(buffer)) != -1){
		    	/* 将数据写入DataOutputStream中 */
		        ds.write(buffer, 0, length);
		    }
		    ds.writeBytes(end);
		    ds.writeBytes(Hyphens + boundary + Hyphens + end);
		    fStream.close();
		    ds.flush();
		    
		    /* 取得Response内容 */
		    is = httpConn.getInputStream();
		    int ch;
		    StringBuffer b = new StringBuffer();
		    while ((ch = is.read()) != -1){
		    	b.append((char) ch);
		    }
		    return true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (is != null){
					is.close();
				}
				if (fStream != null){
					fStream.close();
				}
				if (ds != null){
					ds.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	 
}
