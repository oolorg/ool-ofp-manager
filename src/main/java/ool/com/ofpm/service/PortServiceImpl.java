package ool.com.ofpm.service;

import java.lang.reflect.Type;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ool.com.ofpm.business.PortBusiness;
import ool.com.ofpm.business.PortBusinessImpl;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.PortJsonIn;
import ool.com.ofpm.service.utils.ResponseGenerator;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component
public class PortServiceImpl implements PortService {
	private static final Logger logger = Logger.getLogger(ConfigServiceImpl.class);
	Gson gson = new Gson();

	@Override
	public Response createPort(String params) {
		String fname = "createPort";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(req=%s) - start", fname, params));

		Type type = new TypeToken<PortJsonIn>(){}.getType();
		PortJsonIn inPara = this.gson.fromJson(params, type);

		PortBusiness device_business = new PortBusinessImpl();
		BaseResponse outPara = device_business.createPort(inPara);

		type = new TypeToken<BaseResponse>(){}.getType();
		String resPara = this.gson.toJson(outPara, type);

		Response res = ResponseGenerator.generate(resPara, Status.OK);
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

	@Override
	public Response deletePort(String params) {
		String fname = "deletePort";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(req=%s) - start", fname, params));

		Type type = new TypeToken<PortJsonIn>(){}.getType();
		PortJsonIn inPara = this.gson.fromJson(params, type);

		PortBusiness device_business = new PortBusinessImpl();
		BaseResponse outPara = device_business.deletePort(inPara);

		type = new TypeToken<BaseResponse>(){}.getType();
		String resPara = this.gson.toJson(outPara, type);

		Response res = ResponseGenerator.generate(resPara, Status.OK);
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

	@Override
	public Response updatePort(String params) {
		String fname = "updatePort";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(req=%s) - start", fname, params));

		Type type = new TypeToken<PortJsonIn>(){}.getType();
		PortJsonIn inPara = this.gson.fromJson(params, type);

		PortBusiness device_business = new PortBusinessImpl();
		BaseResponse outPara = device_business.updatePort(inPara);

		type = new TypeToken<BaseResponse>(){}.getType();
		String resPara = this.gson.toJson(outPara, type);

		Response res = ResponseGenerator.generate(resPara, Status.OK);
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

}
