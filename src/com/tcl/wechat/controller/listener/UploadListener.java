package com.tcl.wechat.controller.listener;

/**
 * 文件上传监听
 * @author rex.lei
 *
 */
public interface UploadListener {
	
	/**
	 * 上传文件结果回调
	 * @param result 服务器响应结果
	 */
	public void onResult(String result);
	
	/**
	 * 文件上传进度
	 * @param progress 进度
	 */ 
	public void onProgressUpdate(int progress);
	
	/**
	 * 上传错误结果
	 * @param errorCode 错误码
	 */
	public void onError(int errorCode);

}
