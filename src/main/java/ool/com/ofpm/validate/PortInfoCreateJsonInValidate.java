package ool.com.ofpm.validate;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.PortInfoCreateJsonIn;
import ool.com.ofpm.utils.ErrorMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class PortInfoCreateJsonInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(PortInfoCreateJsonInValidate.class);

	public void checkValidation(PortInfoCreateJsonIn portInfoJson) throws ValidateException {
		String fname = "checkValidateion";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(portInfoJson=%s) - start", fname, portInfoJson));
		}

		if (BaseValidate.checkNull(portInfoJson)) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "Input parameter"));
		}
		if (StringUtils.isBlank(portInfoJson.getDeviceName())) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "deviceName"));
		}
		if (StringUtils.isBlank(portInfoJson.getPortName())) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "portName"));
		}
		if (StringUtils.isBlank(portInfoJson.getType())) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "type"));
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

}
