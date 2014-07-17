package ool.com.ofpm.json.device;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class PortInfo {
	private String portName;
	private int portNumber;
	private String deviceName;
	private String type;

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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (this.getClass() != obj.getClass()) return false;
		PortInfo other = (PortInfo)obj;
		if (! this.deviceName.equals(other.deviceName)) return false;
		return (this.portName.equals(other.portName));
	}
	@Override
	public int hashCode() {
		return this.deviceName.hashCode();
	}
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this, new TypeToken<PortInfo>(){}.getType());
	}
}
