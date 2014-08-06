package ool.com.ofpm.business;

import static ool.com.constants.ErrorMessage.*;
import static ool.com.constants.OfpmDefinition.*;
import static ool.com.constants.OrientDBDefinition.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ool.com.dmdb.client.DMDBClient;
import ool.com.dmdb.client.DMDBClientImpl;
import ool.com.dmdb.client.DeviceManagerDBClient;
import ool.com.dmdb.client.DeviceManagerDBClientImpl;
import ool.com.dmdb.exception.DMDBClientException;
import ool.com.dmdb.exception.DeviceManagerDBClientException;
import ool.com.dmdb.json.Nic;
import ool.com.dmdb.json.Port;
import ool.com.dmdb.json.Used;
import ool.com.dmdb.json.nic.NicReadRequest;
import ool.com.dmdb.json.nic.NicReadResponse;
import ool.com.dmdb.json.port.PortReadRequest;
import ool.com.dmdb.json.port.PortReadResponse;
import ool.com.ofpm.business.common.OFPatchCommon;
import ool.com.ofpm.business.common.OFPatchCommonImpl;
import ool.com.ofpm.client.OFCClient;
import ool.com.ofpm.client.NetworkConfigSetupperClient;
import ool.com.ofpm.client.NetworkConfigSetupperClientImpl;
import ool.com.ofpm.client.OFCClientImpl;
import ool.com.ofpm.exception.AgentManagerException;
import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.common.BaseResponse;
import ool.com.ofpm.json.common.GraphDevicePort;
import ool.com.ofpm.json.device.ConnectedPortGetJsonOut;
import ool.com.ofpm.json.device.PortData;
import ool.com.ofpm.json.ncs.NetworkConfigSetupperIn;
import ool.com.ofpm.json.ncs.NetworkConfigSetupperInData;
import ool.com.ofpm.json.ofc.AgentClientUpdateFlowReq;
import ool.com.ofpm.json.ofc.AgentClientUpdateFlowReq.AgentUpdateFlowData;
import ool.com.ofpm.json.ofc.PatchLink;
import ool.com.ofpm.json.ofc.SetFlowIn;
import ool.com.ofpm.json.topology.logical.LogicalLink;
import ool.com.ofpm.json.topology.logical.LogicalTopology;
import ool.com.ofpm.json.topology.logical.LogicalTopology.OfpConDeviceInfo;
import ool.com.ofpm.json.topology.logical.LogicalTopology.OfpConPortInfo;
import ool.com.ofpm.json.topology.logical.LogicalTopologyGetJsonOut;
import ool.com.ofpm.json.topology.logical.LogicalTopologyUpdateJsonIn;
import ool.com.ofpm.utils.Config;
import ool.com.ofpm.utils.ConfigImpl;
import ool.com.ofpm.utils.GraphDBUtil;
import ool.com.ofpm.utils.OFPMUtils;
import ool.com.ofpm.validate.common.BaseValidate;
import ool.com.ofpm.validate.topology.logical.LogicalTopologyValidate;
import ool.com.openam.client.OpenAmClient;
import ool.com.openam.client.OpenAmClientException;
import ool.com.openam.client.OpenAmClientImpl;
import ool.com.openam.json.OpenAmIdentitiesOut;
import ool.com.openam.json.TokenIdOut;
import ool.com.orientdb.client.ConnectionUtilsJdbc;
import ool.com.orientdb.client.ConnectionUtilsJdbcImpl;
import ool.com.orientdb.client.Dao;
import ool.com.orientdb.client.DaoImpl;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;

public class LogicalBusinessImpl implements LogicalBusiness {
	private static final Logger logger = Logger.getLogger(LogicalBusinessImpl.class);

	private AgentManager agentManager;
	private AgentClientUpdateFlowReq agentFlowJson = new AgentClientUpdateFlowReq();

	Config conf = new ConfigImpl();

	OFPatchCommon ofPatchBusiness = new OFPatchCommonImpl();

	Dao dao = null;

