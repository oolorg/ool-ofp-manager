package ool.com.ofpm.business;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import ool.com.ofpm.client.AgentClient;
import ool.com.ofpm.client.AgentClientImpl;
import ool.com.ofpm.utils.Config;
import ool.com.ofpm.utils.ConfigImpl;
import ool.com.ofpm.utils.Definition;

import org.apache.log4j.Logger;

public class AgentManager {
	private static final Logger logger = Logger.getLogger(AgentManager.class);
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
		String fname = "AgentManager";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s() - start", fname));

		if(instance == null) {
			instance = new AgentManager();
		}
		Config config = new ConfigImpl();
		Object[] recode = config.getList(Definition.AGENT_RECODE).toArray();
		//String[] rec = recode.split(",");
		instance.setAgentClient(recode[0].toString(), recode[1].toString(), recode[2].toString(), recode[3].toString());

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, instance));
		return instance;
	}

	public void setAgentClient(String deviceName, String switchIp, String agentIp, String ofcIp) {
		String fname = "setAgentClient";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(deviceName=%s, switchIp=%s, agentIp=%s, ofcIp=%s) - start", fname, deviceName, switchIp, agentIp, ofcIp));

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

		if(logger.isDebugEnabled()) logger.debug(String.format("%s() - end", fname));
	}

	public AgentClient getAgentClient(String ip) {
		String fname = "getAgentClient";
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ip=%s) - start", fname, ip));
			logger.debug(String.format("%s(ret=%s) -end", fname, ipTable.get(ip)));
		}
		return ipTable.get(ip);
	}

	public String getSwitchIp(String deviceName) {
		String fname = "getSwitchIp";
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s) - start", fname, deviceName));
			logger.debug(String.format("%s(ret=%s) -end", fname, nameToIp.get(deviceName)));
		}
		return nameToIp.get(deviceName);
	}

	public String getOfcIp(String switchIp) {
		String fname = "getOfcIp";
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ip=%s) - start", fname, switchIp));
			logger.debug(String.format("%s(ret=%s) -end", fname, switchToOfc.get(switchIp)));
		}
		return switchToOfc.get(switchIp);
	}
}
