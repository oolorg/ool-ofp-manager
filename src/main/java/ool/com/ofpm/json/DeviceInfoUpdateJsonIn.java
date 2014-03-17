package ool.com.ofpm.json;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DeviceInfoUpdateJsonIn {
	private String deviceName;
	private DeviceInfo params;
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public DeviceInfo getParams() {
		return params;
	}
	public void setParams(DeviceInfo params) {
		this.params = params;
	}

	public static DeviceInfoUpdateJsonIn fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<DeviceInfoUpdateJsonIn>(){}.getType();
		return gson.fromJson(json, type);
	}
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<DeviceInfoUpdateJsonIn>(){}.getType();
		return gson.toJson(this, type);
	}
	@Override
	public String toString() {
		return toJson();
	}
}