package ool.com.ofpm.validate;

import java.util.List;

import ool.com.ofpm.json.BaseNode;
import ool.com.ofpm.json.LogicalTopology;
import ool.com.ofpm.json.LogicalTopology.LogicalLink;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class LogicalTopologyValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(LogicalTopologyValidate.class);

	// TODO: テナントのチェックが必要です
	public void checkValidationRequestIn(LogicalTopology params) throws ValidateException {
		String fname = "checkValidation";
		if(logger.isDebugEnabled()) {
			logger.debug(String.format("%s(%s) - start", fname, params));
		}

		if(params == null) {
			throw new ValidateException("Parameter is null");
		}
		if(BaseValidate.checkNull(params.getNodes())) {
			throw new ValidateException("Nodes is null");
		}
		if(BaseValidate.checkNull(params.getLinks())) {
			throw new ValidateException("Links is null");
		}
		List<BaseNode> nodes = params.getNodes();
		for(BaseNode node : nodes) {
			if(StringUtils.isBlank(node.getDeviceName())) throw new ValidateException("Find Node whos deviceName is null");
		}

		BaseNode searchingNode = new BaseNode();
		for(LogicalLink link: params.getLinks()) {
			List<String> deviceNames = link.getDeviceName();
			if(deviceNames == null) throw new ValidateException("DeviceName in Link is null");
			if(deviceNames.size() != 2) throw new ValidateException("DeviceName length is not 2");

			for(String deviceName : deviceNames) {
				searchingNode.setDeviceName(deviceName);
				if(!nodes.contains(searchingNode)) throw new ValidateException("Find Link with Node who not included nodes");
			}
		}

		if(logger.isDebugEnabled()) {
			logger.debug(String.format("%s() - end", fname));
		}
	}
}
