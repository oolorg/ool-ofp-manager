package ool.com.ofpm.service;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ool.com.ofpm.business.LogicalBusiness;
import ool.com.ofpm.business.LogicalBusinessImpl;
import ool.com.ofpm.service.utils.ResponseGenerator;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/*
 * 指摘の修正
 * ログ Log4j
 * 単体テスト
 */

@Component
public class LogicalServiceImpl implements LogicalService {
	private static final Logger logger = Logger.getLogger(LogicalServiceImpl.class);

	@Override
	public Response getLogicalTopology(String deviceNamesCSV) {
		String fname = "getLogicalTopology";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(deviceNamesCSV=%s) - start", fname, deviceNamesCSV));

		LogicalBusiness logiBiz = new LogicalBusinessImpl();
		String resLogiBiz = logiBiz.getLogicalTopology(deviceNamesCSV);

		Response res = ResponseGenerator.generate(resLogiBiz, Status.OK);
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

	@Override
	public Response updateLogicalTopology(String requestedTopologyJson) {
		String fname = "updateLogicalTopology";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(requestedTopologyJson=%s) - start", fname, requestedTopologyJson));

		LogicalBusiness logiBiz = new LogicalBusinessImpl();
		String resLogiBiz = logiBiz.updateLogicalTopology(requestedTopologyJson);

		Response res = ResponseGenerator.generate(resLogiBiz, Status.OK);
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

	@Override
	public Response allowConnection() {
		Response res = ResponseGenerator.generate("", Status.OK);
		return res;
	}
}

