package ool.com.ofpm.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.web.bind.annotation.RequestBody;

@Path("/sample")
public interface HelloWorldService {
	
    @GET
    @Path("/hello/{message}")
    @Produces({ MediaType.APPLICATION_JSON })
    String sayHello(@PathParam("message") String message, @Context HttpServletRequest req, @Context HttpServletResponse res);
    
    @GET
    @Path("/hello")
    String allHello();
    
    @POST
    @Path("/hello/create")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    Response createHello(@RequestBody String params);
}
