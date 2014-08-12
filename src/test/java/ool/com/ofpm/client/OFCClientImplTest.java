package ool.com.ofpm.client;

import static org.junit.Assert.*;
import ool.com.ofpm.exception.OFCClientException;

import org.junit.Test;

public class OFCClientImplTest {
	private static final String OFC_URL = "172.16.1.85:38080";

	private static final String DPID_LEAF1	= "678c089e01e99481";
	private static final String DPID_LEAF2	= "678c089e01e994cc";
	private static final String DPID_SPINE1	= "1fad089e0153785e";
	private static final String DPID_SPINE2	= "678c089e0153787c";

	//private static final String MAC_OFP_TEST1	= "08:00:27:f8:fe:7f";
	//private static final String MAC_OFP_TEST2	= "08:00:27:59:08:77";
	private static final String MAC_OFP_TEST1	= "08:00:27:f8:fe:7f";
	private static final String MAC_OFP_TEST2	= "08:00:27:59:08:77";
	private static final String MAC_ARP_REQ		= "ff:ff:ff:ff:ff:ff";

	private static final String INTERNAL_SRC_MAC1	= "00:00:00:00:00:01";
	private static final String INTERNAL_SRC_MAC2	= "00:00:00:00:00:02";
	private static final String INTERNAL_SRC_MAC3	= "00:00:00:00:00:03";
	private static final String INTERNAL_SRC_MAC4	= "00:00:00:00:00:04";

	private static final String INTERNAL_DST_MAC1	= "ff:ff:ff:ff:ff:fe";
	private static final String INTERNAL_DST_MAC2	= "ff:ff:ff:ff:ff:fd";
	private static final String INTERNAL_DST_MAC3	= "ff:ff:ff:ff:ff:fc";
	private static final String INTERNAL_DST_MAC4	= "ff:ff:ff:ff:ff:fb";

	@Test
	public void testFlows() {
		try {
			// フロー削除
			delFlow(new OFCClientImpl(OFC_URL));
			// フロー登録
			// type:0 packet-in
			// type:1 port1-Leaf1-Spine1-Leaf2-port1のシンプルなフロー登録
			// type:2 port1-Leaf1-Spine2-Leaf2-port1のシンプルなフロー登録
			// type:3 port1-Leaf1-Spine1-Leaf2-port1のシンプルなフロー登録
			// type:4 port1-Leaf1-Spine2-Leaf2-port1のシンプルなフロー登録
			// type:5 LOFS1-Port1 to SOFS1 to LOFS2-Port1 (ARP MAC)
			// type:6 LOFS1-Port1 to LOFS2-Port1
			setFlow(new OFCClientImpl(OFC_URL), 0);
		} catch (OFCClientException ofcce) {
			System.out.println(ofcce.getMessage());
			fail();
		}
	}
	
	private void delFlow(OFCClient ofcClient) throws OFCClientException {
		ofcClient.deleteFlows(DPID_LEAF1, 1, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_LEAF1, 25, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_LEAF1, 49, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_LEAF1, 50, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_LEAF1, 51, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_LEAF1, 52, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_LEAF2, 1, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_LEAF2, 25, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_LEAF2, 49, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_LEAF2, 50, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_LEAF2, 51, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_LEAF2, 52, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_SPINE1, 1, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_SPINE1, 2, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_SPINE1, 25, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_SPINE1, 26, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_SPINE2, 1, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_SPINE2, 2, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_SPINE2, 25, null, null, null, null, null, null, null);
		ofcClient.deleteFlows(DPID_SPINE2, 26, null, null, null, null, null, null, null);
	}
	
