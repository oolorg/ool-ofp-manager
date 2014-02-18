package ool.com.ofpm.service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import ool.com.ofpm.business.AgentManageBusiness;
import ool.com.ofpm.business.AgentManageBusinessImpl;
import ool.com.ofpm.utils.Definition;

import org.springframework.stereotype.Component;

@Component
@Path("/config")
public class ConfigServiceImpl {
	@Path("/gdb")
	@GET
	public String gdbConfig(@Context HttpServletRequest req) {
		String ip             = req.getParameter("ip");
		String linkGetPath    = req.getParameter("linkGetPath");
		String linkCreatePath = req.getParameter("linkCreatePath");
		String linkDeletePath = req.getParameter("linkDeletePath");
		if(ip != "") Definition.GRAPH_DB_ADDRESS           = "http://" + ip;
		if(ip != "") Definition.GRAPH_DB_LINK_GET          = linkGetPath;
		if(ip != "") Definition.GRAPH_DB_LINK_CREATE_PATH  = linkCreatePath;
		if(ip != "") Definition.GRAPH_DB_LINK_DELETE_PATH  = linkDeletePath;
		return "Success";
	}

	@Path("/agent")
	@GET
	public String doGET(HttpServletRequest req) {
		String deviceName = req.getParameter("deviceName").trim();
		String switchIp   = req.getParameter("switchIp").trim();
		String agentIp    = req.getParameter("agentIp").trim();
		String ofcUrl     = req.getParameter("ofcUrl").trim();
		AgentManageBusiness amb = AgentManageBusinessImpl.getInstance();
		amb.setAgentClient(deviceName, switchIp, agentIp, ofcUrl);
		return "Setting";
	}
}
