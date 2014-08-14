package ool.com.ofpm.business;

import static ool.com.constants.ErrorMessage.*;
import static ool.com.constants.OfpmDefinition.*;
import static ool.com.constants.OrientDBDefinition.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.common.BaseResponse;
import ool.com.ofpm.json.device.DeviceInfoCreateJsonIn;
import ool.com.ofpm.json.device.DeviceInfoUpdateJsonIn;
import ool.com.ofpm.json.device.DeviceManagerGetConnectedPortInfoJsonOut;
import ool.com.ofpm.json.device.PortInfoCreateJsonIn;
import ool.com.ofpm.json.device.PortInfoUpdateJsonIn;
import ool.com.ofpm.utils.Config;
import ool.com.ofpm.utils.ConfigImpl;
import ool.com.ofpm.utils.OFPMUtils;
import ool.com.ofpm.validate.common.BaseValidate;
import ool.com.ofpm.validate.device.DeviceInfoCreateJsonInValidate;
import ool.com.ofpm.validate.device.DeviceInfoUpdateJsonInValidate;
import ool.com.ofpm.validate.device.PortInfoCreateJsonInValidate;
import ool.com.ofpm.validate.device.PortInfoUpdateJsonInValidate;
import ool.com.orientdb.client.ConnectionUtilsJdbc;
import ool.com.orientdb.client.ConnectionUtilsJdbcImpl;
import ool.com.orientdb.client.Dao;
import ool.com.orientdb.client.DaoImpl;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;


public class DeviceBusinessImpl implements DeviceBusiness {
	private static final Logger logger = Logger.getLogger(DeviceBusinessImpl.class);

	Config conf = new ConfigImpl();

