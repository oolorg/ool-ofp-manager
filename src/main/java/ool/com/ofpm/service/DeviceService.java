package ool.com.ofpm.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.RequestBody;

@Path("/device_mng")
public interface DeviceService {
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String createDevice(@RequestBody String newDeviceInfoJson);

	@DELETE
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_JSON })
	public String deleteDevice(@QueryParam("deviceName") String deviceName);

	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateDevice(@RequestBody String updateDeviceInfoJson);
}
