package ool.com.ofpm.client;

import ool.com.ofpm.exception.AgentClientException;
import ool.com.ofpm.json.common.BaseResponse;
import ool.com.ofpm.json.ofc.AgentClientUpdateFlowReq;

public interface AgentClient {
	public BaseResponse updateFlows(AgentClientUpdateFlowReq flows) throws AgentClientException;

	public String getIp();
}
