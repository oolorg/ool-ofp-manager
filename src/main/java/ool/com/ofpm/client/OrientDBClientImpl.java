package ool.com.ofpm.client;

import javax.ws.rs.core.MediaType;

import ool.com.ofpm.json.GraphDBResult;
import ool.com.ofpm.json.PhysicalRequestIn.NodeStatus;
import ool.com.ofpm.json.PhysicalRequestIn.NodeType;
import ool.com.ofpm.utils.Definition;

import com.google.gson.Gson;
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
	private final String uri;

	public static synchronized OrientDBClientImpl getInstance(String uri) {
		if(instance == null) {
			instance = new OrientDBClientImpl(uri);
		}
		return instance;
	}
	OrientDBClientImpl(String uri) {
		this.uri = uri;
		this.gdb_client = Client.create();
		this.gson = new Gson();
	}

	private GraphDBResult commonDBRequest(String deviceName, NodeType type, NodeStatus status, String portName, String uri) {
		ClientResponse gdb_response;
		Builder res_builder;
		WebResource resource = this.gdb_client.resource(uri);
		resource     = resource.queryParam("name", deviceName);
		resource     = resource.queryParam("type", type.name());
		resource     = resource.queryParam("status", status.name());
		resource     = resource.queryParam("port", portName);
		res_builder  = resource.accept(MediaType.APPLICATION_JSON);
		res_builder  = res_builder.type(MediaType.APPLICATION_JSON);
		gdb_response = res_builder.get(ClientResponse.class);
		if(Definition.CONNECTION_SUCCESS != gdb_response.getStatus()) {
			// TODO エラー処理をしましょう
			return null;
		}

		String        res_body_str_gdb = gdb_response.getEntity(String.class);
		GraphDBResult res_body_obj_gdb = gson.fromJson(res_body_str_gdb, GraphDBResult.TYPE);
		return res_body_obj_gdb;
	}

	public GraphDBResult appendDevice(String deviceName, NodeType type) {
		GraphDBResult res_body_gdb = this.commonDBRequest(deviceName, type, null, null, this.uri + Definition.GRAPH_DB_ADD_DEVICE);
		return res_body_gdb;
	}

	public GraphDBResult deleteDevice(String deviceName, NodeType type) {
		GraphDBResult res_body_gdb = this.commonDBRequest(deviceName, type, null, null, this.uri + Definition.GRAPH_DB_DEL_DEVICE);
		return res_body_gdb;
	}

	public GraphDBResult updateDevice(String deviceName, NodeType type, NodeStatus status) {
		GraphDBResult res_body_gdb = this.commonDBRequest(deviceName, type, status, null, this.uri + Definition.GRAPH_DB_UPDATE_DEVICE);
		return res_body_gdb;
	}
	public GraphDBResult appendDevice(String deviceName, NodeType type, String portName) {
		GraphDBResult res_body_gdb = this.commonDBRequest(deviceName, type, null, portName, this.uri + Definition.GRAPH_DB_ADD_DEVICE);
		return res_body_gdb;
	}

	public GraphDBResult deleteDevice(String deviceName, NodeType type, String portName) {
		GraphDBResult res_body_gdb = this.commonDBRequest(deviceName, type, null, portName, this.uri + Definition.GRAPH_DB_DEL_DEVICE);
		return res_body_gdb;
	}

	public GraphDBResult updateDevice(String deviceName, NodeType type, String portName, NodeStatus status) {
		GraphDBResult res_body_gdb = this.commonDBRequest(deviceName, type, status, portName, this.uri + Definition.GRAPH_DB_UPDATE_DEVICE);
		return res_body_gdb;
	}
}
