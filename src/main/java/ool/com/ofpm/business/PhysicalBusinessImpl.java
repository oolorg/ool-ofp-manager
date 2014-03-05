package ool.com.ofpm.business;

import ool.com.ofpm.client.GraphDBClient;
import ool.com.ofpm.client.GraphDBClientException;
import ool.com.ofpm.client.OrientDBClientImpl;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.PhysicalLinkJsonInOut;
import ool.com.ofpm.utils.Definition;
import ool.com.ofpm.validate.PhysicalLinkJsonInOutValidate;
import ool.com.ofpm.validate.ValidateException;

import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;

public class PhysicalBusinessImpl implements PhysicalBusiness {
	private static final Logger logger = Logger.getLogger(PhysicalBusinessImpl.class);

	@Override
	public String connectPhysicalLink(String physicalLinkJson) {
		final String fname = "connectPhysicalLinkJson";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(physicalLinkJson=%s) - start", fname, physicalLinkJson));

		BaseResponse res = new BaseResponse();

		try {
			PhysicalLinkJsonInOutValidate validator = new PhysicalLinkJsonInOutValidate();
			validator.checkStringBlank(physicalLinkJson);
			PhysicalLinkJsonInOut physicalLink = PhysicalLinkJsonInOut.fromJson(physicalLinkJson);
			validator.checkValidation(physicalLink);


			GraphDBClient gdbClient = OrientDBClientImpl.getInstance();
			if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.connectPhysicalLink(physicalLinkJson=%s) - called", physicalLinkJson));
			res = gdbClient.connectPhysicalLink(physicalLink);
			if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.connectPhysicalLink(ret=%s) - returned", res));

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
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, resBody));
		return resBody;
	}

	@Override
	public String disconnectPhysicalLink(String physicalLinkJson) {
		final String fname = "disconnectPhysicalLinkJson";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(physicalLinkJson=%s) - start", fname, physicalLinkJson));

		BaseResponse res = new BaseResponse();

		try {
			PhysicalLinkJsonInOutValidate validator = new PhysicalLinkJsonInOutValidate();
			validator.checkStringBlank(physicalLinkJson);
			PhysicalLinkJsonInOut physicalLink = PhysicalLinkJsonInOut.fromJson(physicalLinkJson);
			validator.checkValidation(physicalLink);


			GraphDBClient gdbClient = OrientDBClientImpl.getInstance();
			if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.disconnectPhysicalLink(physicalLinkJson=%s) - called", physicalLinkJson));
			res = gdbClient.disconnectPhysicalLink(physicalLink);
			if(logger.isInfoEnabled()) logger.info(String.format("graphDBClient.disconnectPhysicalLink(ret=%s) - returned", res));

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
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, resBody));
		return resBody;
	}


}
