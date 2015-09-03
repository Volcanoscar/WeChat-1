package com.tcl.wechat.utils;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

public class WeakRefHandler<T> extends Handler
{
    private WeakReference<T> mRefContext;
    
    public WeakRefHandler(T context){
        mRefContext = new WeakReference<T>(context);
    }
    
    public T getContext(){
        return mRefContext.get();
    }
    
    @Override
    public void handleMessage(Message msg){

    }
}
