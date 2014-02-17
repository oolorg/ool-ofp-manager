package ool.com.ofpm.validate;

import ool.com.ofpm.json.LogicalTopology.LogicalLink;

public class LogicalLinkValidate {
	public static void checkValidation(LogicalLink link) throws ValidateException {
		if(link == null) throw new ValidateException("parameter is null/empty");
		if(link.getDeviceName() == null) throw new ValidateException("LogicalLink: deviceName is null/empty");
		if(link.getDeviceName().size() != 2) throw new ValidateException("LogicalLink: deviceNameは2つでなければなりません");
	}
}
