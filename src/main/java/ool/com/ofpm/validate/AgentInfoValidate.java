package ool.com.ofpm.validate;

import java.util.List;

import static ool.com.constants.ErrorMessage.*;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.ofc.AgentInfo;
import ool.com.ofpm.json.ofc.AgentInfo.SwitchInfo;
import ool.com.ofpm.validate.common.BaseValidate;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class AgentInfoValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(AgentInfoValidate.class);

	/**
	 *
	 * @param
	 * @return
	 * @param agentInfo
	 * @throws ValidateException
	 */
	public void checkValidation(AgentInfo agentInfo) throws ValidateException {
		String fname = "checkValidateion";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(agentInfo=%s) - start", fname, agentInfo));
		}

		if (BaseValidate.checkNull(agentInfo)) {
			throw new ValidateException(String.format(IS_NULL, "Input parameter"));
		}
		if (StringUtils.isBlank(agentInfo.getIp())) {
			throw new ValidateException(String.format(IS_BLANK, "ip"));
		}

		List<SwitchInfo> switchInfos = agentInfo.getSwitches();
		for (int si = 0; si < switchInfos.size(); si++) {
			SwitchInfo switchInfo = switchInfos.get(si);

			if (StringUtils.isBlank(switchInfo.getDeviceName())) {
				throw new ValidateException(String.format(IS_BLANK, "switchies[" + si + "].deviceName"));
			}
			if (StringUtils.isBlank(switchInfo.getIp())) {
				throw new ValidateException(String.format(IS_BLANK, "switchies[" + si + "].ip"));
			}
			if (StringUtils.isBlank(switchInfo.getOfcUrl())) {
				throw new ValidateException(String.format(IS_BLANK, "switchies[" + si + "].ofcUrl"));
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
