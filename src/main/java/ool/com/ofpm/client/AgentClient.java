package ool.com.ofpm.client;

import ool.com.ofpm.json.AgentUpdateFlowRequest;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.BaseResultIn;

public interface AgentClient {
	public BaseResultIn getTopology() throws AgentClientException;
	public BaseResponse updateFlows(AgentUpdateFlowRequest flows) throws AgentClientException;
	public String getIp();
}
