package ool.com.ofpm.business;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import ool.com.ofpm.client.AgentClient;
import ool.com.ofpm.client.AgentClientImpl;
import ool.com.ofpm.exception.AgentManagerException;
import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.AgentInfo;
import ool.com.ofpm.json.AgentInfo.SwitchInfo;
import ool.com.ofpm.json.AgentInfoListConfigIn;
import ool.com.ofpm.utils.Definition;
import ool.com.ofpm.utils.ErrorMessage;
import ool.com.ofpm.validate.AgentInfoValidate;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class AgentManager {
	private static final Logger logger = Logger.getLogger(AgentManager.class);
	private static AgentManager instance = null;

	private Map<String, AgentClient> ipTable = new HashMap<String, AgentClient>();
	private Map<String, String> nameToIp = new HashMap<String, String>();
	private Map<String, String> switchToOfc = new HashMap<String, String>();

	private AgentManager() {
	}

	public static AgentManager getInstance() {
		String fname = "getInstance";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - start", fname));
		}

		if (instance == null) {
			instance = new AgentManager();
		}

		try {
			JAXBContext context = JAXBContext.newInstance(AgentInfoListConfigIn.class);
			Unmarshaller ums = context.createUnmarshaller();
			URL url = Thread.currentThread().getContextClassLoader().getResource(Definition.AGENT_CONFIG_FILE);

			AgentInfoListConfigIn agentConfig =  (AgentInfoListConfigIn)ums.unmarshal(url);
			List<AgentInfo> agentInfos = agentConfig.getAgents();
			AgentInfoValidate validator = new AgentInfoValidate();
			for (int ai = 0; ai < agentInfos.size(); ai++) {
				AgentInfo agentInfo = agentInfos.get(ai);
				try {
					validator.checkValidation(agentInfo);
					for (SwitchInfo switchInfo : agentInfo.getSwitches()) {
						instance.setAgentClient(switchInfo.getDeviceName(), switchInfo.getIp(), switchInfo.getOfcUrl(), agentInfo.getIp());
					}
				} catch (ValidateException ve) {
					logger.error(ve.getClass().getName() + ": [" + ai +  "]." + ve.getMessage());
				}
			}
		} catch (Exception e) {
			logger.error(e);
		} catch (Throwable t) {
			logger.error(t);
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, instance));
		}
		return instance;
	}

	public void setAgentClient(String deviceName, String switchIp, String ofcUrl, String agentIp) {
		String fname = "setAgentClient";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s, switchIp=%s, ofcUrl=%s, agentIp=%s) - start", fname, deviceName, switchIp, ofcUrl, agentIp));
		}

		AgentClient agentClient = null;
		for (AgentClient client : ipTable.values()) {
			if (client.getIp().equals(agentIp)) {
				agentClient = client;
				break;
			}
		}
		if (agentClient == null) {
			agentClient = new AgentClientImpl(agentIp);
		}
		ipTable.put(switchIp, agentClient);
		nameToIp.put(deviceName, switchIp);
		switchToOfc.put(switchIp, ofcUrl);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

	public AgentClient getAgentClient(String ip) throws AgentManagerException {
		String fname = "getAgentClient";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ip=%s) - start", fname, ip));
		}
		AgentClient ret = this.ipTable.get(ip);
		if (ret == null) {
			throw new AgentManagerException(String.format(ErrorMessage.NOT_FOUND, "OF-Patch Agent-" + ip));
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) -end", fname, ret));
		}
		return ret;
	}

	public String getSwitchIp(String deviceName) throws AgentManagerException {
		String fname = "getSwitchIp";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s) - start", fname, deviceName));
		}
		String ret = this.nameToIp.get(deviceName);
		if (StringUtils.isBlank(ret)) {
			throw new AgentManagerException(String.format(ErrorMessage.NOT_FOUND, "OF-Patch Switch(" + deviceName + ")"));
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) -end", fname, ret));
		}
		return ret;
	}

	public String getOfcUrl(String switchIp) throws AgentManagerException {
		String fname = "getOfcIp";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ip=%s) - start", fname, switchIp));
		}
		String ret = this.switchToOfc.get(switchIp);
		if (StringUtils.isBlank(ret)) {
			throw new AgentManagerException(String.format(ErrorMessage.NOT_FOUND, "OF-Patch Controlelr(managing-" + switchIp + ")"));
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) -end", fname, ret));
		}
		return ret;
	}
}
