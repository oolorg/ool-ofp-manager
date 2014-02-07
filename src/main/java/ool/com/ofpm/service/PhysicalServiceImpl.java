package ool.com.ofpm.service;

import java.lang.reflect.Type;

import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ool.com.ofpm.business.PhysicalServiceBypusBusiness;
import ool.com.ofpm.json.ResultOut;
import ool.com.ofpm.service.utils.ResponseGenerator;
import ool.com.ofpm.utils.Definition;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

//Path("/PhysicalTopology")
@Component
public class PhysicalServiceImpl implements PhysicalService {

	private Gson gson = new Gson();

	public Response doGet() {
		PhysicalServiceBypusBusiness bypus = new PhysicalServiceBypusBusiness(Definition.OLD_TEST_APP_ADDR);
		ResultOut result_out = bypus.get();
		Type collectionType = new TypeToken<ResultOut>(){}.getType();
		String res_str =  this.gson.toJson(result_out, collectionType);
		return ResponseGenerator.generate(res_str, Status.OK);
	}

	public Response doGet(String switchId){
		// TODO ↑のswitchIdは一時的なもの
		return null;
	}

	@PUT
	@Path("/")
	public Response doPut(@RequestBody String body) {
		// TODO Auto-generated method stub
		return null;
	}

	@DELETE
	@Path("/")
	public Response doDelete(@RequestBody String body) {
		// TODO Auto-generated method stub
		return null;
	}

}

