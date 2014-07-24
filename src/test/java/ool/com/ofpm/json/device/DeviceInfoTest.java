package ool.com.ofpm.json.device;

import static org.junit.Assert.*;

import org.junit.Test;

public class DeviceInfoTest {
	String deviceName = "TEST";
	String deviceType = "SERVER";
	String datapathId = "0123456789abcdef";
	String ofcIp      = "123.123.123.123";

	@Test
	public void checkEquals() {
		DeviceInfo dev1 = new DeviceInfo();
		dev1.setDeviceName(deviceName);
		dev1.setDeviceType(deviceType);
		dev1.setDatapathId(datapathId);
		dev1.setOfcIp(ofcIp);

		DeviceInfo dev2 = new DeviceInfo();
		if (dev1.equals(dev2)) {
			fail();
		}

		dev2.setDeviceName(deviceName);
		if (dev1.equals(dev2)) {
			fail();
		}
//		dev2.setDeviceType(deviceType);
//		if (dev1.equals(dev2)) {
//			fail();
//		}

		dev2.setDatapathId(datapathId);
		if (dev1.equals(dev2)) {
			fail();
		}

		dev2.setOfcIp(ofcIp);
		if (!dev1.equals(dev2)) {
			fail();
		}
	}

	@Test
	public void checkClone() {
		DeviceInfo base = new DeviceInfo();
		DeviceInfo copy = base.clone();

		base.setDeviceName(deviceName);
		base.setDeviceType(deviceType);
		base.setDatapathId(datapathId);
		base.setOfcIp(ofcIp);
		copy = base.clone();

		if (base == copy) {
			fail();
		}
		if (copy.getDeviceName() == base.getDeviceName()) {
			fail();
		}
		if (copy.getDeviceType() == base.getDeviceType()) {
			fail();
		}
		if (copy.getOfcIp() == base.getOfcIp()) {
			fail();
		}
		if (copy.getDatapathId() == base.getDatapathId()) {
			fail();
		}

		if (!copy.equals(base)) {
			fail();
		}
		if (!copy.getDeviceName().equals(base.getDeviceName())) {
			fail();
		}
		if (!copy.getDeviceType().equals(base.getDeviceType())) {
			fail();
		}
		if (!copy.getDatapathId().equals(base.getDatapathId())) {
			fail();
		}
		if (!copy.getOfcIp().equals(base.getOfcIp())) {
			fail();
		}
	}
}
