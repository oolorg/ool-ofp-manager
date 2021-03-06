package ool.com.ofpm.validate.topology.physical;

import static ool.com.constants.ErrorMessage.*;
import static ool.com.constants.OfpmDefinition.*;

import java.util.List;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.device.PortData;
import ool.com.ofpm.json.topology.physical.DisconnectPhysicalLinksJsonIn;
import ool.com.ofpm.json.topology.physical.PhysicalLink;
import ool.com.ofpm.validate.common.BaseValidate;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class DisconnectPhysicalLinksJsonInValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(DisconnectPhysicalLinksJsonInValidate.class);

	public void checkValidation(DisconnectPhysicalLinksJsonIn disconnectPhysicalLink) throws ValidateException {
		final String fname = "checkValidateion";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(disconnectPhysicalLink=%s) - start", fname, disconnectPhysicalLink));
		}

		if (BaseValidate.checkNull(disconnectPhysicalLink)) {
			throw new ValidateException(String.format(IS_NULL, "Input parameter"));
		}

		List<PhysicalLink> links = disconnectPhysicalLink.getLinks();
		if (BaseValidate.checkNull(links)) {
			throw new ValidateException(String.format(IS_NULL, "links"));
		}
		for (int i = 0; i < links.size(); i++) {
			String msgLinks = "links[" + i + "]";
			PhysicalLink physicalLink = links.get(i);
			if (BaseValidate.checkNull(physicalLink)) {
				throw new ValidateException(String.format(IS_NULL, msgLinks));
			}
			List<PortData> ports = physicalLink.getLink();
			if (BaseValidate.checkNull(ports)) {
				throw new ValidateException(String.format(IS_NULL, msgLinks + ".link"));
			}
			if (ports.size() != COLLECT_NUMBER_OF_DEVICE_NAMES_IN_LINK) {
				throw new ValidateException(String.format(INVALID_PARAMETER, "Length of " + msgLinks + ".link"));
			}
			for (int pi = 0; pi < ports.size(); pi++) {
				String msgLink = msgLinks + ".link[" + pi + "]";
				PortData port = ports.get(pi);
				if (BaseValidate.checkNull(port)) {
					throw new ValidateException(String.format(IS_NULL,  msgLink));
				}
				if (StringUtils.isBlank(port.getDeviceName())) {
					throw new ValidateException(String.format(IS_BLANK, msgLink + ".deviceName:" + port.getDeviceName()));
				}
				if (StringUtils.isBlank(port.getPortName())) {
					throw new ValidateException(String.format(IS_BLANK, msgLink + ".portName" + port.getPortName()));
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
