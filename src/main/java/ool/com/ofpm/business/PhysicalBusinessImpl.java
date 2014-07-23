package ool.com.ofpm.business;

import static ool.com.constants.ErrorMessage.*;
import static ool.com.constants.OfpmDefinition.*;
import static ool.com.constants.OrientDBDefinition.*;

import java.sql.SQLException;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.common.BaseResponse;
import ool.com.ofpm.json.device.PortData;
import ool.com.ofpm.json.topology.physical.ConnectPhysicalLinksJsonIn;
import ool.com.ofpm.json.topology.physical.DisconnectPhysicalLinksJsonIn;
import ool.com.ofpm.json.topology.physical.PhysicalLink;
import ool.com.ofpm.utils.Config;
import ool.com.ofpm.utils.ConfigImpl;
import ool.com.ofpm.validate.topology.physical.ConnectPhysicalLinksJsonInValidate;
import ool.com.ofpm.validate.topology.physical.DisconnectPhysicalLinksJsonInValidate;
import ool.com.orientdb.client.ConnectionUtils;
import ool.com.orientdb.client.ConnectionUtilsImpl;
import ool.com.orientdb.client.Dao;
import ool.com.orientdb.client.DaoImpl;

import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class PhysicalBusinessImpl implements PhysicalBusiness {
	private static final Logger logger = Logger.getLogger(PhysicalBusinessImpl.class);

	Config conf = new ConfigImpl();

/*
	public String getPhysicalTopology(String deviceNamesCSV, String tokenId) {
		String fname = "getPhysicalTopology";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceNames=%s, tokenId%s) - start", fname, deviceNamesCSV, tokenId));
		}

//		PhysicalTopologyGetJsonOut res = new PhysicalTopologyGetJsonOut();
		String openamUrl = conf.getString(Definition.OPEN_AM_URL);
		OpenAmClient openAmClient = new OpenAmClientImpl(openamUrl);
		try {
			CommonValidate validator = new CommonValidate();
			validator.checkStringBlank(deviceNamesCSV);
			List<String> deviceNames = Arrays.asList(deviceNamesCSV.split(Definition.CSV_SPLIT_REGEX));
			validator.checkArrayStringBlank(deviceNames);
			validator.checkArrayOverlapped(deviceNames);

			validator.checkStringBlank(tokenId);

			boolean isTokenValid = false;
			if (openAmClient != null) {
				TokenValidChkOut tokenValidchkOut = openAmClient.tokenValidateCheck(tokenId);
				isTokenValid = tokenValidchkOut.getIsTokenValid();
			}
			if (isTokenValid != true) {
				if (logger.isDebugEnabled()) {
					logger.error(String.format("Invalid tokenId. tokenId=%s", tokenId));
				}
				res.setStatus(Definition.STATUS_BAD_REQUEST);
				res.setMessage(String.format("Invalid tokenId. tokenId=%s", tokenId));
				String ret = res.toJson();
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("%s(ret=%s) - end", fname, ret));
				}
				return ret;
			}

			List<Node> nodes = new ArrayList<Node>();
			for (String deviceName : deviceNames) {
				Node node = new Node();
				node.setDeviceName(deviceName);
				nodes.add(node);
			}

			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.getLogicalTopology(nodes=%s) - called", nodes));
			}
//			res = graphDBClient.getLogicalTopology(nodes);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.getLogicalTopology(ret=%s) - returned", res));
			}

			this.filterTopology(nodes, res.getResult());
		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());

		} catch (GraphDBClientException gdbe) {
			logger.error(gdbe);
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(gdbe.getMessage());

		} catch (OpenAmClientException oace) {
			logger.error(oace);
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(oace.getMessage());

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
*/


	@Override
	public String connectPhysicalLink(String physicalLinkJson) {
		final String fname = "connectPhysicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(physicalLinkJson=%s) - start", fname, physicalLinkJson));
		}

		BaseResponse res = new BaseResponse();
		Dao dao = null;
		try {
			ConnectPhysicalLinksJsonIn inParam = ConnectPhysicalLinksJsonIn.fromJson(physicalLinkJson);

			ConnectPhysicalLinksJsonInValidate validator = new ConnectPhysicalLinksJsonInValidate();
			validator.checkValidation(inParam);

			ConnectionUtils utils = new ConnectionUtilsImpl();
			dao = new DaoImpl(utils);

			for (PhysicalLink physicalLink : inParam.getLinks()) {
				PortData port0 = physicalLink.getLink().get(0);
				ODocument doc0 = dao.getPortInfo(port0.getPortName(), port0.getDeviceName());
				String    rid0 = doc0.getIdentity().toString();
				if (rid0.isEmpty()) {
					res.setMessage(String.format(NOT_FOUND, port0.getPortName() + "," + port0.getDeviceName()));
					res.setStatus(STATUS_NOTFOUND);
					String ret = res.toJson();
					return ret;
				}

				PortData port1 = physicalLink.getLink().get(1);
				ODocument doc1 = dao.getPortInfo(port1.getPortName(), port1.getDeviceName());
				String    rid1 = doc1.getIdentity().toString();
				if (rid1.isEmpty()) {
					res.setMessage(String.format(NOT_FOUND, port1.getPortName() + "," + port1.getDeviceName()));
					res.setStatus(STATUS_NOTFOUND);
					String ret = res.toJson();
					return ret;
				}

				int band = Integer.parseInt(physicalLink.getBand());

				if (dao.createLinkInfo(rid0, rid1, band, 0) == DB_RESPONSE_STATUS_EXIST) {
					res.setStatus(STATUS_CONFLICT);
					res.setMessage(String.format(ALREADY_EXIST, port0.getPortName() + "," + port1.getPortName()));
					String ret = res.toJson();
					return ret;
				}
				if (dao.createLinkInfo(rid1, rid0, band, 0) == DB_RESPONSE_STATUS_EXIST) {
					res.setStatus(STATUS_CONFLICT);
					res.setMessage(String.format(ALREADY_EXIST, port0.getPortName() + "," + port1.getPortName()));
					String ret = res.toJson();
					return ret;
				}
			}

			res.setStatus(STATUS_CREATED);
			return res.toJson();

		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			return res.toJson();

		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
			return res.toJson();

		} catch (SQLException e) {
    		logger.error(e.getMessage());
    		res.setMessage(e.getMessage());
    		if (e.getCause() == null) {
    			res.setStatus(STATUS_INTERNAL_ERROR);
    		} else {
    			res.setStatus(STATUS_NOTFOUND);
    		}
    		return res.toJson();

		}  catch (RuntimeException re) {
			logger.error(re.getMessage());
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(re.getMessage());
			return res.toJson();

		} finally {
			try {
				if(dao != null) {
					dao.close();
				}
			} catch (final SQLException e) {
				logger.error(e.getMessage());
				res.setStatus(STATUS_INTERNAL_ERROR);
				res.setMessage(e.getMessage());
			}
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
	}

	@Override
	public String disconnectPhysicalLink(String physicalLinkJson) {
		final String fname = "disconnectPhysicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(physicalLinkJson=%s) - start", fname, physicalLinkJson));
		}

		BaseResponse res = new BaseResponse();
		Dao dao = null;
		try {
			DisconnectPhysicalLinksJsonIn disconPhysicalLinks = new DisconnectPhysicalLinksJsonIn();

			DisconnectPhysicalLinksJsonInValidate validator = new DisconnectPhysicalLinksJsonInValidate();
			validator.checkValidation(disconPhysicalLinks);

			ConnectionUtils utils = new ConnectionUtilsImpl();
			dao = new DaoImpl(utils);

			for (PhysicalLink physicalLink : disconPhysicalLinks.getLinks()) {
				PortData port0 = physicalLink.getLink().get(0);
				ODocument doc0 = dao.getPortInfo(port0.getPortName(), port0.getDeviceName());
				String    rid0 = doc0.getIdentity().toString();
				if (rid0.isEmpty()) {
					res.setMessage(String.format(NOT_FOUND, port0.getPortName() + "," + port0.getDeviceName()));
					res.setStatus(STATUS_NOTFOUND);
					String ret = res.toJson();
					return ret;
				}

				PortData port1 = physicalLink.getLink().get(1);
				ODocument doc1 = dao.getPortInfo(port1.getPortName(), port1.getDeviceName());
				String    rid1 = doc1.getIdentity().toString();
				if (rid1.isEmpty()) {
					res.setMessage(String.format(NOT_FOUND, port1.getPortName() + "," + port1.getDeviceName()));
					res.setStatus(STATUS_NOTFOUND);
					String ret = res.toJson();
					return ret;
				}

				if (dao.deleteLinkInfo(rid0, rid1) == DB_RESPONSE_STATUS_NOT_FOUND) {
					res.setStatus(STATUS_NOTFOUND);
					res.setMessage(String.format(NOT_FOUND, port0.getPortName() + "," + port1.getPortName()));
					String ret = res.toJson();
					return ret;
				}
				if (dao.deleteLinkInfo(rid1, rid0) == DB_RESPONSE_STATUS_NOT_FOUND) {
					res.setStatus(STATUS_NOTFOUND);
					res.setMessage(String.format(NOT_FOUND, port0.getPortName() + "," + port1.getPortName()));
					String ret = res.toJson();
					return ret;
				}
			}

			res.setStatus(STATUS_SUCCESS);
			return res.toJson();

		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			return res.toJson();

		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
			return res.toJson();

		} catch (SQLException e) {
    		logger.error(e.getMessage());
    		res.setMessage(e.getMessage());
    		if (e.getCause() == null) {
    			res.setStatus(STATUS_INTERNAL_ERROR);
    		} else {
    			res.setStatus(STATUS_NOTFOUND);
    		}
    		return res.toJson();

		}  catch (RuntimeException re) {
			logger.error(re.getMessage());
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(re.getMessage());
			return res.toJson();

		} finally {
			try {
				if(dao != null) {
					dao.close();
				}
			} catch (final SQLException e) {
				logger.error(e.getMessage());
				res.setStatus(STATUS_INTERNAL_ERROR);
				res.setMessage(e.getMessage());
			}
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

}
