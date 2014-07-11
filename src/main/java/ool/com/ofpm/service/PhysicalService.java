package ool.com.ofpm.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.RequestBody;

@Path("/physical_topology")
public interface PhysicalService {
	@GET
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_JSON })
	public String getPhysicalTopology(@QueryParam("deviceNames") String deviceNamesCSV, @QueryParam("tokenId") String tokenId);

	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updatePhysicalTopology(@RequestBody String requestedTopologyJson);

	@POST
	@Path("/connect")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String connectPhysicalLink(@RequestBody String physicalLinkJson);

	@POST
	@Path("/disconnect")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String disconnectPhysicalLink(@RequestBody String physicalLinkJson);
}
