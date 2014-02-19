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
		String debugMsg = null;
		if(logger.isDebugEnabled()) {
			debugMsg = String.format("getLogicalTopology(\"%s\")", deviceNames);
			logger.debug(debugMsg + " - start");
		}

		String[] splitedDeviceNames = deviceNames.split(",");
		LogicalBusiness logiBiz = new LogicalBusinessImpl();
		LogicalTopologyJsonInOut outPara = logiBiz.getLogicalTopology(splitedDeviceNames);

		Type type = new TypeToken<LogicalTopologyJsonInOut>(){}.getType();
		String res = this.gson.toJson(outPara, type);

		if(logger.isDebugEnabled()) {
			logger.debug(debugMsg + " - end");
		}

		return ResponseGenerator.generate(res,  Status.OK);
	}

	@Override
	public Response updateLogicalTopology(String params) {
		Type type = new TypeToken<LogicalTopology>(){}.getType();
		LogicalTopology inPara = this.gson.fromJson(params, type);

		LogicalBusiness logiBiz = new LogicalBusinessImpl();
		BaseResponse outPara = logiBiz.updateLogicalTopology(inPara);

		type = new TypeToken<BaseResponse>(){}.getType();
		String res = this.gson.toJson(outPara, type);
		System.out.println("HTTP[PUT]:" + params + " , res:" + res);
		return ResponseGenerator.generate(res,  Status.OK);
	}

}

