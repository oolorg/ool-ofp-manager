package ool.com.ofpm.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.web.bind.annotation.RequestBody;

// TODO 戻り値をResponse型にする：　HTTPヘッダを編集できないため
@Path("/physical_topology")
public interface PhysicalService {
	@GET
	@Path("/")
	public Response getPhysicalTopology();

	@PUT
	@Path("/")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
	public Response updatePhysicalTopology(@RequestBody String params);

}
