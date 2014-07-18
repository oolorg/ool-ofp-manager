package ool.com.ofpm.business;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;

import static ool.com.constants.OfpmDefinition.*;
import static ool.com.constants.OrientDBDefinition.*;
import static ool.com.constants.ErrorMessage.*;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.common.BaseResponse;
import ool.com.ofpm.json.device.DeviceInfoCreateJsonIn;
import ool.com.ofpm.json.device.DeviceInfoUpdateJsonIn;
import ool.com.ofpm.json.device.DeviceManagerGetConnectedPortInfoJsonOut;
import ool.com.ofpm.json.device.PortInfoCreateJsonIn;
import ool.com.ofpm.json.device.PortInfoUpdateJsonIn;
import ool.com.ofpm.utils.Config;
import ool.com.ofpm.utils.ConfigImpl;
import ool.com.ofpm.validate.common.BaseValidate;
import ool.com.ofpm.validate.device.DeviceInfoCreateJsonInValidate;
import ool.com.ofpm.validate.device.DeviceInfoUpdateJsonInValidate;
import ool.com.ofpm.validate.device.PortInfoCreateJsonInValidate;
import ool.com.ofpm.validate.device.PortInfoUpdateJsonInValidate;
import ool.com.orientdb.client.ConnectionUtils;
import ool.com.orientdb.client.ConnectionUtilsImpl;
import ool.com.orientdb.client.Dao;
import ool.com.orientdb.client.DaoImpl;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.orientechnologies.orient.core.record.impl.ODocument;


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
			if (dao.createNodeInfo(deviceInfo.getDeviceName(), deviceInfo.getDeviceType(), deviceInfo.getOfpFlag()) == DB_RESPONSE_STATUS_EXIST) {
				res.setStatus(STATUS_BAD_REQUEST);
				res.setMessage(String.format(ALREADY_EXIST, deviceInfo.getDeviceName()));
			} else {
				res.setStatus(STATUS_CREATED);
			}
		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);

		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());

		} catch (SQLException e) {
    		logger.error(e.getMessage());
    		res.setStatus(STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
		}  catch (RuntimeException re) {
			logger.error(re.getMessage());
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(re.getMessage());
		} finally {
			try {
				if(dao != null) {
					dao.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
				res.setStatus(STATUS_INTERNAL_ERROR);
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
			BaseValidate.checkStringBlank(deviceName);

			ConnectionUtils utils = new ConnectionUtilsImpl();
			dao = new DaoImpl(utils);

			int status = dao.deleteDeviceInfo(deviceName);
			if (status == DB_RESPONSE_STATUS_NOT_FOUND) {
				res.setStatus(DB_RESPONSE_STATUS_NOT_FOUND);
				res.setMessage(String.format(NOT_FOUND, deviceName));
			} else if (status == STATUS_FORBIDDEN) {
				res.setStatus(STATUS_FORBIDDEN);
				res.setMessage(String.format(IS_PATCHED, deviceName));
			} else {
				res.setStatus(STATUS_SUCCESS);
			}

		} catch (ValidateException ve) {
			String message = String.format(IS_BLANK, "deviceName");
			logger.error(ve.getClass().getName() + ": " + message);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(message);

		} catch (SQLException e) {
    		logger.error(e.getMessage());
    		res.setStatus(STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
		}  catch (RuntimeException re) {
			logger.error(re.getMessage());
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(re.getMessage());
		} finally {
			try {
				if(dao != null) {
					dao.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
				res.setStatus(STATUS_INTERNAL_ERROR);
				res.setMessage(e.getMessage());
			}
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	public String updateDevice(String deviceName, String updateDeviceInfoJson) {

		String fname = "updateDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s, newDeviceInfoJson=%s) - start", fname, deviceName, updateDeviceInfoJson));
		}

		BaseResponse res = new BaseResponse();
		Dao dao = null;
		try {
			DeviceInfoUpdateJsonIn newDeviceInfo = DeviceInfoUpdateJsonIn.fromJson(updateDeviceInfoJson);

			DeviceInfoUpdateJsonInValidate validator = new DeviceInfoUpdateJsonInValidate();
			validator.checkValidation(deviceName, newDeviceInfo);

			ConnectionUtils utils = new ConnectionUtilsImpl();
			dao = new DaoImpl(utils);

//			int status = dao.updateNodeInfo(
//					newDeviceInfo.getDeviceName(),
//					newDeviceInfo.getParams().getDeviceName(),
//					newDeviceInfo.getParams().getOfpFlag());
			int status = dao.updateNodeInfo(
					deviceName,
					newDeviceInfo.getDeviceName(),
					newDeviceInfo.getOfpFlag());
			if (status == DB_RESPONSE_STATUS_NOT_FOUND) {
				res.setStatus(STATUS_NOTFOUND);
				res.setMessage(String.format(NOT_FOUND, newDeviceInfo.getDeviceName()));
			} else if (status == DB_RESPONSE_STATUS_EXIST) {
				res.setStatus(STATUS_CONFLICT);
				res.setMessage(String.format(ALREADY_EXIST, newDeviceInfo.getDeviceName()));
			} else {
				res.setStatus(STATUS_CREATED);
			}
		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
		} catch (SQLException e) {
			logger.error(e.getMessage());
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		}  catch (RuntimeException re) {
			logger.error(re.getMessage());
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(re.getMessage());
		} finally {
			try {
				if(dao != null) {
					dao.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
				res.setStatus(STATUS_INTERNAL_ERROR);
				res.setMessage(e.getMessage());
			}
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

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

			if ( status == DB_RESPONSE_STATUS_EXIST) {
				res.setStatus(STATUS_BAD_REQUEST);
				res.setMessage(String.format(ALREADY_EXIST, portInfo.getPortName()));
			} else if ( status == DB_RESPONSE_STATUS_NOT_FOUND) {
				res.setStatus(STATUS_NOTFOUND);
				res.setMessage(String.format(NOT_FOUND, portInfo.getDeviceName()));
			}
			else {
				res.setStatus(STATUS_CREATED);
			}
		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
		} catch (SQLException e) {
    		logger.error(e.getMessage());
    		res.setStatus(STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
		}  catch (RuntimeException re) {
			logger.error(re.getMessage());
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(re.getMessage());
		} finally {
			try {
				if(dao != null) {
					dao.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
				res.setStatus(STATUS_INTERNAL_ERROR);
				res.setMessage(e.getMessage());
			}
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	public String deletePort(String deviceName, String portName) {
		String fname = "deletePort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s, portName=%s) - start", fname, deviceName, portName));
		}

		BaseResponse res = new BaseResponse();
		Dao dao = null;
		try {
			BaseValidate.checkStringBlank(deviceName);
			BaseValidate.checkStringBlank(portName);

			ConnectionUtils utils = new ConnectionUtilsImpl();
			dao = new DaoImpl(utils);

			int status = dao.deletePortInfo(portName, deviceName);
			if (status == DB_RESPONSE_STATUS_NOT_FOUND) {
				res.setStatus(DB_RESPONSE_STATUS_NOT_FOUND);
				res.setMessage(String.format(NOT_FOUND, portName));
			} else if (status == DB_RESPONSE_STATUS_FORBIDDEN) {
				res.setStatus(DB_RESPONSE_STATUS_FORBIDDEN);
				res.setMessage(String.format(IS_PATCHED, portName));
			} else {
				res.setStatus(STATUS_SUCCESS);
			}
		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
		} catch (ValidateException ve) {
			String message = String.format(IS_BLANK, "portName or deviceName");
			logger.error(ve.getClass().getName() + ": " + message);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(message);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		}  catch (RuntimeException re) {
			logger.error(re.getMessage());
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(re.getMessage());
		} finally {
			try {
				if(dao != null) {
					dao.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
				res.setStatus(STATUS_INTERNAL_ERROR);
				res.setMessage(e.getMessage());
			}
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	public String updatePort(String deviceName, String portName, String updatePortInfoJson) {
		String fname = "updatePort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s, portName=%s, updatePortInfoJson=%s) - start", fname, deviceName, portName, updatePortInfoJson));
		}

		BaseResponse res = new BaseResponse();
		Dao dao = null;
		try {
			PortInfoUpdateJsonIn portInfo = PortInfoUpdateJsonIn.fromJson(updatePortInfoJson);

			PortInfoUpdateJsonInValidate validator = new PortInfoUpdateJsonInValidate();
			validator.checkValidation(deviceName, portName, portInfo);

			ConnectionUtils utils = new ConnectionUtilsImpl();
			dao = new DaoImpl(utils);

//			int status = dao.updatePortInfo(
//					portInfo.getPortName(),
//					portInfo.getDeviceName(),
//					portInfo.getParams().getPortName(),
//					portInfo.getParams().getPortNumber(),
//					portInfo.getParams().getType());
			int status = dao.updatePortInfo(
					portName,
					deviceName,
					portInfo.getPortName(),
					portInfo.getPortNumber(),
					portInfo.getType());

			if (status == DB_RESPONSE_STATUS_NOT_FOUND) {
				res.setStatus(STATUS_NOTFOUND);
				res.setMessage(String.format(NOT_FOUND, portInfo.getPortName()));
			} else if (status == DB_RESPONSE_STATUS_EXIST) {
				res.setStatus(STATUS_CONFLICT);
				res.setMessage(String.format(ALREADY_EXIST, portInfo.getPortName()));
			} else {
				res.setStatus(STATUS_CREATED);
			}
		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
		} catch (SQLException e) {
    		logger.error(e.getMessage());
    		res.setStatus(STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
		}  catch (RuntimeException re) {
			logger.error(re.getMessage());
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(re.getMessage());
		} finally {
			try {
				if(dao != null) {
					dao.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
				res.setStatus(STATUS_INTERNAL_ERROR);
				res.setMessage(e.getMessage());
			}
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}


	/* (non-Javadoc)
	 * @see ool.com.orientdb.business.DeviceManagerBusiness#getConnectedPortInfo(java.lang.String)
	 */
	@Override
	public String getConnectedPortInfo(String deviceName) {
    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("getConnectedPortInfo(params=%s) - start ", deviceName));
    	}

    	Gson gson = new Gson();

		String ret = "";
		Dao dao = null;
		DeviceManagerGetConnectedPortInfoJsonOut outPara = new DeviceManagerGetConnectedPortInfoJsonOut();
		DeviceManagerGetConnectedPortInfoJsonOut.ResultData resultData;
		DeviceManagerGetConnectedPortInfoJsonOut.ResultData.LinkData linkData;

		try {
        	ConnectionUtils utils = new ConnectionUtilsImpl();
        	dao = new DaoImpl(utils);

        	List<ODocument> documents = dao.getPortList(deviceName);

        	ODocument targetNodeInfo = dao.getDeviceInfo(deviceName);

        	for (ODocument document : documents) {
        		// list connected port with target device
        		List<ODocument> connectedPorts = dao.getConnectedLinks(document.getIdentity().toString());
        		// check connected port num
        		// if 1, no connected
        		if (connectedPorts.size() <= 1) {
        			continue;
        		}

        		resultData = outPara.new ResultData();
        		linkData = resultData.new LinkData();

            	linkData.setDeviceName(targetNodeInfo.field("name").toString());
    			linkData.setDeviceType(targetNodeInfo.field("type").toString());
    			linkData.setOfpFlag(targetNodeInfo.field("ofpFlag").toString());
    			linkData.setPortName(document.field("name").toString());
    			linkData.setPortNumber(Integer.parseInt(document.field("number").toString()));
    			resultData.addLinkData(linkData);

        		for (ODocument connectedPort : connectedPorts) {
        			try {
        				ODocument port = connectedPort.field("out");
        				ODocument portInfo = dao.getPortInfo(port.getIdentity().toString());

        				linkData = resultData.new LinkData();
        				linkData.setPortName(portInfo.field("name").toString());
        				linkData.setPortNumber(Integer.parseInt(portInfo.field("number").toString()));
        				ODocument nodeInfo = dao.getDeviceInfo(portInfo.field("deviceName").toString());
        				linkData.setDeviceName(nodeInfo.field("name").toString());
        				linkData.setDeviceType(nodeInfo.field("type").toString());
        				linkData.setOfpFlag(nodeInfo.field("ofpFlag").toString());
        				resultData.addLinkData(linkData);
        			} catch (SQLException sqlex) {
        				if (sqlex.getCause() == null) {
        					throw sqlex;
        				}
        			}
        		}
        		outPara.addResultData(resultData);
        	}
        	outPara.setStatus(STATUS_SUCCESS);
    	} catch (SQLException e) {
    		logger.error(e.getMessage());
			if (e.getCause() == null) {
				outPara.setStatus(STATUS_INTERNAL_ERROR);
			} else {
				outPara.setStatus(STATUS_NOTFOUND);
			}
    		outPara.setMessage(e.getMessage());
		}  catch (RuntimeException re) {
			logger.error(re.getMessage());
			outPara.setStatus(STATUS_INTERNAL_ERROR);
			outPara.setMessage(re.getMessage());
		} finally {
			try {
				if(dao != null) {
					dao.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
				outPara.setStatus(STATUS_INTERNAL_ERROR);
				outPara.setMessage(e.getMessage());
			}
			Type type = new TypeToken<DeviceManagerGetConnectedPortInfoJsonOut>(){}.getType();
	        ret = gson.toJson(outPara, type);
		}
		if (logger.isDebugEnabled()) {
    		logger.debug(String.format("getConnectedPortInfo(ret=%s) - end ", ret));
    	}
		return ret;
	}
}
