package ool.com.ofpm.validate;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class CommonValidate {
	private static Logger logger = Logger.getLogger(CommonValidate.class);

	//テナントのチェックを実装する必要がある
	public void checkDeviceNameArray(String[] params) throws ValidateException {
		String fname = "checkDeviceNameArray";
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("%s([\"%s\"]) - start", fname, StringUtils.join(params, ",")));
		}

		if(params == null) throw new ValidateException("Parameter is null");
		for(String deviceName : params) {
			if(StringUtils.isBlank(deviceName)) {
				throw new ValidateException(String.format("Parameter include blank: %s", StringUtils.join(params, ",")));
			}
		}

		if(logger.isDebugEnabled()) {
			logger.debug(String.format("%s - end", fname));
		}
	}
}
