package ool.com.ofpm.business;

import static ool.com.constants.ErrorMessage.*;
import static ool.com.constants.OfpmDefinition.*;
import static ool.com.constants.OrientDBDefinition.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import ool.com.dmdb.client.DMDBClient;
import ool.com.dmdb.client.DMDBClientImpl;
import ool.com.dmdb.client.DeviceManagerDBClient;
import ool.com.dmdb.client.DeviceManagerDBClientImpl;
import ool.com.dmdb.exception.DMDBClientException;
import ool.com.dmdb.exception.DeviceManagerDBClientException;
import ool.com.dmdb.json.Nic;
import ool.com.dmdb.json.PlaneType;
import ool.com.dmdb.json.Port;
import ool.com.dmdb.json.Switch;
import ool.com.dmdb.json.Used;
import ool.com.dmdb.json.nic.NicReadRequest;
import ool.com.dmdb.json.nic.NicReadResponse;
import ool.com.dmdb.json.port.PortReadRequest;
import ool.com.dmdb.json.port.PortReadResponse;
import ool.com.dmdb.json.sw.SwitchReadRequest;
import ool.com.dmdb.json.sw.SwitchReadResponse;
import ool.com.ofpm.client.NetworkConfigSetupperClient;
import ool.com.ofpm.client.NetworkConfigSetupperClientImpl;
import ool.com.ofpm.client.OFCClient;
import ool.com.ofpm.client.OFCClientImpl;
import ool.com.ofpm.exception.NoRouteException;
import ool.com.ofpm.exception.OFCClientException;
import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.common.BaseResponse;
import ool.com.ofpm.json.common.GraphDevicePort;
import ool.com.ofpm.json.device.ConnectedPortGetJsonOut;
import ool.com.ofpm.json.device.PortData;
import ool.com.ofpm.json.ncs.NetworkConfigSetupperIn;
import ool.com.ofpm.json.ncs.NetworkConfigSetupperInData;
import ool.com.ofpm.json.ofc.InitFlowIn;
import ool.com.ofpm.json.ofc.SetFlowIn;
import ool.com.ofpm.json.topology.logical.LogicalLink;
import ool.com.ofpm.json.topology.logical.LogicalTopology;
import ool.com.ofpm.json.topology.logical.LogicalTopology.OfpConDeviceInfo;
import ool.com.ofpm.json.topology.logical.LogicalTopology.OfpConPortInfo;
import ool.com.ofpm.json.topology.logical.LogicalTopologyGetJsonOut;
import ool.com.ofpm.json.topology.logical.LogicalTopologyUpdateJsonIn;
import ool.com.ofpm.json.topology.logical.LogicalTopologyUpdateJsonOut;
import ool.com.ofpm.utils.Config;
import ool.com.ofpm.utils.ConfigImpl;
import ool.com.ofpm.utils.GraphDBUtil;
import ool.com.ofpm.utils.OFPMUtils;
import ool.com.ofpm.validate.common.BaseValidate;
import ool.com.ofpm.validate.ofc.InitFlowInValidate;
import ool.com.ofpm.validate.topology.logical.LogicalTopologyValidate;
import ool.com.openam.client.OpenAmClient;
import ool.com.openam.client.OpenAmClientException;
import ool.com.openam.client.OpenAmClientImpl;
import ool.com.openam.json.OpenAmAttributesOut;
import ool.com.openam.json.OpenAmIdentitiesOut;
import ool.com.openam.json.TokenIdOut;
import ool.com.openam.json.TokenValidChkOut;
import ool.com.orientdb.client.ConnectionUtilsJdbc;
import ool.com.orientdb.client.ConnectionUtilsJdbcImpl;
import ool.com.orientdb.client.Dao;
import ool.com.orientdb.client.DaoImpl;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;

public class LogicalBusinessImpl implements LogicalBusiness {
	private static final Logger logger = Logger.getLogger(LogicalBusinessImpl.class);

	Config conf = new ConfigImpl();
	Dao dao = null;

	public LogicalBusinessImpl() {
		if (logger.isDebugEnabled()) {
			logger.debug("LogicalBusinessImpl");
		}
	}

	/**
	 * Normalize nodes for update/get LogicalTopology.
	 * Remove node that deviceType is not SERVER or SWITCH and remove node that have no ports.
	 * @param conn
	 * @param nodes
	 * @throws SQLException
	 */
	private void normalizeLogicalNode(Connection conn, Collection<OfpConDeviceInfo> nodes) throws SQLException {
		String fname = "normalizeLogicalNode";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(conn=%s, nodes=%s) - start", fname, conn, nodes));
		}
		Map<String, Boolean> devTypeMap = new HashMap<String, Boolean>();
		List<OfpConDeviceInfo> removalNodeList = new ArrayList<OfpConDeviceInfo>();
		for (OfpConDeviceInfo node : nodes) {
			String devType = node.getDeviceType();
			if (!devType.equals(NODE_TYPE_SERVER) && !devType.equals(NODE_TYPE_SWITCH) && !devType.equals(NODE_TYPE_EX_SWITCH)) {
				removalNodeList.add(node);
				continue;
			}

			List<OfpConPortInfo> ports = node.getPorts();
			List<OfpConPortInfo> removalPortList = new ArrayList<OfpConPortInfo>();
			for (OfpConPortInfo port : ports) {
				String neiDevName = port.getOfpPortLink().getDeviceName();
				/* check outDevice is LEAF, other wise don't append port */
				Boolean isOfpSw = devTypeMap.get(neiDevName);
				if (isOfpSw == null) {
					Map<String, Object> outDevDoc = dao.getNodeInfoFromDeviceName(conn, neiDevName);
					String outDevType = (String)outDevDoc.get("type");
					isOfpSw = OFPMUtils.isNodeTypeOfpSwitch(outDevType);
					devTypeMap.put(neiDevName, isOfpSw);
				}
				if (!isOfpSw) {
					removalPortList.add(port);
				}
			}
			ports.removeAll(removalPortList);

			/* if node don't has port that connect to leaf-switch, node remove from nodeList */
			if (ports.isEmpty()) {
				removalNodeList.add(node);
			}
		}
		nodes.removeAll(removalNodeList);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

	/**
	 * Make node for update/get-LogicalTopology from deviceName, and return it.
	 * @param conn
	 * @param devName
	 * @return node for Logicaltopology.
	 * @throws SQLException
	 */
	private OfpConDeviceInfo getLogicalNode(Connection conn, String devName) throws SQLException {
		String fname = "getLogicalNode";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(conn=%s, devName=%s) - start", fname, conn, devName));
		}
		Map<String, Object> devDoc = dao.getNodeInfoFromDeviceName(conn, devName);
		if (devDoc == null) {
			return null;
		}
		String   devType = (String)devDoc.get("type");
		OfpConDeviceInfo node = new OfpConDeviceInfo();
		node.setDeviceName(devName);
		node.setDeviceType(devType);

