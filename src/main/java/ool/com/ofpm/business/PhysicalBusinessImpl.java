package ool.com.ofpm.business;

import java.sql.SQLException;
import java.util.List;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.PhysicalLinkJsonIn;
import ool.com.ofpm.json.PortInfo;
import ool.com.ofpm.utils.Config;
import ool.com.ofpm.utils.ConfigImpl;
import ool.com.ofpm.validate.PhysicalLinkJsonInValidate;
import ool.com.orientdb.client.ConnectionUtils;
import ool.com.orientdb.client.ConnectionUtilsImpl;
import ool.com.orientdb.client.Dao;
import ool.com.orientdb.client.DaoImpl;
import ool.com.util.Definition;
import ool.com.util.ErrorMessage;

import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class PhysicalBusinessImpl implements PhysicalBusiness {
	private static final Logger logger = Logger.getLogger(PhysicalBusinessImpl.class);
	private static final String CONNECT = "connectPhysicalLink";
	private static final String DISCONNECT = "disconnectPhysicalLink";

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

	private String commonLogic(String physicalLinkJson, String gdbClientMethodName) {
		final String fname = "commonLogic";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(physicalLinkJson=%s, gdbClientMethodName=%s) - start", fname, physicalLinkJson, gdbClientMethodName));
		}

		BaseResponse res = new BaseResponse();
		Dao dao = null;
		try {
			PhysicalLinkJsonIn physicalLink = PhysicalLinkJsonIn.fromJson(physicalLinkJson);

			PhysicalLinkJsonInValidate validator = new PhysicalLinkJsonInValidate();
			validator.checkValidation(physicalLink);

			ConnectionUtils utils = new ConnectionUtilsImpl();
			dao = new DaoImpl(utils);

			List<PortInfo> portList = physicalLink.getLink();
			String rid1 = "";
			String rid2 = "";

			// portList 2つ以上アウト

			// get port rid
			ODocument doc = dao.getPortInfo(portList.get(0).getPortName(), portList.get(0).getDeviceName());
			rid1 = doc.getIdentity().toString();
			doc = dao.getPortInfo(portList.get(1).getPortName(), portList.get(1).getDeviceName());
			rid2 = doc.getIdentity().toString();

			if (rid1.isEmpty()) {
				res.setMessage(String.format(ErrorMessage.NOT_FOUND, portList.get(0).getPortName() + "," + portList.get(0).getDeviceName()));
				res.setStatus(Definition.STATUS_NOTFOUND);
				String ret = res.toJson();
				return ret;
			} else if (rid2.isEmpty()) {
				res.setMessage(String.format(ErrorMessage.NOT_FOUND, portList.get(1).getPortName() + "," + portList.get(1).getDeviceName()));
				res.setStatus(Definition.STATUS_NOTFOUND);
				String ret = res.toJson();
				return ret;
			}

			if (gdbClientMethodName.equals(PhysicalBusinessImpl.CONNECT)) {
				if (dao.createLinkInfo(rid1, rid2) == Definition.DB_RESPONSE_STATUS_EXIST) {
					res.setStatus(Definition.STATUS_CONFLICT);
					res.setMessage(String.format(ErrorMessage.ALREADY_EXIST, portList.get(0).getPortName() + "," + portList.get(1).getPortName()));
					String ret = res.toJson();
					return ret;
				}
				if (dao.createLinkInfo(rid2, rid1) == Definition.DB_RESPONSE_STATUS_EXIST) {
					res.setStatus(Definition.STATUS_CONFLICT);
					res.setMessage(String.format(ErrorMessage.ALREADY_EXIST, portList.get(0).getPortName() + "," + portList.get(1).getPortName()));
					String ret = res.toJson();
					return ret;
				}
			} else if (gdbClientMethodName.equals(PhysicalBusinessImpl.DISCONNECT)) {
				if (dao.deleteLinkInfo(rid1, rid2) == Definition.DB_RESPONSE_STATUS_NOT_FOUND) {
					res.setStatus(Definition.STATUS_NOTFOUND);
					res.setMessage(String.format(ErrorMessage.NOT_FOUND, portList.get(0).getPortName() + "," + portList.get(1).getPortName()));
				} else {
					if (dao.deleteLinkInfo(rid2, rid1) == Definition.DB_RESPONSE_STATUS_NOT_FOUND) {
						res.setStatus(Definition.STATUS_NOTFOUND);
						res.setMessage(String.format(ErrorMessage.NOT_FOUND, portList.get(0).getPortName() + "," + portList.get(1).getPortName()));
					} else {
						res.setStatus(Definition.STATUS_SUCCESS);
					}
				}
			}

			res.setStatus(Definition.STATUS_CREATED);

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
    		res.setMessage(e.getMessage());
    		if (e.getCause() == null) {
    			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
    		} else {
    			res.setStatus(Definition.STATUS_NOTFOUND);
    		}
		}  catch (RuntimeException re) {
			logger.error(re.getMessage());
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(re.getMessage());
		} finally {
			try {
				if(dao != null) {
					dao.close();
				}
			} catch (final SQLException e) {
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

	@Override
	public String connectPhysicalLink(String physicalLinkJson) {
		final String fname = "connectPhysicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(physicalLinkJson=%s) - start", fname, physicalLinkJson));
		}

		String res = null;//this.commonLogic(physicalLinkJson, PhysicalBusinessImpl.CONNECT);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, res));
		}
		return res;
	}

	@Override
	public String disconnectPhysicalLink(String physicalLinkJson) {
		final String fname = "disconnectPhysicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(physicalLinkJson=%s) - start", fname, physicalLinkJson));
		}

		String res = null;//this.commonLogic(physicalLinkJson, PhysicalBusinessImpl.DISCONNECT);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, res));
		}
		return res;
	}

}
