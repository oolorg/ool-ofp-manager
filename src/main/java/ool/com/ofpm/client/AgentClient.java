package ool.com.ofpm.client;

import ool.com.ofpm.exception.AgentClientException;
import ool.com.ofpm.json.AgentClientUpdateFlowReq;
import ool.com.ofpm.json.BaseResponse;

public interface AgentClient {
	public BaseResponse updateFlows(AgentClientUpdateFlowReq flows) throws AgentClientException;

	public String getIp();
}
