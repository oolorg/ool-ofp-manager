package ool.com.ofpm.business;

import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.DeviceJsonIn;

import org.apache.log4j.Logger;

public class DeviceBusinessImpl implements DeviceBusiness {
	private static final Logger logger = Logger.getLogger(DeviceBusinessImpl.class);

	public BaseResponse createDevice(DeviceJsonIn params) {
		String fname = "createDevice";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(params=%s) - start", fname, params));

		BaseResponse res = null;
		// TODO Auto-generated method stub
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

	public BaseResponse deleteDevice(DeviceJsonIn params) {
		String fname = "deleteDevice";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(params=%s) - start", fname, params));

		BaseResponse res = null;
		// TODO Auto-generated method stub
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

	public BaseResponse updateDevice(DeviceJsonIn params) {
		String fname = "updateDevice";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(params=%s) - start", fname, params));

		BaseResponse res = null;
		// TODO Auto-generated method stub
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

}
