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

@Path("/logical_topology")
public interface LogicalService {
	/**
	 * Get LogicalTopology 
	 * @param deviceNamesCSV String list of deviceName split comma
	 * @param tokenId String auth key
	 * @return Http Response
	 */
	@GET
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getLogicalTopology(@QueryParam("deviceNames") String deviceNamesCSV, @QueryParam("tokenId") String tokenId);

	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateLogicalTopology(@RequestBody String requestedTopologyJson);
}
