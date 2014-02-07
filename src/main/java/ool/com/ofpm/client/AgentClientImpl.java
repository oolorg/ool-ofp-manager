package ool.com.ofpm.client;

import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;

import ool.com.ofpm.json.BaseResultIn;
import ool.com.ofpm.utils.Definition;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

public final class AgentClientImpl implements AgentClient {
	private static AgentClientImpl instance;
	private static WebResource resource;
	private static Gson gson;

	private AgentClientImpl() {}
	public static synchronized AgentClientImpl getInstance(String url) {
		if(instance == null) {
			instance = new AgentClientImpl();
			resource = Client.create().resource(url);
			gson = new Gson();
		}
		return instance;
	}
	public BaseResultIn getTopology() throws Exception {
		ClientResponse response;
		Builder res_builder;
		res_builder = resource.accept(MediaType.APPLICATION_JSON);
		res_builder = res_builder.type(MediaType.APPLICATION_JSON);
		response    = res_builder.get(ClientResponse.class);
		if(response.getStatus() != Definition.CONNECTION_SUCCESS) {
			// TODO 例外を決め、正確に受け渡す。
			throw new Exception("Errorが発生しました");
		}
		Type collectionType = new TypeToken<BaseResultIn>(){}.getType();
		String res_str = response.getEntity(String.class);
		return gson.fromJson(res_str, collectionType);
	}
	public BaseResultIn addFlows() {
		return null;
	}
	public BaseResultIn delFlows() {
		return null;
	}
}
