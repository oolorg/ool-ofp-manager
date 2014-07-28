package ool.com.ofpm.json.topology.logical;

import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ool.com.ofpm.json.device.PortData;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LogicalLinkTest {
	final String DEV1  = "DEV1";
	final String PORT1 = "DEV1/1";
	final int    NUM1  = 1;
	final String DEV2  = "DEV2";
	final String PORT2 = "DEV2/2";
	final int    NUM2  = 2;

	@Test
	public void testLogicalLink() {
		final String json = "{link:[{deviceName:'DEV1', portName:'DEV1/1', portNumber:1}, {deviceName:'DEV2', portName:'DEV2/2', portNumber:2}]}";
		Gson gson = new Gson();
		Type type = new TypeToken<LogicalLink>() {}.getType();
		LogicalLink link = gson.fromJson(json, type);

		for (PortData port : link.getLink()) {
			String devName = port.getDeviceName();
			String portName = port.getPortName();
			int    portNmbr = port.getPortNumber();
			if (devName.equals(DEV1)) {
				if (!portName.equals(PORT1)) {
					fail();
				}
				if (portNmbr != NUM1) {
					fail();
				}
			} else if (devName.equals(DEV2)) {
				if (!portName.equals(PORT2)) {
					fail();
				}
				if (portNmbr != NUM2) {
					fail();
				}
			} else {
				fail();
			}
		}
	}

	@Test
	public void testEquals() {
		/* not implemented yet */
	}

	@Test
	public void testClone() {
		this.testClone(new LogicalLink());
	}
	public void testClone(LogicalLink base) {
		new LogicalLink().clone();

		PortData port1 = new PortData();
		port1.setDeviceName(DEV1);
		port1.setPortName(PORT1);
		port1.setPortNumber(NUM1);

		PortData port2 = new PortData();
		port2.setDeviceName(DEV2);
		port2.setPortName(PORT2);
		port2.setPortNumber(NUM2);

		List<PortData> link = new ArrayList<PortData>();
		link.add(port1);

		base.setLink(link);

		LogicalLink copy = base.clone();
		if (!base.equals(copy)) {
			fail();
		}

		if (base.getLink() == copy.getLink()) {
			fail();
		}
		if (!base.getLink().equals(copy.getLink())) {
			fail();
		}
	}
}