	private void setFlow(OFCClient ofcClient, int type) throws OFCClientException {	
		switch(type) {
			case 0:
				ofcClient.setFlows(DPID_LEAF1, 1, null, null, null, null, null, true, null);
				ofcClient.setFlows(DPID_LEAF2, 1, null, null, null, null, null, true, null);
				ofcClient.setFlows(DPID_LEAF1, 25, null, null, null, null, null, true, null);
				ofcClient.setFlows(DPID_LEAF2, 25, null, null, null, null, null, true, null);
				break;
			case 1:
				ofcClient.setFlows(DPID_LEAF1, 1, null, null, 49, null, null, null, null);
				ofcClient.setFlows(DPID_LEAF1, 49, null, null, 1, null, null, null, null);
				ofcClient.setFlows(DPID_SPINE1, 1, null, null, 2, null, null, null, null);
				ofcClient.setFlows(DPID_SPINE1, 2, null, null, 1, null, null, null, null);
				ofcClient.setFlows(DPID_LEAF2, 1, null, null, 49, null, null, null, null);
				ofcClient.setFlows(DPID_LEAF2, 49, null, null, 1, null, null, null, null);
				break;
			case 2:
				ofcClient.setFlows(DPID_LEAF1, 1, null, null, 50, null, null, null, null);
				ofcClient.setFlows(DPID_LEAF1, 50, null, null, 1, null, null, null, null);
				ofcClient.setFlows(DPID_SPINE2, 1, null, null, 2, null, null, null, null);
				ofcClient.setFlows(DPID_SPINE2, 2, null, null, 1, null, null, null, null);
				ofcClient.setFlows(DPID_LEAF2, 1, null, null, 50, null, null, null, null);
				ofcClient.setFlows(DPID_LEAF2, 50, null, null, 1, null, null, null, null);
				break;
			case 3:
				ofcClient.setFlows(DPID_LEAF1, 1, null, null, 51, null, null, null, null);
				ofcClient.setFlows(DPID_LEAF1, 51, null, null, 1, null, null, null, null);
				ofcClient.setFlows(DPID_SPINE1, 25, null, null, 26, null, null, null, null);
				ofcClient.setFlows(DPID_SPINE1, 26, null, null, 25, null, null, null, null);
				ofcClient.setFlows(DPID_LEAF2, 1, null, null, 51, null, null, null, null);
				ofcClient.setFlows(DPID_LEAF2, 51, null, null, 1, null, null, null, null);
				break;
			case 4:
				ofcClient.setFlows(DPID_LEAF1, 1, null, null, 52, null, null, null, null);
				ofcClient.setFlows(DPID_LEAF1, 52, null, null, 1, null, null, null, null);
				ofcClient.setFlows(DPID_SPINE2, 25, null, null, 26, null, null, null, null);
				ofcClient.setFlows(DPID_SPINE2, 26, null, null, 25, null, null, null, null);
				ofcClient.setFlows(DPID_LEAF2, 1, null, null, 52, null, null, null, null);
				ofcClient.setFlows(DPID_LEAF2, 52, null, null, 1, null, null, null, null);
				break;
			case 5:
				ofcClient.setFlows(DPID_LEAF1, 1, MAC_OFP_TEST1, MAC_ARP_REQ, 49, INTERNAL_SRC_MAC1, INTERNAL_DST_MAC1, null, null);
				ofcClient.setFlows(DPID_LEAF1, 49, INTERNAL_SRC_MAC3, null, 1, MAC_OFP_TEST2, MAC_ARP_REQ, null, null);
				ofcClient.setFlows(DPID_SPINE1, 1, INTERNAL_SRC_MAC1, null, 2, null, null, null, null);
				ofcClient.setFlows(DPID_SPINE1, 2, INTERNAL_SRC_MAC3, null, 1, null, null, null, null);
				ofcClient.setFlows(DPID_LEAF2, 1, MAC_OFP_TEST2, MAC_ARP_REQ, 49, INTERNAL_SRC_MAC3, INTERNAL_DST_MAC3, null, null);
				ofcClient.setFlows(DPID_LEAF2, 49, INTERNAL_SRC_MAC1, null, 1, MAC_OFP_TEST1, MAC_ARP_REQ, null, null);
				break;
			case 6:
				ofcClient.setFlows(DPID_LEAF1, 1, MAC_OFP_TEST1, MAC_OFP_TEST2, 49, INTERNAL_SRC_MAC2, INTERNAL_DST_MAC2, null, null);
				ofcClient.setFlows(DPID_LEAF1, 49, INTERNAL_SRC_MAC4, null, 1, MAC_OFP_TEST2, MAC_OFP_TEST1, null, null);
				ofcClient.setFlows(DPID_SPINE1, 1, INTERNAL_SRC_MAC2, null, 2, null, null, null, null);
				ofcClient.setFlows(DPID_SPINE1, 2, INTERNAL_SRC_MAC4, null, 1, null, null, null, null);
				ofcClient.setFlows(DPID_LEAF2, 1, MAC_OFP_TEST2, MAC_OFP_TEST1, 49, INTERNAL_SRC_MAC4, INTERNAL_DST_MAC4, null, null);
				ofcClient.setFlows(DPID_LEAF2, 49, INTERNAL_SRC_MAC2, null, 1, MAC_OFP_TEST1, MAC_OFP_TEST2, null, null);
				break;
		}
	}
}
