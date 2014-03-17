package ool.com.ofpm.client;

import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import ool.com.ofpm.exception.GraphDBClientException;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.DeviceInfoCreateJsonIn;
import ool.com.ofpm.json.PortInfoCreateJsonIn;
import ool.com.ofpm.json.LogicalTopologyGetJsonOut;
import ool.com.ofpm.json.GraphDBPatchLinkJsonRes;
import ool.com.ofpm.json.LogicalTopology.LogicalLink;
import ool.com.ofpm.json.Node;
import ool.com.ofpm.json.PhysicalLinkJsonIn;
import ool.com.ofpm.json.DeviceInfoUpdateJsonIn;
import ool.com.ofpm.json.PortInfoUpdateJsonIn;
import ool.com.ofpm.utils.Config;
import ool.com.ofpm.utils.ConfigImpl;
import ool.com.ofpm.utils.Definition;
import ool.com.ofpm.utils.ErrorMessage;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

public class OrientDBClientImpl implements GraphDBClient {
	private static final Logger logger = Logger.getLogger(OrientDBClientImpl.class);
	private static OrientDBClientImpl instance;
	private final Client gdbClient;
	Config conf = new ConfigImpl();

	private OrientDBClientImpl() {
		this.gdbClient = Client.create();
	}

	public static synchronized OrientDBClientImpl getInstance() {
		if (logger.isDebugEnabled()) {
			logger.debug("getInstance() - start");
		}
		if (instance == null) {
			instance = new OrientDBClientImpl();
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("getInstance(ret=%s) - end", instance.toString()));
		}
		return instance;
	}

	/**
	 * @param url
	 * @param methodType
	 * @param reqBody
	 * @return
	 * @throws GraphDBClientException
	 */
	private String sendBodyRequest(String url, String methodType, String reqBody) throws GraphDBClientException {
		final String func = "sendBodyRequest";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(url=%s, methodType=%s, reqBody=%s) - start", func, url, methodType, reqBody));
		}

		ClientResponse gdbResponse = null;
		try {
			Builder resBuilder;
			WebResource resource = this.gdbClient.resource(url);
			resBuilder = resource.entity(reqBody);
			resBuilder = resBuilder.accept(MediaType.APPLICATION_JSON);
			resBuilder = resBuilder.type(MediaType.APPLICATION_JSON);
			if (methodType.equals(Definition.HTTP_METHOD_POST)) {
				gdbResponse = resBuilder.post(ClientResponse.class);
			} else if (methodType.equals(Definition.HTTP_METHOD_PUT)) {
				gdbResponse = resBuilder.put(ClientResponse.class);
			} else {
				/* Unreachable */
			}

			if (gdbResponse.getStatus() != Definition.STATUS_SUCCESS) {
				logger.error(gdbResponse.getEntity(String.class));
				throw new GraphDBClientException(String.format(ErrorMessage.WRONG_RESPONSE, "OrientDB Server"));
			}

		} catch (UniformInterfaceException uie) {
			logger.error(uie);
			throw new GraphDBClientException(String.format(ErrorMessage.WRONG_RESPONSE, "OrientDB Server"));

		} catch (ClientHandlerException che) {
			logger.error(che);
			throw new GraphDBClientException(String.format(ErrorMessage.CONNECTION_FAIL, "OrientDB Server"));

		} catch (Exception e) {
			logger.error(e);
			throw new GraphDBClientException(ErrorMessage.UNEXPECTED_ERROR);
		}

		String ret = gdbResponse.getEntity(String.class);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(res=%s) - end", func, ret));
		}
		return ret;
	}

	/**
	 * @param url
	 * @param methodType
	 * @param query
	 * @return
	 * @throws GraphDBClientException
	 */
	private String sendQueryRequest(String url, String methodType, MultivaluedMap<String, String> query) throws GraphDBClientException {
		final String func = "sendQueryRequest";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(url=%s, methodType=%s, querys=%s) - start", func, url, methodType, query));
		}

		ClientResponse gdbResponse = null;
		try {
			Builder resBuilder;
			WebResource resource = this.gdbClient.resource(url);
			resource = resource.queryParams(query);
			resBuilder = resource.accept(MediaType.APPLICATION_JSON);
			resBuilder = resBuilder.type(MediaType.APPLICATION_FORM_URLENCODED);
			if (methodType.equals(Definition.HTTP_METHOD_GET)) {
				gdbResponse = resBuilder.get(ClientResponse.class);
			} else if (methodType.equals(Definition.HTTP_METHOD_DELETE)) {
				gdbResponse = resBuilder.delete(ClientResponse.class);
			} else {
				/* Unreachable */
			}

			if (gdbResponse.getStatus() != Definition.STATUS_SUCCESS) {
				logger.error(gdbResponse.getEntity(String.class));
				throw new GraphDBClientException(String.format(ErrorMessage.WRONG_RESPONSE, "OrientDB Server"));
			}

		} catch (UniformInterfaceException uie) {
			logger.error(uie);
			throw new GraphDBClientException(String.format(ErrorMessage.WRONG_RESPONSE, "OrientDB Server"));

		} catch (ClientHandlerException che) {
			logger.error(che);
			throw new GraphDBClientException(String.format(ErrorMessage.CONNECTION_FAIL, "OrientDB Server"));

		} catch (Exception e) {
			logger.error(e);
			throw new GraphDBClientException(ErrorMessage.UNEXPECTED_ERROR);
		}

		String ret = gdbResponse.getEntity(String.class);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", func, ret));
		}
		return ret;
	}

	@Override
	public synchronized LogicalTopologyGetJsonOut getLogicalTopology(List<Node> nodes) throws GraphDBClientException {
		final String func = "getLogicalTopology";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(nodes=%s) - start", func, nodes));
		}

		LogicalTopologyGetJsonOut ret;
		StringBuilder deviceNames = new StringBuilder();
		Iterator<Node> ni = nodes.iterator();
		deviceNames.append(ni.next().getDeviceName());
		while (ni.hasNext()) {
			deviceNames.append(",");
			deviceNames.append(ni.next().getDeviceName());
		}

		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_LOGI_TOPO_GET_PATH;
		MultivaluedMap<String, String> query = new MultivaluedHashMap<String, String>();
		query.add("deviceNames", deviceNames.toString());
		String res = this.sendQueryRequest(url, Definition.HTTP_METHOD_GET, query);
		ret = LogicalTopologyGetJsonOut.fromJson(res);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", func, res));
		}
		return ret;
	}

	@Override
	public synchronized GraphDBPatchLinkJsonRes addLogicalLink(LogicalLink link) throws GraphDBClientException {
		final String func = "addLogicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(link=%s) - start", func, link));
		}

		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_LOGI_LINK_CONNECT_PATH;
		String res = this.sendBodyRequest(url, Definition.HTTP_METHOD_POST, link.toJson());
		GraphDBPatchLinkJsonRes ret = GraphDBPatchLinkJsonRes.fromJson(res);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", func, res));
		}
		return ret;
	}

	@Override
	public synchronized GraphDBPatchLinkJsonRes delLogicalLink(LogicalLink link) throws GraphDBClientException {
		final String func = "delLogicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(link=%s) - start", func, link));
		}

		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_LOGI_LINK_DISCONNECT_PATH;
		String res = this.sendBodyRequest(url, Definition.HTTP_METHOD_POST, link.toJson());
		GraphDBPatchLinkJsonRes ret = GraphDBPatchLinkJsonRes.fromJson(res);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", func, res));
		}
		return ret;
	}

	@Override
	public synchronized BaseResponse nodeCreate(DeviceInfoCreateJsonIn deviceInfo) throws GraphDBClientException {
		final String func = "nodeCreate";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceInfo=%s) - start", func, deviceInfo));
		}

		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_NODE_CREATE_PATH;
		String res = this.sendBodyRequest(url, Definition.HTTP_METHOD_POST, deviceInfo.toJson());
		BaseResponse ret = BaseResponse.fromJson(res);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", func, res));
		}
		return ret;
	}

	@Override
	public synchronized BaseResponse nodeUpdate(DeviceInfoUpdateJsonIn updateDeviceInfo) throws GraphDBClientException {
		final String func = "nodeUpdate";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(updateDeviceInfo=%s) - start", func, updateDeviceInfo));
		}

		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_NODE_UPDATE_PATH;
		String res = this.sendBodyRequest(url, Definition.HTTP_METHOD_PUT, updateDeviceInfo.toJson());
		BaseResponse ret = BaseResponse.fromJson(res);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", func, res));
		}
		return ret;
	}

	@Override
	public synchronized BaseResponse nodeDelete(String deviceName) throws GraphDBClientException {
		final String func = "nodeDelete";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s) - start", func, deviceName));
		}

		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_NODE_DELETE_PATH;
		MultivaluedMap<String, String> query = new MultivaluedHashMap<String, String>();
		query.add("deviceName", deviceName);
		String res = this.sendQueryRequest(url, Definition.HTTP_METHOD_DELETE, query);
		BaseResponse ret = BaseResponse.fromJson(res);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", func, res));
		}
		return ret;
	}

	@Override
	public synchronized BaseResponse portCreate(PortInfoCreateJsonIn portInfo) throws GraphDBClientException {
		final String func = "portCreate";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(portInfo=%s) - start", func, portInfo));
		}

		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_PORT_CREATE_PATH;
		String res = this.sendBodyRequest(url, Definition.HTTP_METHOD_POST, portInfo.toJson());
		BaseResponse ret = BaseResponse.fromJson(res);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", func, res));
		}
		return ret;
	}

	@Override
	public synchronized BaseResponse portUpdate(PortInfoUpdateJsonIn portInfo) throws GraphDBClientException {
		final String func = "portUpdate";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(portInfo=%s) - start", func, portInfo));
		}

		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_PORT_UPDATE_PATH;
		String res = this.sendBodyRequest(url, Definition.HTTP_METHOD_PUT, portInfo.toJson());
		BaseResponse ret = BaseResponse.fromJson(res);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", func, res));
		}
		return ret;
	}

	@Override
	public synchronized BaseResponse portDelete(String portName, String deviceName) throws GraphDBClientException {
		final String func = "portDelete";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(portInfo=%s, deviceName=%s) - start", func, portName, deviceName));
		}

		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_PORT_DELETE_PATH;
		MultivaluedMap<String, String> query = new MultivaluedHashMap<String, String>();
		query.add("portName", portName);
		query.add("deviceName", deviceName);
		String res = this.sendQueryRequest(url, Definition.HTTP_METHOD_DELETE, query);
		BaseResponse ret = BaseResponse.fromJson(res);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", func, res));
		}
		return ret;
	}

	@Override
	public synchronized BaseResponse connectPhysicalLink(PhysicalLinkJsonIn physicalLink) throws GraphDBClientException {
		final String func = "connectPhysicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(physicalLink=%s) - start", func, physicalLink));
		}

		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_PHYS_LINK_CONNECT_PATH;
		String res = this.sendBodyRequest(url, Definition.HTTP_METHOD_POST, physicalLink.toJson());
		BaseResponse ret = BaseResponse.fromJson(res);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", func, res));
		}
		return ret;
	}

	@Override
	public synchronized BaseResponse disconnectPhysicalLink(PhysicalLinkJsonIn physicalLink) throws GraphDBClientException {
		final String func = "disconnectPhysicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(physicalLink=%s) - start", func, physicalLink));
		}

		String url = conf.getString(Definition.GRAPH_DB_URL) + Definition.GRAPH_DB_PHYS_LINK_DISCONNECT_PATH;
		String res = this.sendBodyRequest(url, Definition.HTTP_METHOD_POST, physicalLink.toJson());
		BaseResponse ret = BaseResponse.fromJson(res);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", func, res));
		}
		return ret;
	}

}
