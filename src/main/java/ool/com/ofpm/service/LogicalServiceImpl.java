package ool.com.ofpm.service;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class LogicalServiceImpl implements LogicalService {

	@GET
	@Path("/{switchId}")
	public Response doGet(
			@PathParam("switchId") String switchId) {
		// TODO Auto-generated method stub
		return null;
	}

	@PUT
	@Path("/")
	public Response doPut(
			@RequestBody String body) {
		// TODO Auto-generated method stub
		return null;
	}

	@DELETE
	@Path("/")
	public Response doDelete(
			@RequestBody String body) {
		// TODO Auto-generated method stub
		return null;
	}
}

