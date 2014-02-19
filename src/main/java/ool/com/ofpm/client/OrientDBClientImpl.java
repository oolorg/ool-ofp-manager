package ool.com.ofpm.client;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.MediaType;

import ool.com.ofpm.json.BaseNode;
import ool.com.ofpm.json.LogicalTopology.LogicalLink;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;
import ool.com.ofpm.json.PatchLinkJsonIn;
import ool.com.ofpm.utils.Config;
import ool.com.ofpm.utils.ConfigImpl;
import ool.com.ofpm.utils.Definition;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;


public class OrientDBClientImpl implements GraphDBClient {
	private static OrientDBClientImpl instance;
	private final Client gdb_client;
	private final Gson gson;
	Config conf = new ConfigImpl();

	public static synchronized OrientDBClientImpl getInstance() {
		if(instance == null) {
			instance = new OrientDBClientImpl();
		}
		return instance;
	}
	OrientDBClientImpl() {
		this.gdb_client = Client.create();
		this.gson = new Gson();
	}

	public LogicalTopologyJsonInOut getLogicalTopology(List<BaseNode> nodes) throws GraphDBClientException {
		LogicalTopologyJsonInOut res;
		Iterator<BaseNode> ni = nodes.iterator();
		String deviceNames = StringUtils.join(ni, ",");

		try {
			ClientResponse gdbResponse;
			Builder resBuilder;
			String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_LINK_GET_PATH;
			WebResource resource = this.gdb_client.resource(url);
			resource    = resource.queryParam("deviceNames", deviceNames);
			resBuilder  = resource.accept(MediaType.APPLICATION_JSON);
			resBuilder  = resBuilder.type(MediaType.APPLICATION_JSON);
			gdbResponse = resBuilder.get(ClientResponse.class);

			String resBody = gdbResponse.getEntity(String.class);
			Type type = new TypeToken<LogicalTopologyJsonInOut>(){}.getType();
			res = gson.fromJson(resBody, type);
		} catch (UniformInterfaceException uie) {
			//HTTPレスポンスが300以上の場合つうちされます.
			throw new GraphDBClientException("Connection faild with OrientDB", Definition.STATUS_INTERNAL_ERROR);
		}
		return res;
	}
	public PatchLinkJsonIn addLogicalLink(LogicalLink link) throws GraphDBClientException {
		Type type = new TypeToken<LogicalLink>(){}.getType();
		String reqBody = gson.toJson(link, type);

		ClientResponse gdbResponse;
		Builder resBuilder;
		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_LINK_CREATE_PATH;
		WebResource resource = this.gdb_client.resource(url);
		resBuilder  = resource.entity(reqBody);
		resBuilder  = resBuilder.accept(MediaType.APPLICATION_JSON);
		resBuilder  = resBuilder.type(MediaType.APPLICATION_JSON);
		gdbResponse = resBuilder.post(ClientResponse.class);
		if(Definition.STATUS_SUCCESS != gdbResponse.getStatus()) {
			throw new GraphDBClientException("Connection faild with OrientDB", gdbResponse.getStatus());
		}

		String resBody = gdbResponse.getEntity(String.class);
		type = new TypeToken<PatchLinkJsonIn>(){}.getType();
		PatchLinkJsonIn res = gson.fromJson(resBody, type);
		return res;
	}
	public PatchLinkJsonIn delLogicalLink(LogicalLink link) throws GraphDBClientException {
		Type type = new TypeToken<LogicalLink>(){}.getType();
		String reqBody = gson.toJson(link, type);

		ClientResponse gdbResponse;
		Builder resBuilder;
		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_LINK_DELETE_PATH;
		WebResource resource = this.gdb_client.resource(url);
		resBuilder  = resource.entity(reqBody);
		resBuilder  = resBuilder.accept(MediaType.APPLICATION_JSON);
		resBuilder  = resBuilder.type(MediaType.APPLICATION_JSON);
		gdbResponse = resBuilder.post(ClientResponse.class);
		if(Definition.STATUS_SUCCESS != gdbResponse.getStatus()) {
			throw new GraphDBClientException("Connection faild with OrientDB", gdbResponse.getStatus());
		}

		String resBody = gdbResponse.getEntity(String.class);
		type = new TypeToken<PatchLinkJsonIn>(){}.getType();
		PatchLinkJsonIn res = gson.fromJson(resBody, type);
		return res;
	}
}
