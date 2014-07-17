package ool.com.ofpm.validate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ool.com.ofpm.exception.ValidateException;
import ool.com.ofpm.json.common.Node;
import ool.com.ofpm.json.topology.logical.LogicalLink;
import ool.com.ofpm.json.topology.logical.LogicalTopology;
import ool.com.util.Definition;
import ool.com.util.ErrorMessage;

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
			throw new ValidateException(String.format(ErrorMessage.IS_NULL, "LogicalTopology"));
		}

		List<Node> nodes = logicalTopology.getNodes();
		List<LogicalLink> links = logicalTopology.getLinks();
		if (BaseValidate.checkNull(nodes)) {
			throw new ValidateException(String.format(ErrorMessage.IS_NULL, "nodes"));
		}
		if (BaseValidate.checkNull(links)) {
			throw new ValidateException(String.format(ErrorMessage.IS_NULL, "links"));
		}

		Set<Node> noOverlapsNodes = new HashSet<Node>(nodes);
		if (noOverlapsNodes.size() != nodes.size()) {
			throw new ValidateException(String.format(ErrorMessage.THERE_ARE_OVERLAPPED, "node"));
		}
		Set<LogicalLink> noOverlapsLinks = new HashSet<LogicalLink>(links);
		if (noOverlapsLinks.size() != links.size()) {
			throw new ValidateException(String.format(ErrorMessage.THERE_ARE_OVERLAPPED, "link"));
		}

		for (Node node : nodes) {
			if (StringUtils.isBlank(node.getDeviceName())) {
				throw new ValidateException(String.format(ErrorMessage.FIND_NULL, "node", "deviceName"));
			}
		}
		Node searchingNode = new Node();
		for (LogicalLink link : links) {
			List<String> deviceNames = link.getDeviceName();
			if (BaseValidate.checkNull(link.getDeviceName())) {
				throw new ValidateException(String.format(ErrorMessage.IS_NULL, "deviceName in link"));
			}
			if (deviceNames.size() != Definition.COLLECT_NUMBER_OF_DEVICE_NAMES_IN_LINK) {
				throw new ValidateException(String.format(ErrorMessage.INVALID_PARAMETER, "number of deviceName in link"));
			}

			for (String deviceName : deviceNames) {
				searchingNode.setDeviceName(deviceName);
				if (!nodes.contains(searchingNode)) {
					throw new ValidateException(String.format(ErrorMessage.IS_NOT_INCLUDED, deviceName, "nodes"));
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}

	public void checkStringBlank(String param) throws ValidateException {
		String fname = "checkStringBlank";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(param=%s) - start", fname, param));
		}
		if (StringUtils.isBlank(param)) {
			throw new ValidateException(String.format(ErrorMessage.IS_BLANK, "parameter"));
		}
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
