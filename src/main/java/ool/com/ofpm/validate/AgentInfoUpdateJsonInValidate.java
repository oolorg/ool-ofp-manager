package ool.com.ofpm.validate;

import java.util.List;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.AgentInfoUpdateJsonIn;
import ool.com.ofpm.json.AgentInfoUpdateJsonIn.SwitchInfo;
import ool.com.ofpm.utils.ErrorMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class AgentInfoUpdateJsonInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(AgentInfoUpdateJsonInValidate.class);

	/**
	 * @param updateDeviceInfo
	 * @throws ValidateException
	 */
	public void checkValidation(AgentInfoUpdateJsonIn updateAgentInfo) throws ValidateException {
		String fname = "checkValidateion";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(updateAgentInfo=%s) - start", fname, updateAgentInfo));
		}

		if (BaseValidate.checkNull(updateAgentInfo)) {
			throw new ValidateException(String.format(ErrorMessage.IS_NULL, "Input parameter"));
		}
		if (StringUtils.isBlank(updateAgentInfo.getIp())) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "ip"));
		}

		List<SwitchInfo> switchInfos = updateAgentInfo.getSwitchies();
		for (int si = 0; si < switchInfos.size(); si++) {
			SwitchInfo switchInfo = switchInfos.get(si);

			if (StringUtils.isBlank(switchInfo.getDeviceName())) {
				throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "switchies[" + si + "].deviceName"));
			}
			if (StringUtils.isBlank(switchInfo.getIp())) {
				throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "switchies[" + si + "].ip"));
			}
			if (StringUtils.isBlank(switchInfo.getOfcUrl())) {
				throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "switchies[" + si + "].ofcUrl"));
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
