package ool.com.ofpm.client;

import ool.com.ofpm.json.AgentUpdateFlowRequest;
import ool.com.ofpm.json.BaseResponse;

public interface AgentClient {
	public BaseResponse updateFlows(AgentUpdateFlowRequest flows) throws AgentClientException;
	public String getIp();
}
