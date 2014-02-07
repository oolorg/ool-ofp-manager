package ool.com.ofpm.service;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
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
	Response doGet();

	@GET
	@Path("/{switchId}")
	Response doGet(@PathParam("switchId") String switchId);

	@PUT
	@Path("/")
	Response doPut(@RequestBody String body);

	@DELETE
	@Path("/")
	Response doDelete(@RequestBody String body);
}
