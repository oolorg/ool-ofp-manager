package ool.com.ofpm.json.device;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class PortInfo {
	private String portName;
	private int portNumber;

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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (this.getClass() != obj.getClass()) return false;
		PortInfo other = (PortInfo)obj;
		return (this.portName.equals(other.portName));
	}
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this, new TypeToken<PortInfo>(){}.getType());
	}
}
