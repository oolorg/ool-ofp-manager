package ool.com.ofpm.business;

import java.sql.SQLException;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.PortInfoCreateJsonIn;
import ool.com.ofpm.json.PortInfoUpdateJsonIn;
import ool.com.ofpm.utils.Config;
import ool.com.ofpm.utils.ConfigImpl;
import ool.com.ofpm.validate.CommonValidate;
import ool.com.ofpm.validate.PortInfoCreateJsonInValidate;
import ool.com.ofpm.validate.PortInfoUpdateJsonInValidate;
import ool.com.orientdb.client.ConnectionUtils;
import ool.com.orientdb.client.ConnectionUtilsImpl;
import ool.com.orientdb.client.Dao;
import ool.com.orientdb.client.DaoImpl;
import ool.com.util.Definition;
import ool.com.util.ErrorMessage;

import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;

public class PortBusinessImpl implements PortBusiness {
	private static final Logger logger = Logger.getLogger(PortBusinessImpl.class);

	Config conf = new ConfigImpl();

	public String createPort(String newPortInfoJson) {
		String fname = "createPort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(newPortInfoJson=%s) - start", fname, newPortInfoJson));
		}

		BaseResponse res = new BaseResponse();
		Dao dao = null;
		try {
			PortInfoCreateJsonIn portInfo = PortInfoCreateJsonIn.fromJson(newPortInfoJson);

			PortInfoCreateJsonInValidate validator = new PortInfoCreateJsonInValidate();
			validator.checkValidation(portInfo);

			ConnectionUtils utils = new ConnectionUtilsImpl();
			dao = new DaoImpl(utils);
			int status = dao.createPortInfo(portInfo.getPortName(), portInfo.getPortNumber(), portInfo.getType(), portInfo.getDeviceName());

			if ( status == Definition.DB_RESPONSE_STATUS_EXIST) {
				res.setStatus(Definition.STATUS_BAD_REQUEST);
				res.setMessage(String.format(ErrorMessage.ALREADY_EXIST, portInfo.getPortName()));
			} else if ( status == Definition.DB_RESPONSE_STATUS_NOT_FOUND) {
				res.setStatus(Definition.STATUS_NOTFOUND);
				res.setMessage(String.format(ErrorMessage.NOT_FOUND, portInfo.getDeviceName()));
			}
			else {
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

	public String deletePort(String portName, String deviceName) {
		String fname = "deletePort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(portName=%s, deviceName=%s) - start", fname, portName, deviceName));
		}

		BaseResponse res = new BaseResponse();
		Dao dao = null;
		try {
			CommonValidate validator = new CommonValidate();
			validator.checkStringBlank(portName);
			validator.checkStringBlank(deviceName);

			ConnectionUtils utils = new ConnectionUtilsImpl();
			dao = new DaoImpl(utils);

			int status = dao.deletePortInfo(portName, deviceName);
			if (status == Definition.DB_RESPONSE_STATUS_NOT_FOUND) {
				res.setStatus(Definition.DB_RESPONSE_STATUS_NOT_FOUND);
				res.setMessage(String.format(ErrorMessage.NOT_FOUND, portName));
			} else if (status == Definition.DB_RESPONSE_STATUS_FORBIDDEN) {
				res.setStatus(Definition.DB_RESPONSE_STATUS_FORBIDDEN);
				res.setMessage(String.format(ErrorMessage.IS_PATCHED, portName));
			} else {
				res.setStatus(Definition.STATUS_SUCCESS);
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

	public String updatePort(String updatePortInfoJson) {
		String fname = "updatePort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(updatePortInfoJson=%s) - start", fname, updatePortInfoJson));
		}

		BaseResponse res = new BaseResponse();
		Dao dao = null;
		try {
			PortInfoUpdateJsonIn portInfo = PortInfoUpdateJsonIn.fromJson(updatePortInfoJson);

			PortInfoUpdateJsonInValidate validator = new PortInfoUpdateJsonInValidate();
			validator.checkValidation(portInfo);

			ConnectionUtils utils = new ConnectionUtilsImpl();
			dao = new DaoImpl(utils);

			int status = dao.updatePortInfo(
					portInfo.getPortName(),
					portInfo.getDeviceName(),
					portInfo.getParams().getPortName(),
					portInfo.getParams().getPortNumber(),
					portInfo.getParams().getType());

			if (status == Definition.DB_RESPONSE_STATUS_NOT_FOUND) {
				res.setStatus(Definition.STATUS_NOTFOUND);
				res.setMessage(String.format(ErrorMessage.NOT_FOUND, portInfo.getPortName()));
			} else if (status == Definition.DB_RESPONSE_STATUS_EXIST) {
				res.setStatus(Definition.STATUS_CONFLICT);
				res.setMessage(String.format(ErrorMessage.ALREADY_EXIST, portInfo.getPortName()));
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
