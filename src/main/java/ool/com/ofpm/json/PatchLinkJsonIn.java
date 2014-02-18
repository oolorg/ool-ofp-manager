package ool.com.ofpm.json;

import java.util.HashSet;
import java.util.Set;

public class PatchLinkJsonIn extends BaseResponse {
	private Set<PatchLink> result = new HashSet<PatchLink>();

	public class PatchLink {
		private String deviceName;
		private Set<String> portName = new HashSet<String>();

		public String getDeviceName() {
			return deviceName;
		}
		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
		}
		public Set<String> getPortName() {
			return portName;
		}
		public void setPortName(
				Set<String> portName) {
			this.portName = portName;
		}

	}

	public Set<PatchLink> getResult() {
		return result;
	}

	public void setResult(
			Set<PatchLink> result) {
		this.result = result;
	}

}
