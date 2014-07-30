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

	@Test
	public void testBandWidthToBaseMbps() {
		assertEquals(1024L,                OFPMUtils.bandWidthToBaseMbps("1024Mbps"));
		assertEquals(1024L,                OFPMUtils.bandWidthToBaseMbps("1Gbps"));
		assertEquals(2097152L,             OFPMUtils.bandWidthToBaseMbps("2Tbps"));
		assertEquals(3221225472L,          OFPMUtils.bandWidthToBaseMbps("3Pbps"));
		assertEquals(4398046511104L,       OFPMUtils.bandWidthToBaseMbps("4Ebps"));
		assertEquals(5629499534213120L,    OFPMUtils.bandWidthToBaseMbps("5Zbps"));
		assertEquals(6917529027641081856L, OFPMUtils.bandWidthToBaseMbps("6Ybps"));
	}
}
