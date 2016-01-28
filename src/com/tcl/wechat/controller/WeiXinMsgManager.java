package com.tcl.wechat.controller;

import java.util.Collection;
import java.util.LinkedList;

import com.tcl.wechat.common.IConstant.ChatMsgStatus;
import com.tcl.wechat.database.WeiRecordDao;
import com.tcl.wechat.model.WeiXinMessage;

/**
 * 聊天界面消息管理类
 * @author rex.lei
 *
 */
public class WeiXinMsgManager {
	
	/**
	 * 消息存储实体类 
	 */
	private LinkedList<WeiXinMessage> mAllMessage = new LinkedList<WeiXinMessage>();
	
	private static class ChatMsgManagerInstannce{
		private static final WeiXinMsgManager mInstance = new WeiXinMsgManager();
	}
	
	private WeiXinMsgManager() {
		super();
	}

	public static WeiXinMsgManager getInstance(){
		return ChatMsgManagerInstannce.mInstance;
	}
	
	/**
	 * 获取消息
	 * @param position 消息编号
	 * @return
	 */
	public WeiXinMessage getMessage(int position){
		return mAllMessage.get(position);
	}
	
	/**
	 * 添加消息
	 * @param message
	 */
	public void addMessage(WeiXinMessage message){
		mAllMessage.add(message);
	}
	
	/**
	 * 删除消息
	 * @param position 消息编号
	 */
	public void deleteMessage(int position){
		if (mAllMessage == null || mAllMessage.isEmpty()){
			return ;
		}
		if (position < 0 || position >= mAllMessage.size()){
			return ;
		}
		mAllMessage.remove(position);
	}
	
	public void deleteAllMessage(){
		mAllMessage.clear();
	}
	
	/**
	 * 获取消息总条数
	 * @return
	 */
	public int getMessageCount(){
		return mAllMessage.size();
	}
	
	/**
	 * 加载消息
	 * @param openid 指定用户的openid
	 */
	public synchronized void loadMessage( String openid){
		LinkedList<WeiXinMessage> datas = WeiRecordDao.getInstance().getUserRecorder(
				0, openid);
		if (datas == null || datas.isEmpty()){
			return ;
		}
		mAllMessage.addAll(datas);
	}
	
	/**
	 * 获取所有未读消息
	 * @param lastMsgTime
	 * @param openid
	 */
	public synchronized void loadAllUnreadMessage(String lastMsgTime, String openid){
		LinkedList<WeiXinMessage> datas = WeiRecordDao.getInstance().getUnreadRecorder(
				lastMsgTime, openid);
		if (datas == null || datas.isEmpty()){
			return ;
		}
		mAllMessage.addAll(datas);
	}
	
	/**
	 * 添加消息
	 * @param messages
	 */
	public void addMessage(Collection<? extends WeiXinMessage> messages){
		if (messages == null || messages.isEmpty()){
			return ;
		}
		mAllMessage.addAll(0, messages);
	}
	
	/**
	 * 设置消息状态 
	 * @param position 消息编号
	 * @param status 消息状态
	 */
	public void setMessageStatus(int position, String status){
		
		if (position < 0 || position > mAllMessage.size()){
			return ;
		}
		
		WeiXinMessage message = mAllMessage.get(position);
		if (message != null){
			message.setStatus(status);
			mAllMessage.remove(position);
			mAllMessage.add(position, message);
			WeiRecordDao.getInstance().updateMessageState(message.getMsgid(), status);
		}
	}
	
	/**
	 * 更新消息状态
	 * @param msgid
	 * @param status
	 */
	public void setMessageStatus(String msgid, String status){
		int size = getMessageCount();
		for (int i = 0; i < size; i++) {
			WeiXinMessage message = mAllMessage.get(i);
			if (msgid.equals(message.getMsgid())){
				mAllMessage.remove(i);

				message.setStatus(status);
				mAllMessage.add(i, message);
				break;
			}
		}
	}
	
	/**
	 * 设置消息url
	 * @param position 消息编号
	 * @param url
	 */
	public void setMessageUrl(int position, String url){
		if (position < 0 || position > mAllMessage.size()){
			return ;
		}
		
		WeiXinMessage message = mAllMessage.get(position);
		if (message != null){
			message.setUrl(url);
			mAllMessage.remove(position);
			mAllMessage.add(position, message);
			WeiRecordDao.getInstance().updateRecorderUrl(message.getMsgid(), url);
		}
	}
	
	/**
	 * 设置消息Url
	 * @param msgid
	 * @param url
	 */
	public void setMessageUrl(String msgid, String url){
		int size = getMessageCount();
		for (int i = 0; i < size; i++) {
			WeiXinMessage message = mAllMessage.get(i);
			if (msgid.equals(message.getMsgid())){
				message.setStatus(ChatMsgStatus.SUCCESS);
				message.setUrl(url);
				mAllMessage.remove(i);
				mAllMessage.add(i, message);
				WeiRecordDao.getInstance().updateRecorderUrl(message.getMsgid(), url);
				break;
			}
		}
	}
}
