package ool.com.ofpm.service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.springframework.web.bind.annotation.RequestBody;

@Path("/agent_manage")
public interface AgentManageService {
	@GET
	public String doGET(@Context HttpServletRequest req);
	@PUT
	public Response doPUT(@RequestBody String params);
}
