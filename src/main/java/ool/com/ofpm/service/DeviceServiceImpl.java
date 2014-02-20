package ool.com.ofpm.service;

import java.lang.reflect.Type;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ool.com.ofpm.business.DeviceBusiness;
import ool.com.ofpm.business.DeviceBusinessImpl;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.DeviceJsonIn;
import ool.com.ofpm.service.utils.ResponseGenerator;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component
public class DeviceServiceImpl implements DeviceService {
	private static final Logger logger = Logger.getLogger(ConfigServiceImpl.class);
	Gson gson = new Gson();

	@Override
	public Response createDevice(String params) {
		String fname = "createDevice";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(req=%s) - start", fname, params));

		Type type = new TypeToken<DeviceJsonIn>(){}.getType();
		DeviceJsonIn inPara = this.gson.fromJson(params, type);

		DeviceBusiness device_business = new DeviceBusinessImpl();
		BaseResponse outPara = device_business.createDevice(inPara);

		type = new TypeToken<BaseResponse>(){}.getType();
		String res = this.gson.toJson(outPara, type);
		return ResponseGenerator.generate(res,  Status.OK);
	}

	@Override
	public Response deleteDevice(String params) {
		String fname = "deleteDevice";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(req=%s) - start", fname, params));

		Type type = new TypeToken<DeviceJsonIn>(){}.getType();
		DeviceJsonIn inPara = this.gson.fromJson(params, type);

		DeviceBusiness device_business = new DeviceBusinessImpl();
		BaseResponse outPara = device_business.deleteDevice(inPara);

		type = new TypeToken<BaseResponse>(){}.getType();
		String res = this.gson.toJson(outPara, type);
		return ResponseGenerator.generate(res,  Status.OK);
	}

	@Override
	public Response updateDevice(String params) {
		String fname = "updateDevice";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(req=%s) - start", fname, params));

		Type type = new TypeToken<DeviceJsonIn>(){}.getType();
		DeviceJsonIn inPara = this.gson.fromJson(params, type);

		DeviceBusiness device_business = new DeviceBusinessImpl();
		BaseResponse outPara = device_business.updateDevice(inPara);

		type = new TypeToken<BaseResponse>(){}.getType();
		String res = this.gson.toJson(outPara, type);
		return ResponseGenerator.generate(res,  Status.OK);
	}

}
