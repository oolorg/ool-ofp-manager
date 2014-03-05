package ool.com.ofpm.service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import ool.com.ofpm.business.AgentManager;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
@Path("/config")
public class ConfigServiceImpl {
	private static final Logger logger = Logger.getLogger(ConfigServiceImpl.class);

	@Path("/gdb")
	@GET
	public String gdbConfig(@Context HttpServletRequest req) {
		String fname = "gdbConfig";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(req=%s) - start", fname, req));

//		String ip             = req.getParameter("ip");
//		String linkGetPath    = req.getParameter("linkGetPath");
//		String linkCreatePath = req.getParameter("linkCreatePath");
//		String linkDeletePath = req.getParameter("linkDeletePath");
//		//if(ip != "") Definition.GRAPH_DB_ADDRESS           = "http://" + ip;
//		if(ip != "") Definition.GRAPH_DB_LINK_GET_PATH          = linkGetPath;
//		if(ip != "") Definition.GRAPH_DB_LINK_CREATE_PATH  = linkCreatePath;
//		if(ip != "") Definition.GRAPH_DB_LINK_DELETE_PATH  = linkDeletePath;

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(req=%s) - start", fname, req));
		return "Success";
	}

	@Path("/agent")
	@GET
	public String doGET(HttpServletRequest req) {
		String fname = "doGET";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(req=%s) - start", fname, req));

		String deviceName = req.getParameter("deviceName").trim();
		String switchIp   = req.getParameter("switchIp").trim();
		String agentIp    = req.getParameter("agentIp").trim();
		String ofcUrl     = req.getParameter("ofcUrl").trim();
		AgentManager amb = AgentManager.getInstance();
		amb.setAgentClient(deviceName, switchIp, agentIp, ofcUrl);
		return "Setting";
	}
}
