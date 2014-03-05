package ool.com.ofpm.business;


public interface DeviceBusiness {
	public String createDevice(String deviceInfoJson);

	public String deleteDevice(String params);

	public String updateDevice(String params);
}
