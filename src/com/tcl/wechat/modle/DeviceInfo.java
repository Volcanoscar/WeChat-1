package com.tcl.wechat.modle;

/**
 * 设备信息
 * @author rex.lei
 *
 */
public class DeviceInfo {
	private String deviceId;
	private String macAddr;
	private String memberId;
	
	public DeviceInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DeviceInfo(String deviceId, String macAddr, String memberId) {
		super();
		this.deviceId = deviceId;
		this.macAddr = macAddr;
		this.memberId = memberId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getMacAddr() {
		return macAddr;
	}

	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	@Override
	public String toString() {
		return "DeviceInfo [deviceId=" + deviceId + ", macAddr=" + macAddr
				+ ", memberId=" + memberId + "]";
	}
}
