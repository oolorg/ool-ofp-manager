package ool.com.ofpm.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
	public Response getLogicalTopology(String deviceNamesCSV, String tokenId) {
		String fname = "getLogicalTopology";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceNamesCSV=%s, tokenId=%s) - start", fname, deviceNamesCSV, tokenId));
		}

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(LogicalBusiness.class).to(LogicalBusinessImpl.class);
			}
		});
		LogicalServiceImpl main = this.injector.getInstance(LogicalServiceImpl.class);
		String resLogiBiz = main.logiBiz.getLogicalTopology(deviceNamesCSV, tokenId);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resLogiBiz));
		}
		return Response.ok(resLogiBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Override
	public Response updateLogicalTopology(String requestedTopologyJson) {
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
		return Response.ok(resLogiBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}
}
