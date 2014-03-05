package ool.com.ofpm.service;

import ool.com.ofpm.business.PhysicalBusiness;
import ool.com.ofpm.business.PhysicalBusinessImpl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class PhysicalServiceImpl implements PhysicalService {
	private static final Logger logger = Logger.getLogger(LogicalServiceImpl.class);

	@Override
	public String connectPhysicalLink(String physicalLinkJson) {
		String fname = "connectPhysicalLink";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(req=%s) - start", fname, physicalLinkJson));

		PhysicalBusiness physBiz = new PhysicalBusinessImpl();
		String resPhysBiz = physBiz.connectPhysicalLink(physicalLinkJson);

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, resPhysBiz));
		return resPhysBiz;
	}

	@Override
	public String disconnectPhysicalLink(String physicalLinkJson) {
		String fname = "disconnectPhysicalLink";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(req=%s) - start", fname, physicalLinkJson));

		PhysicalBusiness physBiz = new PhysicalBusinessImpl();
		String resPhysBiz = physBiz.disconnectPhysicalLink(physicalLinkJson);

		if(logger.isDebugEnabled()) logger.debug(String.format("%s(ret=%s) - end", fname, resPhysBiz));
		return resPhysBiz;
	}

}

