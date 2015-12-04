package com.tcl.wechat.utils;

public class TimeInfo{
	
	private long startTime;//开始时间
	  
	private long endTime;  //结束时间
	
	public TimeInfo() {
		super();
	}
  
	public long getStartTime() {
		return startTime;
	}
	
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public long getEndTime() {
		return endTime;
	}
	
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return "TimeInfo [startTime=" + startTime + ", endTime=" + endTime
				+ "]";
	}
}