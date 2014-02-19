package ool.com.ofpm.service;

import java.lang.reflect.Type;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ool.com.ofpm.business.LogicalBusiness;
import ool.com.ofpm.business.LogicalBusinessImpl;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.LogicalTopology;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;
import ool.com.ofpm.service.utils.ResponseGenerator;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/*
 * 指摘の修正
 * ログ Log4j
 * 単体テスト
 */

@Component
public class LogicalServiceImpl implements LogicalService {
	private static final Logger logger = Logger.getLogger(LogicalServiceImpl.class);
	Gson gson = new Gson();

	@Override
	public Response getLogicalTopology(String deviceNames) {
		String fname = "getLogicalTopology";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(deviceNames=%s) - start", fname, deviceNames));

		String[] splitedDeviceNames = deviceNames.split(",");
		LogicalBusiness logiBiz = new LogicalBusinessImpl();
		LogicalTopologyJsonInOut outPara = logiBiz.getLogicalTopology(splitedDeviceNames);

		Type type = new TypeToken<LogicalTopologyJsonInOut>(){}.getType();
		String resPara = this.gson.toJson(outPara, type);

		Response res = ResponseGenerator.generate(resPara, Status.OK);
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

	@Override
	public Response updateLogicalTopology(String params) {
		String fname = "updateLogicalTopology";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(params=%s) - start", fname, params));

		Type type = new TypeToken<LogicalTopology>(){}.getType();
		LogicalTopology inPara = this.gson.fromJson(params, type);

		LogicalBusiness logiBiz = new LogicalBusinessImpl();
		BaseResponse outPara = logiBiz.updateLogicalTopology(inPara);

		type = new TypeToken<BaseResponse>(){}.getType();
		String resPara = this.gson.toJson(outPara, type);

		Response res = ResponseGenerator.generate(resPara, Status.OK);
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

}

