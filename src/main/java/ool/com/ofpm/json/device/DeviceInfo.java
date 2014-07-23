package ool.com.ofpm.json.device;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class DeviceInfo extends Node {
	private String datapathId;
	private String ofcIp;

	public String getDatapathId() {
		return datapathId;
	}

	public void setDatapathId(String datapathId) {
		this.datapathId = datapathId;
	}

	public String getOfcIp() {
		return ofcIp;
	}

	public void setOfcIp(String ofcIp) {
		this.ofcIp = ofcIp;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		Type type = new TypeToken<DeviceInfo>() {}.getType();
		return gson.toJson(this, type);
	}
}
