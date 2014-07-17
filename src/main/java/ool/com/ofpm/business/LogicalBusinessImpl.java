package ool.com.ofpm.business;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ool.com.dmdb.client.DeviceManagerDBClient;
import ool.com.dmdb.client.DeviceManagerDBClientImpl;
import ool.com.dmdb.exception.DeviceManagerDBClientException;
import ool.com.dmdb.json.Used;
import ool.com.ofpm.business.common.OFPatchCommon;
import ool.com.ofpm.business.common.OFPatchCommonImpl;
import ool.com.ofpm.client.AgentClient;
import ool.com.ofpm.client.NetworkConfigSetupperClient;
import ool.com.ofpm.client.NetworkConfigSetupperClientImpl;
import ool.com.ofpm.exception.AgentClientException;
import ool.com.ofpm.exception.AgentManagerException;
import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.common.BaseResponse;
import ool.com.ofpm.json.common.GraphDevicePort;
import ool.com.ofpm.json.common.Node;
import ool.com.ofpm.json.device.ConnectedPortGetJsonOut;
import ool.com.ofpm.json.ncs.NetworkConfigSetupperIn;
import ool.com.ofpm.json.ncs.NetworkConfigSetupperInData;
import ool.com.ofpm.json.ofc.AgentClientUpdateFlowReq;
import ool.com.ofpm.json.ofc.PatchLink;
import ool.com.ofpm.json.ofc.AgentClientUpdateFlowReq.AgentUpdateFlowData;
import ool.com.ofpm.json.ofpatch.GraphDBPatchLinkJsonRes;
import ool.com.ofpm.json.topology.logical.LogicalLink;
import ool.com.ofpm.json.topology.logical.LogicalTopology;
import ool.com.ofpm.json.topology.logical.LogicalTopologyGetJsonOut;
import ool.com.ofpm.json.topology.logical.LogicalTopologyUpdateJsonIn;
import ool.com.ofpm.utils.Config;
import ool.com.ofpm.utils.ConfigImpl;
import ool.com.ofpm.utils.GraphDBUtil;
import ool.com.ofpm.validate.CommonValidate;
import ool.com.ofpm.validate.LogicalTopologyValidate;
import ool.com.openam.client.OpenAmClient;
import ool.com.openam.client.OpenAmClientException;
import ool.com.openam.client.OpenAmClientImpl;
import ool.com.openam.json.OpenAmIdentitiesOut;
import ool.com.openam.json.TokenIdOut;
import ool.com.openam.json.TokenValidChkOut;
import ool.com.orientdb.client.ConnectionUtils;
import ool.com.orientdb.client.ConnectionUtilsImpl;
import ool.com.orientdb.client.Dao;
import ool.com.orientdb.client.DaoImpl;
import ool.com.util.Definition;
import ool.com.util.ErrorMessage;

