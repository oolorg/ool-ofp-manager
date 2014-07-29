package ool.com.ofpm.business;

import static ool.com.constants.ErrorMessage.*;
import static ool.com.constants.OfpmDefinition.*;
import static ool.com.constants.OrientDBDefinition.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ool.com.dmdb.client.DeviceManagerDBClient;
import ool.com.dmdb.client.DeviceManagerDBClientImpl;
import ool.com.dmdb.exception.DeviceManagerDBClientException;
import ool.com.dmdb.json.Used;
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
import ool.com.openam.json.TokenValidChkOut;
import ool.com.orientdb.client.ConnectionUtilsImpl;
import ool.com.orientdb.client.ConnectionUtilsJdbcImpl;
import ool.com.orientdb.client.Dao;
import ool.com.orientdb.client.DaoImpl;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class LogicalBusinessImpl implements LogicalBusiness {
	private static final Logger logger = Logger.getLogger(LogicalBusinessImpl.class);

	private AgentManager agentManager;
	private AgentClientUpdateFlowReq agentFlowJson = new AgentClientUpdateFlowReq();

	Config conf = new ConfigImpl();

	OFPatchCommon ofPatchBusiness = new OFPatchCommonImpl();
	Dao dao = null;

	public LogicalBusinessImpl() {
		if (logger.isDebugEnabled()) {
			logger.debug("LogicalBusinessImpl");
		}
	}

	/**
	 * Normalize nodes for update/get LogicalTopology.
	 * Remove node that deviceType is not SERVER or SWITCH and remove node that have no ports.
	 * and remove node that have no ports.
	 * @param nodes
	 * @throws SQLException
	 */
	private void normalizeLogicalNode(List<OfpConDeviceInfo> nodes) throws SQLException {
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
					ODocument outDevDoc = dao.getDeviceInfo(neiDevName);
					String outDevType = outDevDoc.field("type");
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
	 * @param devName
	 * @return node for Logicaltopology.
	 * @throws SQLException
	 */
	private OfpConDeviceInfo getLogicalNode(String devName) throws SQLException {
		ODocument devDoc = dao.getDeviceInfo(devName);
		if (devDoc == null) {
			return null;
		}
		String   devType = devDoc.field("type");
		OfpConDeviceInfo node = new OfpConDeviceInfo();
		node.setDeviceName(devName);
		node.setDeviceType(devType);

		List<OfpConPortInfo> portList = new ArrayList<OfpConPortInfo>();
		List<ODocument> linkDocList = dao.getCableLinks(devName);
		if (linkDocList == null) {
			return null;
		}
		for (ODocument linkDoc : linkDocList) {
			String outDevName = linkDoc.field("outDeviceName");

			PortData ofpPort = new PortData();
			String outPortName = linkDoc.field("outPortName");
			String outPortNmbr = linkDoc.field("outPortNmbr");
			ofpPort.setDeviceName(outDevName);
			ofpPort.setPortName(outPortName);
			ofpPort.setPortNumber(Integer.parseInt(outPortNmbr));

			String inPortName = linkDoc.field("inPortName");
			String inPortNmbr = linkDoc.field("inPortNo");
			OfpConPortInfo port = new OfpConPortInfo();
			port.setPortName(inPortName);
			port.setPortNumber(Integer.parseInt(inPortNmbr));
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
	private void normalizeLogicalLink(List<OfpConDeviceInfo> nodes, List<LogicalLink> links) {
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
	 * @param devName
	 * @return list of link for LogicalTopology.
	 * @throws SQLException
	 */
	private Set<LogicalLink> getLogicalLink(String devName) throws SQLException {
		Set<LogicalLink> linkSet = new HashSet<LogicalLink>();
		List<ODocument> patchDocList = dao.getPatchWirings(devName);
		if (patchDocList == null) {
			return null;
		}
		for (ODocument patchDoc : patchDocList) {
			String inDevName  = patchDoc.field("inDeviceName");
			String inPortName = patchDoc.field("inPortName");
			PortData inPort = new PortData();
			inPort.setDeviceName(inDevName);
			inPort.setPortName(inPortName);

			String outDevName  = patchDoc.field("outDeviceName");
			String outPortName = patchDoc.field("outPortName");
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
		try {
			dao = new DaoImpl(new ConnectionUtilsImpl());
			List<OfpConDeviceInfo> nodeList = new ArrayList<OfpConDeviceInfo>();
			List<LogicalLink> linkList = new ArrayList<LogicalLink>();
			for (String devName : deviceNames) {
				OfpConDeviceInfo node = this.getLogicalNode(devName);
				if (node == null) {
					continue;
				}
				nodeList.add(node);

				Set<LogicalLink> linkSet = this.getLogicalLink(devName);
				if (linkSet == null) {
					continue;
				}
				linkList.addAll(linkSet);
			}
			this.normalizeLogicalNode(nodeList);
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
		try {
			List<OfpConDeviceInfo> requestedNodes = requestedTopology.getNodes();
			List<LogicalLink> requestedLinkList = requestedTopology.getLinks();
			List<LogicalLink> currentLinkList = new ArrayList<LogicalLink>();

			/* Create current links */
			for (OfpConDeviceInfo requestedNode : requestedNodes) {
				String devName = requestedNode.getDeviceName();

				Set<LogicalLink> linkSet = this.getLogicalLink(devName);
				if (linkSet != null) {
					currentLinkList.addAll(linkSet);
				}
			}
			this.normalizeLogicalLink(requestedNodes, currentLinkList);

			/* Create/Delete LogicalLink */
			List<LogicalLink> incLinkList = new ArrayList<LogicalLink>();
			incLinkList.addAll(requestedLinkList);
			incLinkList.removeAll(currentLinkList);

			List<LogicalLink> decLinkList = new ArrayList<LogicalLink>();
			decLinkList.addAll(currentLinkList);
			decLinkList.removeAll(requestedLinkList);

			List<PatchLink> reducedLinks = new ArrayList<PatchLink>();
			List<PatchLink> augmentedLinks = new ArrayList<PatchLink>();
			for (LogicalLink link : decLinkList) {
				PortData inPort  = link.getLink().get(0);
				PortData outPort = link.getLink().get(1);
				List<Map<String, Object>> patchDocList = dao.getPatchWirings(inPort.getDeviceName(), inPort.getPortName());
				dao.deletePatchWiring(inPort.getDeviceName(), inPort.getPortName());

				for (Map<String, Object> patchDoc : patchDocList) {
					String ofpRid = (String)patchDoc.get("parent");
					Map<String, Object> nodeDoc = dao.getDeviceInfoFromDeviceRid(ofpRid);
					String deviceName = (String)nodeDoc.get("name");
					String deviceType = (String)nodeDoc.get("type");
					int inPortNumber = 0;
					int outPortNumber = 0;
					if (deviceType.equals(NODE_TYPE_LEAF)) {
						String inPortRid  = (String)patchDoc.get("in");
						Map<String, Object> inLink = dao.getCableLinkFromPortRid(inPortRid);
						int newUsed = this.declementCableLinkUsed(inLink);
						dao.updateCableLinkUsedFromPortRid(inPortRid, newUsed);
						inPortNumber = (Integer)inLink.get("inPortNumber");

						String outPortRid = (String)patchDoc.get("out");
						Map<String, Object> outLink = dao.getCableLinkFromPortRid(outPortRid);
						newUsed = this.declementCableLinkUsed(outLink);
						dao.updateCableLinkUsedFromPortRid(outPortRid, newUsed);
						outPortNumber = (Integer)outLink.get("outPortNumber");
					}

					/* make remove patch link */
					List<Integer> portNames = new ArrayList<Integer>();
					portNames.add(inPortNumber);
					portNames.add(outPortNumber);

					PatchLink patchLink = new PatchLink();
					patchLink.setDeviceName(deviceName);
					patchLink.setPortName(portNames);

					reducedLinks.add(patchLink);
				}
			}
			for (LogicalLink link : incLinkList) {
				PortData txPort = link.getLink().get(0);
				PortData rxPort = link.getLink().get(1);
				ODocument txPortDoc = dao.getPortInfo(txPort.getPortName(), txPort.getDeviceName());
				ODocument rxPortDoc = dao.getPortInfo(rxPort.getPortName(), rxPort.getDeviceName());
				String txPortRid = txPortDoc.getIdentity().toString();
				String rxPortRid = rxPortDoc.getIdentity().toString();

				List<Map<String, Object>> path = dao.getShortestPath(txPortRid, rxPortRid);
				for (int i = 0; i < path.size(); i++) {
					Map<String, Object> vertex = path.get(i);
					String deviceType = (String)vertex.get("type");
					if (!StringUtils.equals(deviceType, NODE_TYPE_LEAF) && !StringUtils.equals(deviceType, NODE_TYPE_SPINE)) {
						continue;
					}

					if (deviceType.equals(NODE_TYPE_LEAF)) {
						/* update used value */
						Map<String, Object>  inPortMap = path.get(i-1);
						String  inPortRid = (String)inPortMap.get("@RID");
						Map<String, Object> inLink = dao.getCableLinkFromPortRid(inPortRid);
						int band = (Integer)inLink.get("band");
						int used = (Integer)inLink.get("used");
						int nicBand  = (Integer)path.get(i-2).get("band");
						int portBand = (Integer)path.get(i-1).get("band");
						int useBand  = (nicBand < portBand)? nicBand: portBand;
						int newUsed  = used + useBand;
						if (newUsed > used) {
							// error
						}
						if (newUsed == used) {
							newUsed = band * 1024 * 1024 * 1024;
						}
						dao.updateCableLinkUsedFromPortRid(inPortRid, newUsed);

						Map<String, Object>  outPortMap = path.get(i+1);
						String  outPortRid = (String)outPortMap.get("@RID");
						Map<String, Object> outLink = dao.getCableLinkFromPortRid(outPortRid);
						band = (Integer)outLink.get("band");
						used = (Integer)outLink.get("used");
						nicBand  = (Integer)path.get(i+2).get("band");
						portBand = (Integer)path.get(i+1).get("band");
						useBand  = (nicBand < portBand)? nicBand: portBand;
						newUsed  = used + useBand;
						if (newUsed > used) {
							// error
						}
						if (newUsed == used) {
							newUsed = band * 1024 * 1024 * 1024;
						}
						dao.updateCableLinkUsedFromPortRid(outPortRid, newUsed);
					}
					/* insert patch wiring */
					Map<String, Object>  inPortDataMap = path.get(i-1);
					Map<String, Object> ofpPortDataMap = path.get(i);
					Map<String, Object> outPortDataMap = path.get(i+1);
					dao.insertPatchWiring(
							(String)ofpPortDataMap.get("@RID"),
							(String) inPortDataMap.get("@RID"),
							(String)outPortDataMap.get("@RID"),
							(String)txPort.getDeviceName(),
							(String)txPort.getPortName(),
							(String)rxPort.getDeviceName(),
							(String)rxPort.getPortName());

					/* make PatchLink */
					List<Integer> ofpPortNmbrList = new ArrayList<Integer>();
					ofpPortNmbrList.add((Integer) inPortDataMap.get("number"));
					ofpPortNmbrList.add((Integer)outPortDataMap.get("number"));

					PatchLink patchLink = new PatchLink();
					patchLink.setDeviceName((String)ofpPortDataMap.get("name"));
					patchLink.setPortName(ofpPortNmbrList);

					augmentedLinks.add(patchLink);
				}

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
//		} catch (Exception e) {
//			logger.error(e);
//			res.setStatus(STATUS_INTERNAL_ERROR);
//			res.setMessage(UNEXPECTED_ERROR);
//			return res.toJson();

		} catch (SQLException e) {
			return res.toJson();
		} finally {
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
			dao = new DaoImpl(new ConnectionUtilsJdbcImpl());
			rid = dao.getDeviceNameFromDatapathId(requestedData);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}
		return rid;
	}

	private int declementCableLinkUsed(Map<String, Object> link) {
		final String fname = "updateCableLinkUsed";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(portRid=%s) - start"));
		}
		int band = (Integer)link.get("band");
		int used = (Integer)link.get("used");
		int inBand  = (Integer)link.get("inBand");
		int outBand = (Integer)link.get("outBand");
		int useBand = (inBand < outBand)? inBand : outBand;
		if (used < band) {
			used -= useBand;
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, used));
		}
		return used;
	}
}
