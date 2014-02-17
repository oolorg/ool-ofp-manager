package ool.com.ofpm.client;

import java.util.HashMap;
import java.util.Map;

public class AgentClientManagerImpl implements AgentClientManager {
	private static AgentClientManager instance;

	private Map<String, AgentClient> ipTable = new HashMap<String, AgentClient>();
	private Map<String, String> nameToIp = new HashMap<String, String>();

	public static AgentClientManager getInstance() {
		if(instance == null) {
			instance = new AgentClientManagerImpl();
		}
		return instance;
	}

	public AgentClient getAgentClient(String ip) {
		return ipTable.get(ip);
	}

	public String getSwitchIp(String deviceName) {
		return nameToIp.get(deviceName);
	}

	public void setAgentClient(String deviceName, String switchIp, String agentIp) {
		AgentClient agentClient = null;
		for(AgentClient client : ipTable.values()) {
			if(client.getIp() == agentIp) {
				agentClient = client;
				break;
			}
		}
		ipTable.put(switchIp, agentClient);
		nameToIp.put(deviceName, switchIp);
	}

}
