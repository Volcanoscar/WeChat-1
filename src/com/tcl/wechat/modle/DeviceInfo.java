package com.tcl.wechat.modle;

/**
 * 设备信息
 * @author rex.lei
 *
 */
public class DeviceInfo {
	private String deviceId;
	private String deviceName;
	
	private DeviceInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	private DeviceInfo(String deviceId, String deviceName) {
		super();
		this.deviceId = deviceId;
		this.deviceName = deviceName;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	@Override
	public String toString() {
		return "DeviceInfo [deviceId=" + deviceId + ", deviceName="
				+ deviceName + "]";
	}
}
