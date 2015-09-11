package com.tcl.wechat.utils;


import android.os.Handler;
import android.os.Looper;

import com.tcl.wechat.common.WeiConstant.CommandReturnType;


public class BaseUIHandler<T, AppAcitvity> extends Handler
{

	private T data;

	private String status = CommandReturnType.STATUS_SUCCESS;
	private AppAcitvity activity;

	public BaseUIHandler()
	{
		super();
	}

	public BaseUIHandler(Looper looper)
	{
		super(looper);
	}

	public BaseUIHandler(AppAcitvity appActivity)
	{
		super();
		this.activity = appActivity;
	}

	public AppAcitvity getActivity()
	{
		return activity;
	}

	public void setActivity(AppAcitvity activity)
	{
		this.activity = activity;
	}

	public T getData()
	{
		return data;
	}

	public void setData(T data)
	{
		this.data = data;
	}

	public synchronized String getStatus()
	{
		return status;
	}

	public synchronized void setStatus(String status)
	{
		this.status = status;
	}
}
