package ool.com.ofpm.validate;

import ool.com.ofpm.json.BaseNode;
import ool.com.ofpm.json.LogicalTopology;
import ool.com.ofpm.json.LogicalTopology.LogicalLink;

public class LogicalTopologyValidate extends BaseValidate {
	public void checkValidation(LogicalTopology params) throws ValidateException {
		if(params == null) {
			throw new ValidateException("arguments is null/empty");
		}
		if(BaseValidate.checkNull(params.getNodes())) {
			throw new ValidateException("nodes is null/empty");
		}
		if(BaseValidate.checkNull(params.getLinks())) {
			throw new ValidateException("links is null/empty");
		}
		for(BaseNode node : params.getNodes()) {
			if(node.getDeviceName() == null) throw new ValidateException("Node: Device name is null/empty");
			if(node.getDeviceName() == "") throw new ValidateException("Node: device name is ''/empty");
		}
		for(LogicalLink link: params.getLinks()) {
			if(link.getDeviceName() == null) throw new ValidateException("LogicalLink: Device name is null/empty");
		}
	}
	public void checkValidationGET(LogicalTopology params) throws ValidateException {
		if(params == null) {
			throw new ValidateException("Nullはパラメータとして有効ではありません");
		}
		if(BaseValidate.checkNull(params.getNodes())) {
			throw new ValidateException("nodesは必須なパラメータです");
		}
	}
}
