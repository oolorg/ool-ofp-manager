package ool.com.ofpm.business;

public interface DeviceBusiness {
	public String createDevice(String newDeviceInfoJson);

	public String deleteDevice(String deviceName);

	public String updateDevice(String updateDeviceInfoJson);
}
