package ool.com.ofpm.json;

import java.util.List;

public class Node {
	private List<Integer> portName;
	private String deviceName;
	private int id;
	private String deviceKind;
	public Integer[] getPortName() {
		return (Integer[]) this.portName.toArray();
	}
	public void setPortName(final List<Integer> portName) {
		this.portName = portName;
	}
	public String getDeviceName() {
		return this.deviceName;
	}
	public void setDeviceName(final String deviceName) {
		this.deviceName = deviceName;
	}
	public int getId() {
		return this.id;
	}
	public void setId(final int id) {
		this.id = id;
	}
	public String getDeviceKind() {
		return this.deviceKind;
	}
	public void setDeviceKind(final String deviceKind) {
		this.deviceKind = deviceKind;
	}
}
