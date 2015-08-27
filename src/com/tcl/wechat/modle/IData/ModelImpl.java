package com.tcl.wechat.modle.IData;

import com.google.gson.Gson;

/**
 * 资源解析
 * @author rex.lei
 */
public class ModelImpl implements IModel{

	private Class<?> cls;
	
	public ModelImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ModelImpl(Class<?> cls) {
		this.cls = cls;
	}

	@Override
	public IData doParser(Object obj) {
		if (obj == null){
			return null;
		}
		IData data = null;
		Gson gson = new Gson();
		try {
			data = (IData) gson.fromJson(obj.toString(), cls);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
}
