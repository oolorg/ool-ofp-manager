package ool.com.ofpm.validate;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;



public class CommonValidate {
	private static Logger logger = Logger.getLogger(CommonValidate.class);

	public void checkDeviceNameArray(String[] deviceNames) throws ValidateException {
		String fname = "checkDeviceNameArray";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(deviceNames=%s) - start", fname, deviceNames));

		// TODO テナント処理を実装しなきゃ
		for(int dni = 0; dni < deviceNames.length; dni++) {
			if(StringUtils.isBlank(deviceNames[dni])) throw new ValidateException("There is blank DeviceName");
			for(int ci = dni + 1; ci < deviceNames.length; ci++) {
				if(deviceNames[dni].equals(deviceNames[ci])) throw new ValidateException(String.format("Find overlapped DeviceName: %s", deviceNames[dni]));
			}
		}

		if(logger.isDebugEnabled()) logger.debug(String.format("%s() - end", fname));
	}
}
