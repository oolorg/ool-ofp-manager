package ool.com.ofpm.service;

import ool.com.ofpm.business.DeviceBusiness;
import ool.com.ofpm.business.DeviceBusinessImpl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

@Component
public class DeviceServiceImpl implements DeviceService {
	private static final Logger logger = Logger.getLogger(DeviceServiceImpl.class);

	@Inject
	DeviceBusiness deviceBiz;
	Injector injector;

	@Override
	public String createDevice(String newDeviceInfoJson) {
		String fname = "createDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceInfoJson=%s) - start", fname, newDeviceInfoJson));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.createDevice(newDeviceInfoJson);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return resDeviceBiz;
	}

	@Override
	public String deleteDevice(String deviceName) {
		String fname = "deleteDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceName=%s) - start", fname, deviceName));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.deleteDevice(deviceName);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return resDeviceBiz;
	}

	@Override
	public String updateDevice(String updateDeviceInfoJson) {
		String fname = "updateDevice";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceInfoJson=%s) - start", fname, updateDeviceInfoJson));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.updateDevice(updateDeviceInfoJson);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return resDeviceBiz;
	}
}
