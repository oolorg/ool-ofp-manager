package ool.com.ofpm.json.device;

import static org.junit.Assert.*;

import org.junit.Test;

public class DeviceInfoTest {
	String datapathId = "0123456789abcdef";
	String ofcIp      = "123.123.123.123";

	@Test
	public void checkEquals() {
		this.checkEquals(new DeviceInfo(), new DeviceInfo());
	}
	public void checkEquals(DeviceInfo dev1, DeviceInfo dev2) {
		if (dev1.equals(null)) {
			fail();
		}

		dev1.setDatapathId(new String(datapathId));
		dev1.setOfcIp(null);
		dev2.setDatapathId(null);
		dev2.setOfcIp(null);
		if (dev1.equals(dev2)) {
			fail();
		}
		dev2.setDatapathId(new String(datapathId));
		if (!dev1.equals(dev2)) {
			fail();
		}

		dev1.setOfcIp(new String(ofcIp));
		if (dev1.equals(dev2)) {
			fail();
		}
		dev2.setOfcIp(new String(ofcIp));
		if (!dev1.equals(dev2)) {
			fail();
		}

		new NodeTest().checkEquals(dev1, dev2);
	}

	@Test
	public void checkClone() {
		this.checkClone(new DeviceInfo());
	}
	public void checkClone(DeviceInfo base) {
		new DeviceInfo().clone();

		base.setDatapathId(new String(datapathId));
		base.setOfcIp(new String(ofcIp));

		DeviceInfo copy = base.clone();
		if (!base.equals(copy)) {
			fail();
		}

		if (base.getDatapathId() == copy.getDatapathId()) {
			fail();
		}
		if (!base.getDatapathId().equals(copy.getDatapathId())) {
			fail();
		}

		if (base.getOfcIp() == copy.getOfcIp()) {
			fail();
		}
		if (!base.getOfcIp().equals(copy.getOfcIp())) {
			fail();
		}

		new NodeTest().checkClone(base);
	}
}
