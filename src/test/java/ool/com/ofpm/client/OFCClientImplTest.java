package ool.com.ofpm.client;

import static org.junit.Assert.*;
import ool.com.ofpm.exception.OFCClientException;

import org.junit.Test;

public class OFCClientImplTest {
	private static final String OFC_URL = "172.16.1.85:28080";

	private static final String DPID_LEAF1	= "678c089e01e99481";
	private static final String DPID_LEAF2	= "678c089e01e994cc";
	private static final String DPID_SPINE1	= "1fad089e0153785e";
	private static final String DPID_SPINE2	= "678c089e0153787c";

	@Test
	public void testFlows() {

		OFCClient ofcClient = new OFCClientImpl(OFC_URL);

		try {
			ofcClient.setFlows(DPID_LEAF1, 1, "08:00:27:f8:fe:7f", "ff:ff:ff:ff:ff:ff", 49, "00:00:00:00:00:01", "FF:FF:FF:FF:FF:FE", null, null);
		} catch (OFCClientException ofcce) {
			System.out.println(ofcce.getMessage());
			fail();
		}

		try {
			ofcClient.setFlows(DPID_SPINE1, 1, "00:00:00:00:00:01", null, 2, null, null, null, null);
		} catch (OFCClientException ofcce) {
			System.out.println(ofcce.getMessage());
			fail();
		}

		try {
			ofcClient.setFlows(DPID_LEAF2, 1, "00:00:00:00:00:01", null, 49, "08:00:27:f8:fe:7f", "FF:FF:FF:FF:FF:FE", null, null);
		} catch (OFCClientException ofcce) {
			System.out.println(ofcce.getMessage());
			fail();
		}

		try {
			ofcClient.deleteFlows(DPID_LEAF1, 2, "08:00:27:f8:fe:7f", "ff:ff:ff:ff:ff:ff", 49, "00:00:00:00:00:01", "FF:FF:FF:FF:FF:FE", false, false);
		} catch (OFCClientException ofcce) {
			System.out.println(ofcce.getMessage());
			fail();
		}

		try {
			ofcClient.deleteFlows(DPID_LEAF1, 1, null, null, null, null, null, null, null);
		} catch (OFCClientException ofcce) {
			System.out.println(ofcce.getMessage());
			fail();
		}

	}
}
