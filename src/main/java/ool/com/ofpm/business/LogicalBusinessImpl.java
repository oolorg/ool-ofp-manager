package ool.com.ofpm.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ool.com.ofpm.client.AgentClient;
import ool.com.ofpm.client.AgentClientException;
import ool.com.ofpm.client.GraphDBClient;
import ool.com.ofpm.client.GraphDBClientException;
import ool.com.ofpm.client.OrientDBClientImpl;
import ool.com.ofpm.json.AgentFlowJsonOut;
import ool.com.ofpm.json.AgentFlowJsonOut.AgentFlow;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class LogicalBusinessImpl implements LogicalBusiness {
	private static final Logger logger = Logger.getLogger(LogicalBusinessImpl.class);

	private AgentManager acm;
	private AgentFlowJsonOut agentFlowJson = new AgentFlowJsonOut();

	private void filterTopology(List<BaseNode> nodes, LogicalTopology topology) {
		String fname = "filterTopology";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(nodes=%s, topology=%s) - start", fname, nodes, topology));

		List<BaseNode> topoNodes = topology.getNodes();
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
			if(!deviceNames.containsAll(topoLink.getDeviceName())) {
				removalLinks.add(topoLink);
			}
		}
		topoLinks.removeAll(removalLinks);

		if(logger.isDebugEnabled()) logger.debug(String.format("%s() - end", fname));
	}

	public LogicalTopologyJsonInOut getLogicalTopology(String[] deviceNames) {
		String fname = "getLogicalTopology";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(deviceNames=[\"%s\"]) - start", fname, StringUtils.join(deviceNames, "\",\"")));

		LogicalTopologyJsonInOut res = new LogicalTopologyJsonInOut();
		try {
			CommonValidate validator = new CommonValidate();
			validator.checkDeviceNameArray(deviceNames);

			List<BaseNode> nodes = new ArrayList<BaseNode>();
			for(String deviceName : deviceNames) {
				BaseNode node = new BaseNode();
				node.setDeviceName(deviceName);
				nodes.add(node);
			}

			GraphDBClient graphDBClient = OrientDBClientImpl.getInstance();

			if(logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.getLogicalTopology(nodes=%s) - called", nodes));
			}

			res = graphDBClient.getLogicalTopology(nodes);

			if(logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.getLogicalTopology(ret=%s) - returned", res));
			}

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

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

	public BaseResponse updateLogicalTopology(LogicalTopology requestedTopology) {
		String fname = "updateLogicalTopology";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(requestedTopology=%s) - start", fname, requestedTopology));

		LogicalTopologyValidate validator = new LogicalTopologyValidate();
		acm = AgentManager.getInstance();
		BaseResponse res = new BaseResponse();
		try {
			validator.checkValidationRequestIn(requestedTopology);

			List<BaseNode> requestedNodes = requestedTopology.getNodes();
			final GraphDBClient graphDBClient = OrientDBClientImpl.getInstance(); // これprivate 変数に変更

			if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.getLogicalTopology(nodes=%s) - called", requestedNodes));
			LogicalTopologyJsonInOut responseGraphDB = graphDBClient.getLogicalTopology(requestedNodes);
			if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.getLogicalTopology(ret=%s) - returned", responseGraphDB));

			LogicalTopology currentTopology = responseGraphDB.getResult();
			this.filterTopology(requestedNodes, currentTopology);

			// inTopo と outTopo の差分を作成し、加算リスト、減算リスト
			LogicalTopology incTopology = requestedTopology.sub(currentTopology);
			LogicalTopology decTopology = currentTopology.sub(requestedTopology);

			List<PatchLink> reducedLinks = new ArrayList<PatchLink>();
			List<PatchLink> augmentedLinks = new ArrayList<PatchLink>();
//			this.updateLinks(decTopology, reducedLinks, new DelegateGraphDBUpdateLink() {
//				public PatchLinkJsonIn update(LogicalLink link) throws GraphDBClientException {
//					return graphDBClient.delLogicalLink(link);
//				}
//			});
//			this.updateLinks(incTopology, augmentedLinks, new DelegateGraphDBUpdateLink() {
//				public PatchLinkJsonIn update(LogicalLink link) throws GraphDBClientException {
//					return graphDBClient.addLogicalLink(link);
//				}
//			});
			for(LogicalLink link : decTopology.getLinks()) {
				if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.delLogicalTopology(link=%s) - called", link));
				PatchLinkJsonIn reducedPatches = graphDBClient.delLogicalLink(link);
				if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.delLogicalTopology(ret=%s) - returned", reducedPatches));

				if(reducedPatches.getStatus() != Definition.STATUS_SUCCESS) {
					res.setStatus( reducedPatches.getStatus());
					res.setMessage(reducedPatches.getMessage());
					return res;
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
					return res;
				}
				augmentedLinks.addAll(augmentedPatches.getResult());
			}

			// Agent へ通知するリンク更新情報を作製する
			Map<AgentClient, List<AgentFlow>> agentUpdateFlowReq = new HashMap<AgentClient, List<AgentFlow>>();
			this.registUpdateFlowRequest(agentUpdateFlowReq, reducedLinks, "delete");
			this.registUpdateFlowRequest(agentUpdateFlowReq, augmentedLinks, "create");

			for(AgentClient agentClient : agentUpdateFlowReq.keySet()) {
				agentFlowJson.setList(agentUpdateFlowReq.get(agentClient));

				if(logger.isInfoEnabled()) logger.info(String.format("agentClient.updateFlows(flows=%s) - called", agentFlowJson));
				BaseResponse resAgent = agentClient.updateFlows(agentFlowJson);
				if(logger.isInfoEnabled()) logger.info(String.format("agentClinet.updateFlows(ret=%s) - returned", agentFlowJson));

				if(resAgent.getStatus() != Definition.STATUS_SUCCESS) {
					// TODO: ここでトランザクション入れないとだめだよ
					return resAgent;
				}
			}

			res.setStatus(Definition.STATUS_SUCCESS);

		} catch (ValidateException ve) {
			logger.error(ve.getMessage());
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());

		} catch (AgentClientException ace) {
			logger.error(ace.getMessage());
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(ace.getMessage());

		} catch (GraphDBClientException gdbe) {
			logger.error(gdbe.getMessage());
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(gdbe.getMessage());

		}

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

	// 現在の実装では使用しません
	class DelegateGraphDBUpdateLink {
		public PatchLinkJsonIn update(LogicalLink link) throws GraphDBClientException {
			return null;
		}
	}

	// 現在の実装では使用しません
	private void updateLinks(LogicalTopology topology, List<PatchLink> updatedLinks, DelegateGraphDBUpdateLink ext) throws GraphDBClientException {
		for(LogicalLink link : topology.getLinks()) {
			PatchLinkJsonIn updatedPatches = ext.update(link);

			if(updatedPatches.getStatus() != Definition.STATUS_SUCCESS) {
				this.cancelUpdate(updatedPatches.getStatus(), updatedPatches.getMessage());
			}
			updatedLinks.addAll(updatedPatches.getResult());
		}
	}

	// 現在の実装では使用しません
	private BaseResponse cancelUpdate(int status, String message) {
		BaseResponse res = new BaseResponse();
		res.setStatus(status);
		res.setMessage(message);
		return res;
	}

	private void registUpdateFlowRequest(Map<AgentClient, List<AgentFlow>> agentFlows, List<PatchLink> updatedLinks, String type) {
		String fname = "registUpdateFlowRequest";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(agentFlows=%s, updatedLinks=%s, type=%s) - start", fname, agentFlows, updatedLinks, type));

		for(PatchLink link : updatedLinks) {
			String switchIp = acm.getSwitchIp(link.getDeviceName());
			String ofcUrl   = acm.getOfcIp(switchIp);

			AgentFlow newFlow = agentFlowJson.new AgentFlow();
			newFlow.setIp(switchIp);
			newFlow.setType(type);
			newFlow.setPort(link.getPortName());
			newFlow.setOfcUrl(ofcUrl);

			AgentClient agentClient = acm.getAgentClient(switchIp);
			if(!agentFlows.containsKey(agentClient)) {
				agentFlows.put(agentClient, new ArrayList<AgentFlow>());
			}
			List<AgentFlow> flows = agentFlows.get(agentClient);
			flows.add(newFlow);
		}

		if(logger.isDebugEnabled()) logger.debug(String.format("%s() - end", fname));
	}
}
