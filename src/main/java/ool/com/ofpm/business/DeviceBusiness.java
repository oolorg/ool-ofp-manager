package ool.com.ofpm.business;

public interface DeviceBusiness {
	/* Device */
	public String createDevice(String newDeviceInfoJson);

	public String deleteDevice(String deviceName);

	public String updateDevice(String updateDeviceInfoJson);

	/* Port */
	public String createPort(String newPortInfoJson);

	public String deletePort(String portName, String deviceName);

	public String updatePort(String updatePortInfoJson);

	/* Connect */
	public String getConnectedPortInfo(String deviceName);
}
