package ool.com.ofpm.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.web.bind.annotation.RequestBody;

@Path("/agent_manage")
public interface AgentManageService {
	@GET
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
	public String doGET(@QueryParam("deviceName") String deviceName, @QueryParam("switchIp") String switchIp, @QueryParam("agentIp") String agentIp, @QueryParam("ofcIp") String ofcIp);

	@PUT
	public Response doPUT(@RequestBody String params);
}
