/**
 * @author OOL 1134380013430
 * @date 2014/05/08
 * @TODO TODO
 */
package ool.com.ofpm.json;

import java.util.List;

/**
 * @author 1134380013430
 *
 */
public class NetworkConfigSetupperInData {
	private String deviceName;
	private String vlanId;
	private List<String> portNames;

	public NetworkConfigSetupperInData( String deviceName, String vlanId, List<String> portNames) {
		super();
		this.deviceName = deviceName;
		this.vlanId = vlanId;
		this.portNames = portNames;
	}

	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(
			String deviceName) {
		this.deviceName = deviceName;
	}
	public String getVlanId() {
		return vlanId;
	}
	public void setVlanId(String vlanId) {
		this.vlanId = vlanId;
	}
	public List<String> getPortNames() {
		return portNames;
	}
	public void setPortNames(
			List<String> portNames) {
		this.portNames = portNames;
	}
}
