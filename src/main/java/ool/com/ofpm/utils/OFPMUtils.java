package ool.com.ofpm.utils;

import static ool.com.constants.ErrorMessage.*;
import static ool.com.constants.OfpmDefinition.*;

import java.util.IllegalFormatException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	/**
	 * Charactor is represented band-width converted to value of Mbps. For example, 1.2Gbps is  converted to 1229.
	 * Available range is from Mbps to Ybps.
	 * @param bandWidth [Number][kMGTPEZY]bps
	 * @return
	 */
	public static long bandWidthToBaseMbps(String bandWidth) {
		String reg = "([0-9]+)([MGTPEZY])bps";
		Pattern pat = Pattern.compile(reg);
		Matcher mat = pat.matcher(bandWidth);
		if (!mat.find()) {
			throw new NumberFormatException(String.format(PARSE_ERROR, bandWidth));
		}
		String numb = mat.group(1);
		String base = mat.group(2);

		long value = Long.parseLong(numb);
		if (base.equals("G")) {
			value *= 1024L;
		} else if (base.equals("T")) {
			value *= 1048576L;
		} else if (base.equals("P")) {
			value *= 1073741824L;
		} else if (base.equals("E")) {
			value *= 1099511627776L;
		} else if (base.equals("Z")) {
			value *= 1125899906842624L;
		} else if (base.equals("Y")) {
			value *= 1152921504606846976L;
		}
		return value;
	}
}
