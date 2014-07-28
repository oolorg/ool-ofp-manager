package ool.com.ofpm.business;

public interface LogicalBusiness {
	public String getLogicalTopology(String deviceNames, String tokenId);

	public String updateLogicalTopology(String requestedTopologyJson);
	
	/**
	 * request set flow to each OFC
	 * @param requestedData ex.datapathId, inPort, srcMac, dstMac
	 * @return result set flow(json)
	 */
	public String setFlow(String requestedData);
}
