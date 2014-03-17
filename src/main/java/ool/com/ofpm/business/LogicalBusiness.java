package ool.com.ofpm.business;

public interface LogicalBusiness {
	public String getLogicalTopology(String deviceNames);

	public String updateLogicalTopology(String requestedTopologyJson);
}
