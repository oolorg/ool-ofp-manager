package ool.com.ofpm.service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;

import ool.com.ofpm.business.AgentManageBusiness;
import ool.com.ofpm.business.AgentManageBusinessImpl;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class AgentManageServiceImpl implements AgentManageService {

	@Override
	public String doGET(HttpServletRequest req) {
		String deviceName = req.getParameter("deviceName").trim();
		String switchIp   = req.getParameter("switchIp").trim();
		String agentIp    = req.getParameter("agentIp").trim();
		String ofcIp      = req.getParameter("ofcIp").trim();
		AgentManageBusiness amb = AgentManageBusinessImpl.getInstance();
		amb.setAgentClient(deviceName, switchIp, agentIp, ofcIp);
		return "Setting";
	}

	@Override
	@PUT
	public Response doPUT(@RequestBody String params) {

		// TODO Auto-generated method stub
		return null;
	}
}
