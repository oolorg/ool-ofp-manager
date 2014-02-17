package ool.com.ofpm.client;


public interface AgentClientManager {
	public AgentClient getAgentClient(String ip);
	public String getSwitchIp(String deviceName);
	public void setAgentClient(String deviceName, String switchIp, String agentIp);
}
