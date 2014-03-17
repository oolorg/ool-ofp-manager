package ool.com.ofpm.validate;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.PortInfo;
import ool.com.ofpm.json.PortInfoUpdateJsonIn;
import ool.com.ofpm.utils.ErrorMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class PortInfoUpdateJsonInValidate {
	private static Logger logger = Logger.getLogger(PortInfoUpdateJsonInValidate.class);

	/**
	 * @param updatePortInfo
	 * @throws ValidateException
	 */
	public void checkValidation(PortInfoUpdateJsonIn updatePortInfo) throws ValidateException {
		String fname = "checkValidateion";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(updateDeviceInfo=%s) - start", fname, updatePortInfo));
		}

		if (BaseValidate.checkNull(updatePortInfo)) {
			throw new ValidateException(String.format(ErrorMessage.IS_NULL, "parameter"));
		}
		if (StringUtils.isBlank(updatePortInfo.getDeviceName())) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "deviceName"));
		}
		if (StringUtils.isBlank(updatePortInfo.getPortName())) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "portName"));
		}

		PortInfo newPortInfo = updatePortInfo.getParams();
		if (BaseValidate.checkNull(newPortInfo)) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "params"));
		}
		if (! BaseValidate.checkNull(newPortInfo.getDeviceName())) {
			throw new ValidateException(String.format(ErrorMessage.IS_NOT_NULL, "params.deviceName"));
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