	/*
	 * (non-Javadoc)
	 * @see ool.com.ofpm.business.DeviceBusiness#createDevice(java.lang.String)
	 */
	public String createDevice(String newDeviceInfoJson) {
		String fname = "createDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(newDeviceInfoJson=%s) - start", fname, newDeviceInfoJson));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: Convert to DeviceInfoCreateJsonIn from json and validation check. */
		DeviceInfoCreateJsonIn deviceInfo = null;
		try {
			deviceInfo = DeviceInfoCreateJsonIn.fromJson(newDeviceInfoJson);
			DeviceInfoCreateJsonInValidate validator = new DeviceInfoCreateJsonInValidate();
			validator.checkValidation(deviceInfo);
		} catch (JsonSyntaxException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);

			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return res.toString();
		} catch (ValidateException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(e.getMessage());

			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return res.toString();
		}

		/* PHASE 2: Add node info to ofpdb */
		ConnectionUtilsJdbc utils = null;
		Connection conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);
			int stat = dao.createNodeInfo(
					conn,
					deviceInfo.getDeviceName(),
					deviceInfo.getDeviceType(),
					deviceInfo.getDatapathId(),
					deviceInfo.getOfcIp());
			if (stat == DB_RESPONSE_STATUS_EXIST) {
				res.setStatus(STATUS_BAD_REQUEST);
				res.setMessage(String.format(ALREADY_EXIST, deviceInfo.getDeviceName()));
				utils.rollback(conn);
			} else {
				res.setStatus(STATUS_CREATED);
				utils.commit(conn);
			}

		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);

			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		} finally {
			utils.close(conn);
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.ofpm.business.DeviceBusiness#deleteDevice(java.lang.String)
	 */
	public String deleteDevice(String deviceName) {
		String fname = "deleteDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s) - start", fname, deviceName));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: check validation */
		try {
			BaseValidate.checkStringBlank(deviceName);
		} catch (ValidateException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(e.getMessage());

			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		/* PHASE 2: Delete node from ofp db */
		ConnectionUtilsJdbc utils = null;
		Connection conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);
			int status = dao.deleteNodeInfo(conn, deviceName);
			if (status == DB_RESPONSE_STATUS_NOT_FOUND) {
				res.setStatus(DB_RESPONSE_STATUS_NOT_FOUND);
				res.setMessage(String.format(NOT_FOUND, deviceName));
			} else if (status == STATUS_FORBIDDEN) {
				res.setStatus(STATUS_FORBIDDEN);
				res.setMessage(String.format(IS_PATCHED, deviceName));
			} else if (status == DB_RESPONSE_STATUS_FAIL) {
				res.setStatus(STATUS_INTERNAL_ERROR);
				res.setMessage(String.format(COULD_NOT_DELETE, deviceName));
			}  else {
				res.setStatus(STATUS_SUCCESS);
			}

			if (status == DB_RESPONSE_STATUS_OK) {
				utils.commit(conn);
			} else {
				utils.rollback(conn);
			}
		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);

			OFPMUtils.logErrorStackTrace(logger, e);
    		res.setStatus(STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
		} finally {
			utils.close(conn);
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, res));
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.ofpm.business.DeviceBusiness#updateDevice(java.lang.String, java.lang.String)
	 */
	public String updateDevice(String deviceName, String updateDeviceInfoJson) {

		String fname = "updateDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s, newDeviceInfoJson=%s) - start", fname, deviceName, updateDeviceInfoJson));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: json -> DeviceInfoUpdateJosnIn and check validation */
		DeviceInfoUpdateJsonIn newDeviceInfo = null;
		try {
			newDeviceInfo = DeviceInfoUpdateJsonIn.fromJson(updateDeviceInfoJson);
			DeviceInfoUpdateJsonInValidate validator = new DeviceInfoUpdateJsonInValidate();
			validator.checkValidation(deviceName, newDeviceInfo);
		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		ConnectionUtilsJdbc utils = null;
		Connection conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);

			int status = dao.updateNodeInfo(
					conn,
					deviceName,
					newDeviceInfo.getDeviceName(),
					newDeviceInfo.getDatapathId(),
					newDeviceInfo.getOfcIp());
			if (status == DB_RESPONSE_STATUS_NOT_FOUND) {
				res.setStatus(STATUS_NOTFOUND);
				res.setMessage(String.format(NOT_FOUND, deviceName));
			} else if (status == DB_RESPONSE_STATUS_EXIST) {
				res.setStatus(STATUS_CONFLICT);
				res.setMessage(String.format(ALREADY_EXIST, newDeviceInfo.getDeviceName()));
			} else {
				res.setStatus(STATUS_SUCCESS);
			}

			if (status == STATUS_SUCCESS) {
				utils.commit(conn);
			} else {
				utils.rollback(conn);
			}
		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);

			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		} finally {
			utils.close(conn);
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.ofpm.business.DeviceBusiness#createPort(java.lang.String, java.lang.String)
	 */
	public String createPort(String deviceName, String newPortInfoJson) {
		String fname = "createPort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(newPortInfoJson=%s) - start", fname, newPortInfoJson));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: json -> obj and validation check */
		PortInfoCreateJsonIn portInfo = null;
		try {
			portInfo = PortInfoCreateJsonIn.fromJson(newPortInfoJson);
			PortInfoCreateJsonInValidate validator = new PortInfoCreateJsonInValidate();
			validator.checkValidation(deviceName ,portInfo);
		} catch (JsonSyntaxException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		} catch (ValidateException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(e.getMessage());
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		/* PHASE 2: */
		ConnectionUtilsJdbc utils = null;
		Connection conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);
			int status = dao.createPortInfo(
					conn,
					portInfo.getPortName(),
					portInfo.getPortNumber(),
					deviceName);
			if ( status == DB_RESPONSE_STATUS_EXIST) {
				res.setStatus(STATUS_BAD_REQUEST);
				res.setMessage(String.format(ALREADY_EXIST, portInfo.getPortName()));
			} else if ( status == DB_RESPONSE_STATUS_NOT_FOUND) {
				res.setStatus(STATUS_NOTFOUND);
				res.setMessage(String.format(NOT_FOUND, deviceName));
			} else {
				res.setStatus(STATUS_CREATED);
			}
			if (status == DB_RESPONSE_STATUS_OK) {
				utils.commit(conn);
			} else {
				utils.rollback(conn);
			}
		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);

    		OFPMUtils.logErrorStackTrace(logger, e);
    		res.setStatus(STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
		} finally {
			utils.close(conn);
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.ofpm.business.DeviceBusiness#deletePort(java.lang.String, java.lang.String)
	 */
	public String deletePort(String deviceName, String portName) {
		String fname = "deletePort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s, portName=%s) - start", fname, deviceName, portName));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: check validation */
		try {
			BaseValidate.checkStringBlank(deviceName);
			BaseValidate.checkStringBlank(portName);
		} catch (ValidateException ve) {
			String message = String.format(IS_BLANK, "portName or deviceName");
			logger.error(ve.getClass().getName() + ": " + message);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(message);
		}

		/* PHASE 2: Delete port from ofp db */
		ConnectionUtilsJdbc utils = null;
		Connection conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);
			int status = dao.deletePortInfo(
					conn,
					portName,
					deviceName);
			if (status == DB_RESPONSE_STATUS_NOT_FOUND) {
				res.setStatus(DB_RESPONSE_STATUS_NOT_FOUND);
				res.setMessage(String.format(NOT_FOUND, deviceName + "." + portName));
			} else if (status == DB_RESPONSE_STATUS_FORBIDDEN) {
				res.setStatus(STATUS_FORBIDDEN);
				res.setMessage(String.format(IS_PATCHED, deviceName + "." + portName));
			} else if (status == DB_RESPONSE_STATUS_FAIL) {
				res.setStatus(STATUS_INTERNAL_ERROR);
				res.setMessage(String.format(COULD_NOT_DELETE, deviceName + "." + portName));
			} else {
				res.setStatus(STATUS_SUCCESS);
			}

			if (status == STATUS_SUCCESS) {
				utils.commit(conn);
			} else {
				utils.rollback(conn);
			}
		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);

			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		} finally {
			utils.close(conn);
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.ofpm.business.DeviceBusiness#updatePort(java.lang.String, java.lang.String, java.lang.String)
	 */
	public String updatePort(String deviceName, String portName, String updatePortInfoJson) {
		String fname = "updatePort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s, portName=%s, updatePortInfoJson=%s) - start", fname, deviceName, portName, updatePortInfoJson));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: json -> obj and check validation */
		PortInfoUpdateJsonIn portInfo = null;
		try {
			portInfo = PortInfoUpdateJsonIn.fromJson(updatePortInfoJson);
			PortInfoUpdateJsonInValidate validator = new PortInfoUpdateJsonInValidate();
			validator.checkValidation(deviceName, portName, portInfo);
		} catch (JsonSyntaxException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		} catch (ValidateException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(e.getMessage());
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		/* PHASE 2: update port info */
		ConnectionUtilsJdbc utils = null;
		Connection conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);
			int status = dao.updatePortInfo(
					conn,
					portName,
					deviceName,
					portInfo.getPortName(),
					portInfo.getPortNumber());

			if (status == DB_RESPONSE_STATUS_NOT_FOUND) {
				res.setStatus(STATUS_NOTFOUND);
				res.setMessage(String.format(NOT_FOUND, portName));
			} else if (status == DB_RESPONSE_STATUS_EXIST) {
				res.setStatus(STATUS_CONFLICT);
				res.setMessage(String.format(ALREADY_EXIST, portInfo.getPortName()));
			} else {
				res.setStatus(STATUS_SUCCESS);
			}

			if (status == DB_RESPONSE_STATUS_OK) {
				utils.commit(conn);
			} else {
				utils.rollback(conn);
			}
		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);

    		OFPMUtils.logErrorStackTrace(logger, e);
    		res.setStatus(STATUS_INTERNAL_ERROR);
    		res.setMessage(e.getMessage());
		} finally {
			utils.close(conn);
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
		DeviceManagerGetConnectedPortInfoJsonOut outPara = new DeviceManagerGetConnectedPortInfoJsonOut();

		try {
			BaseValidate.checkStringBlank(deviceName);
		} catch (ValidateException e) {
			String message = String.format(IS_BLANK, "portName or deviceName");
			logger.error(e.getClass().getName() + ": " + message);
			outPara.setStatus(STATUS_BAD_REQUEST);
			outPara.setMessage(message);
		}

		ConnectionUtilsJdbc utils = null;
		Connection conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);
			DeviceManagerGetConnectedPortInfoJsonOut.ResultData resultData = null;
			DeviceManagerGetConnectedPortInfoJsonOut.ResultData.LinkData linkData = null;

			Map<String, Object> devMap = dao.getNodeInfoFromDeviceName(conn, deviceName);
			if (devMap == null) {
				outPara.setStatus(STATUS_NOTFOUND);
				outPara.setMessage(String.format(NOT_FOUND, deviceName));
				return outPara.toJson();
			}
			String devName = (String) devMap.get("name");
			String devType = (String) devMap.get("type");
			List<Map<String, Object>> portMapList = dao.getPortInfoListFromDeviceName(conn, deviceName);
			if (portMapList == null) {
				portMapList = new ArrayList<Map<String, Object>>();
			}

			Map<String, Map<String, Object>> devCache = new HashMap<String, Map<String, Object>>();
			for (Map<String, Object> portMap: portMapList) {
				Map<String, Object> nghbrPortMap = dao.getNeighborPortFromPortRid(conn, (String)portMap.get("rid"));
				if (nghbrPortMap == null) {
					continue;
				}
				String nghbrDevName = (String) nghbrPortMap.get("deviceName");
				Map<String, Object> nghbrDevMap = devCache.get(nghbrDevName);
				if (nghbrDevMap == null) {
					nghbrDevMap = dao.getNodeInfoFromDeviceName(conn, nghbrDevName);
					if (nghbrDevMap == null) {
						outPara.setStatus(STATUS_NOTFOUND);
						outPara.setMessage(String.format(NOT_FOUND, deviceName));
						return outPara.toJson();
					}
					devCache.put(nghbrDevName, nghbrDevMap);
				}

				resultData = outPara.new ResultData();

				linkData = resultData.new LinkData();
				linkData.setDeviceName(devName);
				linkData.setDeviceType(devType);
				linkData.setPortName((String)portMap.get("name"));
				linkData.setPortNumber((Integer)portMap.get("number"));
				if (StringUtils.equals(devType, NODE_TYPE_LEAF) || StringUtils.endsWith(devType, NODE_TYPE_SPINE)) {
					linkData.setOfpFlag(OFP_FLAG_TRUE);
				} else {
					linkData.setOfpFlag(OFP_FLAG_FALSE);
				}
				resultData.addLinkData(linkData);

				String nghbrDevType = (String) nghbrDevMap.get("type");
				linkData = resultData.new LinkData();
				linkData.setDeviceName(nghbrDevName);
				linkData.setDeviceType(nghbrDevType);
				linkData.setPortName((String)nghbrPortMap.get("name"));
				linkData.setPortNumber((Integer)nghbrPortMap.get("number"));
				if (StringUtils.equals(nghbrDevType, NODE_TYPE_LEAF) || StringUtils.equals(nghbrDevType, NODE_TYPE_SPINE)) {
					linkData.setOfpFlag(OFP_FLAG_TRUE);
				} else {
					linkData.setOfpFlag(OFP_FLAG_FALSE);
				}
				resultData.addLinkData(linkData);

				outPara.addResultData(resultData);
			}
			outPara.setStatus(STATUS_SUCCESS);
			return outPara.toJson();
		}catch (SQLException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			if (e.getCause() == null) {
				outPara.setStatus(STATUS_INTERNAL_ERROR);
			} else {
				outPara.setStatus(STATUS_NOTFOUND);
			}
    		outPara.setMessage(e.getMessage());
			return outPara.toJson();
		}  catch (RuntimeException re) {
			OFPMUtils.logErrorStackTrace(logger, re);
			outPara.setStatus(STATUS_INTERNAL_ERROR);
			outPara.setMessage(re.getMessage());
			return outPara.toJson();
		}  finally {
			utils.rollback(conn);
			utils.close(conn);

			String ret = outPara.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("getConnectedPortInfo(ret=%s) - end ", ret));
			}
		}
	}
}
