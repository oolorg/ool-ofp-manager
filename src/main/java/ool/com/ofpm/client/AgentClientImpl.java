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
		this.resource = Client.create().resource(ip);
	}
	public BaseResultIn getTopology() throws AgentClientException {
		ClientResponse response;
		Builder res_builder;
		res_builder = resource.accept(MediaType.APPLICATION_JSON);
		res_builder = res_builder.type(MediaType.APPLICATION_JSON);
		response    = res_builder.get(ClientResponse.class);
		if(response.getStatus() != Definition.CONNECTION_SUCCESS) {
			// TODO 例外を決め、正確に受け渡す。
			throw new AgentClientException("Errorが発生しました");
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
			throw new AgentClientException("入力されたフローオブジェクトは不正です");
		}

		try {
			Builder resBuilder = this.resource.entity(reqData);
			resBuilder = resBuilder.accept(MediaType.APPLICATION_JSON);
			resBuilder = resBuilder.type(MediaType.APPLICATION_JSON);
			resAgent = resBuilder.put(ClientResponse.class);
			if(resAgent.getStatus() != 201) throw new Exception();
		} catch (Exception e) {
			throw new AgentClientException("Agentとの接続でエラーが発生しました");
		}

		try {
			type = new TypeToken<BaseResponse>(){}.getType();
			res = gson.fromJson(resAgent.getEntity(String.class), type);
		} catch (Exception e) {
			throw new AgentClientException("Agent(" + this.agentIp + ")が規定外の戻り値を返しました。");
		}
		return res;
	}
	public String getIp() {
		return this.agentIp;
	}
}
