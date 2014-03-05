package ool.com.ofpm.validate;

import ool.com.ofpm.json.DeviceInfoJsonInOut;
import ool.com.ofpm.utils.Definition;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


public class DeviceInfoInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(LogicalTopologyValidate.class);

	@Override
	public void checkStringBlank(String value) throws ValidateException {
		String fname = "checkStringBlank";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(value=%s) - start", fname, value));
		if(StringUtils.isBlank(value)) throw new ValidateException("Input string is blank.");

		if(logger.isDebugEnabled()) logger.debug(String.format("%s() - end", fname));
	}

	public void checkValidation(DeviceInfoJsonInOut deviceInfoJson) throws ValidateException {
		String fname = "checkValidateion";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(deviceInfoJson=%s) - start", fname, deviceInfoJson));

		String deviceType = deviceInfoJson.getDeviceType();
		if(BaseValidate.checkNull(deviceInfoJson))                            throw new ValidateException("DeviceInfo is null.");
		if(StringUtils.isBlank(deviceInfoJson.getDeviceName()))               throw new ValidateException("DeviceName is blank or null.");
		if(StringUtils.isBlank(deviceType))                                   throw new ValidateException("Devicetype is blank or null.");
		if(! ArrayUtils.contains(Definition.DEVICE_TYPE_ENABLES, deviceType)) throw new ValidateException("DeviceType must be Server or Switch.");

		if(logger.isDebugEnabled()) logger.debug(String.format("%s() - end", fname));
	}
}
