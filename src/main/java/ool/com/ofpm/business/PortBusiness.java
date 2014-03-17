package ool.com.ofpm.business;

public interface PortBusiness {
	public String createPort(String newPortInfoJson);

	public String deletePort(String portName, String deviceName);

	public String updatePort(String updatePortInfoJson);
}
