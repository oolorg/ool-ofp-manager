package ool.com.ofpm.validate;

import ool.com.ofpm.json.DeviceJsonIn;


public class DeviceJsonValidate extends BaseValidate {
	public static boolean paramsValidation(DeviceJsonIn params) {
		if(params == null) return false;
		if(params.getKey() == null) return false;
		if(params.getType() == null) return false;
		// TODO ここは比較する文字列の種類SwitchとかServerとか限定すべきか要相談
		return true;
	}
}