		List<OfpConPortInfo> portList = new ArrayList<OfpConPortInfo>();
		List<Map<String, Object>> linkDocList = dao.getCableLinksFromDeviceName(conn, devName);
		if (linkDocList == null) {
			return null;
		}
		for (Map<String, Object> linkDoc : linkDocList) {
			String outDevName = (String)linkDoc.get("outDeviceName");

			PortData ofpPort = new PortData();
			String  outPortName = (String)linkDoc.get("outPortName");
			Integer outPortNmbr = (Integer)linkDoc.get("outPortNumber");
			ofpPort.setDeviceName(outDevName);
			ofpPort.setPortName(outPortName);
			ofpPort.setPortNumber(outPortNmbr);

			String  inPortName = (String)linkDoc.get("inPortName");
			Integer inPortNmbr = (Integer)linkDoc.get("inPortNumber");
			OfpConPortInfo port = new OfpConPortInfo();
			port.setPortName(inPortName);
			port.setPortNumber(inPortNmbr);
			port.setOfpPortLink(ofpPort);

			portList.add(port);
		}
		node.setPorts(portList);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, node));
		}
		return node;
	}


	/**
	 * Normalize links for update/get LogicalTopology.
	 * Remove list that does not contains nodes.
	 * @param nodes
	 * @param links
	 */
	private void normalizeLogicalLink(Collection<OfpConDeviceInfo> nodes, Collection<LogicalLink> links) {
		String fname = "normalizeLogicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(nodes=%s, links=%s) - start", fname, nodes, links));
		}

		List<LogicalLink> removalLinks = new ArrayList<LogicalLink>();
		for (LogicalLink link : links) {
			List<PortData> ports = link.getLink();
//			if (!OFPMUtils.nodesContainsPort(nodes, ports.get(0).getDeviceName(), ports.get(0).getPortName())) {
			if (!OFPMUtils.nodesContainsPort(nodes, ports.get(0).getDeviceName(), null)) {
				removalLinks.add(link);
//			} else if (!OFPMUtils.nodesContainsPort(nodes, ports.get(1).getDeviceName(), ports.get(1).getPortName())) {
			} else if (!OFPMUtils.nodesContainsPort(nodes, ports.get(1).getDeviceName(), null)) {
				removalLinks.add(link);
			}
		}
		links.removeAll(removalLinks);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

	/**
	 * Make list of link for LogicalTopology from deviceName, and return it.
	 * @param conn
	 * @param devName
	 * @param setPortNumber If this value is false, portNumber is every time 0.
	 * @return list of link for LogicalTopology.
	 * @throws SQLException
	 */
	private Set<LogicalLink> getLogicalLink(Connection conn, String devName, boolean setPortNumber) throws SQLException {
		String fname = "getLogicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(conn=%s, devName=%s, setPortNumber=%s) - start", fname, conn, devName, setPortNumber));
		}
		Set<LogicalLink> linkSet = new HashSet<LogicalLink>();
		List<Map<String, Object>> patchDocList = dao.getPatchWiringsFromDeviceName(conn, devName);
		if (patchDocList == null) {
			return null;
		}
		for (Map<String, Object> patchDoc : patchDocList) {
			String inDevName  = (String)patchDoc.get("inDeviceName");
			String inPortName = (String)patchDoc.get("inPortName");
			PortData inPort = new PortData();
			inPort.setDeviceName(inDevName);
			inPort.setPortName(inPortName);

			String outDevName  = (String)patchDoc.get("outDeviceName");
			String outPortName = (String)patchDoc.get("outPortName");
			PortData outPort = new PortData();
			outPort.setDeviceName(outDevName);
			outPort.setPortName(outPortName);

			List<PortData> ports = new ArrayList<PortData>();
			ports.add(inPort);
			ports.add(outPort);

			LogicalLink link = new LogicalLink();
			link.setLink(ports);

			linkSet.add(link);
		}
		if (linkSet.isEmpty()) {
			return null;
		}
		if (!setPortNumber) {
			return linkSet;
		}
		/* Set port number at port data in logical link */
		for (LogicalLink link : linkSet) {
			for (PortData port : link.getLink()) {
				Map<String, Object> portMap = dao.getPortInfoFromPortName(
						conn,
						port.getDeviceName(),
						port.getPortName());
				port.setPortNumber((Integer)portMap.get("number"));
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, linkSet));
		}
		return linkSet;
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.ofpm.business.LogicalBusiness#getLogicalTopology(java.lang.String, java.lang.String)
	 */
	public String getLogicalTopology(String deviceNamesCSV, String tokenId) {
		String fname = "getLogicalTopology";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceNames=%s, tokenId%s) - start", fname, deviceNamesCSV, tokenId));
		}
		LogicalTopologyGetJsonOut res = new LogicalTopologyGetJsonOut();

		/* PHASE 1: Authentication */
		try {
			String openamUrl = conf.getString(OPEN_AM_URL);
			OpenAmClient openAmClient = new OpenAmClientImpl(openamUrl);
			boolean isTokenValid = false;
			if (!StringUtils.isBlank(tokenId) && openAmClient != null) {
				TokenValidChkOut tokenValidchkOut = openAmClient.tokenValidateCheck(tokenId);
				isTokenValid = tokenValidchkOut.getIsTokenValid();
			}
			if (isTokenValid != true) {
				logger.error(String.format("Invalid tokenId. tokenId=%s", tokenId));
				res.setStatus(STATUS_UNAUTHORIZED);
				res.setMessage(String.format("Invalid tokenId. tokenId=%s", tokenId));
				String ret = res.toJson();
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("%s(ret=%s) - end", fname, ret));
				}
				return ret;
			}
		} catch (OpenAmClientException e) {
			logger.error(e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		/* PHASE 2: Validation */
		List<String> deviceNames = null;
		try {
			BaseValidate.checkStringBlank(deviceNamesCSV);
			deviceNames = Arrays.asList(deviceNamesCSV.split(CSV_SPLIT_REGEX));
			BaseValidate.checkArrayStringBlank(deviceNames);
			BaseValidate.checkArrayOverlapped(deviceNames);
			BaseValidate.checkStringBlank(tokenId);
			// TODO: check user tenant(by used-info from DMDB).
		} catch (ValidateException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(e.getMessage());
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		}

		/* PHASE 3: Get logical topology */
		ConnectionUtilsJdbc utilsJdbc = null;
		Connection conn = null;
		try {
			utilsJdbc = new ConnectionUtilsJdbcImpl();
			conn = utilsJdbc.getConnection(true);
			dao = new DaoImpl(utilsJdbc);

			/* Make nodes and links */
			List<OfpConDeviceInfo> nodeList = new ArrayList<OfpConDeviceInfo>();
			List<LogicalLink>      linkList = new ArrayList<LogicalLink>();
			Set<LogicalLink>       linkSet  = new HashSet<LogicalLink>();
			for (String devName : deviceNames) {
				OfpConDeviceInfo node = this.getLogicalNode(conn, devName);
				if (node == null) {
					continue;
				}
				nodeList.add(node);

				Set<LogicalLink> links = this.getLogicalLink(conn, devName, true);
				if (links == null) {
					continue;
				}
				linkSet.addAll(links);
			}
			linkList.addAll(linkSet);
			this.normalizeLogicalNode(conn,     nodeList);
//			this.normalizeLogicalLink(nodeList, linkList);

			LogicalTopology topology = new LogicalTopology();
			topology.setNodes(nodeList);
			topology.setLinks(linkList);

			// create response data
			res.setResult(topology);
			res.setStatus(STATUS_SUCCESS);
		} catch (Exception e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		} finally {
//			utilsJdbc.rollback(conn);
			utilsJdbc.close(conn);
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.ofpm.business.LogicalBusiness#updateLogicalTopology(java.lang.String)
	 */
	public String updateLogicalTopology(String requestedTopologyJson) {
		String fname = "updateLogicalTopology";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestedTopology=%s) - start", fname, requestedTopologyJson));
		}
		LogicalTopologyUpdateJsonOut res = new LogicalTopologyUpdateJsonOut();
		res.setStatus(STATUS_SUCCESS);
		res.setResult(null);

		LogicalTopologyUpdateJsonIn requestedTopology = null;
		try {
			requestedTopology = LogicalTopologyUpdateJsonIn.fromJson(requestedTopologyJson);
		} catch (JsonSyntaxException jse) {
			OFPMUtils.logErrorStackTrace(logger, jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			String ret = res.toString();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		/* PHASE 1: Authenticate */
		try {
			String openamUrl = conf.getString(OPEN_AM_URL);
			OpenAmClient openAmClient = new OpenAmClientImpl(openamUrl);
			String tokenId = requestedTopology.getTokenId();
			boolean isTokenValid = false;
			if (openAmClient != null) {
				TokenValidChkOut tokenValidchkOut = openAmClient.tokenValidateCheck(tokenId);
				isTokenValid = tokenValidchkOut.getIsTokenValid();
			}
			if (isTokenValid != true) {
				if (logger.isDebugEnabled()) {
					logger.error(String.format("Invalid tokenId. tokenId=%s", tokenId));
				}
				res.setStatus(STATUS_BAD_REQUEST);
				res.setMessage(String.format("Invalid tokenId. tokenId=%s", tokenId));
				String ret = res.toJson();
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("%s(ret=%s) - end", fname, ret));
				}
				return ret;
			}
		} catch (OpenAmClientException e) {
			logger.error(e);
			res.setStatus(STATUS_UNAUTHORIZED);
			res.setMessage(e.getMessage());
			String ret = res.toString();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		/* PHASE 2: Validation */
		try {
			LogicalTopologyValidate validator = new LogicalTopologyValidate();
			validator.checkValidationRequestIn(requestedTopology);
			// TODO: check user tenant (by used-info in DMDB).
		} catch (ValidateException ve) {
			OFPMUtils.logErrorStackTrace(logger, ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		}

		/* PHASE 3: Get Auth token */
		String ofpmToken = null;
		try {
			String url  = conf.getString(OPEN_AM_URL);
			String user = conf.getString(CONFIG_KEY_AUTH_USERNAME);
			String pass = conf.getString(CONFIG_KEY_AUTH_PASSWORD);
			OpenAmClient client    = new OpenAmClientImpl(url);
			TokenIdOut   tokenInfo = client.authenticate(user, pass);
			ofpmToken = tokenInfo.getTokenId();
		} catch (OpenAmClientException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		}

		/* PHASE 4: Update topology */
		MultivaluedMap<String, Map<String, Object>> reducedFlows   = new MultivaluedHashMap<String, Map<String, Object>>();
		MultivaluedMap<String, Map<String, Object>> augmentedFlows = new MultivaluedHashMap<String, Map<String, Object>>();
		List<LogicalLink> ncsNotifyLinkList = new ArrayList<LogicalLink>();
		ConnectionUtilsJdbc utilsJdbc = null;
		Connection conn = null;
		try {
			/* initialize db connectors */
			utilsJdbc = new ConnectionUtilsJdbcImpl();
			conn = utilsJdbc.getConnection(false);
			dao  = new DaoImpl(utilsJdbc);

			/* compute Inclement/Declement LogicalLink */
			List<LogicalLink> incLinkList = new ArrayList<LogicalLink>();
			List<LogicalLink> decLinkList = new ArrayList<LogicalLink>();
			{
				List<OfpConDeviceInfo> requestedNodes    = requestedTopology.getNodes();
				List<LogicalLink>      requestedLinkList = requestedTopology.getLinks();
				Set<LogicalLink>       currentLinkList   = new HashSet<LogicalLink>();

				/* Create current links */
				for (OfpConDeviceInfo requestedNode : requestedNodes) {
					String devName = requestedNode.getDeviceName();
					Set<LogicalLink> linkSet = this.getLogicalLink(conn, devName, false);
					if (linkSet != null) {
						currentLinkList.addAll(linkSet);
					}
				}
				this.normalizeLogicalLink(requestedNodes, currentLinkList);

				/* Set port number 0, because when run Collection.removeAll, port number remove influence. */
				for (LogicalLink link : requestedLinkList) {
					for (PortData port : link.getLink()) {
						port.setPortNumber(null);
					}
				}

				/* get difference between current and next */
				decLinkList.addAll(currentLinkList);
				decLinkList.removeAll(requestedLinkList);

				incLinkList.addAll(requestedLinkList);
				incLinkList.removeAll(currentLinkList);

				/* sort incliment links. 1st link have port name, final link no have port name. */
				Collections.sort(incLinkList, new Comparator<LogicalLink>() {
					@Override
					public int compare(LogicalLink link1, LogicalLink link2) {
						int score1 = 0;
						for (PortData port1 : link1.getLink()) {
							if (StringUtils.isBlank(port1.getPortName())) {
								score1++;
							}
						}
						int score2 = 0;
						for (PortData port2 : link2.getLink()) {
							if (StringUtils.isBlank(port2.getPortName())) {
								score2++;
							}
						}
						return score1 - score2;
					}
				});

				List<LogicalLink> trushIncLinkList = new ArrayList<LogicalLink>();
				for (LogicalLink incLink : incLinkList) {
					List<PortData> incPorts = incLink.getLink();
					for (LogicalLink decLink: decLinkList) {
						List<PortData> decPorts = decLink.getLink();
						if (OFPMUtils.PortDataNonStrictEquals(decPorts.get(0), incPorts.get(0)) && OFPMUtils.PortDataNonStrictEquals(decPorts.get(1), incPorts.get(1))) {
							decLinkList.remove(decLink);
							trushIncLinkList.add(incLink);
							break;
						} else if (OFPMUtils.PortDataNonStrictEquals(decPorts.get(0), incPorts.get(1)) && OFPMUtils.PortDataNonStrictEquals(decPorts.get(1), incPorts.get(0))) {
							decLinkList.remove(decLink);
							trushIncLinkList.add(incLink);
							break;
						}
					}
				}
				incLinkList.removeAll(trushIncLinkList);
			}

			/* update patch wiring and make patch link */
			DMDBClient client = new DMDBClientImpl(conf.getString(DEVICE_MANAGER_URL));
			for (LogicalLink link : decLinkList) {
				this.addDeclementLogicalLink(conn, link, client, ofpmToken, reducedFlows);
			}
			for (LogicalLink link : incLinkList) {
				this.addInclementLogicalLink(conn, link, client, ofpmToken, augmentedFlows);
			}
			ncsNotifyLinkList = incLinkList;

			/* Make nodes and links */
			List<LogicalLink>      linkList = new ArrayList<LogicalLink>();
			Set<LogicalLink>       linkSet  = new HashSet<LogicalLink>();
			List<OfpConDeviceInfo> nodeList    = requestedTopology.getNodes();
			for (OfpConDeviceInfo node : nodeList) {
				String devName = node.getDeviceName();
				Set<LogicalLink> links = this.getLogicalLink(conn, devName, false);
				if (links == null) {
					continue;
				}
				linkSet.addAll(links);
			}
			linkList.addAll(linkSet);

			LogicalTopology topology = new LogicalTopology();
			topology.setNodes(nodeList);
			topology.setLinks(linkList);

			// create response data
			res.setResult(topology);

			utilsJdbc.commit(conn);
		} catch (NoRouteException e) {
			utilsJdbc.rollback(conn);
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_NOTFOUND);
			res.setMessage(e.getMessage());
			return res.toJson();
		} catch (Exception e) {
			utilsJdbc.rollback(conn);
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
			return res.toJson();
		} finally {
			utilsJdbc.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}

		/* PHASE 5: Set flow to OFPS via OFC */
		try {
			for (Entry<String, List<Map<String, Object>>> entry : reducedFlows.entrySet()) {
				OFCClient client = new OFCClientImpl(entry.getKey());
				for (Map<String, Object> flow : entry.getValue()) {
					client.deleteFlows(
							(String) flow.get("datapathId"),
							(Integer)flow.get("inPort"),
							(String) flow.get("srcMac"),
							(String) flow.get("dstMac"),
							(Integer)flow.get("outPort"),
							(String) flow.get("modSrcMac"),
							(String) flow.get("modDstMac"),
							(Boolean)flow.get("packetInFlg"),
							(Boolean)flow.get("dropFlg"));
				}
			}
			for (Entry<String, List<Map<String, Object>>> entry : augmentedFlows.entrySet()) {
				OFCClient client = new OFCClientImpl(entry.getKey());
				for (Map<String, Object> flow : entry.getValue()) {
					client.setFlows(
							(String) flow.get("datapathId"),
							(Integer)flow.get("inPort"),
							(String) flow.get("srcMac"),
							(String) flow.get("dstMac"),
							(Integer)flow.get("outPort"),
							(String) flow.get("modSrcMac"),
							(String) flow.get("modDstMac"),
							(Boolean)flow.get("packetInFlg"),
							(Boolean)flow.get("dropFlg"));
				}
			}
		} catch (OFCClientException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	private Map<String, Switch> dmdbPlaneCache = new HashMap<String, Switch>();
	/**
	 * Get plane switch from DMDB. If DB no have requested device, return null.
	 * @param deviceName
	 * @param client
	 * @param ofpmToken
	 * @return
	 * @throws DMDBClientException
	 */
	private Switch getPlaneSwitch(String deviceName, DMDBClient client, String ofpmToken) throws DMDBClientException {
		Switch sw = dmdbPlaneCache.get(deviceName);
		if (sw == null) {
			Switch params = new Switch();
			params.setTrafficType(DEVICE_TRAFFIC_TYPE_PLANE);
			SwitchReadRequest req = new SwitchReadRequest();
			req.setAuth(ofpmToken);
			req.setParams(params);
			SwitchReadResponse res = client.switchRead(req);
			if (res.getStatus() != STATUS_SUCCESS) {
				logger.error(res.getMessage());
			}
			if (res.getResult() == null) {
				return null;
			}
			for (Switch swbuf : res.getResult()) {
				dmdbPlaneCache.put(swbuf.getDeviceName(), swbuf);
				if (StringUtils.equals(swbuf.getDeviceName(), deviceName)) {
					sw = swbuf;
				}
			}
		}
		return sw;
	}

	/**
	 * Notify patching with plane switch to NCS.
	 * @param userToken
	 * @param links
	 * @param ofpmToken
	 * @return
	 * @throws DMDBClientException
	 * @throws OpenAmClientException
	 */
	private int notifyNcs(String userToken, List<LogicalLink> links, String ofpmToken) throws DMDBClientException, OpenAmClientException {
		if (links == null || links.isEmpty()) {
			return STATUS_SUCCESS;
		}

		OpenAmClient amClient = new OpenAmClientImpl(conf.getString(OPEN_AM_URL));
		DMDBClient dmdbClient = new DMDBClientImpl(conf.getString(DEVICE_MANAGER_URL));
		NetworkConfigSetupperClient ncsClient = new NetworkConfigSetupperClientImpl(conf.getString(NETWORK_CONFIG_SETUPPER_URL));

		MultivaluedMap<String, String> dPlaneDevVlanMap = new MultivaluedHashMap<String, String>();
//		MultivaluedMap<String, String> cPlaneDevVlanMap = new MultivaluedHashMap<String, String>();
		for (LogicalLink link: links) {
			List<PortData> ports = link.getLink();
			for (PortData port : ports) {
				String deviceName = port.getDeviceName();
				String portName   = port.getPortName();

				Switch swInfo = this.getPlaneSwitch(deviceName, dmdbClient, ofpmToken);
				if (swInfo == null) {
					continue;
				}
				if (!StringUtils.equals(swInfo.getTrafficType(), DEVICE_TRAFFIC_TYPE_PLANE)) {
					continue;
				}

				Port portInfo = this.getPort(deviceName, portName, dmdbClient, ofpmToken);
				if (portInfo == null) {
					logger.warn(String.format(NOT_FOUND, "port=" + deviceName + "." + portName));
					continue;
				}

				PlaneType type = portInfo.getTrafficType();
				if (type.equals(PlaneType.D)) {
					dPlaneDevVlanMap.add(deviceName, portName);
//				} else if (type.equals(PlaneType.C)) {
//					cPlaneDevVlanMap.add(deviceName, portName);
				}
			}
		}

		if (dPlaneDevVlanMap.isEmpty()) {
			return STATUS_SUCCESS;
		}

		OpenAmAttributesOut attrsRes = amClient.getAttributes(userToken);
		Map<String, List<String>> attrs = attrsRes.getAttrMap();
		NetworkConfigSetupperIn ncsReq = new NetworkConfigSetupperIn();
		ncsReq.setTokenId(ofpmToken);
		List<NetworkConfigSetupperInData> params = new ArrayList<NetworkConfigSetupperInData>();
		/* D */
		List<String> dPlaneVlan = attrs.get(KEY_AM_DPLANE_VLAN);
		if (dPlaneVlan != null && !dPlaneVlan.isEmpty()) {
			for (Entry<String, List<String>> entry : dPlaneDevVlanMap.entrySet()) {
				NetworkConfigSetupperInData data = new NetworkConfigSetupperInData(
						entry.getKey(),
						dPlaneVlan.get(0),
						entry.getValue());
				params.add(data);
			}
		}
//		for (Entry<String, List<String>> entry : cPlaneDevVlanMap.entrySet()) {
//			NetworkConfigSetupperInData data = new NetworkConfigSetupperInData(
//					entry.getKey(),
//					attrs.get("").get(0),
//					entry.getValue());
//			params.add(data);
//		}
		ncsReq.setParams(params);
		BaseResponse resNcs = ncsClient.sendPlaneSwConfigData(ncsReq);
		return resNcs.getStatus();
	}
	public int notifyNcs(String tokenId, List<String> deviceNames, List<Integer> portNames) {
		int ret = STATUS_SUCCESS;
		String openamUrl = conf.getString(OPEN_AM_URL);
		OpenAmClient openAmClient = new OpenAmClientImpl(openamUrl);
		String deviceManagerUrl = conf.getString(DEVICE_MANAGER_URL);
		DeviceManagerDBClient deviceManagerDBClient = new DeviceManagerDBClientImpl(deviceManagerUrl);
		String networkConfigSetupperUrl = conf.getString(NETWORK_CONFIG_SETUPPER_URL);
		NetworkConfigSetupperClient networkConfigSetupperClien = new NetworkConfigSetupperClientImpl(networkConfigSetupperUrl);
		DeviceBusiness deviceManagerBusiness = new DeviceBusinessImpl();

		try {
			if (deviceNames.contains(D_PLANE_SW_HOST_NAME)) {
				String res = deviceManagerBusiness.getConnectedPortInfo(OFP_SW_HOST_NAME);
				ConnectedPortGetJsonOut connectedPortGetJsonOut = ConnectedPortGetJsonOut.fromJson(res);
				if (connectedPortGetJsonOut.getStatus() != STATUS_SUCCESS) {
					return connectedPortGetJsonOut.getStatus();
				}

				String dPlaneSwPortName = new String();
				String deviceName = new String();
				for (Integer portNumber : portNames) {
					GraphDevicePort graphDevicePort = GraphDBUtil.searchNeighborPort(OFP_SW_HOST_NAME, portNumber, connectedPortGetJsonOut.getResult());
					if (graphDevicePort.getDeviceName().equals(D_PLANE_SW_HOST_NAME)) {
						dPlaneSwPortName = graphDevicePort.getPortName();
					} else {
						deviceName = graphDevicePort.getDeviceName();
					}
				}

				// Get device used info from Device manager.
				//List<Used> uses = deviceManagerDBClient.readUsed(tokenId, deviceName, null);
				List<Used> uses = deviceManagerDBClient.readUsed(tokenId, deviceName, null);
				Used used = uses.get(0);

				// Get vlanID from OpenAM with userID and tokenId of administrator.
				TokenIdOut adminToken = openAmClient.authenticate(OPEN_AM_ADMIN_USER_ID , OPEN_AM_ADMIN_USER_PW);
				OpenAmIdentitiesOut openAmIdentitiesOut = openAmClient.readIdentities(adminToken.getTokenId(), used.getUserName());
				String dVlan = openAmIdentitiesOut.getdVlan().get(0);

				// Send parameters(auth id,deviceName, vlan id) to NCS.
				NetworkConfigSetupperIn networkConfigSetupperIn = new NetworkConfigSetupperIn();
				networkConfigSetupperIn.setTokenId(tokenId);
				List<ool.com.ofpm.json.ncs.NetworkConfigSetupperInData> params = networkConfigSetupperIn.getParams();
				List<String> portNamesData = new ArrayList<String>();
				portNamesData.add(dPlaneSwPortName);
				NetworkConfigSetupperInData param = new NetworkConfigSetupperInData(D_PLANE_SW_HOST_NAME, dVlan, portNamesData);
				params.add(param);
				BaseResponse resNcs = networkConfigSetupperClien.sendPlaneSwConfigData(networkConfigSetupperIn);
				if (resNcs.getStatus() != STATUS_SUCCESS) {
					return resNcs.getStatus();
				}
			}
		} catch(OpenAmClientException oace) {
			logger.error(oace);
			return STATUS_INTERNAL_ERROR;
		} catch(DeviceManagerDBClientException dmdce) {
			logger.error(dmdce);
			return STATUS_INTERNAL_ERROR;
		}

		return ret;
	}

	/* (non-Javadoc)
	 * @see ool.com.ofpm.business.LogicalBusiness#setFlow(java.lang.String)
	 */
	@Override
	public String setFlow(String requestedData) {
		final String fname = "setFlow";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestedData=%s) - start", fname, requestedData));
		}

		BaseResponse res = new BaseResponse();
		Connection conn = null;
		ConnectionUtilsJdbc utilsJdbc = null;
		SetFlowIn req = null;
		String rid = "";
		String deviceName = null;

		try {
			req = SetFlowIn.fromJson(requestedData);
			// TODO: validation
		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
		}

		try {
			dao = new DaoImpl();
			utilsJdbc = new ConnectionUtilsJdbcImpl();
			dao.setConnectionUtilsJdbc(utilsJdbc);
			conn = utilsJdbc.getConnection(true);
			deviceName = dao.getDeviceNameFromDatapathId(conn, req.getDpId());
			rid = dao.getPortRidFromDeviceNamePortNumber(conn, deviceName, Integer.parseInt(req.getInPort()));
			List<Map<String, Map<String, Object>>> ret = dao.getDevicePortInfoSetFlowFromPortRid(conn, rid);

			// generate InternalMac and setFlow to OFC
			Iterator<Map<String, Map<String, Object>>> it = ret.iterator();
			//String srcMac = req.getSrcMac();
			//String dstMac = req.getDstMac();
			//String internalMac = dao.getInternalMacFromDeviceNameInPortSrcMacDstMac(conn, deviceName, req.getInPort(), req.getSrcMac(), req.getDstMac());
			String internalMac = DaoImpl.getInternalMacFromDeviceNameInPortSrcMacDstMac(utilsJdbc, conn, deviceName, req.getInPort(), req.getSrcMac(), req.getDstMac());
			Long tmp = ~(OFPMUtils.macAddressToLong(internalMac));
			String internalDstMac = OFPMUtils.longToMacAddress(tmp);

			int switchNum = ret.size();
			int i = 1;
			while (it.hasNext()) {
				Map<String, Map<String, Object>> deviceportInfo = it.next();	// in, out, parent
				Map<String, Object> parentInfo = deviceportInfo.get("parent");
				Map<String, Object> inPortInfo = deviceportInfo.get("in");
				Map<String, Object> outPortInfo = deviceportInfo.get("out");

				// new client
				String ofcIp = parentInfo.get("ofcIp").toString();
				OFCClient restClient = new OFCClientImpl(ofcIp);

				// dpid
				String dpid = parentInfo.get("datapathId").toString();
				// inPort
				Integer inPort = new Integer(inPortInfo.get("number").toString());
				// srcMac
				String type = parentInfo.get("type").toString();
				String srcMac = null;
				if (type.equals("Leaf") && i == 1) {
					srcMac = internalMac;
				} else if (type.equals("Leaf") && i == switchNum) {
					srcMac = req.getSrcMac();
				} else {
					srcMac = internalMac;
				}
				// dstMac
				String dstMac = null;
				if (type.equals("Leaf") && i == 1) {
					// noting
				} else if (type.equals("Leaf") && i == switchNum) {
					dstMac = req.getDstMac();
				} else {
					// nothing
				}
				// outPort
				Integer outPort = new Integer(outPortInfo.get("number").toString());
				// modSrcMac
				String modSrcMac = null;
				if (type.equals("Leaf") && i == 1) {
					modSrcMac = req.getSrcMac();
				} else if (type.equals("Leaf") && i == switchNum) {
					modSrcMac = internalMac;
				} else {
					// nothing
				}
				// modDstMac
				String modDstMac = null;
				if (type.equals("Leaf") && i == 1) {
					modDstMac = req.getDstMac();
				} else if (type.equals("Leaf") && i == switchNum) {
					modDstMac = internalDstMac;
				} else {
					// nothing
				}
				restClient.setFlows(dpid, inPort, srcMac, dstMac, outPort, modSrcMac, modDstMac, false, false);
				i++;
			}
		} catch (SQLException e) {
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		} catch (Exception e) {
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		}
		finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.ofpm.business.LogicalBusiness#initFlow(java.lang.String)
	 */
	@Override
	public String initFlow(String requestedData) {
		final String fname = "initFlow";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestedData=%s)", fname, requestedData));
		}
		BaseResponse res = new BaseResponse();
		res.setStatus(STATUS_SUCCESS);

		/* PHASE 1: validation check */
		InitFlowIn req = null;
		try {
			req = InitFlowIn.fromJson(requestedData);
			InitFlowInValidate validator = new InitFlowInValidate();
			validator.checkValidation(req);
		} catch (Throwable t) {
			OFPMUtils.logErrorStackTrace(logger, t);
			{
				if (t instanceof JsonSyntaxException) {
					res.setStatus(STATUS_BAD_REQUEST);
					res.setMessage(INVALID_JSON);
				} else if (t instanceof ValidateException) {
					res.setStatus(STATUS_BAD_REQUEST);
					res.setMessage(t.getMessage());
				} else {
					res.setStatus(STATUS_INTERNAL_ERROR);
					res.setMessage(t.getMessage());
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s)", fname, res));
			}
			return res.toJson();
		}

		/* PHASE 2:Set Flows into OFPS that is designated by datapathId. */
		ConnectionUtilsJdbc utils = null;
		Connection conn = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(true);

			Dao dao = new DaoImpl(utils);
			String dpid = req.getDatapathId();
			String devName = dao.getDeviceNameFromDatapathId(conn, dpid);
			Map<String, Object> devInfo = dao.getNodeInfoFromDeviceName(conn, devName);
			OFCClient client = new OFCClientImpl((String) devInfo.get("ofcIp"));

			List<Map<String, Object>> patchMapList = dao.getPatchWiringsFromParentRid(conn, (String) devInfo.get("rid"));
			for (Map<String, Object> patchMap : patchMapList) {
				List<Map<String, Object>> path = dao.getPatchWiringsFromDeviceNamePortName(
						conn,
						(String) patchMap.get("inDeviceName"),
						(String) patchMap.get("inPortName"));
				Integer sequence = (Integer) patchMap.get("sequence");

				Map<String, Object>  inPortInfo = dao.getPortInfoFromPortRid(conn, (String) patchMap.get("in"));
				Map<String, Object> outPortInfo = dao.getPortInfoFromPortRid(conn, (String) patchMap.get("out"));
				Integer  inPort = (Integer)  inPortInfo.get("number");
				Integer outPort = (Integer) outPortInfo.get("number");
				/* port 2 port flow */
				if (path.size() == 1) {
					client.setFlows(dpid, inPort, null, null, outPort, null, null, null, null);
					continue;
				}

				/* not port 2 prot flow */
				Map<String, Object> firstOfpsPort = dao.getPortInfoFromPortRid(conn, (String) path.get(0).get("in"));
				String  firstOfpsDevName    = (String)  firstOfpsPort.get("deviceName");
				Integer firstOfpsPortNumber = (Integer) firstOfpsPort.get("number");

				List<Map<String, Object>> macList = dao.getInternalMacInfoListFromDeviceNameInPort(
						conn,
						firstOfpsDevName,
						firstOfpsPortNumber);
				for (Map<String, Object> mac : macList) {
					Long interMac = (Long)   mac.get("internalMac");
					String srcMac = null;
					String dstMac = null;
					String modSrcMac = null;
					String modDstMac = null;

					if (sequence == 1) {
						/* first patch switch */
						srcMac = (String) mac.get("srcMac");
						dstMac = (String) mac.get("dstMac");
						modSrcMac = (OFPMUtils.longToMacAddress(interMac));
						modDstMac = (OFPMUtils.longToMacAddress(~interMac));
					} else if (sequence == path.size()) {
						/* final patch switch */
						srcMac = (OFPMUtils.longToMacAddress(interMac));
						modSrcMac = (String) mac.get("srcMac");
						modDstMac = (String) mac.get("dstMac");
					} else {
						/* relay patch swtich */
						srcMac = (OFPMUtils.longToMacAddress(interMac));
					}

					client.setFlows(dpid, inPort, srcMac, dstMac, outPort, modSrcMac, modDstMac, null, null);
				}

				/* If the OFPS is first patch switch for the route, set Packet-In flow. */
				if (sequence == 1) {
					client.setFlows(dpid, inPort, null, null, null, null, null, true, null);
				}
			}

		} catch (SQLException | OFCClientException e) {
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		} finally {
//			utils.rollback(conn);
			utils.close(conn);
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s)", fname, ret));
		}
		return res.toJson();
	}

	/**
	 * Calculate new used-value when reduced patchWiring.
	 * @param conn
	 * @param link
	 * @param band
	 * @param client
	 * @param ofpmToken
	 * @return
	 * @throws DMDBClientException
	 * @throws SQLException
	 */
	private long calcReduceCableLinkUsed(Connection conn, Map<String, Object> link, long band, DMDBClient client, String ofpmToken) throws DMDBClientException, SQLException {
		final String fname = "updateCableLinkUsed";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(conn=%s, link=%s, client=%s, ofpmToken=%s) - start", fname, conn, link, client, ofpmToken));
		}
		long used = (Long)link.get("used");
		long inBand = this.getBandWidth(conn, (String)link.get("inDeviceName"), (String)link.get("inPortName"), client, ofpmToken);
		long outBand = this.getBandWidth(conn, (String)link.get("outDeviceName"), (String)link.get("outPortName"), client, ofpmToken);
		long useBand = (inBand < outBand)? inBand : outBand;
		used -= band;
		if (used > useBand) {
			used = useBand - band;
		}
		if (used < 0) {
			used = 0;
			// MEMO: output log message, however not throw exception. That's right?
			logger.warn(String.format("Used value was been under than zero, and the value modify zero. %s", link));
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, used));
		}
		return used;
	}

	private MultivaluedMap<String, Nic> dmdbNicCache = new MultivaluedHashMap<String, Nic>();
	/**
	 * Get Nic info from Device Manager DB.
	 * @param deviceName
	 * @param nicName
	 * @param client DMDBClient
	 * @param ofpmToken
	 * @return
	 * @throws DMDBClientException
	 */
	private Nic getNic(String deviceName, String nicName, DMDBClient client, String ofpmToken) throws DMDBClientException {
		Nic ret = null;
		List<Nic> nics = dmdbNicCache.get(deviceName);
		if (nics == null) {
			NicReadRequest req = new NicReadRequest();
			req.setDeviceName(deviceName);
			req.setAuth(ofpmToken);
			NicReadResponse res = client.nicRead(req);
			if (res.getStatus() != STATUS_SUCCESS) {
				logger.error(res.getMessage());
			}
			if (res.getResult() == null) {
				return null;
			}
			nics = res.getResult();
			dmdbNicCache.put(deviceName, nics);
		}
		for (Nic nic: nics) {
			if (StringUtils.equals(nic.getNicName(), nicName)) {
				ret = nic;
			}
		}
		return ret;
	}

	private MultivaluedMap<String, Port> dmdbPortCache = new MultivaluedHashMap<String, Port>();
	/**
	 * Get port info from Device Manager DB.
	 * @param deviceName
	 * @param portName
	 * @param client DMDBClinet
	 * @param ofpmToken
	 * @return
	 * @throws DMDBClientException
	 */
	private Port getPort(String deviceName, String portName, DMDBClient client, String ofpmToken) throws DMDBClientException {
		Port ret = null;
		List<Port> ports = dmdbPortCache.get(deviceName);
		if (ports == null) {
			PortReadRequest req = new PortReadRequest();
			req.setDeviceName(deviceName);
			req.setAuth(ofpmToken);
			PortReadResponse res = client.portRead(req);
			if (res.getStatus() != STATUS_SUCCESS) {
				logger.error(res.getMessage());
			}
			if (res.getResult() == null) {
				return null;
			}
			ports = res.getResult();
			dmdbPortCache.put(deviceName, ports);
		}
		for (Port port : ports) {
			if (StringUtils.equals(port.getPortName(), portName)) {
				ret = port;
			}
		}
		return ret;
	}

	/**
	 * Get band width from Device Manager DB
	 * @param conn
	 * @param deviceName
	 * @param portName
	 * @param client DMDBClient
	 * @param ofpmToken
	 * @return
	 * @throws DMDBClientException
	 * @throws SQLException
	 */
	private long getBandWidth(Connection conn, String deviceName, String portName, DMDBClient client, String ofpmToken) throws DMDBClientException, SQLException {
		long band = 0;
		try {
			Map<String, Object> devMap = dao.getNodeInfoFromDeviceName(conn, deviceName);
			String devType = (String)devMap.get("type");
			if (StringUtils.equals(devType, NODE_TYPE_SERVER)) {
				Nic nic = this.getNic(deviceName, portName, client, ofpmToken);
				band = OFPMUtils.bandWidthToBaseMbps(nic.getBand());
			} else {
				Port port = this.getPort(deviceName, portName, client, ofpmToken);
				band = OFPMUtils.bandWidthToBaseMbps(port.getBand());
			}
		} catch (NullPointerException e) {
			throw new RuntimeException(String.format(NOT_FOUND, "port={deviceName:'" + deviceName + "', portName:'" + portName + "'}"));
		}
		return band;
	}

	/**
	 * Delete logical link, in fact remove patch wiring and update links used value.
	 * @param conn
	 * @param link
	 * @throws SQLException
	 * @throws DMDBClientException
	 */
	private void addDeclementLogicalLink(Connection conn, LogicalLink link, DMDBClient client, String ofpmToken, MultivaluedMap<String, Map<String, Object>> reducedFlows) throws SQLException, DMDBClientException {
		PortData inPort  = link.getLink().get(0);

		/* get patch wiring, and check it is exist. */
		List<Map<String, Object>> patchMapList = dao.getPatchWiringsFromDeviceNamePortName(conn, inPort.getDeviceName(), inPort.getPortName());
		if (patchMapList == null || patchMapList.isEmpty()) {
			throw new RuntimeException(String.format(NOT_FOUND, "patchWiring=" + link));
		}

		/* delete patch wiring */
		int reducedNumb = dao.deletePatchWiring(conn, inPort.getDeviceName(), inPort.getPortName());
		if (reducedNumb == 0) {
			throw new RuntimeException(String.format(COULD_NOT_DELETE, "patchWiring=" + link));
		}

		/* delete internal mac address */
		for (PortData portData : link.getLink()) {
			Map<String, Object> portMap = dao.getPortInfoFromPortName(
					conn,
					portData.getDeviceName(),
					portData.getPortName());
			Map<String, Object> nghbrPortMap = dao.getNeighborPortFromPortRid(conn, (String)portMap.get("rid"));
			if (nghbrPortMap != null) {
				dao.deleteInternalMac(conn,
									(String) nghbrPortMap.get("deviceName"),
									(int) nghbrPortMap.get("number"));
			}
		}

		Map<String, Object> txPatchMap = patchMapList.get(0);
		Map<String, Object> rxPatchMap = patchMapList.get(patchMapList.size() - 1);
		/* calc patch band width */
		long band = 0L;
		{
			Map<String, Object> txLinkMap = dao.getCableLinkFromInPortRid(conn, (String)txPatchMap.get("in"));
			Map<String, Object> rxLinkMap = dao.getCableLinkFromInPortRid(conn, (String)rxPatchMap.get("out"));
			long txBand = this.getBandWidth(conn, (String)txLinkMap.get("inDeviceName"), (String)txLinkMap.get("inPortName"), client, ofpmToken);
			long rxBand = this.getBandWidth(conn, (String)rxLinkMap.get("inDeviceName"), (String)rxLinkMap.get("inPortName"), client, ofpmToken);
			long txOfpBand = this.getBandWidth(conn, (String)txLinkMap.get("outDeviceName"), (String)txLinkMap.get("outPortName"), client, ofpmToken);
			long rxOfpBand = this.getBandWidth(conn, (String)rxLinkMap.get("outDeviceName"), (String)rxLinkMap.get("outPortName"), client, ofpmToken);
			band = (txBand < rxBand)   ? txBand:    rxBand;
			band = (band   < txOfpBand)?   band: txOfpBand;
			band = (band   < rxOfpBand)?   band: rxOfpBand;
		}

		/* update link-used-value and make patch link for ofc */
		List<String> alreadyProcCable = new ArrayList<String>();
		for (Map<String, Object> patchMap : patchMapList) {
			String inPortRid  = (String)patchMap.get("in");
			Map<String, Object> inLink = dao.getCableLinkFromInPortRid(conn, inPortRid);
			String inCableRid = (String)inLink.get("rid");
			if (!alreadyProcCable.contains(inCableRid)) {
				long newUsed = this.calcReduceCableLinkUsed(conn, inLink, band, client, ofpmToken);
				dao.updateCableLinkUsedFromPortRid(conn, inPortRid, newUsed);
				alreadyProcCable.add(inCableRid);
			}

			String outPortRid = (String)patchMap.get("out");
			Map<String, Object> outLink = dao.getCableLinkFromOutPortRid(conn, outPortRid);
			String outCableRid = (String)outLink.get("rid");
			if (!alreadyProcCable.contains(outCableRid)) {
				long newUsed = this.calcReduceCableLinkUsed(conn, outLink, band, client, ofpmToken);
				dao.updateCableLinkUsedFromPortRid(conn, outPortRid, newUsed);
				alreadyProcCable.add(outCableRid);
			}
		}
		/* make flow edge-switch tx side */
		Map<String, Object> txOfpsMap = null;
		Map<String, Object> txInPortMap = null;
		{
			txOfpsMap = dao.getDeviceInfoFromDeviceRid(conn, (String)txPatchMap.get("parent"));
			String dpid  = (String)txOfpsMap.get("datapathId");
			String ofcIp = (String)txOfpsMap.get("ofcIp");

			txInPortMap = dao.getPortInfoFromPortRid(conn, (String)txPatchMap.get("in"));
			Map<String, Object> flow = new HashMap<String, Object>();
			flow.put("datapathId", dpid);
			flow.put("inPort", txInPortMap.get("number"));
			flow.put("dropFlg", true);
			reducedFlows.add(ofcIp, flow);

			Map<String, Object> txOutPortMap = dao.getPortInfoFromPortRid(conn, (String)txPatchMap.get("out"));
			flow = new HashMap<String, Object>();
			flow.put("datapathId", dpid);
			flow.put("inPort", txOutPortMap.get("number"));
			flow.put("dropFlg", true);
			reducedFlows.add(ofcIp, flow);
		}
		/* make flow edge-switch rx side */
		Map<String, Object> rxOfpsMap = null;
		Map<String, Object> rxOutPortMap = null;
		{
			rxOfpsMap = dao.getDeviceInfoFromDeviceRid(conn, (String)rxPatchMap.get("parent"));
			String dpid  = (String)rxOfpsMap.get("datapathId");
			String ofcIp = (String)rxOfpsMap.get("ofcIp");

			rxOutPortMap = dao.getPortInfoFromPortRid(conn, (String)rxPatchMap.get("out"));
			Map<String, Object> flow = new HashMap<String, Object>();
			flow.put("datapathId", dpid);
			flow.put("inPort", rxOutPortMap.get("number"));
			flow.put("dropFlg", true);
			reducedFlows.add(ofcIp, flow);

			Map<String, Object> rxInPortMap = dao.getPortInfoFromPortRid(conn, (String)rxPatchMap.get("in"));
			flow = new HashMap<String, Object>();
			flow.put("datapathId", dpid);
			flow.put("inPort", rxInPortMap.get("number"));
			flow.put("dropFlg", true);
			reducedFlows.add(ofcIp, flow);
		}
		/* make flow internal switch */
		{
			List<String> txInterMacList = dao.getInternalMacListFromDeviceNameInPort(
					conn,
					(String)txOfpsMap.get("name"),
					((Integer)txInPortMap.get("number")).toString());
			List<String> rxInterMacList = dao.getInternalMacListFromDeviceNameInPort(
					conn,
					(String)rxOfpsMap.get("name"),
					((Integer)rxOutPortMap.get("number")).toString());
			for (int i = 1; i < patchMapList.size() - 1; i++) {
				String rid = (String)patchMapList.get(i).get("parent");

				Map<String, Object> ofpsMap = dao.getDeviceInfoFromDeviceRid(conn, rid);
				String dpid  = (String)ofpsMap.get("datapathId");
				String ofcIp = (String)ofpsMap.get("ofcIp");

				for (String txInterMac : txInterMacList) {
					Map<String, Object> flow = new HashMap<String, Object>();
					flow.put("datapathId", dpid);
					flow.put("srcMac", txInterMac);
					flow.put("dropFlg", true);
					reducedFlows.add(ofcIp, flow);
				}
				for (String rxInterMac : rxInterMacList) {
					Map<String, Object> flow = new HashMap<String, Object>();
					flow.put("datapathId", dpid);
					flow.put("srcMac", rxInterMac);
					flow.put("dropFlg", true);
					reducedFlows.add(ofcIp, flow);
				}
			}
		}
		return;
	}

	/**
	 * Create logical link, in fact insert patch wiring, update links used value, and then notify NCS.
	 * @param conn
	 * @param link
	 * @param client
	 * @param ofpmToken
	 * @param augmentedFlows
	 * @throws SQLException
	 * @throws DMDBClientException
	 * @throws NoRouteException
	 */
	private void addInclementLogicalLink(Connection conn, LogicalLink link, DMDBClient client, String ofpmToken, MultivaluedMap<String, Map<String, Object>> augmentedFlows) throws SQLException, DMDBClientException, NoRouteException {
		PortData tx = link.getLink().get(0);
		PortData rx = link.getLink().get(1);
		/* get rid of txPort/rxPort */
		String txRid = null;
		String rxRid = null;
		{
			Map<String, Object> txMap =
					(StringUtils.isBlank(tx.getPortName()))
					? dao.getNodeInfoFromDeviceName(conn, tx.getDeviceName())
					: dao.getPortInfoFromPortName(conn, tx.getDeviceName(), tx.getPortName());
			Map<String, Object> rxMap =
					(StringUtils.isBlank(rx.getPortName()))
					? dao.getNodeInfoFromDeviceName(conn, rx.getDeviceName())
					: dao.getPortInfoFromPortName(conn, rx.getDeviceName(), rx.getPortName());
			txRid = (String)txMap.get("rid");
			rxRid = (String)rxMap.get("rid");
		}

		/* get shortest path */
		List<Map<String, Object>> path = dao.getShortestPath(conn, txRid, rxRid);

		/* search first/last port */
		int txPortIndex = (StringUtils.isBlank(tx.getPortName()))? 1: 0;
		int rxPortIndex = (StringUtils.isBlank(rx.getPortName()))? path.size() - 2: path.size() - 1;
		Map<String, Object> txPort = path.get(txPortIndex);
		Map<String, Object> rxPort = path.get(rxPortIndex);

		/* check patch wiring exist */
		{
			boolean isTxPatch = dao.isContainsPatchWiringFromDeviceNamePortName(conn, (String)txPort.get("deviceName"), (String)txPort.get("name"));
			boolean isRxPatch = dao.isContainsPatchWiringFromDeviceNamePortName(conn, (String)rxPort.get("deviceName"), (String)rxPort.get("name"));
			if (isTxPatch || isRxPatch) {
				throw new NoRouteException(String.format(IS_NO_ROUTE, (String)txPort.get("deviceName") + " " + (String)txPort.get("name"), (String)rxPort.get("deviceName") + " " + (String)rxPort.get("name")));
			}
		}

		/* get band width of port info from dmdb */
		Map<Map<String, Object>, Long> portBandMap = new HashMap<Map<String, Object>, Long>();
		for (Map<String, Object> current : path) {
			if (StringUtils.equals((String)current.get("class"), "port")) {
				long band = this.getBandWidth(conn, (String)current.get("deviceName"), (String)current.get("name"), client, ofpmToken);
				portBandMap.put(current, band);
			}
		}

		/* conmute need band-width for patching */
		long needBand = 0;
		{
			long txBand = portBandMap.get(txPort);
			long rxBand = portBandMap.get(rxPort);
			long txNextBand = portBandMap.get(path.get(txPortIndex + 1));
			long rxNextBand = portBandMap.get(path.get(rxPortIndex - 1));
			needBand = (  txBand <     rxBand)?   txBand:     rxBand;
			needBand = (needBand < txNextBand)? needBand: txNextBand;
			needBand = (needBand < rxNextBand)? needBand: rxNextBand;
		}

		/* Update links used value */
		for (int i = 1; i < path.size(); i++) {
			Map<String, Object> nowV = path.get(i);
			Map<String, Object> prvV = path.get(i - 1);
			String nowClass = (String)nowV.get("class");
			String prvClass = (String)prvV.get("class");
			if (!StringUtils.equals(nowClass, "port") || !StringUtils.equals(prvClass, "port")) {
				continue;
			}

			String nowPortRid = (String)nowV.get("rid");
			Map<String, Object> cableLink = dao.getCableLinkFromInPortRid(conn, nowPortRid);
			long nowUsed = (long)(Long)cableLink.get("used");
			long  inBand = portBandMap.get(nowV);
			long outBand = portBandMap.get(prvV);
			long maxBand = (inBand < outBand)? inBand: outBand;
			long newUsed = nowUsed + needBand;
			if (newUsed > maxBand) {
				throw new NoRouteException(String.format(NOT_FOUND, "Path"));
			}
			else if (newUsed == maxBand) {
				newUsed = maxBand * LINK_MAXIMUM_USED_RATIO;
			}

			dao.updateCableLinkUsedFromPortRid(conn, nowPortRid, newUsed);
		}

		/* Make ofpatch index list */
		/* MEMO: Don't integrate to the loop for the above for easy to read. */
		List<Integer> ofpIndexList = new ArrayList<Integer>();
		for (int i = 1; i < path.size(); i++) {
			Map<String, Object> nowV = path.get(i);
			String nowClass = (String)nowV.get("class");
			String devType  = (String)nowV.get("type");
			if (!StringUtils.equals(nowClass, "node")) {
				continue;
			}
			if (!StringUtils.equals(devType, NODE_TYPE_LEAF) && !StringUtils.equals(devType, NODE_TYPE_SPINE)) {
				continue;
			}
			ofpIndexList.add(new Integer(i));
		}
		/* insert patch-wiring */
		for (int seq = 0; seq < ofpIndexList.size(); seq++) {
			int i = ofpIndexList.get(seq);
			/* insert frowarding patch wiring */
			Map<String, Object>  inPortDataMap = path.get(i-1);
			Map<String, Object> ofpPortDataMap = path.get(i);
			Map<String, Object> outPortDataMap = path.get(i+1);
			dao.insertPatchWiring(
					conn,
					(String)ofpPortDataMap.get("rid"),
					(String) inPortDataMap.get("rid"),
					(String)outPortDataMap.get("rid"),
					(String)txPort.get("deviceName"),
					(String)txPort.get("name"),
					(String)rxPort.get("deviceName"),
					(String)rxPort.get("name"),
					seq + 1);

			/* insert reversing patch wiring */
			dao.insertPatchWiring(
					conn,
					(String)ofpPortDataMap.get("rid"),
					(String)outPortDataMap.get("rid"),
					(String) inPortDataMap.get("rid"),
					(String)rxPort.get("deviceName"),
					(String)rxPort.get("name"),
					(String)txPort.get("deviceName"),
					(String)txPort.get("name"),
					ofpIndexList.size() - seq);

		}

		/* make SetFlowToOFC list for each ofcIp */
		/* port to port patching */
		if (ofpIndexList.size() == 1) {
			int i = ofpIndexList.get(0);
			Map<String, Object>  inPortDataMap = path.get(i - 1);
			Map<String, Object> ofpNodeDataMap = path.get(i);
			Map<String, Object> outPortDataMap = path.get(i + 1);

			Map<String, Object> flow = new HashMap<String, Object>();
			flow.put("datapathId", ofpNodeDataMap.get("datapathId"));
			flow.put("inPort", inPortDataMap.get("number"));
			flow.put("outPort", outPortDataMap.get("number"));
			flow.put("packetInFlg", false);
			augmentedFlows.add((String)ofpNodeDataMap.get("ofcIp"), flow);

			flow = new HashMap<String, Object>();
			flow.put("datapathId", ofpNodeDataMap.get("datapathId"));
			flow.put("inPort", outPortDataMap.get("number"));
			flow.put("outPort", inPortDataMap.get("number"));
			flow.put("packetInFlg", false);
			augmentedFlows.add((String)ofpNodeDataMap.get("ofcIp"), flow);

			return;
		}
		/* the first ofps flow */
		{
			int i = ofpIndexList.get(0);
			Map<String, Object>  inPortDataMap = path.get(i - 1);
			Map<String, Object> ofpNodeDataMap = path.get(i);

			Map<String, Object> flow = new HashMap<String, Object>();
			flow.put("datapathId", ofpNodeDataMap.get("datapathId"));
			flow.put("inPort", inPortDataMap.get("number"));
			flow.put("packetInFlg", true);
			augmentedFlows.add((String)ofpNodeDataMap.get("ofcIp"), flow);
		}
		/* the final ofps flow */
		{
			int i = ofpIndexList.get(ofpIndexList.size() - 1);
			Map<String, Object>  inPortDataMap = path.get(i + 1);
			Map<String, Object> ofpNodeDataMap = path.get(i);

			Map<String, Object> flow = new HashMap<String, Object>();
			flow.put("datapathId", ofpNodeDataMap.get("datapathId"));
			flow.put("inPort", inPortDataMap.get("number"));
			flow.put("packetInFlg", true);
			augmentedFlows.add((String)ofpNodeDataMap.get("ofcIp"), flow);
		}
		return;
	}
}
