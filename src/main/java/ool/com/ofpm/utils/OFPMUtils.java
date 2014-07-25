package ool.com.ofpm.utils;

import java.util.IllegalFormatException;
import java.util.List;

import static ool.com.constants.OfpmDefinition.*;
import static ool.com.constants.ErrorMessage.*;
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
	
	/**
	 * transfer macAddress→long
	 * @param mac String ex.AA:BB:CC:DD:EE:FF
	 * @return long transfered
	 * @throws NullPointerException mac is null
	 * @throws NumberFormatException if the String does not contain a parsable long
	 */
	public static long macAddressToLong(String mac) throws NullPointerException, NumberFormatException {
		//String macTmp = StringUtils.join(mac.split(":"));
		String hexMac = mac.replace(":", "");
		long longMac = Long.decode("0x" + hexMac);
		if (longMac < MIN_MACADDRESS_VALUE || MAX_MACADDRESS_VALUE < longMac) {
			String errMsg = String.format(PARSE_ERROR, mac);
			throw new NumberFormatException(errMsg);
		}
		return longMac;
	}
	
	/**
	 * transfer macAddress→long
	 * @param longMac long
	 * @return macAddress transfered
	 * @throws IllegalFormatException If a format string contains an illegal syntax
	 * @throws NullPointerException longMac is null
	 * @throws NumberFormatException if the String does not contain a parsable long
	 */
	public static String longToMacAddress(long longMac) throws IllegalFormatException, NullPointerException, NumberFormatException {
		if (longMac < MIN_MACADDRESS_VALUE || MAX_MACADDRESS_VALUE < longMac) {
			String errMsg = String.format(PARSE_ERROR, longMac);
			throw new NumberFormatException(errMsg);
		}
		String hex = "000000000000" + Long.toHexString(longMac);
		StringBuilder hexBuilder = new StringBuilder(hex.substring(hex.length()-12));
		
		for (int i=2; i < 16; i=i+3) {
			hexBuilder.insert(i, ":");
		}
		return hexBuilder.toString();
	}
}
