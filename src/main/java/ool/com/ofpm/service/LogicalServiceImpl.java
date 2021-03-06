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
		final String fname = "getLogicalTopology";
		long time = 0L;
		if (logger.isInfoEnabled()) {
			time = System.currentTimeMillis();
			logger.info(String.format("###  REQUESTED ### %s ###", fname));
		}
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
		if (logger.isInfoEnabled()) {
			time = System.currentTimeMillis() - time;
			logger.info(String.format("###     END    ### %s ### %s[ms] ###", fname, time));
		}
		return Response.ok(resLogiBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Override
	public Response updateLogicalTopology(String requestedTopologyJson) {
		final String fname = "updateLogicalTopology";
		long time = 0L;
		if (logger.isInfoEnabled()) {
			time = System.currentTimeMillis();
			logger.info(String.format("###  REQUESTED ### %s ###", fname));
		}
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
		if (logger.isInfoEnabled()) {
			time = System.currentTimeMillis() - time;
			logger.info(String.format("###     END    ### %s ### %s[ms] ###", fname, time));
		}
		return Response.ok(resLogiBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	/* (non-Javadoc)
	 * @see ool.com.ofpm.service.LogicalService#setFlow(java.lang.String)
	 */
	@Override
	public Response setFlow(String requestedData) {
		final String fname = "setFlow";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestedData=%s) - start", fname, requestedData));
		}

		//Client c = Client.create();
		//WebResource r = c.resource("http://172.16.1.85:28080/ofc/ryu/ctrl/test");
		//String html = r.post(String.class, "{}");

		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(LogicalBusiness.class).to(LogicalBusinessImpl.class);
			}
		});
		LogicalServiceImpl main = this.injector.getInstance(LogicalServiceImpl.class);
		String resLogiBiz = main.logiBiz.setFlow(requestedData);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resLogiBiz));
		}
		return Response.ok(resLogiBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}


	/*
	 * @Override(non-Javadoc)
	 * @see ool.com.ofpm.service.LogicalService#initFlow(java.lang.String)
	 */
	public Response initFlow(String requestedData) {
		final String fname = "initFlow";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(requestedData=%s) - start", fname, requestedData));
		}
		this.injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bind(LogicalBusiness.class).to(LogicalBusinessImpl.class);
			}
		});
		LogicalServiceImpl main = this.injector.getInstance(LogicalServiceImpl.class);
		String resLogiBiz = main.logiBiz.initFlow(requestedData);

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(ret=%s) - end", fname, resLogiBiz));
		}
		return Response.ok(resLogiBiz).type(MediaType.APPLICATION_JSON_TYPE).build();
	}
}
