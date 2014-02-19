package ool.com.ofpm.validate;

import ool.com.ofpm.json.LogicalTopologyJsonInOut;

public class LogicalTopologyJsonInOutValidate extends BaseValidate {
	public void checkValidation(LogicalTopologyJsonInOut params) throws ValidateException {
		if(params == null) {
			throw new ValidateException("パラメータはnullです");
		}
		if(params.getResult() == null) {
			throw new ValidateException("Resultがnullです");
		}
	}
}
