package ool.com.ofpm.business;

import ool.com.ofpm.client.AgentClient;


public interface AgentManageBusiness {
	public AgentClient getAgentClient(String ip);
	public String getSwitchIp(String deviceName);
	public String getOfcIp(String switchIp);
	public void setAgentClient(String deviceName, String switchIp, String agentIp, String ofcIp);
}
