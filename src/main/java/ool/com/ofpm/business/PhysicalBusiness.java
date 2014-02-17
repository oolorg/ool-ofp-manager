package ool.com.ofpm.business;

import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.ResultOut;

public interface PhysicalBusiness {
	public ResultOut getPhysicalTopology();

	public BaseResponse updatePhysicalTopology();
}
