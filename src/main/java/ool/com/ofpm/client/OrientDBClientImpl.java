package ool.com.ofpm.client;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.MediaType;

import ool.com.ofpm.json.BaseNode;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.DeviceInfoJsonInOut;
import ool.com.ofpm.json.LogicalTopology.LogicalLink;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;
import ool.com.ofpm.json.PatchLinkJsonIn;
import ool.com.ofpm.json.PhysicalLinkJsonInOut;
import ool.com.ofpm.json.PortInfoJsonInOut;
import ool.com.ofpm.utils.Config;
import ool.com.ofpm.utils.ConfigImpl;
import ool.com.ofpm.utils.Definition;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;


public class OrientDBClientImpl implements GraphDBClient {
	private static final Logger logger = Logger.getLogger(OrientDBClientImpl.class);
	private static OrientDBClientImpl instance;
	private final Client gdb_client;
	private final Gson gson = new Gson();
	Config conf = new ConfigImpl();


	private OrientDBClientImpl() {
		this.gdb_client = Client.create();
	}

	public static OrientDBClientImpl getInstance() {
		if(logger.isDebugEnabled()) {
			logger.debug("getInstance() - start");
		}
		if(instance == null) {
			instance = new OrientDBClientImpl();
		}
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("getInstance(ret=%s) - end", instance));
		}
		return instance;
	}

	private String post(String url, String reqBody) throws GraphDBClientException {
		final String func = "post";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(url=%s, requestBody=%s) - start", func, url, reqBody));

		String resBody;
		try {

			ClientResponse gdbResponse;
			Builder resBuilder;
			WebResource resource = this.gdb_client.resource(url);
			resBuilder  = resource.entity(reqBody);
			resBuilder  = resBuilder.accept(MediaType.APPLICATION_JSON);
			resBuilder  = resBuilder.type(MediaType.APPLICATION_JSON);
			gdbResponse = resBuilder.post(ClientResponse.class);

			if(gdbResponse.getStatus() != Definition.STATUS_SUCCESS) {
				logger.error(gdbResponse.getEntity(String.class));
				throw new GraphDBClientException("OrientDBs response is wrong.");
			}

			resBody = gdbResponse.getEntity(String.class);

		} catch (UniformInterfaceException uie) {
			logger.error(uie.getMessage());
			throw new GraphDBClientException("OrientDBs response is wrong.");

		} catch (ClientHandlerException che) {
			logger.error(che.getMessage());
			throw new GraphDBClientException("Connection faild with OrientDB.");

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new GraphDBClientException(e.getMessage());
		}

		if(logger.isDebugEnabled()) {
			logger.debug(String.format("%s(res=%s) - end", func, resBody));
		}
		return resBody;
	}

	@Override
	public LogicalTopologyJsonInOut getLogicalTopology(List<BaseNode> nodes) throws GraphDBClientException {
		final String func = "getLogicalTopology";
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("%s(%s) - start", func, nodes));
		}

		LogicalTopologyJsonInOut res;
		StringBuilder deviceNames = new StringBuilder();
		Iterator<BaseNode> ni = nodes.iterator();
		deviceNames.append(ni.next().getDeviceName());
		while(ni.hasNext()) {
			deviceNames.append(",");
			deviceNames.append(ni.next().getDeviceName());
		}

		try {
			ClientResponse gdbResponse;
			Builder resBuilder;
			String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_LOGI_TOPO_GET_PATH;
			WebResource resource = this.gdb_client.resource(url);
			resource    = resource.queryParam("deviceNames", deviceNames.toString());
			resBuilder  = resource.accept(MediaType.APPLICATION_JSON);
			resBuilder  = resBuilder.type(MediaType.APPLICATION_JSON);
			gdbResponse = resBuilder.get(ClientResponse.class);

			if(gdbResponse.getStatus() != Definition.STATUS_SUCCESS) {
				logger.error(gdbResponse.getEntity(String.class));
				throw new GraphDBClientException("OrientDBs response is wrong.");
			}

			String resBody = gdbResponse.getEntity(String.class);
			Type type = new TypeToken<LogicalTopologyJsonInOut>(){}.getType();
			res = gson.fromJson(resBody, type);

		} catch (UniformInterfaceException uie) {
			logger.error(uie.getMessage());
			throw new GraphDBClientException("OrientDBs response is wrong.");

		} catch (ClientHandlerException che) {
			logger.error(che.getMessage());
			throw new GraphDBClientException("Connection faild with OrientDB.");

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new GraphDBClientException(e.getMessage());
		}

		if(logger.isDebugEnabled()) {
			logger.debug(String.format("%s(res=%s) - end", func, res));
		}
		return res;
	}

	@Override
	public PatchLinkJsonIn addLogicalLink(LogicalLink link) throws GraphDBClientException {
		final String func = "addLogicalLink";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(%s) - start", func, link));

		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_LOGI_LINK_CONNECT_PATH;
		String reqBody = link.toJson();
		String resBody = this.post(url, reqBody);
		PatchLinkJsonIn res = PatchLinkJsonIn.fromJson(resBody);

//		PatchLinkJsonIn res;
//		try {
//			Type type = new TypeToken<LogicalLink>(){}.getType();
//			String reqBody = gson.toJson(link, type);
//
//			ClientResponse gdbResponse;
//			Builder resBuilder;
//			String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_LOGI_LINK_CONNECT_PATH;
//			WebResource resource = this.gdb_client.resource(url);
//			resBuilder  = resource.entity(reqBody);
//			resBuilder  = resBuilder.accept(MediaType.APPLICATION_JSON);
//			resBuilder  = resBuilder.type(MediaType.APPLICATION_JSON);
//			gdbResponse = resBuilder.post(ClientResponse.class);
//
//			if(gdbResponse.getStatus() != Definition.STATUS_SUCCESS) {
//				logger.error(gdbResponse.getEntity(String.class));
//				throw new GraphDBClientException("OrientDBs response is wrong.");
//			}
//
//			String resBody = gdbResponse.getEntity(String.class);
//			type = new TypeToken<PatchLinkJsonIn>(){}.getType();
//			res = gson.fromJson(resBody, type);
//
//		} catch (UniformInterfaceException uie) {
//			logger.error(uie.getMessage());
//			throw new GraphDBClientException("OrientDBs response is wrong.");
//		} catch (ClientHandlerException che) {
//			logger.error(che.getMessage());
//			throw new GraphDBClientException("Connection faild with OrientDB.");
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			throw new GraphDBClientException(e.getMessage());
//		}

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(res=%s) - end", func, res));
		return res;
	}

	@Override
	public PatchLinkJsonIn delLogicalLink(LogicalLink link) throws GraphDBClientException {
		final String func = "delLogicalLink";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(%s) - start", func, link));


		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_LOGI_LINK_DISCONNECT_PATH;
		String reqBody = link.toJson();
		String resBody = this.post(url, reqBody);
		PatchLinkJsonIn res = PatchLinkJsonIn.fromJson(resBody);

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(res=%s) - end", func, res));
		return res;
	}

	@Override
	public BaseResponse nodeCreate(DeviceInfoJsonInOut newNode) throws GraphDBClientException {
		final String func = "nodeCreate";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(newDevice=%s) - start", func, newNode));

		String reqBody = newNode.toJson();
		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_NODE_CREATE_PATH;
		String resBody = this.post(url, reqBody);
		BaseResponse res = BaseResponse.fromJson(resBody);

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(res=%s) - end", func, res));
		return res;
	}

	@Override
	public BaseResponse portCreate(PortInfoJsonInOut portInfo) throws GraphDBClientException {
		final String func = "portCreate";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(portInfo=%s) - start", func, portInfo));

		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_PORT_CREATE_PATH;
		String reqBody = portInfo.toJson();
		String resBody = this.post(url, reqBody);
		BaseResponse res = BaseResponse.fromJson(resBody);

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(res=%s) - end", func, res));
		return res;
	}

	@Override
	public BaseResponse connectPhysicalLink(PhysicalLinkJsonInOut physicalLink) throws GraphDBClientException {
		final String func = "connectPhysicalLink";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(portInfo=%s) - start", func, physicalLink));

		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_PHYS_LINK_CONNECT_PATH;
		String reqBody = physicalLink.toJson();
		String resBody = this.post(url, reqBody);
		BaseResponse res = BaseResponse.fromJson(resBody);

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(res=%s) - end", func, res));
		return res;
	}

	@Override
	public BaseResponse disconnectPhysicalLink(PhysicalLinkJsonInOut physicalLink) throws GraphDBClientException {
		final String func = "disconnectPhysicalLink";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(portInfo=%s) - start", func, physicalLink));

		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_PHYS_LINK_DISCONNECT_PATH;
		String reqBody = physicalLink.toJson();
		String resBody = this.post(url, reqBody);
		BaseResponse res = BaseResponse.fromJson(resBody);

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(res=%s) - end", func, res));
		return res;
	}

}
