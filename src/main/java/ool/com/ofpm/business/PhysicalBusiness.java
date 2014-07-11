package ool.com.ofpm.business;

public interface PhysicalBusiness {
/*
	public String getPhysicalTopology(String deviceNames, String tokenId);

	public String updatePhysicalTopology(String requestedTopologyJson);
*/

	public String connectPhysicalLink(String physicalLinkJson);

	public String disconnectPhysicalLink(String physicalLinkJson);
}
