package ool.com.ofpm.business;

import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.PortJsonIn;

public interface PortBusiness {
	public BaseResponse createPort(
			PortJsonIn params);

	public BaseResponse deletePort(
			PortJsonIn params);

	public BaseResponse updatePort(
			PortJsonIn params);
}
