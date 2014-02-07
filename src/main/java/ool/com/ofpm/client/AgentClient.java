package ool.com.ofpm.client;

import ool.com.ofpm.json.BaseResultIn;

public interface AgentClient {
	public BaseResultIn getTopology() throws Exception;
	public BaseResultIn addFlows() throws Exception;
	public BaseResultIn delFlows() throws Exception;
}
