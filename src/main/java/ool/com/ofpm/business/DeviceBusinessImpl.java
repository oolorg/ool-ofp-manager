package ool.com.ofpm.business;

import ool.com.ofpm.client.GraphDBClient;
import ool.com.ofpm.client.GraphDBClientException;
import ool.com.ofpm.client.OrientDBClientImpl;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.DeviceInfoJsonInOut;
import ool.com.ofpm.utils.Definition;
import ool.com.ofpm.validate.DeviceInfoInValidate;
import ool.com.ofpm.validate.ValidateException;

import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;

public class DeviceBusinessImpl implements DeviceBusiness {
	private static final Logger logger = Logger.getLogger(DeviceBusinessImpl.class);

	public String createDevice(String deviceInfoJson) {
		String fname = "createDevice";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(params=%s) - start", fname, deviceInfoJson));

		BaseResponse res = new BaseResponse();

		try {
			DeviceInfoInValidate validator = new DeviceInfoInValidate();
			validator.checkStringBlank(deviceInfoJson);
			DeviceInfoJsonInOut deviceInfo = DeviceInfoJsonInOut.fromJson(deviceInfoJson);
			validator.checkValidation(deviceInfo);


			GraphDBClient gdbClient = OrientDBClientImpl.getInstance();
			if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.nodeCreate(deviceInfo=%s) - called", deviceInfo));
			res = gdbClient.nodeCreate(deviceInfo);
			if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.nodeCreate(ret=%s) - returned", res));

		} catch (JsonSyntaxException jse) {
			logger.error(jse.getMessage());
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage("Invalid json structure.");

		} catch (ValidateException ve) {
			logger.error(ve.getMessage());
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());

		} catch (GraphDBClientException gdbe) {
			logger.error(gdbe.getMessage());
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(gdbe.getMessage());

		} catch (Exception e) {
			logger.error(e.getMessage());
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		}

		String resBody = res.toJson();

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return resBody;
	}

	public String deleteDevice(String params) {
		String fname = "deleteDevice";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(params=%s) - start", fname, params));

		String res = "";

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

	public String updateDevice(String params) {
		String fname = "updateDevice";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(params=%s) - start", fname, params));

		String res = "";

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

}
