package ool.com.ofpm.json;

import java.util.ArrayList;
import java.util.List;

public class PatchLinkJsonIn extends BaseResponse {
	private List<PatchLink> result = new ArrayList<PatchLink>();

	public class PatchLink {
		private String deviceName;
		private List<Integer> portName = new ArrayList<Integer>();

		public String getDeviceName() {
			return deviceName;
		}
		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
		}
		public List<Integer> getPortName() {
			return portName;
		}
		public void setPortName(
				List<Integer> portName) {
			this.portName = portName;
		}
	}

	public List<PatchLink> getResult() {
		return result;
	}

	public void setResult(
			List<PatchLink> result) {
		this.result = result;
	}

}
