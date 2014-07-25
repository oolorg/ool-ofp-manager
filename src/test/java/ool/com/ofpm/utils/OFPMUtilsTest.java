/**
 * @author OOL 1131080355959
 * @date 2014/07/25
 * @TODO 
 */
package ool.com.ofpm.utils;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author 1131080355959
 *
 */
public class OFPMUtilsTest {

	/**
	 * Test method for {@link ool.com.ofpm.utils.OFPMUtils#macAddressToInteger(java.lang.String)}.
	 */
	@Test
	public void testMacAddressToInteger() {
		String mac = "FF:FF:FF:FF:FF:FF";
		System.out.println(OFPMUtils.macAddressToLong(mac));
	}

	@Test
	public void testLongToMacAddress() {
		long mac = 281474976710655L;
		System.out.println(OFPMUtils.longToMacAddress(mac));
		long mac2 = 1L;
		System.out.println(OFPMUtils.longToMacAddress(mac2));
	}
}
