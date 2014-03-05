package ool.com.ofpm.validate;

import ool.com.ofpm.json.PortInfoJsonInOut;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class PortInfoInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(LogicalTopologyValidate.class);

	@Override
	public void checkStringBlank(String value) throws ValidateException {
		String fname = "checkStringBlank";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(value=%s) - start", fname, value));
		if(StringUtils.isBlank(value)) throw new ValidateException("Input string is blank.");

		if(logger.isDebugEnabled()) logger.debug(String.format("%s() - end", fname));
	}

	public void checkValidation(PortInfoJsonInOut portInfoJson) throws ValidateException {
		String fname = "checkValidateion";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(portInfoJson=%s) - start", fname, portInfoJson));

		if(BaseValidate.checkNull(portInfoJson))                 throw new ValidateException("DeviceInfo is null.");
		if(StringUtils.isBlank(portInfoJson.getDeviceName()))    throw new ValidateException("DeviceName is blank or null.");
		if(StringUtils.isBlank(portInfoJson.getPortName()))      throw new ValidateException("PortName is blank or null.");
		if(BaseValidate.checkNull(portInfoJson.getPortNumber())) throw new ValidateException("PortNumber is null.");
		if(StringUtils.isBlank(portInfoJson.getType()))          throw new ValidateException("Type is blank or null.");

		if(logger.isDebugEnabled()) logger.debug(String.format("%s() - end", fname));
	}

}
