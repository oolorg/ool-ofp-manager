package ool.com.ofpm.business;

import ool.com.ofpm.client.GraphDBClient;
import ool.com.ofpm.client.OrientDBClientImpl;
import ool.com.ofpm.exception.GraphDBClientException;
import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.DeviceInfoCreateJsonIn;
import ool.com.ofpm.json.DeviceInfoUpdateJsonIn;
import ool.com.ofpm.utils.Definition;
import ool.com.ofpm.utils.ErrorMessage;
import ool.com.ofpm.validate.CommonValidate;
import ool.com.ofpm.validate.DeviceInfoCreateJsonInValidate;
import ool.com.ofpm.validate.DeviceInfoUpdateJsonInValidate;

import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;

public class DeviceBusinessImpl implements DeviceBusiness {
	private static final Logger logger = Logger.getLogger(DeviceBusinessImpl.class);

	public String createDevice(String newDeviceInfoJson) {
		String fname = "createDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(newDeviceInfoJson=%s) - start", fname, newDeviceInfoJson));
		}

		BaseResponse res = new BaseResponse();
		try {
			DeviceInfoCreateJsonIn deviceInfo = DeviceInfoCreateJsonIn.fromJson(newDeviceInfoJson);

			DeviceInfoCreateJsonInValidate validator = new DeviceInfoCreateJsonInValidate();
			validator.checkValidation(deviceInfo);

			GraphDBClient gdbClient = OrientDBClientImpl.getInstance();
			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.nodeCreate(deviceInfo=%s) - called", deviceInfo.toJson()));
			}
			res = gdbClient.nodeCreate(deviceInfo);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.nodeCreate(ret=%s) - returned", res.toJson()));
			}
		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ErrorMessage.INVALID_JSON);

		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());

		} catch (GraphDBClientException gdbe) {
			logger.error(gdbe);
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(gdbe.getMessage());

		} catch (Exception e) {
			logger.error(e);
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	public String deleteDevice(String deviceName) {
		String fname = "deleteDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s) - start", fname, deviceName));
		}

		BaseResponse res = new BaseResponse();
		try {
			CommonValidate validator = new CommonValidate();
			validator.checkStringBlank(deviceName);

			GraphDBClient gdbClient = OrientDBClientImpl.getInstance();
			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.nodeDelete(deviceName=%s) - called", deviceName));
			}
			res = gdbClient.nodeDelete(deviceName);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.nodeDelete(ret=%s) - returned", res.toJson()));
			}
		} catch (ValidateException ve) {
			String message = String.format(ErrorMessage.IS_BLANK, "deviceName");
			logger.error(ve.getClass().getName() + ": " + message);
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(message);

		} catch (GraphDBClientException gdbe) {
			logger.error(gdbe);
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(gdbe.getMessage());

		} catch (Exception e) {
			logger.error(e);
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	public String updateDevice(String updateDeviceInfoJson) {
		String fname = "updateDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(newDeviceInfoJson=%s) - start", fname, updateDeviceInfoJson));
		}

		BaseResponse res = new BaseResponse();
		try {
			DeviceInfoUpdateJsonIn newDeviceInfo = DeviceInfoUpdateJsonIn.fromJson(updateDeviceInfoJson);

			DeviceInfoUpdateJsonInValidate validator = new DeviceInfoUpdateJsonInValidate();
			validator.checkValidation(newDeviceInfo);

			GraphDBClient gdbClient = OrientDBClientImpl.getInstance();
			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.nodeUpdate(deviceInfo=%s) - called", newDeviceInfo.toJson()));
			}
			res = gdbClient.nodeUpdate(newDeviceInfo);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.nodeUpdate(ret=%s) - returned", res.toJson()));
			}
		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ErrorMessage.INVALID_JSON);
		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
		} catch (GraphDBClientException gdbe) {
			logger.error(gdbe);
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(gdbe.getMessage());
		} catch (Exception e) {
			logger.error(e);
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}
}
