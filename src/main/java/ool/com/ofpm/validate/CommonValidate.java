package ool.com.ofpm.validate;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;



public class CommonValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(CommonValidate.class);

	public void checkDeviceNamesCSV(String deviceNamesCSV) throws ValidateException {
		String fname = "checkDeviceNameCSV";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(deviceNamesCSV=%s) - start", fname, deviceNamesCSV));

		if(StringUtils.isBlank(deviceNamesCSV)) {
			throw new ValidateException("No there is DeviceNames.");
		}

		if(logger.isDebugEnabled()) logger.debug(String.format("%s() - end", fname));
	}

	public void checkDeviceNameArray(String[] deviceNames) throws ValidateException {
		String fname = "checkDeviceNameArray";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(deviceNames=%s) - start", fname, deviceNames));

		// TODO テナント処理を実装しなきゃ
		for(int dni = 0; dni < deviceNames.length; dni++) {
			if(StringUtils.isBlank(deviceNames[dni])) {
				throw new ValidateException("There is blank DeviceName");
			}
			if(deviceNames[dni].charAt(0) == ' ') {
				throw new ValidateException("Ungrantable the DeviceName that begins with space ' '.");
			}
			if(deviceNames[dni].charAt(deviceNames[dni].length() - 1) == ' ') {
				throw new ValidateException("Ungrantable the DeviceName that ending with space ' '.");
			}
			for(int ci = dni + 1; ci < deviceNames.length; ci++) {
				if(deviceNames[dni].equals(deviceNames[ci])) {
					throw new ValidateException(String.format("Find overlapped DeviceName: %s", deviceNames[dni]));
				}
			}
		}

		if(logger.isDebugEnabled()) logger.debug(String.format("%s() - end", fname));
	}
}
