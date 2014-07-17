package ool.com.ofpm.validate.device;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.device.DeviceInfoUpdateJsonIn;
import ool.com.ofpm.validate.common.BaseValidate;
import ool.com.util.Definition;
import ool.com.util.ErrorMessage;

import org.apache.commons.lang.ArrayUtils;
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
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "deviceName"));
		}

		if (BaseValidate.checkNull(newDeviceInfo)) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "parameter"));
		}
		if (!BaseValidate.checkNull(newDeviceInfo.getDeviceType())) {
			throw new ValidateException(String.format(ErrorMessage.IS_NOT_NULL, "parameter.deviceType"));
		}
		String ofpFlag = newDeviceInfo.getOfpFlag();
		if (StringUtils.isBlank(newDeviceInfo.getDeviceName()) && BaseValidate.checkNull(ofpFlag)) {
			throw new ValidateException(String.format(ErrorMessage.IS_NULL,  "parameter.deviceName and parameter.ofpFlag"));
		}
		if (! StringUtils.isBlank(ofpFlag)) {
			if (! ArrayUtils.contains(Definition.ENABLE_OFP_FLAGS, ofpFlag)) {
				throw new ValidateException(String.format(ErrorMessage.INVALID_PARAMETER,  "parameter.ofpFlag=" + ofpFlag));
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
