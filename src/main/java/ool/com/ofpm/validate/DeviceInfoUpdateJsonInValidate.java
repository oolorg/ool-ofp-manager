package ool.com.ofpm.validate;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.DeviceInfo;
import ool.com.ofpm.json.DeviceInfoUpdateJsonIn;
import ool.com.ofpm.utils.Definition;
import ool.com.ofpm.utils.ErrorMessage;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class DeviceInfoUpdateJsonInValidate {
	private static Logger logger = Logger.getLogger(DeviceInfoUpdateJsonInValidate.class);

	/**
	 * @param updateDeviceInfo
	 * @throws ValidateException
	 */
	public void checkValidation(DeviceInfoUpdateJsonIn updateDeviceInfo) throws ValidateException {
		String fname = "checkValidateion";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(updateDeviceInfo=%s) - start", fname, updateDeviceInfo));
		}

		if (BaseValidate.checkNull(updateDeviceInfo)) {
			throw new ValidateException(String.format(ErrorMessage.IS_NULL, "Input parameter"));
		}
		if (StringUtils.isBlank(updateDeviceInfo.getDeviceName())) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "deviceName"));
		}

		DeviceInfo newDeviceInfo = updateDeviceInfo.getParams();
		if (BaseValidate.checkNull(newDeviceInfo)) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "params"));
		}
		if (!BaseValidate.checkNull(newDeviceInfo.getDeviceType())) {
			throw new ValidateException(String.format(ErrorMessage.IS_NOT_NULL, "params.deviceType"));
		}
		String ofpFlag = newDeviceInfo.getOfpFlag();
		if (StringUtils.isBlank(newDeviceInfo.getDeviceName()) && BaseValidate.checkNull(ofpFlag)) {
			throw new ValidateException(String.format(ErrorMessage.IS_NULL,  "params.deviceName and params.ofpFlag"));
		}
		if (! StringUtils.isBlank(ofpFlag)) {
			if (! ArrayUtils.contains(Definition.ENABLE_OFP_FLAGS, ofpFlag)) {
				throw new ValidateException(String.format(ErrorMessage.INVALID_PARAMETER,  "params.ofpFlag=" + ofpFlag));
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
