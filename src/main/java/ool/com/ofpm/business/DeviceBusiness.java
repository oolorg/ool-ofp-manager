package ool.com.ofpm.business;

public interface DeviceBusiness {
	/* Device */
	public String createDevice(String newDeviceInfoJson);

	public String deleteDevice(String deviceName);

	public String updateDevice(String deviceName, String updateDeviceInfoJson);

	/* Port */
	public String createPort(String newPortInfoJson);

	public String deletePort(String deviceName, String portName);

	public String updatePort(String deviceName, String portName, String updatePortInfoJson);

	/* Connect */
	public String getConnectedPortInfo(String deviceName);
}
