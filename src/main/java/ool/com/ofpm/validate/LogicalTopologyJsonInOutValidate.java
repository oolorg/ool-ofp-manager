package ool.com.ofpm.validate;

import ool.com.ofpm.json.LogicalTopologyJsonInOut;

import org.apache.log4j.Logger;

public class LogicalTopologyJsonInOutValidate extends BaseValidate {
	private static final Logger logger = Logger.getLogger(LogicalTopologyJsonInOutValidate.class);

	public void checkValidation(LogicalTopologyJsonInOut params) throws ValidateException {

		if(params == null) throw new ValidateException("パラメータはnullです");
		if(params.getResult() == null) throw new ValidateException("Resultがnullです");
	}
}
