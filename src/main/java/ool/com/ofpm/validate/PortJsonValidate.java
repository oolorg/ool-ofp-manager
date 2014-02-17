package ool.com.ofpm.validate;

import ool.com.ofpm.json.PortJsonIn;

public class PortJsonValidate extends BaseValidate {
	public static boolean paramsValidation(PortJsonIn params) {
		if(params                == null) return false;
		if(params.getKey()       == null) return false;
		if(params.getPort_name() == null) return false;
		return true;
	}
}
