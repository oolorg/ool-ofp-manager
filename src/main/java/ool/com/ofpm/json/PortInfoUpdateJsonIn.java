package ool.com.ofpm.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PortInfoUpdateJsonIn {
	private String deviceName;
	private String portName;
	private PortInfo params;

	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public PortInfo getParams() {
		return params;
	}
	public void setParams(PortInfo params) {
		this.params = params;
	}

	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this, new TypeToken<PortInfoUpdateJsonIn>(){}.getType());
	}
	public static PortInfoUpdateJsonIn fromJson(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, new TypeToken<PortInfoUpdateJsonIn>(){}.getType());
	}
	@Override
	public String toString() {
		return this.toJson();
	}
}
