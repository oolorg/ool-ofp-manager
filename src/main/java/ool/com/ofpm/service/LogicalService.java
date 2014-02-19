package ool.com.ofpm.service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.web.bind.annotation.RequestBody;

@Path("/logical_topology")
public interface LogicalService {
	@GET
    @Produces({ MediaType.APPLICATION_JSON })
	public Response doGET(@Context HttpServletRequest req);

	@PUT
	@Path("/")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public Response doPUT(@RequestBody String params);
}
