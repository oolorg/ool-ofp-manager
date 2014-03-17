package ool.com.ofpm.business;

import ool.com.ofpm.client.GraphDBClient;
import ool.com.ofpm.client.OrientDBClientImpl;
import ool.com.ofpm.exception.GraphDBClientException;
import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.PhysicalLinkJsonIn;
import ool.com.ofpm.utils.Definition;
import ool.com.ofpm.utils.ErrorMessage;
import ool.com.ofpm.validate.PhysicalLinkJsonInValidate;

import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;

public class PhysicalBusinessImpl implements PhysicalBusiness {
	private static final Logger logger = Logger.getLogger(PhysicalBusinessImpl.class);
	private static final String CONNECT = "connectPhysicalLink";
	private static final String DISCONNECT = "disconnectPhysicalLink";

	private String commonLogic(String physicalLinkJson, String gdbClientMethodName) {
		final String fname = "commonLogic";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(physicalLinkJson=%s, gdbClientMethodName=%s) - start", fname, physicalLinkJson, gdbClientMethodName));
		}

		BaseResponse res = new BaseResponse();
		try {
			PhysicalLinkJsonIn physicalLink = PhysicalLinkJsonIn.fromJson(physicalLinkJson);

			PhysicalLinkJsonInValidate validator = new PhysicalLinkJsonInValidate();
			validator.checkValidation(physicalLink);

			GraphDBClient gdbClient = OrientDBClientImpl.getInstance();
			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.%s(physicalLinkJson=%s) - called", gdbClientMethodName, physicalLinkJson));
			}
			if (gdbClientMethodName.equals(PhysicalBusinessImpl.CONNECT)) {
				res = gdbClient.connectPhysicalLink(physicalLink);
			} else if (gdbClientMethodName.equals(PhysicalBusinessImpl.DISCONNECT)) {
				res = gdbClient.disconnectPhysicalLink(physicalLink);
			}
			if (logger.isInfoEnabled()) {
				logger.info(String.format("graphDBClient.%s(ret=%s) - returned", gdbClientMethodName, res));
			}
		} catch (JsonSyntaxException jse) {
			logger.error(jse);
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ErrorMessage.INVALID_JSON);

		} catch (ValidateException ve) {
			logger.error(ve);
			res.setStatus(Definition.STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());

		} catch (GraphDBClientException gdbe) {
			logger.error(gdbe);
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(gdbe.getMessage());

		} catch (Exception e) {
			logger.error(e);
			res.setStatus(Definition.STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
		}

		String ret = res.toJson();
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	@Override
	public String connectPhysicalLink(String physicalLinkJson) {
		final String fname = "connectPhysicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(physicalLinkJson=%s) - start", fname, physicalLinkJson));
		}

		String res = this.commonLogic(physicalLinkJson, PhysicalBusinessImpl.CONNECT);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, res));
		}
		return res;
	}

	@Override
	public String disconnectPhysicalLink(String physicalLinkJson) {
		final String fname = "disconnectPhysicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(physicalLinkJson=%s) - start", fname, physicalLinkJson));
		}

		String res = this.commonLogic(physicalLinkJson, PhysicalBusinessImpl.DISCONNECT);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, res));
		}
		return res;
	}

}
