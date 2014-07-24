package ool.com.ofpm.json.device;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class PortInfo implements Cloneable {
	private String portName;
	private int portNumber;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (this.getClass() != obj.getClass()) return false;
		PortInfo other = (PortInfo)obj;
		if (!StringUtils.equals(other.portName,  this.portName)) return false;
		if (other.portNumber != this.portNumber) return false;
		return true;
	}
	@Override
	public int hashCode() {
		int hash = this.portNumber;
		if (this.portName != null) {
			hash += this.portName.hashCode();
		}
		return hash;
	}
	@Override
	public PortInfo clone() {
		PortInfo newObj;
		try {
			newObj = (PortInfo)super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
		newObj.portNumber = this.portNumber;
		if (this.portName != null) {
			newObj.portName = new String(this.portName);
		}
		return newObj;
	}
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this, new TypeToken<PortInfo>(){}.getType());
	}

	/* Setters and Getters */
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
}
