package ool.com.ofpm.json;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AgentInfoUpdateJsonIn {
	private String ip;
	private List<SwitchInfo> switchies;

	public class SwitchInfo {
		private String deviceName;
		private String ip;
		private String ofcUrl;

		public String getDeviceName() {
			return deviceName;
		}
		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
		}
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public String getOfcUrl() {
			return ofcUrl;
		}
		public void setOfcUrl(String ofcUrl) {
			this.ofcUrl = ofcUrl;
		}
	}

	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public List<SwitchInfo> getSwitchies() {
		return switchies;
	}
	public void setSwitchies(List<SwitchInfo> switchies) {
		this.switchies = switchies;
	}

	public static AgentInfoUpdateJsonIn fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<AgentInfoUpdateJsonIn>() {}.getType();
		return gson.fromJson(json, type);
	}
	@Override
	public String toString() {
		Gson gson = new Gson();
		Type type = new TypeToken<AgentInfoUpdateJsonIn>() {}.getType();
		return gson.toJson(this, type);
	}
}
