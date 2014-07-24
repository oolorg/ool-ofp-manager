package ool.com.ofpm.json.device;

import static org.junit.Assert.*;

import org.junit.Test;

public class PortDataTest extends PortInfoTest {
	String deviceName = "TEST";

	@Test
	public void checkEquals() {
		this.checkEquals(new PortData(), new PortData());
	}
	public void checkEquals(PortData port1, PortData port2) {
		if (port1.equals(null)) {
			fail();
		}

		port1.setDeviceName(new String(deviceName));
		port2.setDeviceName(null);
		if (port1.equals(port2)) {
			fail();
		}

		port2.setDeviceName(new String(deviceName));
		if (!port1.equals(port2)) {
			fail();
		}

		super.checkEquals(port1, port2);
	}

	@Test
	public void checkClone() {
		this.checkClone(new PortData());
	}
	public void checkClone(PortData base) {
		new PortData().clone();

		base.setDeviceName(new String(deviceName));

		PortData copy = base.clone();
		if (!base.equals(copy)) {
			fail();
		}

		if (base.getDeviceName() == copy.getDeviceName()) {
			fail();
		}
		if (!base.getDeviceName().equals(copy.getDeviceName())) {
			fail();
		}

		super.checkClone(base);
	}
}
