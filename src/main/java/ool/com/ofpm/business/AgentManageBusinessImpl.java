package ool.com.ofpm.business;

import java.util.HashMap;
import java.util.Map;

import ool.com.ofpm.client.AgentClient;
import ool.com.ofpm.client.AgentClientImpl;

public class AgentManageBusinessImpl implements AgentManageBusiness {
	private static AgentManageBusiness instance;

	private Map<String, AgentClient> ipTable = new HashMap<String, AgentClient>();
	private Map<String, String> nameToIp = new HashMap<String, String>();
	private Map<String, String> switchToOfc = new HashMap<String, String>();

	private AgentManageBusinessImpl() {
		this.setAgentClient("sentec", "192.168.1.225:8080", "192.168.1.225:8080", "http://192.168.1.225:3366/ofc/ryu/ctrl");
	}
	public static AgentManageBusiness getInstance() {
		if(instance == null) {
			instance = new AgentManageBusinessImpl();
		}
		return instance;
	}

	public AgentClient getAgentClient(String ip) {
		return ipTable.get(ip);
	}

	public String getSwitchIp(String deviceName) {
		return nameToIp.get(deviceName);
	}

	public String getOfcIp(String switchIp) {
		return switchToOfc.get(switchIp);
	}

	public void setAgentClient(String deviceName, String switchIp, String agentIp, String ofcIp) {
		AgentClient agentClient = null;
		for(AgentClient client : ipTable.values()) {
			if(client.getIp() == agentIp) {
				agentClient = client;
				break;
			}
		}
		if(agentClient == null) {
			agentClient = new AgentClientImpl(agentIp);
		}
		ipTable.put(switchIp, agentClient);
		nameToIp.put(deviceName, switchIp);
		switchToOfc.put(switchIp, ofcIp);
	}

}
