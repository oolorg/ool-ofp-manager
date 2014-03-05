package ool.com.ofpm.validate;

import java.util.List;

import ool.com.ofpm.json.PhysicalLinkJsonInOut;
import ool.com.ofpm.json.PhysicalLinkJsonInOut.PhysicalLink;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class PhysicalLinkJsonInOutValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(LogicalTopologyValidate.class);

	@Override
	public void checkStringBlank(String value) throws ValidateException {
		String fname = "checkStringBlank";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(value=%s) - start", fname, value));

		if(StringUtils.isBlank(value)) throw new ValidateException("Input string is blank.");

		if(logger.isDebugEnabled()) logger.debug(String.format("%s() - end", fname));
	}

	public void checkValidation(PhysicalLinkJsonInOut physicalLink) throws ValidateException {
		String fname = "checkValidateion";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(physicalLink=%s) - start", fname, physicalLink));

		if(BaseValidate.checkNull(physicalLink)) throw new ValidateException("DeviceInfo is null.");
		List<PhysicalLink> links = physicalLink.getLink();
		if(BaseValidate.checkNull(links)) throw new ValidateException("links is null.");
		if(links.size() != 2)             throw new ValidateException("Number of link is not 2.");
		if(links.get(0).equals(links.get(1))) throw new ValidateException("Can not connect, bitween same port.");
		for(PhysicalLink link : links) {
			if(BaseValidate.checkNull(link))              throw new ValidateException("link is null.");
			if(StringUtils.isBlank(link.getDeviceName())) throw new ValidateException("DeviceName is blank.");
			if(StringUtils.isBlank(link.getPortName()))   throw new ValidateException("PortName is blank");
		}

		if(logger.isDebugEnabled()) logger.debug(String.format("%s() - end", fname));
	}
}
