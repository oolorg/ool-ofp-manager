package ool.com.ofpm.business;

public interface LogicalBusiness {
	public String getLogicalTopology(String deviceNames, String tokenId);

	public String updateLogicalTopology(String requestedTopologyJson);
}
