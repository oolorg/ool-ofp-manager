package ool.com.ofpm.json.device;

import java.lang.reflect.Type;



import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class DeviceInfo extends Node {
	private String ofpFlag;

	public String getOfpFlag() {
		return ofpFlag;
	}
	public void setOfpFlag(String ofpFlag) {
		this.ofpFlag = ofpFlag;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		Type type = new TypeToken<DeviceInfo>() {}.getType();
		return gson.toJson(this, type);
	}
}
