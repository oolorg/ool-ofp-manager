package ool.com.ofpm.service;

import java.lang.reflect.Type;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ool.com.ofpm.business.PhysicalBusiness;
import ool.com.ofpm.business.PhysicalBusinessImpl;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.ResultOut;
import ool.com.ofpm.service.utils.ResponseGenerator;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component
public class PhysicalServiceImpl implements PhysicalService {
	Gson gson = new Gson();

	@Override
	public Response getPhysicalTopology() {
		PhysicalBusiness physBiz = new PhysicalBusinessImpl();
		ResultOut outPara = physBiz.getPhysicalTopology();

		Type type = new TypeToken<ResultOut>(){}.getType();
		String res = this.gson.toJson(outPara, type);
		return ResponseGenerator.generate(res,  Status.OK);
	}

	@Override
	public Response updatePhysicalTopology(String params) {
		Type type = new TypeToken<ResultOut.Data>(){}.getType();
		ResultOut.Data inPara = this.gson.fromJson(params, type);

		PhysicalBusiness physBiz = new PhysicalBusinessImpl();
		BaseResponse outPara = physBiz.updatePhysicalTopology();

		type = new TypeToken<BaseResponse>(){}.getType();
		String res = this.gson.toJson(outPara, type);
		return ResponseGenerator.generate(res,  Status.OK);
	}
	public Response get(
			@PathParam("switchId") String switchId) {
		// TODO Auto-generated method stub
		return null;
	}

}

