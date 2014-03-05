package ool.com.ofpm.service;

import ool.com.ofpm.business.PortBusiness;
import ool.com.ofpm.business.PortBusinessImpl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class PortServiceImpl implements PortService {
	private static final Logger logger = Logger.getLogger(ConfigServiceImpl.class);

	@Override
	public String createPort(String portInfoJson) {
		String fname = "createDevice";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(req=%s) - start", fname, portInfoJson));

		PortBusiness portBiz = new PortBusinessImpl();
		String resPortBiz = portBiz.createPort(portInfoJson);

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, resPortBiz));
		return resPortBiz;
	}

	@Override
	public String deletePort(String params) {
		String fname = "deleteDevice";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(req=%s) - start", fname, params));

		String res = "";

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}

	@Override
	public String updatePort(String params) {
		String fname = "updateDevice";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(req=%s) - start", fname, params));

		String res = "";

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, res));
		return res;
	}
}