import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class LogicalBusinessImpl implements LogicalBusiness {
	private static final Logger logger = Logger.getLogger(LogicalBusinessImpl.class);

	private AgentManager agentManager;
	private AgentClientUpdateFlowReq agentFlowJson = new AgentClientUpdateFlowReq();

	Config conf = new ConfigImpl();

	OFPatchCommon ofPatchBusiness = new OFPatchCommonImpl();

	public LogicalBusinessImpl() {
		if (logger.isDebugEnabled()) {
			logger.debug("LogicalBusinessImpl");
		}
	}

	private void filterTopology(List<ool.com.ofpm.json.common.Node> nodes, List<LogicalLink> linkList) {
		String fname = "filterTopology";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(nodes=%s, linkList=%s) - start", fname, nodes, linkList));
		}

		List<String> deviceNames = new ArrayList<String>();
		for (Node node : nodes) {
			deviceNames.add(node.getDeviceName());
		}
		List<LogicalLink> removalLinks = new ArrayList<LogicalLink>();
		for (LogicalLink link : linkList) {
			if (!deviceNames.containsAll(link.getDeviceName())) {
				removalLinks.add(link);
			}
		}
		linkList.removeAll(removalLinks);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

	public BaseResponse getLogicalTopologyExec(List<Node> nodeList,List<LogicalLink> linkList) {
		BaseResponse ret = new BaseResponse();
		ConnectionUtils utils = new ConnectionUtilsImpl();
		ODocument document = null;
		Dao dao = null;
		LogicalLink link = null;
		List<List<String>> connectNodeList = new ArrayList<List<String>>();
		List<List<String>> connectedDeviceNameList = new ArrayList<List<String>>();

		try {
			dao = new DaoImpl(utils);

			for (int i=0; i < nodeList.size(); i++) {
				Node node = nodeList.get(i);
				document = dao.getDeviceInfo(node.getDeviceName());
				node.setDeviceName(document.field("name").toString());
				node.setDeviceType(document.field("type").toString());
			}

			for (Node node : nodeList) {
				connectedDeviceNameList.addAll(dao.getPatchConnectedDevice(node.getDeviceName()));
			}
			for(List<String> connectNode : connectedDeviceNameList) {
				if (isOverlap(connectNodeList, connectNode)) {
					continue;
				}
				connectNodeList.add(connectNode);
			}

			for (List<String> cn : connectNodeList) {
				link = new LogicalLink();
				link.setDeviceName(cn);
				linkList.add(link);
			}

			ret.setStatus(Definition.STATUS_SUCCESS);
    	} catch (SQLException e) {
    		logger.error(e.getMessage());
			ret.setMessage(e.getMessage());
    		if (e.getCause() != null) {
    			ret.setStatus(Definition.STATUS_NOTFOUND);
    		} else {
    			ret.setStatus(Definition.STATUS_INTERNAL_ERROR);
    		}
		} catch (RuntimeException re) {
			logger.error(re.getMessage());
			ret.setStatus(Definition.STATUS_INTERNAL_ERROR);
    		ret.setMessage(re.getMessage());
		}
    	finally {
			try {
				if(dao != null) {
					dao.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
				ret.setStatus(Definition.STATUS_INTERNAL_ERROR);
	    		ret.setMessage(e.getMessage());
			}
		}

		return ret;
	}

	public String getLogicalTopology(String deviceNamesCSV, String tokenId) {
		String fname = "getLogicalTopology";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceNames=%s, tokenId%s) - start", fname, deviceNamesCSV, tokenId));
		}

		LogicalTopologyGetJsonOut res = new LogicalTopologyGetJsonOut();
		LogicalTopology resultData = new LogicalTopology();
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

			List<Node> nodeList = new ArrayList<Node>();
			for (String deviceName : deviceNames) {
				Node node = new Node();
				node.setDeviceName(deviceName);
				nodeList.add(node);
			}

			List<LogicalLink> linkList = new ArrayList<LogicalLink>();
			BaseResponse ret = getLogicalTopologyExec(nodeList, linkList);
			if (ret.getStatus() != Definition.STATUS_SUCCESS) {
				nodeList.removeAll(nodeList);
			}

			this.filterTopology(nodeList, linkList);

			// create response data
			resultData.setNodes(nodeList);
			resultData.setLinks(linkList);
			res.setResult(resultData);
			res.setStatus(ret.getStatus());
			res.setMessage(ret.getMessage());

		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());

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

	public String updateLogicalTopology(String requestedTopologyJson) {
		String fname = "updateLogicalTopology";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestedTopology=%s) - start", fname, requestedTopologyJson));
		}

		BaseResponse res = new BaseResponse();
		res.setStatus(Definition.STATUS_SUCCESS);
		String openamUrl = conf.getString(Definition.OPEN_AM_URL);
		OpenAmClient openAmClient = new OpenAmClientImpl(openamUrl);

		try {
			LogicalTopologyUpdateJsonIn requestedTopology = LogicalTopologyUpdateJsonIn.fromJson(requestedTopologyJson);

			LogicalTopologyValidate validator = new LogicalTopologyValidate();
			validator.checkValidationRequestIn(requestedTopology);

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
				res.setStatus(Definition.STATUS_BAD_REQUEST);
				res.setMessage(String.format("Invalid tokenId. tokenId=%s", tokenId));
				String ret = res.toJson();
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("%s(ret=%s) - end", fname, ret));
				}
				return ret;
			}

			List<Node> requestedNodes = requestedTopology.getNodes();
			List<LogicalLink> currentLinkList = new ArrayList<LogicalLink>();
			BaseResponse getltsRet = getLogicalTopologyExec(requestedNodes, currentLinkList);
			if (getltsRet.getStatus() != Definition.STATUS_SUCCESS) {
				res.setStatus(getltsRet.getStatus());
				res.setMessage(getltsRet.getMessage());
				String ret = res.toJson();
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("%s(ret=%s) - end", fname, ret));
				}
				return ret;
			}

			this.filterTopology(requestedNodes, currentLinkList);

			requestedTopology.getLinks().removeAll(currentLinkList);
			List<LogicalLink> incLinkList = requestedTopology.getLinks();
			currentLinkList.removeAll(incLinkList);
			List<LogicalLink> decLinkList = currentLinkList;

			List<PatchLink> reducedLinks = new ArrayList<PatchLink>();
			List<PatchLink> augmentedLinks = new ArrayList<PatchLink>();
			for (LogicalLink link : decLinkList) {

				GraphDBPatchLinkJsonRes reducedPatches = ofPatchBusiness.disConnectPatch(link.getDeviceName());
				if (reducedPatches.getStatus() != Definition.STATUS_SUCCESS) {
					res.setStatus(reducedPatches.getStatus());
					res.setMessage(reducedPatches.getMessage());
					return res.toJson();
				}
				reducedLinks.addAll(reducedPatches.getResult());
			}
			for (LogicalLink link : incLinkList) {

				GraphDBPatchLinkJsonRes augmentedPatches = ofPatchBusiness.connectPatch(link.getDeviceName());
				if (augmentedPatches.getStatus() != Definition.STATUS_CREATED) {
					res.setStatus(augmentedPatches.getStatus());
					res.setMessage(augmentedPatches.getMessage());
					return res.toJson();
				}

				List<String> deviceNames = link.getDeviceName();
				List<Integer> portNames = augmentedPatches.getResult().get(0).getPortName();
				int notifyNcsRet = notifyNcs(tokenId, deviceNames, portNames);
				if (notifyNcsRet != Definition.STATUS_SUCCESS) {
					res.setStatus(augmentedPatches.getStatus());
					res.setMessage(augmentedPatches.getMessage());
					return res.toJson();
				}

				augmentedLinks.addAll(augmentedPatches.getResult());
			}

			agentManager = AgentManager.getInstance();
			Map<AgentClient, List<AgentUpdateFlowData>> agentUpdateFlowDataList = this.makeAgentUpdateFlowList(reducedLinks, "delete");
			Map<AgentClient, List<AgentUpdateFlowData>> bufAgentUpdateReqList = this.makeAgentUpdateFlowList(augmentedLinks, "create");
			agentUpdateFlowDataList.putAll(bufAgentUpdateReqList);

			for (AgentClient agentClient : agentUpdateFlowDataList.keySet()) {
				agentFlowJson.setList(agentUpdateFlowDataList.get(agentClient));

				if (logger.isInfoEnabled()) {
					logger.info(String.format("agentClient.updateFlows(flows=%s) - called", agentFlowJson));
				}
				ool.com.ofpm.json.common.BaseResponse resAgent = agentClient.updateFlows(agentFlowJson);
				if (logger.isInfoEnabled()) {
					logger.info(String.format("agentClinet.updateFlows(ret=%s) - returned", resAgent.toJson()));
				}

				//res = resAgent;
				if (resAgent.getStatus() != Definition.STATUS_SUCCESS) {
					/* TODO: Implement transaction */
					res.setStatus(Definition.STATUS_INTERNAL_ERROR);
					res.setMessage(ErrorMessage.UNEXPECTED_ERROR);
					break;
				}
			}
			return res.toJson();
		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ErrorMessage.INVALID_JSON);
			return res.toJson();

		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
			return res.toJson();

		} catch (AgentClientException ace) {
			logger.error(ace);
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(ace.getMessage());
			return res.toJson();

		} catch (AgentManagerException ame) {
			logger.error(ame);
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(ErrorMessage.UNEXPECTED_ERROR);
			return res.toJson();
		} catch (Exception e) {
			logger.error(e);
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(ErrorMessage.UNEXPECTED_ERROR);
			return res.toJson();

		} finally {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res.toJson()));
			}
		}
		/* must be not writing code from this point foward */
	}

	public int notifyNcs(String tokenId, List<String> deviceNames, List<Integer> portNames) {
		int ret = Definition.STATUS_SUCCESS;
		String openamUrl = conf.getString(Definition.OPEN_AM_URL);
		OpenAmClient openAmClient = new OpenAmClientImpl(openamUrl);
		String deviceManagerUrl = conf.getString(Definition.DEVICE_MANAGER_URL);
		DeviceManagerDBClient deviceManagerDBClient = new DeviceManagerDBClientImpl(deviceManagerUrl);
		String networkConfigSetupperUrl = conf.getString(Definition.NETWORK_CONFIG_SETUPPER_URL);
		NetworkConfigSetupperClient networkConfigSetupperClien = new NetworkConfigSetupperClientImpl(networkConfigSetupperUrl);
		DeviceManagerBusiness deviceManagerBusiness = new DeviceManagerBusinessImpl();

		try {
			if (deviceNames.contains(Definition.D_PLANE_SW_HOST_NAME)) {
				String res = deviceManagerBusiness.getConnectedPortInfo(Definition.OFP_SW_HOST_NAME);
				ConnectedPortGetJsonOut connectedPortGetJsonOut = ConnectedPortGetJsonOut.fromJson(res);
				if (connectedPortGetJsonOut.getStatus() != Definition.STATUS_SUCCESS) {
					return connectedPortGetJsonOut.getStatus();
				}

				String dPlaneSwPortName = new String();
				String deviceName = new String();
				for (Integer portNumber : portNames) {
					GraphDevicePort graphDevicePort = GraphDBUtil.searchNeighborPort(Definition.OFP_SW_HOST_NAME, portNumber, connectedPortGetJsonOut.getResult());
					if (graphDevicePort.getDeviceName().equals(Definition.D_PLANE_SW_HOST_NAME)) {
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
				TokenIdOut adminToken = openAmClient.authenticate(Definition.OPEN_AM_ADMIN_USER_ID , Definition.OPEN_AM_ADMIN_USER_PW);
				OpenAmIdentitiesOut openAmIdentitiesOut = openAmClient.readIdentities(adminToken.getTokenId(), used.getUserName());
				String dVlan = openAmIdentitiesOut.getdVlan().get(0);

				// Send parameters(auth id,deviceName, vlan id) to NCS.
				NetworkConfigSetupperIn networkConfigSetupperIn = new NetworkConfigSetupperIn();
				networkConfigSetupperIn.setTokenId(tokenId);
				List<ool.com.ofpm.json.ncs.NetworkConfigSetupperInData> params = networkConfigSetupperIn.getParams();
				List<String> portNamesData = new ArrayList<String>();
				portNamesData.add(dPlaneSwPortName);
				NetworkConfigSetupperInData param = new NetworkConfigSetupperInData(Definition.D_PLANE_SW_HOST_NAME, dVlan, portNamesData);
				params.add(param);
				BaseResponse resNcs = networkConfigSetupperClien.sendPlaneSwConfigData(networkConfigSetupperIn);
				if (resNcs.getStatus() != Definition.STATUS_SUCCESS) {
					return resNcs.getStatus();
				}
			}
		} catch(OpenAmClientException oace) {
			logger.error(oace);
			return Definition.STATUS_INTERNAL_ERROR;
		} catch(DeviceManagerDBClientException dmdce) {
			logger.error(dmdce);
			return Definition.STATUS_INTERNAL_ERROR;
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
}
