package ool.com.ofpm.service;

import ool.com.ofpm.business.DeviceBusiness;
import ool.com.ofpm.business.DeviceBusinessImpl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class DeviceServiceImpl implements DeviceService {
	private static final Logger logger = Logger.getLogger(ConfigServiceImpl.class);
	Gson gson = new Gson();

	@Override
	public String createDevice(String deviceInfoJson) {
		String fname = "createDevice";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(req=%s) - start", fname, deviceInfoJson));

		DeviceBusiness deviceBiz = new DeviceBusinessImpl();
		String resDeviceBiz = deviceBiz.createDevice(deviceInfoJson);

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		return resDeviceBiz;
	}

	@Override
	public String deleteDevice(String params) {
		String fname = "createDevice";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(req=%s) - start", fname, params));

		DeviceBusiness deviceBiz = new DeviceBusinessImpl();
		String resDeviceBiz = deviceBiz.deleteDevice(params);

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		return resDeviceBiz;
	}

	@Override
	public String updateDevice(String params) {
		String fname = "createDevice";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(req=%s) - start", fname, params));

		DeviceBusiness deviceBiz = new DeviceBusinessImpl();
		String resDeviceBiz = deviceBiz.updateDevice(params);

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		return resDeviceBiz;
	}

}
