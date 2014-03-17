package ool.com.ofpm.client;

import javax.ws.rs.core.MediaType;

import ool.com.ofpm.exception.AgentClientException;
import ool.com.ofpm.json.AgentClientUpdateFlowReq;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.utils.Definition;
import ool.com.ofpm.utils.ErrorMessage;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

public class AgentClientImpl implements AgentClient {
	private static final Logger logger = Logger.getLogger(AgentClientImpl.class);
	private WebResource resource;
	private String ip;

	public AgentClientImpl(String ip) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("AgentClientImpl(%s) - start", ip));
		}
		this.ip = ip;
		this.resource = Client.create().resource("http://" + ip + Definition.AGENT_PATH);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("AgentClientImpl() - end"));
		}
	}

	public BaseResponse updateFlows(AgentClientUpdateFlowReq flows) throws AgentClientException {
		final String func = "updateFlows";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(%s) - start", func, flows));
		}

		BaseResponse ret = new BaseResponse();
		try {
			Builder resBuilder = this.resource.entity(flows.toJson());
			resBuilder = resBuilder.accept(MediaType.APPLICATION_JSON);
			resBuilder = resBuilder.type(MediaType.APPLICATION_JSON);
			ClientResponse res = resBuilder.put(ClientResponse.class);

			if (res.getStatus() != Definition.STATUS_SUCCESS) {
				logger.error(res.getEntity(String.class));
				throw new AgentClientException(String.format(ErrorMessage.WRONG_RESPONSE, "Agent-" + this.ip));
			}
			ret = BaseResponse.fromJson(res.getEntity(String.class));
		} catch (UniformInterfaceException uie) {
			logger.error(uie.getMessage());
			throw new AgentClientException(String.format(ErrorMessage.CONNECTION_FAIL, "Agent-" + this.ip));
		} catch (ClientHandlerException che) {
			logger.error(che);
			throw new AgentClientException(String.format(ErrorMessage.CONNECTION_FAIL, "Agent-" + this.ip));
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new AgentClientException(ErrorMessage.UNEXPECTED_ERROR);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s (ret=%s) - end", func, ret.toJson()));
		}
		return ret;
	}

	public String getIp() {
		return this.ip;
	}

	@Override
	public String toString() {
		return super.toString() + ":" + this.ip;
	}
}
