package ool.com.ofpm.json.common;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Node implements Cloneable {
	private String deviceName;
	private String deviceType;

	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	@Override
	public Node clone() {
		Node newNode = new Node();
		newNode.deviceName = this.deviceName;
		newNode.deviceType = this.deviceType;
		return newNode;
	}
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(this.getClass() != obj.getClass()) return false;
		Node other = (Node)obj;
		return (this.deviceName.equals(other.deviceName));
	}
	@Override
	public int hashCode() {
		if(this.deviceName == null) return 0;
		return this.deviceName.hashCode();
	}
	@Override
	public String toString() {
		Gson gson = new Gson();
		Type type = new TypeToken<Node>() {}.getType();
		return gson.toJson(this, type);
	}
}
