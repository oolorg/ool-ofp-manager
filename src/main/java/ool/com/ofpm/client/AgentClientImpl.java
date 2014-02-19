package ool.com.ofpm.client;

import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;

import ool.com.ofpm.json.AgentFlowJsonOut;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.BaseResultIn;
import ool.com.ofpm.utils.Definition;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

public class AgentClientImpl implements AgentClient {
	private WebResource resource;
	private Gson gson = new Gson();
	private String agentIp;

	public AgentClientImpl(String ip) {
		this.agentIp = ip;
		this.resource = Client.create().resource("http://" + ip + Definition.AGENT_PATH);
	}
	public BaseResultIn getTopology() throws AgentClientException {
		ClientResponse response;
		Builder res_builder;
		res_builder = resource.accept(MediaType.APPLICATION_JSON);
		res_builder = res_builder.type(MediaType.APPLICATION_JSON);
		response    = res_builder.get(ClientResponse.class);
		if(response.getStatus() != Definition.STATUS_SUCCESS) {
			throw new AgentClientException("Connection faild");
		}
		Type collectionType = new TypeToken<BaseResultIn>(){}.getType();
		String res_str = response.getEntity(String.class);
		return gson.fromJson(res_str, collectionType);
	}

	public BaseResponse updateFlows(AgentFlowJsonOut flows) throws AgentClientException {
		BaseResponse res = new BaseResponse();

		Type type;
		String reqData = "";
		ClientResponse resAgent;
		try {
			type = new TypeToken<AgentFlowJsonOut>(){}.getType();
			reqData = gson.toJson(flows, type);
		} catch (Exception e) {
			throw new AgentClientException("Invalid FlowObject");
		}

		try {
			Builder resBuilder = this.resource.entity(reqData);
			resBuilder = resBuilder.accept(MediaType.APPLICATION_JSON);
			resBuilder = resBuilder.type(MediaType.APPLICATION_JSON);
			resAgent = resBuilder.put(ClientResponse.class);
			if(resAgent.getStatus() != Definition.STATUS_SUCCESS) throw new Exception();
		} catch (Exception e) {
			throw new AgentClientException("Connection faild bitween AgentClient");
		}

		try {
			type = new TypeToken<BaseResponse>(){}.getType();
			res = gson.fromJson(resAgent.getEntity(String.class), type);
		} catch (Exception e) {
			throw new AgentClientException("Bad response from Agent(" + this.agentIp + ")");
		}
		return res;
	}
	public String getIp() {
		return this.agentIp;
	}
}
