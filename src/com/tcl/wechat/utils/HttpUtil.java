package com.tcl.wechat.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.util.HttpURLConnection;

import android.text.TextUtils;
import android.util.Log;



/**
 * Http帮助类
 * @author Administrator
 *
 */
public class HttpUtil {
	
	private static final String TAG = HttpUtil.class.getSimpleName();
	
	/**
	 *  上传多媒体文件路径
	 */
	public static String UPLOAD_MEDIA_URL = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
	
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
	
	public static void upload2(final String accesstoken, final String type, final String filePath){
		
		Log.i(TAG, "upload->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.i(TAG, "1111accesstoken:" + accesstoken + ", type:"  + type + ", filePath:" + filePath);
//				upload1(accesstoken, type, filePath);
			}
		}).start();
		
	}
	
	/**
	 * 上传多媒体文件
	 * @param access_token token
	 * @param type 聊天文件类型
	 * @param filePath 文件节点路径
	 */
	public static void upload(String accesstoken, String type, String filePath) {
		
		if (TextUtils.isEmpty(accesstoken) || TextUtils.isEmpty(filePath)){
			return ;
		}
		
		Log.i(TAG, "2222accesstoken:" + accesstoken + ", type:"  + type + ", filePath:" + filePath);
		
		File file = new File(filePath);
		HttpClient client = new HttpClient();
		
		String uploadurl = UPLOAD_MEDIA_URL.replace("ACCESS_TOKEN", accesstoken).replace("TYPE", type);
		PostMethod post = new PostMethod(uploadurl);
		post.setRequestHeader("User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:30.0) Gecko/20100101 Firefox/30.0");
		post.setRequestHeader("Host", "file.api.weixin.qq.com");
		post.setRequestHeader("Connection", "Keep-Alive");
		post.setRequestHeader("Cache-Control", "no-cache");
		try {
			Log.i(TAG, "file:"  + file);
			Log.i(TAG, "file:"  + file.exists());
			if (file != null && file.exists()) {
				String contentType = "";
				// 图片(image):1M,支持JPG格式 缩略图（thumb）：64KB，支持JPG格式
				if (filePath.endsWith(".jpg") || filePath.endsWith(".JPG")) {
					contentType = "image/jpeg";
					// 语音 voice:2M，播放长度不超过60s，支持AMR\MP3格式
				} else if (filePath.endsWith(".mp3")) {
					contentType = "audio/mp3";
				} else if (filePath.endsWith(".amr")) {
					contentType = "voice/amr";
					// 视频(video):10MB,支持MP4格式
				} else if (filePath.endsWith(".mp4")) {
					contentType = "video/mpeg4";
				} else {
					contentType = "application/octet-stream";
				}
				
				
				Log.i(TAG, "contentType:" + contentType);
				
				
				FilePart filepart = new FilePart("media", file, contentType, "UTF-8");
				Part[] parts = new Part[] { filepart };
				MultipartRequestEntity entity = new MultipartRequestEntity(parts, post.getParams());
				post.setRequestEntity(entity);
				
				
				Log.i(TAG, "start executeMethod!!!");
				
				int rescok = client.executeMethod(post);
				
				Log.i(TAG, "rescok:" + rescok);
				
				Log.i(TAG, "post:" + post);
				
				if (client.executeMethod(post) == HttpStatus.SC_OK) {
					
					
					
					String responseContent = post.getResponseBodyAsString();
					
					Log.i(TAG, "responseContent:" + responseContent);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 
}
