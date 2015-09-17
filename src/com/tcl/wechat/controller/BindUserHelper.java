package com.tcl.wechat.controller;

/**
 * 用户帮助类
 * @author rex.lei
 *
 */
public class BindUserHelper {
	
	private static class BindUserHelperInstance{
		private static final BindUserHelper mInstance = new BindUserHelper();
	}

	private BindUserHelper() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public BindUserHelper getInstance(){
		return BindUserHelperInstance.mInstance;
	}
	
	
}
