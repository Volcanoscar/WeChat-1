package com.tcl.wechat.test.interf;

import java.util.ArrayList;

import com.tcl.wechat.modle.UserRecord;

public interface RecordImpl {

	/**
	 * 获取消息记录个数
	 * @param openId 用户标识Id
	 * @return 消息记录个数
	 */
	public int getRecordCount(String openId);
	
	/**
	 * 增加一条记录
	 * @param record 消息记录
	 * @return
	 */
	public boolean addRecord(UserRecord record);
	
	/**
	 * 获取所有用户消息记录
	 * @param openId
	 * @return
	 */
	public ArrayList<UserRecord> getAllUserRecord(String openId);
	
	/**
	 * 删除一条记录
	 * @param openId 用户标识Id
	 * @return
	 */
	public boolean deleteRecord(String openId);
	
	/**
	 * 删除记录
	 * @param openId 用户标识Id
	 * @param createTime 条件
	 * @return
	 */
	public boolean deleteRecord(String openId, long createTime);
	
}
