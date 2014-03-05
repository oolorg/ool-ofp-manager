package ool.com.ofpm.json;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DeviceInfoJsonInOut {
	private String deviceName;
	private String deviceType;
	private boolean ofpFlag;

	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(
			String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(
			String deviceType) {
		this.deviceType = deviceType;
	}
	public boolean isOfpFlag() {
		return ofpFlag;
	}
	public void setOfpFlag(boolean ofpFlag) {
		this.ofpFlag = ofpFlag;
	}

	public static DeviceInfoJsonInOut fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<DeviceInfoJsonInOut>(){}.getType();
		return gson.fromJson(json, type);
	}
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<DeviceInfoJsonInOut>(){}.getType();
		return gson.toJson(this, type);
	}
}
