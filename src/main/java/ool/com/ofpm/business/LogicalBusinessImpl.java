package ool.com.ofpm.business;

import static ool.com.constants.ErrorMessage.*;
import static ool.com.constants.OfpmDefinition.*;
import static ool.com.constants.OrientDBDefinition.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import ool.com.ofpm.client.AgentClient;
import ool.com.ofpm.client.NetworkConfigSetupperClient;
import ool.com.ofpm.client.NetworkConfigSetupperClientImpl;
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

	/**
	 *
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
		Connection conn = null;
		try {
			ConnectionUtilsJdbc utilsJdbc = new ConnectionUtilsJdbcImpl();
			dao = new DaoImpl(utilsJdbc);
			conn = utilsJdbc.getConnection(false);

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
			try {
				if (conn != null && !conn.isClosed()) {
					conn.rollback();
				}
			} catch (SQLException e1) {
				logger.error(e1.getMessage());
			}
		} finally {
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
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

		/* PHASE 3: Update topology */
		Connection conn = null;
		try {
			/* initialize db connectors */
			{
				ConnectionUtilsJdbc utilsJdbc = new ConnectionUtilsJdbcImpl();
				dao = new DaoImpl(utilsJdbc);
				conn = utilsJdbc.getConnection(false);
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
			for (LogicalLink link : decLinkList) {
				reducedLinks.addAll(this.declementLogicalLink(conn, link));
			}
			for (LogicalLink link : incLinkList) {
				augmentedLinks.addAll(this.inclementLogicalLink(conn, link));
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
			return res.toJson();
		} finally {
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
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

	private Map<AgentClient, List<AgentUpdateFlowData>> makeAgentUpdateFlowList(List<PatchLink> updatedLinks, String type) throws AgentManagerException {
		String fname = "makeAgentUpdateFlowList";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(updatedLinks=%s, type=%s) - start", fname, updatedLinks, type));
		}

		Map<AgentClient, List<AgentUpdateFlowData>> pairAgentClient_UpdateFlowDataList = new HashMap<AgentClient, List<AgentUpdateFlowData>>();
		for (PatchLink link : updatedLinks) {
			String switchIp = agentManager.getSwitchIp(link.getDeviceName());
			String ofcUrl = agentManager.getOfcUrl(switchIp);

			AgentUpdateFlowData newUpdateFlowData = agentFlowJson.new AgentUpdateFlowData();
			newUpdateFlowData.setIp(switchIp);
			newUpdateFlowData.setType(type);
			newUpdateFlowData.setPort(link.getPortName());
			newUpdateFlowData.setOfcUrl(ofcUrl);

			AgentClient agentClient = agentManager.getAgentClient(switchIp);
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

		String rid = "";
		try {
			dao = new DaoImpl();
			ConnectionUtilsJdbc utilsJdbc = new ConnectionUtilsJdbcImpl();
			dao.setConnectionUtilsJdbc(utilsJdbc);
			Connection conn = utilsJdbc.getConnection(false);
			rid = dao.getDeviceNameFromDatapathId(conn, requestedData);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}
		return rid;
	}

	/**
	 *
	 * @param link
	 * @return
	 * @throws SQLException
	 * @throws DMDBClientException
	 */
	private long calcReduceCableLinkUsed(DMDBClient client, Connection conn, Map<String, Object> link) throws DMDBClientException, SQLException {
		final String fname = "updateCableLinkUsed";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(link=%s) - start", fname, link));
		}
		int band = (Integer)link.get("band");
		long used = (Long)link.get("used");
		long inBand = this.getBandWidth(client, conn, (String)link.get("inDeviceName"), (String)link.get("inPortName"));
		long outBand = this.getBandWidth(client, conn, (String)link.get("outDeviceName"), (String)link.get("outPortName"));
		long useBand = (inBand < outBand)? inBand : outBand;
		used -= useBand;
		if (used > band) {
			used = band - useBand;
		}
		if (used < 0) {
			used = 0;
			// TODO: error?
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, used));
		}
		return used;
	}

	/**
	 * Get Nic info from Device Manager DB.
	 * @param client DMDBClient
	 * @param deviceName
	 * @param nicName
	 * @return
	 * @throws DMDBClientException
	 */
	private Nic getNic(DMDBClient client, String deviceName, String nicName) throws DMDBClientException {
		Nic ret = null;
		Nic nic = new Nic();
		nic.setNicName(nicName);
		NicReadRequest req = new NicReadRequest();
		req.setDeviceName(deviceName);
		req.setParams(nic);
		req.setAuth(DEBUG_AUTH_TOKEN);
		NicReadResponse res = client.nicRead(req);
		if (res.getStatus() != STATUS_SUCCESS) {
			// TODO: error
		}
		if (res.getResult() != null && res.getResult().size() > 1) {
			ret = res.getResult().get(0);
		}
		return ret;
	}

	/**
	 * Get port info from Device Manager DB.
	 * @param client DMDBClinet
	 * @param deviceName
	 * @param portName
	 * @return
	 * @throws DMDBClientException
	 */
	private Port getPort(DMDBClient client, String deviceName, String portName) throws DMDBClientException {
		Port ret = null;
		Port port = new Port();
		port.setPortName(portName);
		PortReadRequest req = new PortReadRequest();
		req.setDeviceName(deviceName);
		req.setParams(port);
		req.setAuth(DEBUG_AUTH_TOKEN);
		PortReadResponse res = client.portRead(req);
		if (res.getStatus() != STATUS_SUCCESS) {
			// TODO : error
		}
		if (res.getResult() != null && res.getResult().size() > 1) {
			ret = res.getResult().get(0);
		}
		return ret;
	}

	/**
	 * Get band width from Device Manager DB
	 * @param client DMDBClient
	 * @param conn
	 * @param deviceName
	 * @param portName
	 * @return
	 * @throws DMDBClientException
	 * @throws SQLException
	 */
	private long getBandWidth(DMDBClient client, Connection conn, String deviceName, String portName) throws DMDBClientException, SQLException {
		long band = 0;
		Map<String, Object> devMap = dao.getNodeInfoFromDeviceName(conn, deviceName);
		if (((String)devMap.get("type")).equals(NODE_TYPE_SERVER)) {
			Nic nic = this.getNic(client, deviceName, portName);
//			band = OFPMUtils.bandWidthToBaseMbps(nic.getBand());
			band = OFPMUtils.bandWidthToBaseMbps("1Gbps");
		} else {
			Port port = this.getPort(client, deviceName, portName);
			band = OFPMUtils.bandWidthToBaseMbps("1Gbps");
//			band = OFPMUtils.bandWidthToBaseMbps(port.getBand());
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
	private List<PatchLink> declementLogicalLink(Connection conn, LogicalLink link) throws SQLException, DMDBClientException {
		List<PatchLink> ret = new ArrayList<PatchLink>();
		PortData inPort  = link.getLink().get(0);
		List<Map<String, Object>> patchDocList = dao.getPatchWiringsFromDeviceNamePortName(conn, inPort.getDeviceName(), inPort.getPortName());
		if (patchDocList == null || patchDocList.isEmpty()) {
			// TODO: error
		}
		int reducedNumb = dao.deletePatchWiring(conn, inPort.getDeviceName(), inPort.getPortName());
		if (reducedNumb == 0) {
			// TODO: error
		}

		DMDBClient client = new DMDBClientImpl(conf.getString(DEVICE_MANAGER_URL));
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
				long newUsed = this.calcReduceCableLinkUsed(client, conn, inLink);
				dao.updateCableLinkUsedFromPortRid(conn, inPortRid, newUsed);
				inPortNumber = (Integer)inLink.get("inPortNumber");
				alreadyProcCable.add(inCableRid);
			}

			String outPortRid = (String)patchDoc.get("out");
			Map<String, Object> outLink = dao.getCableLinkFromPortRid(conn, outPortRid);
			String outCableRid = (String)outLink.get("rid");
			if (!alreadyProcCable.contains(outCableRid)) {
				long newUsed = this.calcReduceCableLinkUsed(client, conn, outLink);
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
	 * @return
	 * @throws SQLException
	 * @throws DMDBClientException
	 */
	private List<PatchLink> inclementLogicalLink(Connection conn, LogicalLink link) throws SQLException, DMDBClientException {
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
		if (dao.getPatchWiringsFromDeviceNamePortName(conn, (String)txPort.get("deviceName"), (String)txPort.get("name")).size() > 0) {
			// TODO : error
		}
		if (dao.getPatchWiringsFromDeviceNamePortName(conn, (String)rxPort.get("deviceName"), (String)rxPort.get("name")).size() > 0) {
			// TODO : error
		}
		/* conmute need band-width for patching */
		long needBand = 0;
		{
			DMDBClient client = new DMDBClientImpl(conf.getString(DEVICE_MANAGER_URL));
			Map<String, Object> txNextPort = path.get(txPortIndex + 1);
			Map<String, Object> rxBackPort = path.get(rxPortIndex - 1);
			long txBand = this.getBandWidth(client, conn, (String)txPort.get("deviceName"), (String)txPort.get("name"));
			long rxBand = this.getBandWidth(client, conn, (String)rxPort.get("deviceName"), (String)rxPort.get("name"));
			long txNextBand = this.getBandWidth(client, conn, (String)txNextPort.get("deviceName"), (String)txNextPort.get("name"));
			long rxNextBand = this.getBandWidth(client, conn, (String)rxBackPort.get("deviceName"), (String)rxBackPort.get("name"));
			needBand = (txBand < rxBand)? txBand: rxBand;
			needBand = (needBand < txNextBand)? needBand: txNextBand;
			needBand = (needBand < rxNextBand)? needBand: rxNextBand;
		}

		/* Update links used value */
		for (int i = 1; i < path.size(); i++) {
			Map<String, Object> currentV = path.get(i);
			String vClass = (String)currentV.get("class");
			if (!StringUtils.equals(vClass, "port")) {
				continue;
			}
			Map<String, Object> beforV = path.get(i - 1);
			if (!StringUtils.equals((String)beforV.get("class"), "port")) {
				continue;
			}

			String currentPortRid = (String)currentV.get("rid");
			Map<String, Object> cableLink = dao.getCableLinkFromPortRid(conn, currentPortRid);
			long band = (long)(Integer)cableLink.get("band");
			long used = (long)(Long)cableLink.get("used");
			long newUsed = used + needBand;
			if (newUsed > band) {
				// TODO: error
			}
			else if (newUsed == band) {
				newUsed = band * 10000;
			}

			dao.updateCableLinkUsedFromPortRid(conn, currentPortRid, newUsed);
		}

		/* Make insert patch-wiring and make patch-link */
		/* MEMO: Don't integrate to the loop for the above for easy to read. */
		for (int i = 1; i < path.size(); i++) {
			Map<String, Object> currentV = path.get(i);
			String vClass = (String)currentV.get("class");
			if (!StringUtils.equals(vClass, "node")) {
				continue;
			}
			String deviceType = (String)currentV.get("type");
			if (StringUtils.isBlank(deviceType)) {
				// TODO: error
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

		/* Notify NCS */
//		List<String> deviceNames = link.getDeviceName();
//		List<Integer> portNames = augmentedPatches.getResult().get(0).getPortName();
//		int notifyNcsRet = notifyNcs(tokenId, deviceNames, portNames);
//		if (notifyNcsRet != STATUS_SUCCESS) {
//			res.setStatus(augmentedPatches.getStatus());
//			res.setMessage(augmentedPatches.getMessage());
//			return res.toJson();
//		}
		return ret;
	}
}
