package ool.com.ofpm.client;

import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import ool.com.ofpm.json.AgentFlowJsonOut;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.BaseResultIn;
import ool.com.ofpm.utils.Definition;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

public class AgentClientImpl implements AgentClient {
	private WebResource resource;
	private Gson gson = new Gson();
	private String agentIp;

	private static final Logger logger = Logger.getLogger(AgentClientImpl.class);

	public AgentClientImpl(String ip) {
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("AgentClientImpl(%s) - start", ip));
		}
		this.agentIp = ip;
		this.resource = Client.create().resource("http://" + ip + Definition.AGENT_PATH);
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("AgentClientImpl() - end"));
		}
	}

	// 現在使用されません
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
		final String func = "updateFlows";
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("%s(%s) - start", func, flows));
		}
		BaseResponse res = new BaseResponse();

		Type type;
		String reqData = "";
		ClientResponse resAgent;
		try {
			type = new TypeToken<AgentFlowJsonOut>(){}.getType();
			reqData = gson.toJson(flows, type);

			Builder resBuilder = this.resource.entity(reqData);
			resBuilder = resBuilder.accept(MediaType.APPLICATION_JSON);
			resBuilder = resBuilder.type(MediaType.APPLICATION_JSON);
			resAgent = resBuilder.put(ClientResponse.class);

			type = new TypeToken<BaseResponse>(){}.getType();
			res = gson.fromJson(resAgent.getEntity(String.class), type);
		} catch (UniformInterfaceException uie) {
			ClientResponse cr = uie.getResponse();
			logger.error(uie.getMessage());
			// TODO: Agentとの通信エラーは上に通知する
			throw new AgentClientException("Connection faild bitween AgentClient:" + this.agentIp);

		} catch (Exception e) {
			// Logはき 上には通知しない。
			logger.error(e.getMessage());
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage("Sorry. I have BUG.");
		}
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("%s (ret=%s) - end", func, res));
		}
		return res;
	}
	public String getIp() {
		return this.agentIp;
	}
}
