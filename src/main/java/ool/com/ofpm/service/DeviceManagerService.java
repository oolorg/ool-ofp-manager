package ool.com.ofpm.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/deviceManager")
public interface DeviceManagerService {

	@GET
	@Path("/connectedPort")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_JSON })
	Response getConnectedPortInfo(@QueryParam("deviceName") String deviceName);
}
