package com.tcl.wechat.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;


public class HttpsUtils {
	private static String tag = "HttpsUtils";
	
	 public static String redirct(String url) {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			
			httpGet.getParams().setParameter( ClientPNames.HANDLE_REDIRECTS,false);
			httpGet.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"UTF-8");
			HttpResponse httpResponse = null;
			try {
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (null == httpResponse) {
				return url;
			}

			int statusCode = httpResponse.getStatusLine().getStatusCode();
			Log.d(tag, "statusCode="+statusCode);
			if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
					|| (statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
					|| (statusCode == HttpStatus.SC_SEE_OTHER)
					|| (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)) {

				String newUri = httpResponse.getLastHeader("Location").getValue();
				String result;
				try {
					result = new String(newUri.getBytes("ISO-8859-1"), "UTF-8");
					return result;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
			}
			Log.d(tag, "url="+url);
			return url;
		}

}
