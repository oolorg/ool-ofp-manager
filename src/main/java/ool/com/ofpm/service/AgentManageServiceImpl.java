package ool.com.ofpm.service;

import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;

import ool.com.ofpm.business.AgentManager;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class AgentManageServiceImpl implements AgentManageService {
	private static final Logger logger = Logger.getLogger(AgentManageServiceImpl.class);

	@Override
	public String doGET(String deviceName, String switchIp, String agentIp, String ofcIp) {
		String fname = "doGET";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(deviceName=%s, switchIp=%s, agentIp=%s, ofcIp=%s) - start", fname, deviceName, switchIp, agentIp, ofcIp));

		AgentManager amb = AgentManager.getInstance();
		amb.setAgentClient(deviceName, switchIp, agentIp, ofcIp);

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=Setting) - end", fname));
		return "Setting";
	}

	@Override
	@PUT
	public Response doPUT(@RequestBody String params) {
		String fname = "createDevice";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(params=%s) - start", fname, params));

		Response res = null;
		// TODO Auto-generated method stub
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}
}
