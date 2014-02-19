package ool.com.ofpm.validate;

import ool.com.ofpm.json.DeviceJsonIn;

import org.apache.log4j.Logger;


public class DeviceJsonValidate extends BaseValidate {
	private static Logger logger = Logger.getLogger(LogicalTopologyValidate.class);

	public static void paramsValidation(DeviceJsonIn params) {
		String fname = "paramsValidateion";
		if(logger.isDebugEnabled()) logger.debug(String.format("%s(params=%s) - start", fname, params));

		// TODO: Exception返せよ
		if(params == null) return;
		if(params.getKey() == null) return;
		if(params.getType() == null) return;
		// TODO ここは比較する文字列の種類SwitchとかServerとか限定すべきか要相談

		if(logger.isDebugEnabled()) logger.debug(String.format("%s() - end", fname));
	}
}
