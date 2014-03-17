package ool.com.ofpm.business;

import ool.com.ofpm.client.GraphDBClient;
import ool.com.ofpm.client.OrientDBClientImpl;
import ool.com.ofpm.exception.GraphDBClientException;
import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.PortInfoCreateJsonIn;
import ool.com.ofpm.json.PortInfoUpdateJsonIn;
import ool.com.ofpm.utils.Definition;
import ool.com.ofpm.utils.ErrorMessage;
import ool.com.ofpm.validate.CommonValidate;
import ool.com.ofpm.validate.PortInfoCreateJsonInValidate;
import ool.com.ofpm.validate.PortInfoUpdateJsonInValidate;

import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;

public class PortBusinessImpl implements PortBusiness {
	private static final Logger logger = Logger.getLogger(PortBusinessImpl.class);

	public String createPort(String newPortInfoJson) {
		String fname = "createPort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(newPortInfoJson=%s) - start", fname, newPortInfoJson));
		}

		BaseResponse res = new BaseResponse();
		try {
			PortInfoCreateJsonIn portInfo = PortInfoCreateJsonIn.fromJson(newPortInfoJson);

			PortInfoCreateJsonInValidate validator = new PortInfoCreateJsonInValidate();
			validator.checkValidation(portInfo);

			GraphDBClient gdbClient = OrientDBClientImpl.getInstance();
			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.portCreates(portInfo=%s) - called", portInfo.toJson()));
			}
			res = gdbClient.portCreate(portInfo);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.portCreates(ret=%s) - returned", res));
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

	public String deletePort(String portName, String deviceName) {
		String fname = "deletePort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(portName=%s, deviceName=%s) - start", fname, portName, deviceName));
		}

		BaseResponse res = new BaseResponse();
		try {
			CommonValidate validator = new CommonValidate();
			validator.checkStringBlank(portName);
			validator.checkStringBlank(deviceName);

			GraphDBClient gdbClient = OrientDBClientImpl.getInstance();
			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.portDelete(portName=%s, deviceName=%s) - called", portName, deviceName));
			}
			res = gdbClient.portDelete(portName, deviceName);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.portDelete(ret=%s) - returned", res.toJson()));
			}
		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ErrorMessage.INVALID_JSON);
		} catch (ValidateException ve) {
			String message = String.format(ErrorMessage.IS_BLANK, "portName or deviceName");
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

	public String updatePort(String updatePortInfoJson) {
		String fname = "updatePort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(updatePortInfoJson=%s) - start", fname, updatePortInfoJson));
		}

		BaseResponse res = new BaseResponse();
		try {
			PortInfoUpdateJsonIn portInfo = PortInfoUpdateJsonIn.fromJson(updatePortInfoJson);

			PortInfoUpdateJsonInValidate validator = new PortInfoUpdateJsonInValidate();
			validator.checkValidation(portInfo);

			GraphDBClient gdbClient = OrientDBClientImpl.getInstance();
			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.portUpdates(portInfo=%s) - called", portInfo.toJson()));
			}
			res = gdbClient.portUpdate(portInfo);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.portUpdates(ret=%s) - returned", res.toJson()));
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
