package ool.com.ofpm.service;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.springframework.web.bind.annotation.RequestBody;

@Path("/LogicalTopology")
public interface LogicalService {
	@GET
	@Path("/{switchId}")
	public Response doGet(@PathParam("switchId") String switchId);
	@PUT
	@Path("/")
	public Response doPut(@RequestBody String body);
	@DELETE
	@Path("/")
	public Response doDelete(@RequestBody String body);
}
