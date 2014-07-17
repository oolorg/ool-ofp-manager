/**
 * @author OOL 1134380013430
 * @date 2014/04/23
 * @TODO TODO
 */
package ool.com.ofpm.validate;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.ncs.NetworkConfigSetupperIn;
import ool.com.util.ErrorMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author 1134380013430
 *
 */
public class NetworkConfigSetupperInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(DeviceInfoCreateJsonInValidate.class);

	public static void checkValidation(NetworkConfigSetupperIn networkConfigSetupperIn) throws ValidateException {
		String fname = "checkValidateion";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(networkConfigSetupperIn=%s) - start", fname, (networkConfigSetupperIn == null)? "null" : networkConfigSetupperIn));
		}

		if (BaseValidate.checkNull(networkConfigSetupperIn)) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "Input parameter"));
		}
		if (StringUtils.isBlank(networkConfigSetupperIn.getTokenId())) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "tokenId"));
		}
		if (BaseValidate.checkNull(networkConfigSetupperIn.getParams())) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "params"));
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

}
