package ool.com.ofpm.json.topology.logical;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LogicalLink implements Cloneable {
	private List<String> deviceName = new ArrayList<String>();

	public List<String> getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(List<String> deviceName) {
		this.deviceName = deviceName;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(this.getClass() != obj.getClass()) return false;
		LogicalLink other = (LogicalLink)obj;
		if(this.deviceName.size() != other.deviceName.size()) return false;
		return this.deviceName.containsAll(other.deviceName);
	}
	@Override
	public int hashCode() {
		int hash = 0;
		if(this.deviceName == null) return hash;
		for(String device : this.deviceName) {
			if(device != null) hash += device.hashCode();
		}
		return hash;
	}
	@Override
	public LogicalLink clone() {
		LogicalLink newLogicalLink = new LogicalLink();
		for(String dName : deviceName) {
			newLogicalLink.deviceName.add(dName);
		}
		return newLogicalLink;
	}
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<LogicalLink>(){}.getType();
		return gson.toJson(this, type);
	}
	@Override
	public String toString() {
		return this.toJson();
	}
}
