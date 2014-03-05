package ool.com.ofpm.json;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PortInfoJsonInOut {
	private String portName;
	private int portNumber;
	private String deviceName;
	private String type;

	public static PortInfoJsonInOut fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<PortInfoJsonInOut>(){}.getType();
		return gson.fromJson(json, type);
	}
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<PortInfoJsonInOut>(){}.getType();
		return gson.toJson(this, type);
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
