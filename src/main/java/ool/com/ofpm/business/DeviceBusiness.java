package ool.com.ofpm.business;

import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.DeviceJsonIn;

public interface DeviceBusiness {
	public BaseResponse createDevice(
			DeviceJsonIn params);

	public BaseResponse deleteDevice(
			DeviceJsonIn params);

	public BaseResponse updateDevice(
			DeviceJsonIn params);
}
