package ool.com.ofpm.client;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import ool.com.ofpm.json.BaseNode;
import ool.com.ofpm.json.LogicalTopology.LogicalLink;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;
import ool.com.ofpm.json.PatchLinkJsonIn;
import ool.com.ofpm.utils.Definition;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;


public class OrientDBClientImpl implements GraphDBClient {

	public void exec() {
		// TODO Auto-generated method stub

	}

	private static OrientDBClientImpl instance;
	private final Client gdb_client;
	private final Gson gson;

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

	public LogicalTopologyJsonInOut getLogicalTopology(Set<BaseNode> nodes) throws GraphDBClientException {
		if(nodes == null) {
			throw new NullPointerException();
		}
		Iterator<BaseNode> ni = nodes.iterator();
		String deviceNames = ni.next().getDeviceName();
		while(ni.hasNext()) {
			BaseNode node = ni.next();
			deviceNames += "," + node.getDeviceName();
		}

		ClientResponse gdbResponse;
		Builder resBuilder;
		WebResource resource = this.gdb_client.resource(Definition.GRAPH_DB_ADDRESS + Definition.GRAPH_DB_LINK_GET);
		resource    = resource.queryParam("deviceNames", deviceNames);
		resBuilder  = resource.accept(MediaType.APPLICATION_JSON);
		resBuilder  = resBuilder.type(MediaType.APPLICATION_JSON);
		gdbResponse = resBuilder.get(ClientResponse.class);
		if(Definition.STATUS_SUCCESS != gdbResponse.getStatus()) {
			throw new GraphDBClientException("Connection faild with OrientDB", gdbResponse.getStatus());
		}

		String resBody = gdbResponse.getEntity(String.class);
		Type type = new TypeToken<LogicalTopologyJsonInOut>(){}.getType();
		LogicalTopologyJsonInOut res = gson.fromJson(resBody, type);
		return res;
	}
	public PatchLinkJsonIn addLogicalLink(LogicalLink link) throws GraphDBClientException {
		Type type = new TypeToken<LogicalLink>(){}.getType();
		String reqBody = gson.toJson(link, type);

		ClientResponse gdbResponse;
		Builder resBuilder;
		WebResource resource = this.gdb_client.resource(Definition.GRAPH_DB_ADDRESS + Definition.GRAPH_DB_LINK_CREATE_PATH);
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
//		Iterator<String> si = link.getDeviceName().iterator();
//		String deviceNames = si.next();
//		deviceNames += ',' + si.next();
		Type type = new TypeToken<LogicalLink>(){}.getType();
		String reqBody = gson.toJson(link, type);

		ClientResponse gdbResponse;
		Builder resBuilder;
		WebResource resource = this.gdb_client.resource(Definition.GRAPH_DB_ADDRESS + Definition.GRAPH_DB_LINK_DELETE_PATH);
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
