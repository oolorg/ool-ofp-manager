package ool.com.ofpm.service;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.springframework.web.bind.annotation.RequestBody;

// TODO 戻り値をResponse型にする：　HTTPヘッダを編集できないため
@Path("/PhysicalTopology")
public interface PhysicalService {
	@GET
	@Path("/")
	public Response get();

	@GET
	@Path("/{switchId}")
	public Response get(@PathParam("switchId") String switchId);

	@POST
	@Path("/Device/")
	public Response createDevice(@RequestBody String body);

	@DELETE
	@Path("/Device/")
	public Response deleteDevice(@RequestBody String body);

	@PUT
	@Path("/Device/")
	public Response updateDevice(@RequestBody String body);

	@POST
	@Path("/Port/")
	public Response createPort(@RequestBody String body);

	@DELETE
	@Path("/Port/")
	public Response deletePort(@RequestBody String body);

	@PUT
	@Path("/Port/")
	public Response updatePort(@RequestBody String body);
}
