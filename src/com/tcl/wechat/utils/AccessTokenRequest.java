package com.tcl.wechat.utils;

import java.util.Map;

import com.android.http.RequestManager;
import com.android.http.RequestManager.RequestListener;
import com.tcl.wechat.common.Config;

/**
 * AccessToken请求类
 * @author rex.lei
 *
 */
public class AccessTokenRequest {
	
	public interface TokenRequestListener{
		public void onSuccess(String response, Map<String, String> arg1, String arg2, int arg3);
		public void onRequest();
		public void onError(String arg0, String arg1, int arg2);
	}
	
	private AccessTokenRequest() {
		super();
	}
	
	private static class Instance{
		private static final AccessTokenRequest mInstance = new AccessTokenRequest();
	}
	
	public static AccessTokenRequest getInstance(){
		return Instance.mInstance;
	}
	
	public void get(final TokenRequestListener listener){
		RequestManager.getInstance().get(Config.URL_ACCESS_TOKEN, new RequestListener() {

			@Override
			public void onSuccess(String response,Map<String, String> arg1, String arg2, int arg3) {
				listener.onSuccess(response, arg1, arg2, arg3);
			}

			@Override
			public void onRequest() {
				listener.onRequest();
			}

			@Override
			public void onError(String arg0, String arg1, int arg2) {
				listener.onError(arg0, arg1, arg2);
			}
		}, 0);
	}
}
