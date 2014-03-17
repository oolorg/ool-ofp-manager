package ool.com.ofpm.validate;

import java.util.List;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.PhysicalLinkJsonIn;
import ool.com.ofpm.json.PortInfo;
import ool.com.ofpm.utils.Definition;
import ool.com.ofpm.utils.ErrorMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class PhysicalLinkJsonInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(PhysicalLinkJsonInValidate.class);

	public void checkValidation(PhysicalLinkJsonIn physicalLink) throws ValidateException {
		String fname = "checkValidateion";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(physicalLink=%s) - start", fname, physicalLink));
		}

		if (BaseValidate.checkNull(physicalLink)) {
			throw new ValidateException(String.format(ErrorMessage.IS_NULL, "Input parameter"));
		}
		List<PortInfo> ports = physicalLink.getLink();
		if (BaseValidate.checkNull(ports)) {
			throw new ValidateException(String.format(ErrorMessage.IS_NULL, "link"));
		}
		if (ports.size() != Definition.COLLECT_NUMBER_OF_DEVICE_NAMES_IN_LINK) {
			throw new ValidateException(String.format(ErrorMessage.INVALID_PARAMETER, "Length of link"));
		}
		for (int pi = 0; pi < ports.size(); pi++) {
			PortInfo port = ports.get(pi);
			if (BaseValidate.checkNull(port)) {
				throw new ValidateException(String.format(ErrorMessage.IS_NULL,  "link[" + pi + "]"));
			}
			if (StringUtils.isBlank(port.getDeviceName())) {
				throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "link[" + pi + "].deviceName"));
			}
			if (StringUtils.isBlank(port.getPortName())) {
				throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "link[" + pi + "].portName"));
			}
		}
		if (ports.get(0).equals(ports.get(1))) {
			PortInfo port = ports.get(0);
			throw new ValidateException(String.format(ErrorMessage.THERE_ARE_OVERLAPPED, port.getDeviceName() + '[' + port.getPortName() + ']'));
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
