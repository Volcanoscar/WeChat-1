package com.tcl.wechat.xmpp;

/**
 * Xmpp服务管理类接口
 * 		以下动作均为TV端操作
 * @author rex.lei
 *
 */
public interface WeiXmppManagerImpl {

	/**
	 * 注册
	 */
	public void register();
	
	/**
	 * 登录
	 */
	public void login();
	
	/**
	 * 再次登录
	 */
	public void reLogin();
	
	/**
	 * 绑定
	 */
	public void bind();
	
	/**
	 * 解绑
	 */
	public void unbind();
}
