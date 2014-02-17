package ool.com.ofpm.service;

import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ool.com.ofpm.business.LogicalBusiness;
import ool.com.ofpm.business.LogicalBusinessImpl;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.LogicalTopology;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;
import ool.com.ofpm.service.utils.ResponseGenerator;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component
public class LogicalServiceImpl implements LogicalService {
	Gson gson = new Gson();

	@Override
	public Response doGET(HttpServletRequest req) {
		String params = req.getParameter("deviceNames");
		params = params.replaceAll("^[,\\s]*", "");
		if(params.charAt(0) == ',') params = params.substring(1);
		String[] inPara = params.split("[\\s]*,[,\\s]*");

		LogicalBusiness logiBiz = new LogicalBusinessImpl();
		LogicalTopologyJsonInOut outPara = logiBiz.doGET(inPara);

		Type type = new TypeToken<LogicalTopologyJsonInOut>(){}.getType();
		String res = this.gson.toJson(outPara, type);
		System.out.println("HTTP[GET]:" + params + " , res:");
		return ResponseGenerator.generate(res,  Status.OK);
	}

	@Override
	@PUT
	public Response doPUT(String params) {
		Type type = new TypeToken<LogicalTopology>(){}.getType();
		LogicalTopology inPara = this.gson.fromJson(params, type);

		LogicalBusiness logiBiz = new LogicalBusinessImpl();
		BaseResponse outPara = logiBiz.doPUT(inPara);

		type = new TypeToken<BaseResponse>(){}.getType();
		String res = this.gson.toJson(outPara, type);
		return ResponseGenerator.generate(res,  Status.OK);
	}

}

