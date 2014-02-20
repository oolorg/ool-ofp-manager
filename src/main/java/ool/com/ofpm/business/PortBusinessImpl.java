package ool.com.ofpm.business;

import org.apache.log4j.Logger;

import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.PortJsonIn;

public class PortBusinessImpl implements PortBusiness {
	private static final Logger logger = Logger.getLogger(PortBusinessImpl.class);

	public BaseResponse createPort(PortJsonIn params) {
		String fname = "createPort";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(params=%s) - start", fname, params));

		BaseResponse res = null;
		// TODO Auto-generated method stub
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

	public BaseResponse deletePort(PortJsonIn params) {
		String fname = "deletePort";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(params=%s) - start", fname, params));

		BaseResponse res = null;
		// TODO Auto-generated method stub
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

	public BaseResponse updatePort(PortJsonIn params) {
		String fname = "updatePort";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(params=%s) - start", fname, params));

		BaseResponse res = null;
		// TODO Auto-generated method stub
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

}
