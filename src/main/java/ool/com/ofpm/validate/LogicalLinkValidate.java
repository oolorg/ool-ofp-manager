package ool.com.ofpm.validate;

import ool.com.odbcl.json.LogicalTopology.LogicalLink;
import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.utils.Definition;
import ool.com.ofpm.utils.ErrorMessage;

import org.apache.log4j.Logger;

public class LogicalLinkValidate {
	private static Logger logger = Logger.getLogger(LogicalLinkValidate.class);

	public static void checkValidation(LogicalLink link) throws ValidateException {
		String fname = "checkValidation";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(LogicalLink=%s) - start", fname, link));
		}

		if (BaseValidate.checkNull(link)) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "link"));
		}
		if (BaseValidate.checkNull(link.getDeviceName())) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK,  "deviceName:LogicalLink"));
		}
		if (link.getDeviceName().size() != Definition.COLLECT_NUMBER_OF_DEVICE_NAMES_IN_LINK) {
			throw new ValidateException(String.format(ErrorMessage.INVALID_PARAMETER, "Number of deviceName:LogicalLink"));
		}

		if (logger.isDebugEnabled()) {
    		logger.debug(String.format("checkValidation(ret=%s) - end "));
    	}
	}
}
