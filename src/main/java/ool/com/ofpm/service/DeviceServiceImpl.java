package ool.com.ofpm.service;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ool.com.ofpm.business.DeviceBusiness;
import ool.com.ofpm.business.DeviceBusinessImpl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

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
		final String fname = "createDevice";
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
		final String fname = "deleteDevice";
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
		final String fname = "updateDevice";
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

	/* (non-Javadoc)
	 * @see ool.com.ofpm.service.DeviceService#createPort(java.lang.String)
	 */
	@Override
	public String createPort(@RequestBody String newPortInfoJson) {
		final String fname = "createPort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(portInfoJson=%s) - start", fname, newPortInfoJson));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = this.injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.createPort(newPortInfoJson);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return resDeviceBiz;
	}

	/* (non-Javadoc)
	 * @see ool.com.ofpm.service.DeviceService#deletePort(java.lang.String, java.lang.String)
	 */
	@Override
	public String deletePort(@QueryParam("portName") String portName, @QueryParam("deviceName") String deviceName) {
		final String fname = "deletePort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(portName=%s, deviceName=%s) - start", fname, portName, deviceName));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = this.injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.deletePort(portName, deviceName);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return resDeviceBiz;
	}

	/* (non-Javadoc)
	 * @see ool.com.ofpm.service.DeviceService#updatePort(java.lang.String)
	 */
	@Override
	public String updatePort(@RequestBody String updatePortInfoJson) {
		final String fname = "updatePort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(updatePortInfoJson=%s) - start", fname, updatePortInfoJson));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
			}
		});
		DeviceServiceImpl main = this.injector.getInstance(DeviceServiceImpl.class);
		String resDeviceBiz = main.deviceBiz.updatePort(updatePortInfoJson);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resDeviceBiz));
		}
		return resDeviceBiz;
	}

	/* (non-Javadoc)
	 * @see ool.com.ofpm.service.DeviceService#getConnectedPortInfo(java.lang.String)
	 */
	@Override
	public Response getConnectedPortInfo(@QueryParam("deviceName") String deviceName) {
    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("getConnectedPortInfo(deviceName=%s) - start ", deviceName));
    	}

        this.injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
            	bind(DeviceBusiness.class).to(DeviceBusinessImpl.class);
            }
        });

        DeviceServiceImpl main = injector.getInstance(DeviceServiceImpl.class);
        String resDeviceBiz = main.deviceBiz.getConnectedPortInfo(deviceName);

        if (logger.isDebugEnabled()) {
    		logger.debug(String.format("getConnectedPortInfo(ret=%s) - end ", resDeviceBiz));
    	}
		return Response.ok(resDeviceBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}
}
