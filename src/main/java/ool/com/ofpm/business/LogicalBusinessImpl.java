package ool.com.ofpm.business;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ool.com.ofpm.client.AgentClient;
import ool.com.ofpm.client.AgentClientException;
import ool.com.ofpm.client.AgentClientManager;
import ool.com.ofpm.client.AgentClientManagerImpl;
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
import ool.com.ofpm.validate.LogicalTopologyJsonInOutValidate;
import ool.com.ofpm.validate.LogicalTopologyValidate;
import ool.com.ofpm.validate.ValidateException;

public class LogicalBusinessImpl implements LogicalBusiness {

	LogicalTopologyValidate validator = new LogicalTopologyValidate();

	private void filterTopology(Set<BaseNode> nodes, LogicalTopology topology) {
		Set<BaseNode> topoNodes = topology.getNodes();
		Set<LogicalLink> topoLinks = topology.getLinks();

		Set<BaseNode> removalNodes = new HashSet<BaseNode>();
		for(BaseNode topoNode : topoNodes) {
			if(!nodes.contains(topoNode)) {
				removalNodes.add(topoNode);
			}
		}
		topoNodes.removeAll(removalNodes);

		Set<String> deviceNames = new HashSet<String>();
		for(BaseNode node : nodes) {
			deviceNames.add(node.getDeviceName());
		}
		Set<LogicalLink> removalLinks = new HashSet<LogicalLink>();
		for(LogicalLink topoLink : topoLinks) {
			if(!deviceNames.containsAll(topoLink.getDeviceName())) {
				removalLinks.add(topoLink);
			}
		}
		topoLinks.removeAll(removalLinks);
	}

	public LogicalTopologyJsonInOut doGET(String[] params) {
		LogicalTopologyJsonInOut res = new LogicalTopologyJsonInOut();
		try {
			// validator.checkValidationGET(params);
			if(params == null) throw new ValidateException("deviceNameにnullは許容されません");
			for(String deviceName : params) {
				if(deviceName == null) throw new ValidateException("deviceNameにnullは許可されません");
				if(deviceName.trim() == "") throw new ValidateException("deviceNameに空文字が指定されました");
			}

			Set<BaseNode> nodes = new HashSet<BaseNode>();
			for(String param : params) {
				BaseNode node = new BaseNode();
				node.setDeviceName(param);
				nodes.add(node);
			}

			GraphDBClient gdbClient = OrientDBClientImpl.getInstance();
			LogicalTopologyJsonInOut resGdb = gdbClient.getLogicalTopology(nodes);

			LogicalTopologyJsonInOutValidate ltjValidator = new LogicalTopologyJsonInOutValidate();
			ltjValidator.checkValidation(resGdb);

			LogicalTopology topology = resGdb.getResult();
			this.filterTopology(nodes, topology);

			res.setResult(topology);
			res.setStatus(resGdb.getStatus());
			res.setMessage(resGdb.getMessage());

		} catch (ValidateException ve) {
			res.setStatus(400);
			res.setMessage(ve.getMessage());

		} catch (GraphDBClientException gdbe) {
			res.setStatus(gdbe.getStatus());
			res.setMessage(gdbe.getMessage());
		}
		return res;
	}

	public BaseResponse doPUT(LogicalTopology params) {
		BaseResponse res = new BaseResponse();
		AgentClientManager acm = AgentClientManagerImpl.getInstance();
		try {
			validator.checkValidation(params);
			Set<BaseNode> nodes = params.getNodes();
			this.filterTopology(nodes, params);

			GraphDBClient gdbClient = OrientDBClientImpl.getInstance();
			LogicalTopologyJsonInOut resGdb = gdbClient.getLogicalTopology(nodes);
			LogicalTopology topology = resGdb.getResult();
			this.filterTopology(nodes, topology);

			// inTopo と outTopo の差分を作成し、加算リスト、減算リスト
			LogicalTopology addTopo = params.sub(topology);
			LogicalTopology delTopo = topology.sub(params);

			Set<PatchLink> deletedLinks = new HashSet<PatchLink>();
			for(LogicalLink link : delTopo.getLinks()) {
				PatchLinkJsonIn deletedPatches = gdbClient.delLogicalLink(link);
				// エラー処理実装してOKなら
				deletedLinks.addAll(deletedPatches.getResult());
			}
			Set<PatchLink> addedLinks = new HashSet<PatchLink>();
			for(LogicalLink link : addTopo.getLinks()) {
				PatchLinkJsonIn addedPatches = gdbClient.addLogicalLink(link);
				// エラー処理実装してOKなら
				addedLinks.addAll(addedPatches.getResult());
			}

			// AgentClient使って放り込む
			AgentFlowJsonOut agentFlowJson = new AgentFlowJsonOut();
			Map<AgentClient, Set<AgentFlow>> agentFlows = new HashMap<AgentClient, Set<AgentFlow>>();
			for(PatchLink link : addedLinks) {
				String switchIp = acm.getSwitchIp(link.getDeviceName());
				AgentClient agentClient = acm.getAgentClient(switchIp);
				AgentFlow newFlow = agentFlowJson.new AgentFlow();
				newFlow.setIp(switchIp);
				newFlow.setType("create");
				newFlow.setOfcUrl("");
				newFlow.setPort(link.getPortName());
				if(!agentFlows.containsKey(agentClient)) {
					agentFlows.put(agentClient, new HashSet<AgentFlow>());
				}
				Set<AgentFlow> flows = agentFlows.get(agentClient);
				flows.add(newFlow);
			}
			for(PatchLink link : deletedLinks) {
				String switchIp = acm.getSwitchIp(link.getDeviceName());
				AgentClient agentClient = acm.getAgentClient(switchIp);
				AgentFlow newFlow = agentFlowJson.new AgentFlow();
				newFlow.setIp(switchIp);
				newFlow.setType("create");
				newFlow.setOfcUrl("");
				newFlow.setPort(link.getPortName());
				if(!agentFlows.containsKey(agentClient)) {
					agentFlows.put(agentClient, new HashSet<AgentFlow>());
				}
				Set<AgentFlow> flows = agentFlows.get(agentClient);
				flows.add(newFlow);
			}

			for(AgentClient agentClient : agentFlows.keySet()) {
				agentFlowJson.setList(agentFlows.get(agentClient));
				BaseResponse resAgent = agentClient.updateFlows(agentFlowJson);
				if(resAgent.getStatus() != 201) {
					return resAgent;
				}
			}

			res.setStatus(201);
		} catch (ValidateException ve) {
			res.setStatus(400);
			res.setMessage(ve.getMessage());
		} catch (AgentClientException ae) {
			// TODO Auto-generated catch block
			ae.getMessage();
		} catch (GraphDBClientException gdbe) {
			// TODO Auto-generated catch block
			gdbe.getMessage();
		}
		return res;
	}
}
