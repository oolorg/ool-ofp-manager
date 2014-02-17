package ool.com.ofpm.service;

import java.lang.reflect.Type;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ool.com.ofpm.business.PortBusiness;
import ool.com.ofpm.business.PortBusinessImpl;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.PortJsonIn;
import ool.com.ofpm.service.utils.ResponseGenerator;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component
public class PortServiceImpl implements PortService {
	Gson gson = new Gson();

	@Override
	public Response createPort(String params) {
		Type type = new TypeToken<PortJsonIn>(){}.getType();
		PortJsonIn inPara = this.gson.fromJson(params, type);

		PortBusiness portBiz = new PortBusinessImpl();
		BaseResponse outPara = portBiz.createPort(inPara);

		type = new TypeToken<BaseResponse>(){}.getType();
		String res = this.gson.toJson(outPara, type);
		return ResponseGenerator.generate(res,  Status.OK);
	}

	@Override
	public Response deletePort(String params) {
		Type type = new TypeToken<PortJsonIn>(){}.getType();
		PortJsonIn inPara = this.gson.fromJson(params, type);

		PortBusiness portBiz = new PortBusinessImpl();
		BaseResponse outPara = portBiz.deletePort(inPara);

		type = new TypeToken<BaseResponse>(){}.getType();
		String res = this.gson.toJson(outPara, type);
		return ResponseGenerator.generate(res,  Status.OK);
	}

	@Override
	public Response updatePort(String params) {
		Type type = new TypeToken<PortJsonIn>(){}.getType();
		PortJsonIn inPara = this.gson.fromJson(params, type);

		PortBusiness portBiz = new PortBusinessImpl();
		BaseResponse outPara = portBiz.updatePort(inPara);

		type = new TypeToken<BaseResponse>(){}.getType();
		String res = this.gson.toJson(outPara, type);
		return ResponseGenerator.generate(res,  Status.OK);
	}

}
