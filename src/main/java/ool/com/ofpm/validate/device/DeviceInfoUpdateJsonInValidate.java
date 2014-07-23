package ool.com.ofpm.validate.device;

import static ool.com.constants.ErrorMessage.*;
import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.device.DeviceInfoUpdateJsonIn;
import ool.com.ofpm.validate.common.BaseValidate;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class DeviceInfoUpdateJsonInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(DeviceInfoUpdateJsonInValidate.class);

	/**
	 * @param newDeviceInfo
	 * @throws ValidateException
	 */
	public void checkValidation(String deviceName, DeviceInfoUpdateJsonIn newDeviceInfo) throws ValidateException {
		String fname = "checkValidateion";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(newDeviceInfo=%s) - start", fname, newDeviceInfo));
		}

		if (StringUtils.isBlank(deviceName)) {
			throw new ValidateException(String.format(IS_BLANK, "Target deviceName"));
		}

		if (BaseValidate.checkNull(newDeviceInfo)) {
			throw new ValidateException(String.format(IS_BLANK, "parameter"));
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
