package ool.com.ofpm.json;



public class BaseNode {
	private String deviceName;
	private String deviceType;

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public BaseNode clone() {
		BaseNode newNode = new BaseNode();
		newNode.deviceName = deviceName;
		return newNode;
	}
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(this.getClass() != obj.getClass()) return false;
		BaseNode other = (BaseNode)obj;
		return (this.deviceName.equals(other.deviceName));
	}
	@Override
	public int hashCode() {
		if(this.deviceName == null) return 0;
		return this.deviceName.hashCode();
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
}
