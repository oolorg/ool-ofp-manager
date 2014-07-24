package ool.com.ofpm.json.device;

import static org.junit.Assert.*;

import org.junit.Test;

public class PortInfoTest {
	String portName = "TEST";
	int portNumber  = 1;

	@Test
	public void checkEquals() {
		checkEquals(new PortInfo(), new PortInfo());
	}
	public void checkEquals(PortInfo port1, PortInfo port2) {
		if (port1.equals(null)) {
			fail();
		}

		port1.setPortName(new String(portName));
		port1.setPortNumber(0);
		port2.setPortName(null);
		port2.setPortNumber(0);
		if (port1.equals(port2)) {
			fail();
		}
		port2.setPortName(new String(portName));
		if (!port1.equals(port2)) {
			fail();
		}

		port1.setPortNumber(portNumber);
		if (port1.equals(port2)) {
			fail();
		}
		port2.setPortNumber(portNumber);
		if (!port1.equals(port2)) {
			fail();
		}
	}

	@Test
	public void checkClone() {
		this.checkClone(new PortInfo());
	}
	public void checkClone(PortInfo base) {
		new PortInfo().clone();

		base.setPortName(new String(portName));
		base.setPortNumber(portNumber);

		PortInfo copy = base.clone();
		if (!base.equals(copy)) {
			fail();
		}

		if (!base.equals(copy)) {
			fail();
		}
		if (base.getPortName() == copy.getPortName()) {
			fail();
		}
		if (!base.getPortName().equals(copy.getPortName())) {
			fail();
		}
		if (base.getPortNumber() != copy.getPortNumber()) {
			fail();
		}
	}
}
