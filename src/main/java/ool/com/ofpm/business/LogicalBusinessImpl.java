package ool.com.ofpm.business;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ool.com.ofpm.client.AgentClient;
import ool.com.ofpm.client.AgentClientException;
import ool.com.ofpm.client.GraphDBClient;
import ool.com.ofpm.client.GraphDBClientException;
import ool.com.ofpm.client.OrientDBClientImpl;
import ool.com.ofpm.json.AgentUpdateFlowRequest;
import ool.com.ofpm.json.AgentUpdateFlowRequest.AgentUpdateFlowData;
import ool.com.ofpm.json.BaseNode;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.LogicalTopology;
import ool.com.ofpm.json.LogicalTopology.LogicalLink;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;
import ool.com.ofpm.json.PatchLinkJsonIn;
import ool.com.ofpm.json.PatchLinkJsonIn.PatchLink;
import ool.com.ofpm.utils.Definition;
import ool.com.ofpm.validate.CommonValidate;
import ool.com.ofpm.validate.LogicalTopologyValidate;
import ool.com.ofpm.validate.ValidateException;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class LogicalBusinessImpl implements LogicalBusiness {
	private static final Logger logger = Logger.getLogger(LogicalBusinessImpl.class);

	private AgentManager acm;
	private AgentUpdateFlowRequest agentFlowJson = new AgentUpdateFlowRequest();
	private final GraphDBClient graphDBClient = OrientDBClientImpl.getInstance();
	private Gson gson = new Gson();

	private void filterTopology(List<BaseNode> nodes, LogicalTopology topology) {
		String fname = "filterTopology";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(nodes=%s, topology=%s) - start", fname, nodes, topology));

//		List<BaseNode> topoNodes = topology.getNodes();
		List<LogicalLink> topoLinks = topology.getLinks();

//		List<BaseNode> removalNodes = new ArrayList<BaseNode>();
//		for(BaseNode topoNode : topoNodes) {
//			if(!nodes.contains(topoNode)) {
//				removalNodes.add(topoNode);
//			}
//		}
//		topoNodes.removeAll(removalNodes);

		List<String> deviceNames = new ArrayList<String>();
		for(BaseNode node : nodes) {
			deviceNames.add(node.getDeviceName());
		}
		List<LogicalLink> removalLinks = new ArrayList<LogicalLink>();
		for(LogicalLink topoLink : topoLinks) {
			if(! deviceNames.containsAll(topoLink.getDeviceName())) {
				removalLinks.add(topoLink);
			}
		}
		topoLinks.removeAll(removalLinks);

		if(logger.isDebugEnabled()) logger.debug(String.format("%s() - end", fname));
	}

	public String getLogicalTopology(String deviceNamesCSV) {
		String fname = "getLogicalTopology";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(deviceNames=%s) - start", fname, deviceNamesCSV));

		LogicalTopologyJsonInOut res = new LogicalTopologyJsonInOut();
		try {
			CommonValidate validator = new CommonValidate();
			validator.checkDeviceNamesCSV(deviceNamesCSV);
			String[] deviceNames = deviceNamesCSV.split(",");
			validator.checkDeviceNameArray(deviceNames);

			List<BaseNode> nodes = new ArrayList<BaseNode>();
			for(String deviceName : deviceNames) {
				BaseNode node = new BaseNode();
				node.setDeviceName(deviceName);
				nodes.add(node);
			}

			if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.getLogicalTopology(nodes=%s) - called", nodes));
			res = graphDBClient.getLogicalTopology(nodes);
			this.filterTopology(nodes, res.getResult());
			if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.getLogicalTopology(ret=%s) - returned", res));

		} catch (ValidateException ve) {
			logger.error(ve.getMessage());
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());

		} catch (GraphDBClientException gdbe) {
			logger.error(gdbe.getMessage());
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(gdbe.getMessage());

		} catch (Exception e) {
			logger.error(e.getMessage());
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage("Eroor :-( ");
		}

		Type type = new TypeToken<LogicalTopologyJsonInOut>(){}.getType();
		String resBody = this.gson.toJson(res, type);

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, resBody));
		return resBody;
	}

	public String updateLogicalTopology(String requestedTopologyJson) {
		String fname = "updateLogicalTopology";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(requestedTopology=%s) - start", fname, requestedTopologyJson));

		LogicalTopologyValidate validator = new LogicalTopologyValidate();
		acm = AgentManager.getInstance();
		BaseResponse res = new BaseResponse();
		res.setMessage("");

		try {
			// Type type = new TypeToken<LogicalTopology>(){}.getType();
			// LogicalTopology requestedTopology = this.gson.fromJson(requestedTopologyJson, type);
			CommonValidate varry = new CommonValidate();
			varry.checkDeviceNamesCSV(requestedTopologyJson);
			LogicalTopology requestedTopology = LogicalTopology.fromJson(requestedTopologyJson);
			validator.checkValidationRequestIn(requestedTopology);

			List<BaseNode> requestedNodes = requestedTopology.getNodes();

			if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.getLogicalTopology(nodes=%s) - called", requestedNodes));
			LogicalTopologyJsonInOut responseGraphDB = graphDBClient.getLogicalTopology(requestedNodes);
			if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.getLogicalTopology(ret=%s) - returned", responseGraphDB));

			LogicalTopology currentTopology = responseGraphDB.getResult();
			this.filterTopology(requestedNodes, currentTopology);

			LogicalTopology incTopology = requestedTopology.sub(currentTopology);
			LogicalTopology decTopology = currentTopology.sub(requestedTopology);

			List<PatchLink> reducedLinks   = new ArrayList<PatchLink>();
			List<PatchLink> augmentedLinks = new ArrayList<PatchLink>();
			for(LogicalLink link : decTopology.getLinks()) {
				if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.delLogicalTopology(link=%s) - called", link));
				PatchLinkJsonIn reducedPatches = graphDBClient.delLogicalLink(link);
				if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.delLogicalTopology(ret=%s) - returned", reducedPatches));

				if(reducedPatches.getStatus() != Definition.STATUS_SUCCESS) {
					res.setStatus( reducedPatches.getStatus());
					res.setMessage(reducedPatches.getMessage());
					break;
				}
				reducedLinks.addAll(reducedPatches.getResult());
			}
			for(LogicalLink link : incTopology.getLinks()) {
				if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.addLogicalTopology(nodes=%s) - called", link));
				PatchLinkJsonIn augmentedPatches = graphDBClient.addLogicalLink(link);
				if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.addLogicalTopology(req=%s) - returned", augmentedPatches));

				if(augmentedPatches.getStatus() != Definition.STATUS_CREATED) {
					res.setStatus( augmentedPatches.getStatus());
					res.setMessage(augmentedPatches.getMessage());
					return res.toJson();
				}
				augmentedLinks.addAll(augmentedPatches.getResult());
			}

			Map<AgentClient, List<AgentUpdateFlowData>> agentUpdateFlowDataList = this.makeAgentUpdateFlowList(reducedLinks,   "delete");
			Map<AgentClient, List<AgentUpdateFlowData>> bufAgentUpdateReqList   = this.makeAgentUpdateFlowList(augmentedLinks, "create");
			agentUpdateFlowDataList.putAll(bufAgentUpdateReqList);

			for(AgentClient agentClient : agentUpdateFlowDataList.keySet()) {
				agentFlowJson.setList(agentUpdateFlowDataList.get(agentClient));

				if(logger.isInfoEnabled()) logger.info(String.format("agentClient.updateFlows(flows=%s) - called", agentFlowJson));
				BaseResponse resAgent = agentClient.updateFlows(agentFlowJson);
				if(logger.isInfoEnabled()) logger.info(String.format("agentClinet.updateFlows(ret=%s) - returned", agentFlowJson));

				res = resAgent;
				if(resAgent.getStatus() != Definition.STATUS_SUCCESS) {
					// TODO: do cancel commit to graphdb
					break;
				}
			}

			return res.toJson();

		} catch (JsonSyntaxException jse) {
			logger.error(jse.getMessage());
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage("Invalid json structure.");
			return res.toJson();

		} catch (ValidateException ve) {
			logger.error(ve.getMessage());
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
			return res.toJson();

		} catch (GraphDBClientException gdbe) {
			logger.error(gdbe.getMessage());
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(gdbe.getMessage());
			return res.toJson();

		} catch (AgentClientException ace) {
			logger.error(ace.getMessage());
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(ace.getMessage());
			return res.toJson();

		} catch (Exception e) {
			logger.error(e.getMessage());
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
			return res.toJson();

		} finally {
			if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));

		}
		// must be not writing code from this point foward
	}

	private Map<AgentClient, List<AgentUpdateFlowData>> makeAgentUpdateFlowList(List<PatchLink> updatedLinks, String type) {
		String fname = "makeAgentUpdateFlowList";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(updatedLinks=%s, type=%s) - start", fname, updatedLinks, type));

		Map<AgentClient, List<AgentUpdateFlowData>> pairAgentClient_UpdateFlowDataList = new HashMap<AgentClient, List<AgentUpdateFlowData>>();
		for(PatchLink link : updatedLinks) {
			String switchIp = acm.getSwitchIp(link.getDeviceName());
			String ofcUrl   = acm.getOfcIp(switchIp);

			AgentUpdateFlowData newUpdateFlowData = agentFlowJson.new AgentUpdateFlowData();
			newUpdateFlowData.setIp(switchIp);
			newUpdateFlowData.setType(type);
			newUpdateFlowData.setPort(link.getPortName());
			newUpdateFlowData.setOfcUrl(ofcUrl);

			AgentClient agentClient = acm.getAgentClient(switchIp);
			if(!pairAgentClient_UpdateFlowDataList.containsKey(agentClient)) {
				pairAgentClient_UpdateFlowDataList.put(agentClient, new ArrayList<AgentUpdateFlowData>());
			}
			List<AgentUpdateFlowData> agentClientFlowDataList = pairAgentClient_UpdateFlowDataList.get(agentClient);
			agentClientFlowDataList.add(newUpdateFlowData);
		}

		if(logger.isDebugEnabled()) logger.debug(String.format("%s() - end", fname));
		return pairAgentClient_UpdateFlowDataList;
	}
}
