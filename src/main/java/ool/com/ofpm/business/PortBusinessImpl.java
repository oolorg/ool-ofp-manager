package ool.com.ofpm.business;

import ool.com.ofpm.client.GraphDBClient;
import ool.com.ofpm.client.GraphDBClientException;
import ool.com.ofpm.client.OrientDBClientImpl;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.PortInfoJsonInOut;
import ool.com.ofpm.utils.Definition;
import ool.com.ofpm.validate.PortInfoInValidate;
import ool.com.ofpm.validate.ValidateException;

import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;

public class PortBusinessImpl implements PortBusiness {
	private static final Logger logger = Logger.getLogger(PortBusinessImpl.class);

	public String createPort(String portInfoJson) {
		String fname = "createPort";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(params=%s) - start", fname, portInfoJson));

		BaseResponse res = new BaseResponse();

		try {
			PortInfoInValidate validator = new PortInfoInValidate();
			validator.checkStringBlank(portInfoJson);
			PortInfoJsonInOut portInfo = PortInfoJsonInOut.fromJson(portInfoJson);
			validator.checkValidation(portInfo);


			GraphDBClient gdbClient = OrientDBClientImpl.getInstance();
			if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.portCreate(portInfo=%s) - called", portInfo));
			res = gdbClient.portCreate(portInfo);
			if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.portCreate(ret=%s) - returned", res));

		} catch (JsonSyntaxException jse) {
			logger.error(jse.getMessage());
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage("Invalid json structure.");

		} catch (ValidateException ve) {
			logger.error(ve.getMessage());
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());

		} catch (GraphDBClientException gdbe) {
			logger.error(gdbe.getMessage());
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(gdbe.getMessage());

		} catch (Exception e) {
			logger.error(e.getMessage());
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		}

		String resBody = res.toJson();

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return resBody;
	}

	public String deletePort(String params) {
		String fname = "deletePort";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(params=%s) - start", fname, params));

		// TODO Auto-generated method stub
		String res = "";

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

	public String updatePort(String params) {
		String fname = "updatePort";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(params=%s) - start", fname, params));

		// TODO Auto-generated method stub
		String res = "";

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

}
