package ool.com.ofpm.business;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import ool.com.ofpm.client.AgentClient;
import ool.com.ofpm.client.AgentClientImpl;
import ool.com.ofpm.utils.Config;
import ool.com.ofpm.utils.ConfigImpl;

public class AgentManager {
	private static AgentManager instance = null;

	private Map<String, AgentClient> ipTable = new HashMap<String, AgentClient>();
	private Map<String, String> nameToIp = new HashMap<String, String>();
	private Map<String, String> switchToOfc = new HashMap<String, String>();
	Connection sqlite;

	private AgentManager() {
//		try {
//			sqlite = DriverManager.getConnection("jdbc:sqlite:AgentManage.db");
//			Statement state = sqlite.createStatement();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.getMessage();
//		}
	}

	public static AgentManager getInstance() {
		if(instance == null) {
			instance = new AgentManager();
		}
		Config config = new ConfigImpl();
		//String[] rec = config.getString(Definition.AGENT_RECODE).split(",");
		String[] rec = {"hoge","hoge","hoge","hoge"};
		instance.setAgentClient(rec[0], rec[1], rec[2], rec[3]);
		return instance;
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

	public AgentClient getAgentClient(String ip) {
		return ipTable.get(ip);
	}

	public String getSwitchIp(String deviceName) {
		return nameToIp.get(deviceName);
	}

	public String getOfcIp(String switchIp) {
		return switchToOfc.get(switchIp);
	}
}
