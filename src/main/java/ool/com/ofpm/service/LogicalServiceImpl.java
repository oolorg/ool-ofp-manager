package ool.com.ofpm.service;

import ool.com.ofpm.business.LogicalBusiness;
import ool.com.ofpm.business.LogicalBusinessImpl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

@Component
public class LogicalServiceImpl implements LogicalService {
	private static final Logger logger = Logger.getLogger(LogicalServiceImpl.class);

	@Inject
	LogicalBusiness logiBiz;
	Injector injector;

	@Override
	public String getLogicalTopology(String deviceNamesCSV) {
		String fname = "getLogicalTopology";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceNamesCSV=%s) - start", fname, deviceNamesCSV));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(LogicalBusiness.class).to(LogicalBusinessImpl.class);
			}
		});
		LogicalServiceImpl main = this.injector.getInstance(LogicalServiceImpl.class);
		String resLogiBiz = main.logiBiz.getLogicalTopology(deviceNamesCSV);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resLogiBiz));
		}
		return resLogiBiz;
	}

	@Override
	public String updateLogicalTopology(String requestedTopologyJson) {
		String fname = "updateLogicalTopology";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestedTopologyJson=%s) - start", fname, requestedTopologyJson));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(LogicalBusiness.class).to(LogicalBusinessImpl.class);
			}
		});
		LogicalServiceImpl main = this.injector.getInstance(LogicalServiceImpl.class);
		String resLogiBiz = main.logiBiz.updateLogicalTopology(requestedTopologyJson);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resLogiBiz));
		}
		return resLogiBiz;
	}
}
