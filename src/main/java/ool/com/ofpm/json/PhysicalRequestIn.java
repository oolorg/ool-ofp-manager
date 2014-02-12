package ool.com.ofpm.json;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;

public class PhysicalRequestIn {
	public static Type TYPE = new TypeToken<PhysicalRequestIn>(){}.getType();

	public class Data {
		private String deviceName;
		private NodeType type;
		private String portName;
		private NodeStatus status;

		public String getDeviceName() {
			return deviceName;
		}
		public void setDeviceName(
				String deviceName) {
			this.deviceName = deviceName;
		}
		public NodeType getType() {
			return type;
		}
		public void setType(final NodeType type) {
			this.type = type;
		}
		public String getPortName() {
			return portName;
		}
		public void setPortName(
				String portName) {
			this.portName = portName;
		}
		public NodeStatus getStatus() {
			return status;
		}
		public void setStatus(
				NodeStatus status) {
			this.status = status;
		}
	}

	private List<Data> devices;
	private From from;

	public List<Data> getDevices() {
		return devices;
	}

	public void setDevices(
			List<Data> devices) {
		this.devices = devices;
	}

	public From getFrom() {
		return from;
	}

	public void setFrom(From from) {
		this.from = from;
	}

	public enum From {
		AGENT("agent"),
		GUI("gui");

		private String name;
		public String getName() {
			return this.name;
		}
		private From(String name) {
			this.name = name;
		}
	}

	public enum NodeType {
		SWITCH("switch"),
		SERVER("server");

		private String name;
		public String getName() {
			return this.name;
		}
		private NodeType(String name) {
			this.name = name;
		}
	}

	public enum NodeStatus {
		SWITCH("switch"),
		SERVER("server");

		private String name;
		public String getName() {
			return this.name;
		}
		private NodeStatus(String name) {
			this.name = name;
		}
	}
}
