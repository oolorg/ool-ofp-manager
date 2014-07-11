package ool.com.ofpm.business;

import java.sql.SQLException;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.DeviceInfoCreateJsonIn;
import ool.com.ofpm.json.DeviceInfoUpdateJsonIn;
import ool.com.ofpm.utils.Config;
import ool.com.ofpm.utils.ConfigImpl;
import ool.com.ofpm.validate.CommonValidate;
import ool.com.ofpm.validate.DeviceInfoCreateJsonInValidate;
import ool.com.ofpm.validate.DeviceInfoUpdateJsonInValidate;
import ool.com.orientdb.client.ConnectionUtils;
import ool.com.orientdb.client.ConnectionUtilsImpl;
import ool.com.orientdb.client.Dao;
import ool.com.orientdb.client.DaoImpl;
import ool.com.util.Definition;
import ool.com.util.ErrorMessage;

import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;

public class DeviceBusinessImpl implements DeviceBusiness {
	private static final Logger logger = Logger.getLogger(DeviceBusinessImpl.class);

	Config conf = new ConfigImpl();

	public String createDevice(String newDeviceInfoJson) {
		String fname = "createDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(newDeviceInfoJson=%s) - start", fname, newDeviceInfoJson));
		}

		BaseResponse res = new BaseResponse();
		Dao dao = null;

		try {
			DeviceInfoCreateJsonIn deviceInfo = DeviceInfoCreateJsonIn.fromJson(newDeviceInfoJson);

			DeviceInfoCreateJsonInValidate validator = new DeviceInfoCreateJsonInValidate();
			validator.checkValidation(deviceInfo);

			ConnectionUtils utils = new ConnectionUtilsImpl();
			dao = new DaoImpl(utils);
			if (dao.createNodeInfo(deviceInfo.getDeviceName(), deviceInfo.getDeviceType(), deviceInfo.getOfpFlag()) == Definition.DB_RESPONSE_STATUS_EXIST) {
				res.setStatus(Definition.STATUS_BAD_REQUEST);
				res.setMessage(String.format(ErrorMessage.ALREADY_EXIST, deviceInfo.getDeviceName()));
			} else {
				res.setStatus(Definition.STATUS_CREATED);
			}
		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ErrorMessage.INVALID_JSON);

		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());

		} catch (SQLException e) {
    		logger.error(e.getMessage());
    		res.setStatus(Definition.STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
		}  catch (RuntimeException re) {
			logger.error(re.getMessage());
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(re.getMessage());
		} finally {
			try {
				if(dao != null) {
					dao.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
				res.setStatus(Definition.STATUS_INTERNAL_ERROR);
				res.setMessage(e.getMessage());
			}
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
		Dao dao = null;

		try {
			CommonValidate validator = new CommonValidate();
			validator.checkStringBlank(deviceName);

			ConnectionUtils utils = new ConnectionUtilsImpl();
			dao = new DaoImpl(utils);

			int status = dao.deleteDeviceInfo(deviceName);
			if (status == Definition.DB_RESPONSE_STATUS_NOT_FOUND) {
				res.setStatus(Definition.DB_RESPONSE_STATUS_NOT_FOUND);
				res.setMessage(String.format(ErrorMessage.NOT_FOUND, deviceName));
			} else if (status == Definition.STATUS_FORBIDDEN) {
				res.setStatus(Definition.STATUS_FORBIDDEN);
				res.setMessage(String.format(ErrorMessage.IS_PATCHED, deviceName));
			} else {
				res.setStatus(Definition.STATUS_SUCCESS);
			}

		} catch (ValidateException ve) {
			String message = String.format(ErrorMessage.IS_BLANK, "deviceName");
			logger.error(ve.getClass().getName() + ": " + message);
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(message);

		} catch (SQLException e) {
    		logger.error(e.getMessage());
    		res.setStatus(Definition.STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
		}  catch (RuntimeException re) {
			logger.error(re.getMessage());
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(re.getMessage());
		} finally {
			try {
				if(dao != null) {
					dao.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
				res.setStatus(Definition.STATUS_INTERNAL_ERROR);
				res.setMessage(e.getMessage());
			}
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
		Dao dao = null;
		try {
			DeviceInfoUpdateJsonIn newDeviceInfo = DeviceInfoUpdateJsonIn.fromJson(updateDeviceInfoJson);

			DeviceInfoUpdateJsonInValidate validator = new DeviceInfoUpdateJsonInValidate();
			validator.checkValidation(newDeviceInfo);

			ConnectionUtils utils = new ConnectionUtilsImpl();
			dao = new DaoImpl(utils);

			int status = dao.updateNodeInfo(
					newDeviceInfo.getDeviceName(),
					newDeviceInfo.getParams().getDeviceName(),
					newDeviceInfo.getParams().getOfpFlag());
			if (status == Definition.DB_RESPONSE_STATUS_NOT_FOUND) {
				res.setStatus(Definition.STATUS_NOTFOUND);
				res.setMessage(String.format(ErrorMessage.NOT_FOUND, newDeviceInfo.getDeviceName()));
			} else if (status == Definition.DB_RESPONSE_STATUS_EXIST) {
				res.setStatus(Definition.STATUS_CONFLICT);
				res.setMessage(String.format(ErrorMessage.ALREADY_EXIST, newDeviceInfo.getDeviceName()));
			} else {
				res.setStatus(Definition.STATUS_CREATED);
			}
		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ErrorMessage.INVALID_JSON);
		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
		} catch (SQLException e) {
			logger.error(e.getMessage());
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		}  catch (RuntimeException re) {
			logger.error(re.getMessage());
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(re.getMessage());
		} finally {
			try {
				if(dao != null) {
					dao.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
				res.setStatus(Definition.STATUS_INTERNAL_ERROR);
				res.setMessage(e.getMessage());
			}
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}
}
