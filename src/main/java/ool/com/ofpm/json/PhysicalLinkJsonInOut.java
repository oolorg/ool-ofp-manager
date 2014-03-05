package ool.com.ofpm.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PhysicalLinkJsonInOut {
	private List<PhysicalLink> link = new ArrayList<PhysicalLink>();

	public List<PhysicalLink> getLink() {
		return link;
	}

	public void setLink(List<PhysicalLink> link) {
		this.link = link;
	}

	public class PhysicalLink {
		private String deviceName;
		private String portName;
		public String getDeviceName() {
			return deviceName;
		}
		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
		}
		public String getPortName() {
			return portName;
		}
		public void setPortName(String portName) {
			this.portName = portName;
		}
	}

	public static PhysicalLinkJsonInOut fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<PhysicalLinkJsonInOut>(){}.getType();
		return gson.fromJson(json, type);
	}

	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<PhysicalLinkJsonInOut>(){}.getType();
		return gson.toJson(this, type);
	}
}
