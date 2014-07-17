package ool.com.ofpm.json.ofc;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@XmlRootElement(name="AgentInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentInfo {
	@XmlAttribute(name="ip")
	private String ip;

	@XmlElement(name="SwitchInfo")
	private List<SwitchInfo> switches = new ArrayList<SwitchInfo>();

	@XmlRootElement(name="SwithchInfo")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class SwitchInfo {
		@XmlAttribute(name="deviceName")
		private String deviceName;
		@XmlAttribute(name="ip")
		private String ip;
		@XmlAttribute(name="ofcUrl")
		private String ofcUrl;

		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
		}
		public String getDeviceName() {
			return deviceName;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public String getIp() {
			return ip;
		}
		public void setOfcUrl(String ofcUrl) {
			this.ofcUrl = ofcUrl;
		}
		public String getOfcUrl() {
			return ofcUrl;
		}
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getIp() {
		return ip;
	}
	public void setSwitches(List<SwitchInfo> switches) {
		this.switches = switches;
	}
	public List<SwitchInfo> getSwitches() {
		return switches;
	}

	public static AgentInfo fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<AgentInfo>() {}.getType();
		return gson.fromJson(json, type);
	}
	@Override
	public String toString() {
		Gson gson = new Gson();
		Type type = new TypeToken<AgentInfo>() {}.getType();
		return gson.toJson(this, type);
	}
}
