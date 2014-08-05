package ool.com.ofpm.client;

import ool.com.ofpm.exception.OFCClientException;
import ool.com.ofpm.json.common.BaseResponse;
import ool.com.ofpm.json.ofc.SetFlowToOFC;

public interface OFCClient {
	public BaseResponse setFlows(String dpid, String inPort, String srcMac, String outPort, String modSrcMac, String modDstMac, Boolean packetIn, Boolean drop) throws OFCClientException;
	
	public BaseResponse deleteFlows(String dpid, String inPort, String srcMac, String outPort, String modSrcMac, String modDstMac, Boolean packetIn, Boolean drop) throws OFCClientException;

	public String getIp();
}
