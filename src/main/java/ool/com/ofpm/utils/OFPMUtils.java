package ool.com.ofpm.utils;

import java.util.List;

import ool.com.ofpm.json.device.PortData;
import ool.com.ofpm.json.topology.logical.LogicalTopology.OfpConDeviceInfo;
import ool.com.ofpm.json.topology.logical.LogicalTopology.OfpConPortInfo;

public class OFPMUtils {

	public static boolean nodesContainsPort(List<OfpConDeviceInfo> nodes, PortData port) {
		for (OfpConDeviceInfo device : nodes) {
			if (device.getDeviceName().equals(port.getDeviceName())) {
				for (OfpConPortInfo ofpConPort : device.getPorts()) {
					if (ofpConPort.getPortName().equals(port.getPortName())) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
