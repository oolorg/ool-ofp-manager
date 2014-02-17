package ool.com.ofpm.json;

import java.util.Set;

public class PatchLinkJsonIn extends BaseResponse {
	private Set<PatchLink> result;

	public class PatchLink {
		private String deviceName;
		private Set<Integer> portName;

		public String getDeviceName() {
			return deviceName;
		}
		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
		}
		public Set<Integer> getPortName() {
			return portName;
		}
		public void setPortName(Set<Integer> portName) {
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
