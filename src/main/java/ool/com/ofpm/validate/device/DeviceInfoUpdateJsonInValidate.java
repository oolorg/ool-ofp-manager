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
			throw new ValidateException(String.format(IS_BLANK, "deviceName"));
		}

		if (BaseValidate.checkNull(newDeviceInfo)) {
			throw new ValidateException(String.format(IS_BLANK, "parameter"));
		}
		if (!BaseValidate.checkNull(newDeviceInfo.getDeviceType())) {
			throw new ValidateException(String.format(IS_NOT_NULL, "parameter.deviceType"));
		}
		if (StringUtils.isBlank(newDeviceInfo.getDeviceName())) {
			throw new ValidateException(String.format(IS_NULL,  "parameter.deviceName"));
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
