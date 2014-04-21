package ool.com.ofpm.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ool.com.odbcl.client.GraphDBClient;
import ool.com.odbcl.client.OrientDBClientImpl;
import ool.com.odbcl.exception.GraphDBClientException;
import ool.com.odbcl.json.BaseResponse;
import ool.com.odbcl.json.GraphDBPatchLinkJsonRes;
import ool.com.odbcl.json.GraphDBPatchLinkJsonRes.PatchLink;
import ool.com.odbcl.json.LogicalTopology;
import ool.com.odbcl.json.LogicalTopology.LogicalLink;
import ool.com.odbcl.json.LogicalTopologyGetJsonOut;
import ool.com.odbcl.json.Node;
import ool.com.ofpm.client.AgentClient;
import ool.com.ofpm.exception.AgentClientException;
import ool.com.ofpm.exception.AgentManagerException;
import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.AgentClientUpdateFlowReq;
import ool.com.ofpm.json.AgentClientUpdateFlowReq.AgentUpdateFlowData;
import ool.com.ofpm.utils.Config;
import ool.com.ofpm.utils.ConfigImpl;
import ool.com.ofpm.utils.Definition;
import ool.com.ofpm.utils.ErrorMessage;
import ool.com.ofpm.validate.CommonValidate;
import ool.com.ofpm.validate.LogicalTopologyValidate;
import ool.com.openam.client.OpenAmClient;
import ool.com.openam.client.OpenAmClientException;
import ool.com.openam.client.OpenAmClientImpl;
import ool.com.openam.json.TokenValidChkOut;

import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;

public class LogicalBusinessImpl implements LogicalBusiness {
	private static final Logger logger = Logger.getLogger(LogicalBusinessImpl.class);

	private AgentManager agentManager;
	private AgentClientUpdateFlowReq agentFlowJson = new AgentClientUpdateFlowReq();

	Config conf = new ConfigImpl();

	public LogicalBusinessImpl() {
		if (logger.isDebugEnabled()) {
			logger.debug("LogicalBusinessImpl");
		}
	}

	private void filterTopology(List<Node> nodes, LogicalTopology topology) {
		String fname = "filterTopology";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(nodes=%s, topology=%s) - start", fname, nodes, topology));
		}

		List<LogicalLink> topoLinks = topology.getLinks();
		List<String> deviceNames = new ArrayList<String>();
		for (Node node : nodes) {
			deviceNames.add(node.getDeviceName());
		}
		List<LogicalLink> removalLinks = new ArrayList<LogicalLink>();
		for (LogicalLink topoLink : topoLinks) {
			if (!deviceNames.containsAll(topoLink.getDeviceName())) {
				removalLinks.add(topoLink);
			}
		}
		topoLinks.removeAll(removalLinks);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

	public String getLogicalTopology(String deviceNamesCSV, String tokenId) {
		String fname = "getLogicalTopology";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceNames=%s, tokenId%s) - start", fname, deviceNamesCSV, tokenId));
		}

		LogicalTopologyGetJsonOut res = new LogicalTopologyGetJsonOut();
		String odbsUrl = conf.getString(Definition.GRAPH_DB_URL);
		GraphDBClient graphDBClient = new OrientDBClientImpl(odbsUrl);
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
			res = graphDBClient.getLogicalTopology(nodes);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.getLogicalTopology(ret=%s) - returned", res));
			}
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

	public String updateLogicalTopology(String requestedTopologyJson) {
		String fname = "updateLogicalTopology";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestedTopology=%s) - start", fname, requestedTopologyJson));
		}

		BaseResponse res = new BaseResponse();
		res.setStatus(Definition.STATUS_SUCCESS);
		String odbsUrl = conf.getString(Definition.GRAPH_DB_URL);
		GraphDBClient graphDBClient = new OrientDBClientImpl(odbsUrl);
		String openamUrl = conf.getString(Definition.OPEN_AM_URL);
		OpenAmClient openAmClient = new OpenAmClientImpl(openamUrl);
		try {
			LogicalTopology requestedTopology = LogicalTopology.fromJson(requestedTopologyJson);

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


			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.getLogicalTopology(nodes=%s) - called", requestedNodes));
			}
			LogicalTopologyGetJsonOut responseGraphDB = graphDBClient.getLogicalTopology(requestedNodes);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.getLogicalTopology(ret=%s) - returned", responseGraphDB));
			}
			if (responseGraphDB.getStatus() != Definition.STATUS_SUCCESS) {
				res.setStatus(responseGraphDB.getStatus());
				res.setMessage(responseGraphDB.getMessage());
				return res.toJson();
			}

			LogicalTopology currentTopology = responseGraphDB.getResult();
			this.filterTopology(requestedNodes, currentTopology);

			LogicalTopology incTopology = requestedTopology.sub(currentTopology);
			LogicalTopology decTopology = currentTopology.sub(requestedTopology);

			List<PatchLink> reducedLinks = new ArrayList<PatchLink>();
			List<PatchLink> augmentedLinks = new ArrayList<PatchLink>();
			for (LogicalLink link : decTopology.getLinks()) {
				if (logger.isInfoEnabled()) {
					logger.info(String.format("graphDBClient.delLogicalTopology(link=%s) - called", link));
				}
				GraphDBPatchLinkJsonRes reducedPatches = graphDBClient.delLogicalLink(link);
				if (logger.isInfoEnabled()) {
					logger.info(String.format("graphDBClient.delLogicalTopology(ret=%s) - returned", reducedPatches.toJson()));
				}

				if (reducedPatches.getStatus() != Definition.STATUS_SUCCESS) {
					res.setStatus(reducedPatches.getStatus());
					res.setMessage(reducedPatches.getMessage());
					return res.toJson();
				}
				reducedLinks.addAll(reducedPatches.getResult());
			}
			for (LogicalLink link : incTopology.getLinks()) {
				if (logger.isInfoEnabled()) {
					logger.info(String.format("graphDBClient.addLogicalTopology(nodes=%s) - called", link));
				}
				GraphDBPatchLinkJsonRes augmentedPatches = graphDBClient.addLogicalLink(link);
				if (logger.isInfoEnabled()) {
					logger.info(String.format("graphDBClient.addLogicalTopology(req=%s) - returned", augmentedPatches.toJson()));
				}

				if (augmentedPatches.getStatus() != Definition.STATUS_CREATED) {
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
				ool.com.ofpm.json.BaseResponse resAgent = agentClient.updateFlows(agentFlowJson);
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

		} catch (GraphDBClientException gdbe) {
			logger.error(gdbe);
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(gdbe.getMessage());
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
}
