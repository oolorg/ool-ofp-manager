package ool.com.ofpm.business;

import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.LogicalTopology;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;

public interface LogicalBusiness {
	public LogicalTopologyJsonInOut getLogicalTopology(String[] deviceNames);

	public BaseResponse updateLogicalTopology(LogicalTopology requestedTopology);
}
