package ool.com.ofpm.json.device;

import static org.junit.Assert.*;

import org.junit.Test;

public class NodeTest {
	String deviceName = "TEST";
	String deviceType = "SERVER";

	@Test
	public void checkEquals() {
		this.checkEquals(new Node(), new Node());
	}
	public void checkEquals(Node node1, Node node2) {
		if (node1.equals(null)) {
			fail();
		}

		node1.setDeviceName(new String(deviceName));
		node1.setDeviceType(null);
		node2.setDeviceName(null);
		node2.setDeviceType(null);
		if (node1.equals(node2)) {
			fail();
		}
		node2.setDeviceName(new String(deviceName));
		if (!node1.equals(node2)) {
			fail();
		}

//		node1.setDeviceType(new String(deviceType));
//		node2.setDeviceType(null);
//		if (node1.equals(node2)) {
//			fail();
//		}
//		node2.setDeviceType(new String(deviceType));
//		if (!node1.equals(node2)) {
//			fail();
//		}
	}

	@Test
	public void checkClone() {
		this.checkClone(new Node());
	}
	public void checkClone(Node base) {
		new Node().clone();

		base.setDeviceName(deviceName);
		base.setDeviceType(deviceType);

		Node copy = base.clone();
		if (!base.equals(copy)) {
			fail();
		}

		if (base.getDeviceName() == copy.getDeviceName()) {
			fail();
		}
		if (!base.getDeviceName().equals(copy.getDeviceName())) {
			fail();
		}

		if (base.getDeviceType() == copy.getDeviceType()) {
			fail();
		}
		if (!base.getDeviceType().equals(copy.getDeviceType())) {
			fail();
		}
	}
}
