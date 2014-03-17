package ool.com.ofpm.service;

import ool.com.ofpm.business.PortBusiness;
import ool.com.ofpm.business.PortBusinessImpl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

@Component
public class PortServiceImpl implements PortService {
	private static final Logger logger = Logger.getLogger(PortServiceImpl.class);

	@Inject
	PortBusiness portBiz;
	Injector injector;

	@Override
	public String createPort(String newPortInfoJson) {
		String fname = "createPort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(portInfoJson=%s) - start", fname, newPortInfoJson));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(PortBusiness.class).to(PortBusinessImpl.class);
			}
		});
		PortServiceImpl main = this.injector.getInstance(PortServiceImpl.class);
		String resPortBiz = main.portBiz.createPort(newPortInfoJson);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resPortBiz));
		}
		return resPortBiz;
	}

	@Override
	public String deletePort(String portName, String deviceName) {
		String fname = "deletePort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(portName=%s, deviceName=%s) - start", fname, portName, deviceName));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(PortBusiness.class).to(PortBusinessImpl.class);
			}
		});
		PortServiceImpl main = this.injector.getInstance(PortServiceImpl.class);
		String resPortBiz = main.portBiz.deletePort(portName, deviceName);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resPortBiz));
		}
		return resPortBiz;
	}

	@Override
	public String updatePort(String updatePortInfoJson) {
		String fname = "updatePort";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(updatePortInfoJson=%s) - start", fname, updatePortInfoJson));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(PortBusiness.class).to(PortBusinessImpl.class);
			}
		});
		PortServiceImpl main = this.injector.getInstance(PortServiceImpl.class);
		String resPortBiz = main.portBiz.updatePort(updatePortInfoJson);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resPortBiz));
		}
		return resPortBiz;
	}
}