	final String DEBUG_AUTH_TOKEN = "";

	public LogicalBusinessImpl() {
		if (logger.isDebugEnabled()) {
			logger.debug("LogicalBusinessImpl");
		}
	}

	/**
	 * Normalize nodes for update/get LogicalTopology.
	 * Remove node that deviceType is not SERVER or SWITCH and remove node that have no ports.
	 * and remove node that have no ports.
	 * @param conn
	 * @param nodes
	 * @throws SQLException
	 */
	private void normalizeLogicalNode(Connection conn, Collection<OfpConDeviceInfo> nodes) throws SQLException {
		Map<String, Boolean> devTypeMap = new HashMap<String, Boolean>();
		List<OfpConDeviceInfo> removalNodeList = new ArrayList<OfpConDeviceInfo>();
		for (OfpConDeviceInfo node : nodes) {
			String devType = node.getDeviceType();
			if (!devType.equals(NODE_TYPE_SERVER) && !devType.equals(NODE_TYPE_SWITCH)) {
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
					isOfpSw = StringUtils.equals(outDevType, NODE_TYPE_LEAF);
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
	}

	/**
	 * Make node for update/get-LogicalTopology from deviceName, and return it.
	 * @param conn
	 * @param devName
	 * @return node for Logicaltopology.
	 * @throws SQLException
	 */
	private OfpConDeviceInfo getLogicalNode(Connection conn, String devName) throws SQLException {
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
			if (!OFPMUtils.nodesContainsPort(nodes, link.getLink().get(0))) {
				removalLinks.add(link);
			} else if (!OFPMUtils.nodesContainsPort(nodes, link.getLink().get(1))) {
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
	 * @return list of link for LogicalTopology.
	 * @throws SQLException
	 */
	private Set<LogicalLink> getLogicalLink(Connection conn, String devName) throws SQLException {
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
//		try {
//			String openamUrl = conf.getString(OPEN_AM_URL);
//			OpenAmClient openAmClient = new OpenAmClientImpl(openamUrl);
//			boolean isTokenValid = false;
//			if (!StringUtils.isBlank(tokenId) && openAmClient != null) {
//				TokenValidChkOut tokenValidchkOut = openAmClient.tokenValidateCheck(tokenId);
//				isTokenValid = tokenValidchkOut.getIsTokenValid();
//			}
//			if (isTokenValid != true) {
//				logger.error(String.format("Invalid tokenId. tokenId=%s", tokenId));
//				res.setStatus(STATUS_UNAUTHORIZED);
//				res.setMessage(String.format("Invalid tokenId. tokenId=%s", tokenId));
//				String ret = res.toJson();
//				if (logger.isDebugEnabled()) {
//					logger.debug(String.format("%s(ret=%s) - end", fname, ret));
//				}
//				return ret;
//			}
//		} catch (OpenAmClientException e) {
//			logger.error(e);
//			res.setStatus(STATUS_INTERNAL_ERROR);
//			res.setMessage(e.getMessage());
//			String ret = res.toJson();
//			if (logger.isDebugEnabled()) {
//				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
//			}
//			return ret;
//		}

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
			logger.error(e.getMessage());
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(e.getMessage());
			String ret = res.toJson();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		/* PHASE 3: Get logical topology */
		ConnectionUtilsJdbc utilsJdbc = null;
		Connection conn = null;
		try {
			utilsJdbc = new ConnectionUtilsJdbcImpl();
			conn = utilsJdbc.getConnection(false);
			dao = new DaoImpl(utilsJdbc);

			List<OfpConDeviceInfo> nodeList = new ArrayList<OfpConDeviceInfo>();
			List<LogicalLink> linkList = new ArrayList<LogicalLink>();
			Set<LogicalLink> linkSet = new HashSet<LogicalLink>();
			for (String devName : deviceNames) {
				OfpConDeviceInfo node = this.getLogicalNode(conn, devName);
				if (node == null) {
					continue;
				}
				nodeList.add(node);

				Set<LogicalLink> links = this.getLogicalLink(conn, devName);
				if (links == null) {
					continue;
				}
				linkSet.addAll(links);
			}
			linkList.addAll(linkSet);
			this.normalizeLogicalNode(conn, nodeList);
			this.normalizeLogicalLink(nodeList, linkList);

			LogicalTopology topology = new LogicalTopology();
			topology.setNodes(nodeList);
			topology.setLinks(linkList);

			// create response data
			res.setResult(topology);
			res.setStatus(STATUS_SUCCESS);
		} catch (Exception e) {
			logger.error(e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		} finally {
			utilsJdbc.rollback(conn);
			utilsJdbc.close(conn);
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	public String updateLogicalTopology(String requestedTopologyJson) {
		String fname = "updateLogicalTopology";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestedTopology=%s) - start", fname, requestedTopologyJson));
		}

		BaseResponse res = new BaseResponse();
		res.setStatus(STATUS_SUCCESS);

		LogicalTopologyUpdateJsonIn requestedTopology = null;
		try {
			requestedTopology = LogicalTopologyUpdateJsonIn.fromJson(requestedTopologyJson);
			if (requestedTopology == null) {

			}
		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			String ret = res.toString();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		/* PHASE 1: Authenticate */
//		try {
//			String openamUrl = conf.getString(OPEN_AM_URL);
//			OpenAmClient openAmClient = new OpenAmClientImpl(openamUrl);
//			String tokenId = requestedTopology.getTokenId();
//			boolean isTokenValid = false;
//			if (openAmClient != null) {
//				TokenValidChkOut tokenValidchkOut = openAmClient.tokenValidateCheck(tokenId);
//				isTokenValid = tokenValidchkOut.getIsTokenValid();
//			}
//			if (isTokenValid != true) {
//				if (logger.isDebugEnabled()) {
//					logger.error(String.format("Invalid tokenId. tokenId=%s", tokenId));
//				}
//				res.setStatus(STATUS_BAD_REQUEST);
//				res.setMessage(String.format("Invalid tokenId. tokenId=%s", tokenId));
//				String ret = res.toJson();
//				if (logger.isDebugEnabled()) {
//					logger.debug(String.format("%s(ret=%s) - end", fname, ret));
//				}
//				return ret;
//			}
//		} catch (OpenAmClientException e) {
//			logger.error(e);
//			res.setStatus(STATUS_UNAUTHORIZED);
//			res.setMessage(e.getMessage());
//			String ret = res.toString();
//			if (logger.isDebugEnabled()) {
//				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
//			}
//			return ret;
//		}

		/* PHASE 2: Validation */
		try {
			LogicalTopologyValidate validator = new LogicalTopologyValidate();
			validator.checkValidationRequestIn(requestedTopology);
			// TODO: check user tenant (by used-info in DMDB).
		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
			String ret = res.toString();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		/* PHASE 3: Get Auth token */
		String ofpmToken = null;
		try {
			String url = conf.getString(OPEN_AM_URL);
			String user = conf.getString(CONFIG_KEY_AUTH_USERNAME);
			String pass = conf.getString(CONFIG_KEY_AUTH_PASSWORD);
			OpenAmClient client = new OpenAmClientImpl(url);
			TokenIdOut tokenInfo = client.authenticate(user, pass);
			ofpmToken = tokenInfo.getTokenId();
		} catch (OpenAmClientException e) {
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error(e);
			logger.error(stack.toString());
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
			String ret = res.toString();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		}

		/* PHASE 4: Update topology */
		ConnectionUtilsJdbc utilsJdbc = null;
		Connection conn = null;
		try {
			/* initialize db connectors */
			{
				utilsJdbc = new ConnectionUtilsJdbcImpl();
				conn = utilsJdbc.getConnection(false);
				dao = new DaoImpl(utilsJdbc);
			}

			/* compute Inclement/Declement LogicalLink */
			List<LogicalLink> incLinkList = new ArrayList<LogicalLink>();
			List<LogicalLink> decLinkList = new ArrayList<LogicalLink>();
			{
				List<OfpConDeviceInfo> requestedNodes = requestedTopology.getNodes();
				List<LogicalLink> requestedLinkList = requestedTopology.getLinks();
				Set<LogicalLink> currentLinkList = new HashSet<LogicalLink>();

				/* Create current links */
				for (OfpConDeviceInfo requestedNode : requestedNodes) {
					String devName = requestedNode.getDeviceName();
					Set<LogicalLink> linkSet = this.getLogicalLink(conn, devName);
					if (linkSet != null) {
						currentLinkList.addAll(linkSet);
					}
				}
				this.normalizeLogicalLink(requestedNodes, currentLinkList);

				/* get difference between current and next */
				incLinkList.addAll(requestedLinkList);
				incLinkList.removeAll(currentLinkList);

				decLinkList.addAll(currentLinkList);
				decLinkList.removeAll(requestedLinkList);
			}

			/* update patch wiring and make patch link */
			List<PatchLink> reducedLinks = new ArrayList<PatchLink>();
			List<PatchLink> augmentedLinks = new ArrayList<PatchLink>();
			DMDBClient client = new DMDBClientImpl(conf.getString(DEVICE_MANAGER_URL));
			for (LogicalLink link : decLinkList) {
				reducedLinks.addAll(this.declementLogicalLink(conn, link, client, ofpmToken));
			}
			for (LogicalLink link : incLinkList) {
				augmentedLinks.addAll(this.inclementLogicalLink(conn, link, client, ofpmToken));
				/* Notify NCS */
//				List<String> deviceNames = link.getDeviceName();
//				List<Integer> portNames = augmentedPatches.getResult().get(0).getPortName();
//				int notifyNcsRet = notifyNcs(tokenId, deviceNames, portNames);
//				if (notifyNcsRet != STATUS_SUCCESS) {
//					res.setStatus(augmentedPatches.getStatus());
//					res.setMessage(augmentedPatches.getMessage());
//					return res.toJson();
//				}
			}


			/* Notify changed topology to ofp-controller. */
//			agentManager = AgentManager.getInstance();
//			Map<AgentClient, List<AgentUpdateFlowData>> agentUpdateFlowDataList = this.makeAgentUpdateFlowList(reducedLinks, "delete");
//			Map<AgentClient, List<AgentUpdateFlowData>> bufAgentUpdateReqList = this.makeAgentUpdateFlowList(augmentedLinks, "create");
//			agentUpdateFlowDataList.putAll(bufAgentUpdateReqList);
//
//			for (AgentClient agentClient : agentUpdateFlowDataList.keySet()) {
//				agentFlowJson.setList(agentUpdateFlowDataList.get(agentClient));
//
//				if (logger.isInfoEnabled()) {
//					logger.info(String.format("agentClient.updateFlows(flows=%s) - called", agentFlowJson));
//				}
//				ool.com.ofpm.json.common.BaseResponse resAgent = agentClient.updateFlows(agentFlowJson);
//				if (logger.isInfoEnabled()) {
//					logger.info(String.format("agentClinet.updateFlows(ret=%s) - returned", resAgent.toJson()));
//				}
//
//				//res = resAgent;
//				if (resAgent.getStatus() != STATUS_SUCCESS) {
//					/* TODO: Implement transaction */
//					res.setStatus(STATUS_INTERNAL_ERROR);
//					res.setMessage(UNEXPECTED_ERROR);
//					break;
//				}
//			}
			utilsJdbc.commit(conn);
			return res.toJson();

//		} catch (AgentManagerException ame) {
//			logger.error(ame);
//			res.setStatus(STATUS_INTERNAL_ERROR);
//			res.setMessage(UNEXPECTED_ERROR);
//			return res.toJson();
		} catch (Exception e) {
			logger.error(e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(UNEXPECTED_ERROR);
			utilsJdbc.rollback(conn);
			return res.toJson();
		} finally {
			utilsJdbc.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res.toJson()));
			}
		}
		/* must be not writing code from this point foward */
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

	private Map<OFCClient, List<AgentUpdateFlowData>> makeAgentUpdateFlowList(List<PatchLink> updatedLinks, String type) throws AgentManagerException {
		String fname = "makeAgentUpdateFlowList";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(updatedLinks=%s, type=%s) - start", fname, updatedLinks, type));
		}

		Map<OFCClient, List<AgentUpdateFlowData>> pairAgentClient_UpdateFlowDataList = new HashMap<OFCClient, List<AgentUpdateFlowData>>();
		for (PatchLink link : updatedLinks) {
			String switchIp = agentManager.getSwitchIp(link.getDeviceName());
			String ofcUrl = agentManager.getOfcUrl(switchIp);

			AgentUpdateFlowData newUpdateFlowData = agentFlowJson.new AgentUpdateFlowData();
			newUpdateFlowData.setIp(switchIp);
			newUpdateFlowData.setType(type);
			newUpdateFlowData.setPort(link.getPortName());
			newUpdateFlowData.setOfcUrl(ofcUrl);

			OFCClient agentClient = agentManager.getAgentClient(switchIp);
			if (!pairAgentClient_UpdateFlowDataList.containsKey(agentClient)) {
				pairAgentClient_UpdateFlowDataList.put(agentClient, new ArrayList<AgentUpdateFlowData>());
			}
			List<AgentUpdateFlowData> agentClientFlowDataList = pairAgentClient_UpdateFlowDataList.get(agentClient);
			agentClientFlowDataList.add(newUpdateFlowData);
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, pairAgentClient_UpdateFlowDataList));
		}
		return pairAgentClient_UpdateFlowDataList;
	}

	private boolean isOverlap(List<List<String>> dataList, List<String> data) {
		if (logger.isDebugEnabled()) {
    		logger.debug(String.format("isOverlap(dataList=%s, data=%s) - start ", dataList, data));
    	}
		boolean oneWordOverlapFlg = false;
		for (List<String> dataSet : dataList) {
			oneWordOverlapFlg = false;
			for ( String str : data) {
				if (!dataSet.contains(str)) {
					break;
				} else {
					if (oneWordOverlapFlg) {
						if (logger.isDebugEnabled()) {
				    		logger.debug(String.format("isOverlap(ret=%s) - end ", true));
				    	}
						return true;
					} else {
						oneWordOverlapFlg = true;
					}
				}
			}
		}
		if (logger.isDebugEnabled()) {
    		logger.debug(String.format("isOverlap(ret=%s) - end ", false));
    	}
		return false;
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
			conn = utilsJdbc.getConnection(false);
			deviceName = dao.getDeviceNameFromDatapathId(conn, req.getDpId());
			rid = dao.getPortRidFromDeviceNamePortNumber(conn, deviceName, Integer.parseInt(req.getInPort()));
			List<Map<String, Map<String, Object>>> ret = dao.getDevicePortInfoSetFlowFromPortRid(conn, rid);
			
			// generate InternalMac and setFlow to OFC
			Iterator<Map<String, Map<String, Object>>> it = ret.iterator();
			//String srcMac = req.getSrcMac();
			//String dstMac = req.getDstMac();
			String internalMac = dao.getInternalMacFromDeviceNameInPortSrcMacDstMac(conn, deviceName, req.getInPort(), req.getSrcMac(), req.getDstMac());
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
					srcMac = req.getSrcMac();
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
			logger.debug(String.format("%s(ret=%s) - start", fname, ret));
		}
		return ret;
	}

	/**
	 * Calculate new used-value when reduced patchWiring.
	 * @param conn
	 * @param link
	 * @param client
	 * @param ofpmToken
	 * @return
	 * @throws DMDBClientException
	 * @throws SQLException
	 */
	private long calcReduceCableLinkUsed(Connection conn, Map<String, Object> link, DMDBClient client, String ofpmToken) throws DMDBClientException, SQLException {
		final String fname = "updateCableLinkUsed";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(conn=%s, link=%s, client=%s, ofpmToken=%s) - start", fname, link, client, ofpmToken));
		}
		int band = (Integer)link.get("band");
		long used = (Long)link.get("used");
		long inBand = this.getBandWidth(conn, (String)link.get("inDeviceName"), (String)link.get("inPortName"), client, null);
		long outBand = this.getBandWidth(conn, (String)link.get("outDeviceName"), (String)link.get("outPortName"), client, null);
		long useBand = (inBand < outBand)? inBand : outBand;
		used -= useBand;
		if (used > band) {
			used = band - useBand;
		}
		if (used < 0) {
			used = 0;
			// MEMO: output log message, however not throw exception. That's right?
			logger.error(String.format("Used value was been under than zero, and the value modify zero. %s", link));
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, used));
		}
		return used;
	}

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
		Nic nic = new Nic();
		nic.setNicName(nicName);
		NicReadRequest req = new NicReadRequest();
		req.setDeviceName(deviceName);
		req.setParams(nic);
		req.setAuth(DEBUG_AUTH_TOKEN);
		NicReadResponse res = client.nicRead(req);
		if (res.getStatus() != STATUS_SUCCESS) {
			logger.error(res.getMessage());
		}
		if (res.getResult() != null && res.getResult().size() > 1) {
			ret = res.getResult().get(0);
		}
		return ret;
	}

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
		Port port = new Port();
		port.setPortName(portName);
		PortReadRequest req = new PortReadRequest();
		req.setDeviceName(deviceName);
		req.setParams(port);
		req.setAuth(DEBUG_AUTH_TOKEN);
		PortReadResponse res = client.portRead(req);
		if (res.getStatus() != STATUS_SUCCESS) {
			logger.error(res.getMessage());
		}
		if (res.getResult() != null && res.getResult().size() > 1) {
			ret = res.getResult().get(0);
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
	 * @return
	 * @throws SQLException
	 * @throws DMDBClientException
	 */
	private List<PatchLink> declementLogicalLink(Connection conn, LogicalLink link, DMDBClient client, String ofpmToken) throws SQLException, DMDBClientException {
		List<PatchLink> ret = new ArrayList<PatchLink>();
		PortData inPort  = link.getLink().get(0);

		/* get patch wiring, and check it is exist. */
		List<Map<String, Object>> patchDocList = dao.getPatchWiringsFromDeviceNamePortName(conn, inPort.getDeviceName(), inPort.getPortName());
		if (patchDocList == null || patchDocList.isEmpty()) {
			throw new RuntimeException(String.format(NOT_FOUND, "patchWiring=" + link));
		}

		/* delete patch wiring */
		int reducedNumb = dao.deletePatchWiring(conn, inPort.getDeviceName(), inPort.getPortName());
		if (reducedNumb == 0) {
			throw new RuntimeException(String.format(COULD_NOT_DELETE, "patchWiring=" + link));
		}

		/* update link-used-value and make patch link for ofc */
		List<String> alreadyProcCable = new ArrayList<String>();
		for (Map<String, Object> patchDoc : patchDocList) {
			String ofpRid = (String)patchDoc.get("parent");
			Map<String, Object> nodeDoc = dao.getNodeInfoFromDeviceRid(conn, ofpRid);
			String deviceName = (String)nodeDoc.get("name");
			int inPortNumber = 0;
			int outPortNumber = 0;

			String inPortRid  = (String)patchDoc.get("in");
			Map<String, Object> inLink = dao.getCableLinkFromPortRid(conn, inPortRid);
			String inCableRid = (String)inLink.get("rid");
			if (!alreadyProcCable.contains(inCableRid)) {
				long newUsed = this.calcReduceCableLinkUsed(conn, inLink, client, ofpmToken);
				dao.updateCableLinkUsedFromPortRid(conn, inPortRid, newUsed);
				inPortNumber = (Integer)inLink.get("inPortNumber");
				alreadyProcCable.add(inCableRid);
			}

			String outPortRid = (String)patchDoc.get("out");
			Map<String, Object> outLink = dao.getCableLinkFromPortRid(conn, outPortRid);
			String outCableRid = (String)outLink.get("rid");
			if (!alreadyProcCable.contains(outCableRid)) {
				long newUsed = this.calcReduceCableLinkUsed(conn, outLink, client, ofpmToken);
				dao.updateCableLinkUsedFromPortRid(conn, outPortRid, newUsed);
				outPortNumber = (Integer)outLink.get("inPortNumber");
				alreadyProcCable.add(outCableRid);
			}

			/* make remove patch link */
			List<Integer> portNames = new ArrayList<Integer>();
			portNames.add(inPortNumber);
			portNames.add(outPortNumber);

			PatchLink patchLink = new PatchLink();
			patchLink.setDeviceName(deviceName);
			patchLink.setPortName(portNames);

			ret.add(patchLink);
		}
		return ret;
	}

	/**
	 * Create logical link, in fact insert patch wiring, update links used value, and then notify NCS.
	 * @param conn
	 * @param link
	 * @param client
	 * @param token
	 * @return
	 * @throws SQLException
	 * @throws DMDBClientException
	 */
	private List<PatchLink> inclementLogicalLink(Connection conn, LogicalLink link, DMDBClient client, String token) throws SQLException, DMDBClientException {
		List<PatchLink> ret = new ArrayList<PatchLink>();

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
				throw new RuntimeException(String.format(ALREADY_EXIST, "patchWiring-" + link));
			}
		}

		/* get band width of port info from dmdb */
		Map<Map<String, Object>, Long> portBandMap = new HashMap<Map<String, Object>, Long>();
		for (Map<String, Object> current : path) {
			if (StringUtils.equals((String)current.get("class"), "port")) {
				long band = this.getBandWidth(conn, (String)current.get("deviceName"), (String)current.get("portName"), client, token);
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
			needBand = (txBand < rxBand)? txBand: rxBand;
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
			Map<String, Object> cableLink = dao.getCableLinkFromPortRid(conn, nowPortRid);
			long nowUsed = (long)(Long)cableLink.get("used");
			long  inBand = portBandMap.get(nowV);
			long outBand = portBandMap.get(prvV);
			long maxBand = (inBand < outBand)? inBand: outBand;
			long newUsed = nowUsed + needBand;
			if (newUsed > maxBand) {
				throw new RuntimeException(String.format(NOT_FOUND, "Path"));
			}
			else if (newUsed == maxBand) {
				newUsed = maxBand * LINK_MAXIMUM_USED_RATIO;
			}

			dao.updateCableLinkUsedFromPortRid(conn, nowPortRid, newUsed);
		}

		/* Make insert patch-wiring and make patch-link */
		/* MEMO: Don't integrate to the loop for the above for easy to read. */
		for (int i = 1; i < path.size(); i++) {
			Map<String, Object> nowV = path.get(i);
			String nowClass   = (String)nowV.get("class");
			String deviceType = (String)nowV.get("type");
			if (!StringUtils.equals(nowClass, "node")) {
				continue;
			}
			if (!StringUtils.equals(deviceType, NODE_TYPE_LEAF) && !StringUtils.equals(deviceType, NODE_TYPE_SPINE)) {
				continue;
			}

			/* insert patch wiring */
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
					(String)rxPort.get("name"));

			/* make PatchLink */
			List<Integer> ofpPortNmbrList = new ArrayList<Integer>();
			ofpPortNmbrList.add((Integer) inPortDataMap.get("number"));
			ofpPortNmbrList.add((Integer)outPortDataMap.get("number"));

			PatchLink patchLink = new PatchLink();
			patchLink.setDeviceName((String)ofpPortDataMap.get("name"));
			patchLink.setPortName(ofpPortNmbrList);

			ret.add(patchLink);
		}

		return ret;
	}
}
