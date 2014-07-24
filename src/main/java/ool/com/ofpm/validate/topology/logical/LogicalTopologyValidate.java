package ool.com.ofpm.validate.topology.logical;

import static ool.com.constants.ErrorMessage.*;
import static ool.com.constants.OfpmDefinition.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.device.Node;
import ool.com.ofpm.json.device.PortData;
import ool.com.ofpm.json.topology.logical.LogicalLink;
import ool.com.ofpm.json.topology.logical.LogicalTopology;
import ool.com.ofpm.json.topology.logical.LogicalTopology.OfpConDeviceInfo;
import ool.com.ofpm.json.topology.logical.LogicalTopology.OfpConPortInfo;
import ool.com.ofpm.utils.OFPMUtils;
import ool.com.ofpm.validate.common.BaseValidate;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class LogicalTopologyValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(LogicalTopologyValidate.class);

	public void checkValidationRequestIn(LogicalTopology logicalTopology) throws ValidateException {
		String fname = "checkValidation";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(%s) - start", fname, logicalTopology));
		}

		if (BaseValidate.checkNull(logicalTopology)) {
			throw new ValidateException(String.format(IS_NULL, "LogicalTopology"));
		}

		List<OfpConDeviceInfo> nodes = logicalTopology.getNodes();
		List<LogicalLink> links = logicalTopology.getLinks();
		if (BaseValidate.checkNull(nodes)) {
			throw new ValidateException(String.format(IS_NULL, "nodes"));
		}
		if (BaseValidate.checkNull(links)) {
			throw new ValidateException(String.format(IS_NULL, "links"));
		}

		Set<Node> noOverlapsNodes = new HashSet<Node>(nodes);
		if (noOverlapsNodes.size() != nodes.size()) {
			throw new ValidateException(String.format(THERE_ARE_OVERLAPPED, "node"));
		}
		Set<LogicalLink> noOverlapsLinks = new HashSet<LogicalLink>(links);
		if (noOverlapsLinks.size() != links.size()) {
			throw new ValidateException(String.format(THERE_ARE_OVERLAPPED, "link"));
		}

		for (int ni = 0; ni < nodes.size(); ni++) {
			OfpConDeviceInfo device = nodes.get(ni);
			if (BaseValidate.checkNull(device)) {
				throw new ValidateException(String.format(IS_NULL, "nodes[" + ni + "]"));
			}
			if (StringUtils.isBlank(device.getDeviceName())) {
				throw new ValidateException(String.format(IS_NULL, "nodes[" + ni + "].deviceName"));
			}

			List<OfpConPortInfo> ports = device.getPorts();
			if (BaseValidate.checkNull(ports) || ports.isEmpty()) {
				throw new ValidateException(String.format(IS_NULL, "nodes[" + device.getDeviceName() + "].ports"));
			}
			for (int pi = 0; pi < ports.size(); pi++) {
				OfpConPortInfo port = ports.get(pi);
				if (BaseValidate.checkNull(port)) {
					throw new ValidateException(String.format(IS_NULL, "nodes[" + device.getDeviceName() + "].ports[" + pi + "]"));
				}
				if (StringUtils.isBlank(port.getPortName())) {
					throw new ValidateException(String.format(IS_NULL, "nodes[" + device.getDeviceName() + "].ports[" + pi + "].portName"));
				}
			}
		}
		String nowParamStr = null;
		for (int i = 0; i < links.size(); i++) {
			nowParamStr = "links[" + i + "]";
			LogicalLink link = links.get(i);
			List<PortData> ports = link.getLink();
			if (BaseValidate.checkNull(ports)) {
				throw new ValidateException(String.format(IS_NULL, nowParamStr + ".link"));
			}
			if (ports.size() != COLLECT_NUMBER_OF_DEVICE_NAMES_IN_LINK) {
				throw new ValidateException(String.format(INVALID_PARAMETER, nowParamStr + ".link"));
			}

			for (int pi = 0; pi < ports.size(); pi++) {
				PortData port = ports.get(pi);
				nowParamStr ="links[" + i + "].link[" + pi + "]";

				if (StringUtils.isBlank(port.getDeviceName())) {
					throw new ValidateException(String.format(IS_BLANK, nowParamStr + ".deviceName"));
				}
				if (StringUtils.isBlank(port.getPortName())) {
					throw new ValidateException(String.format(IS_BLANK, nowParamStr + ".portName"));
				}

				if (!OFPMUtils.nodesContainsPort(nodes, port)) {
					throw new ValidateException(String.format(NOT_FOUND, nowParamStr + " in nodes"));
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

}