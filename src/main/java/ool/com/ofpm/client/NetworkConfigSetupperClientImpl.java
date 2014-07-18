/**
 * @author OOL 1134380013430
 * @date 2014/04/23
 * @TODO TODO
 */
package ool.com.ofpm.client;

import javax.ws.rs.core.MediaType;

import static ool.com.constants.OfpmDefinition.*;
import static ool.com.constants.ErrorMessage.*;

import ool.com.ofpm.exception.NetworkConfigSetupperException;
import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.common.BaseResponse;
import ool.com.ofpm.json.ncs.NetworkConfigSetupperIn;
import ool.com.ofpm.validate.ncs.NetworkConfigSetupperInValidate;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

/**
 * @author 1134380013430
 *
 */
public class NetworkConfigSetupperClientImpl implements NetworkConfigSetupperClient {
	private static final Logger logger = Logger.getLogger(NetworkConfigSetupperClientImpl.class);
	private Client ncsClient;
	private String ncsUrl;

	public NetworkConfigSetupperClientImpl(String ncsUrl) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("NetworkConfigSetupperClientImpl(ret=%s) - start", ncsUrl));
		}
		ncsClient = Client.create();
		this.ncsUrl = ncsUrl;
		if (logger.isDebugEnabled()) {
			logger.debug("NetworkConfigSetupperClientImpl() - end");
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.ofpm.client.NetworkConfigSetupperClient#sendPlaneSwConfigData(java.lang.String, java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public BaseResponse sendPlaneSwConfigData(NetworkConfigSetupperIn networkConfigSetupperIn) {
		final String func = "sendPlaneSwConfigData";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(networkConfigSetupperIn=%s) - start", func, networkConfigSetupperIn));
		}
		BaseResponse ret = new BaseResponse();
		try {
			NetworkConfigSetupperInValidate.checkValidation(networkConfigSetupperIn);

			String url = this.ncsUrl + NCS_PLANE_SW_CONFIG;
			String res = this.sendBodyRequest(url, HTTP_METHOD_PUT, networkConfigSetupperIn.toJson());
			ret = BaseResponse.fromJson(res);

		} catch (NetworkConfigSetupperException ne) {
			if (logger.isDebugEnabled()) {
				logger.error(ne);
			}
		} catch (ValidateException ve) {
			if (logger.isDebugEnabled()) {
				logger.error(ve);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret.status=%d) - end", func, ret.getStatus()));
		}
		return ret;
	}

	/**
	 * @param url
	 * @param methodType
	 * @param reqBody
	 * @return
	 * @throws NetworkConfigSetupperException
	 */
	private String sendBodyRequest(String url, String methodType, String reqBody) throws NetworkConfigSetupperException {
		final String func = "sendBodyRequest";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(url=%s, methodType=%s, reqBody=%s) - start", func, url, methodType, reqBody));
		}

		ClientResponse ncsResponse = null;
		try {
			Builder resBuilder;
			WebResource resource = this.ncsClient.resource(url);
			resBuilder = resource.entity(reqBody);
			resBuilder = resBuilder.accept(MediaType.APPLICATION_JSON);
			resBuilder = resBuilder.type(MediaType.APPLICATION_JSON);
			if (methodType.equals(HTTP_METHOD_POST)) {
				ncsResponse = resBuilder.post(ClientResponse.class);
			} else if (methodType.equals(HTTP_METHOD_PUT)) {
				ncsResponse = resBuilder.put(ClientResponse.class);
			} else {
				/* Unreachable */
			}

			if (ncsResponse.getStatus() != STATUS_SUCCESS) {
				logger.error(ncsResponse.getEntity(String.class));
				throw new NetworkConfigSetupperException(String.format(WRONG_RESPONSE, "NetworkConfigSetupper Server"));
			}

		} catch (UniformInterfaceException uie) {
			logger.error(uie);
			throw new NetworkConfigSetupperException(String.format(WRONG_RESPONSE, "NetworkConfigSetupper Server"));

		} catch (ClientHandlerException che) {
			logger.error(che);
			throw new NetworkConfigSetupperException(String.format(CONNECTION_FAIL, "NetworkConfigSetupper Server"));

		} catch (Exception e) {
			logger.error(e);
			throw new NetworkConfigSetupperException(UNEXPECTED_ERROR);
		}

		String ret = ncsResponse.getEntity(String.class);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(res=%s) - end", func, ret));
		}
		return ret;
	}
}
