package ool.com.ofpm.validate;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.DeviceInfoCreateJsonIn;
import ool.com.ofpm.utils.Definition;
import ool.com.ofpm.utils.ErrorMessage;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class DeviceInfoCreateJsonInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(DeviceInfoCreateJsonInValidate.class);

	public void checkValidation(DeviceInfoCreateJsonIn deviceInfo) throws ValidateException {
		String fname = "checkValidateion";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(deviceInfoJson=%s) - start", fname, (deviceInfo == null)? "null" : deviceInfo.toJson()));
		}

		if (BaseValidate.checkNull(deviceInfo)) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "Input parameter"));
		}
		if (StringUtils.isBlank(deviceInfo.getDeviceName())) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "deviceName"));
		}
		String deviceType = deviceInfo.getDeviceType();
		if (StringUtils.isBlank(deviceType)) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "deviceType"));
		}
		if (! ArrayUtils.contains(Definition.ENABLE_DEVICE_TYPES, deviceType)) {
			throw new ValidateException(String.format(ErrorMessage.INVALID_PARAMETER, "deviceType=" + deviceType));
		}
		String ofpFlag = deviceInfo.getOfpFlag();
		if (! StringUtils.isBlank(ofpFlag)) {
			if (! ArrayUtils.contains(Definition.ENABLE_OFP_FLAGS, ofpFlag)) {
				throw new ValidateException(String.format(ErrorMessage.INVALID_PARAMETER, "ofpFlag=" + ofpFlag));
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
