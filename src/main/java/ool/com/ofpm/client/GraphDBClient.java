/**
 * @author OOL 1131080355959
 * @date 2014/02/06
 * @TODO
 */
package ool.com.ofpm.client;

import java.util.List;

import ool.com.ofpm.exception.GraphDBClientException;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.DeviceInfoCreateJsonIn;
import ool.com.ofpm.json.DeviceInfoUpdateJsonIn;
import ool.com.ofpm.json.GraphDBPatchLinkJsonRes;
import ool.com.ofpm.json.LogicalTopology.LogicalLink;
import ool.com.ofpm.json.LogicalTopologyGetJsonOut;
import ool.com.ofpm.json.Node;
import ool.com.ofpm.json.PhysicalLinkJsonIn;
import ool.com.ofpm.json.PortInfoCreateJsonIn;
import ool.com.ofpm.json.PortInfoUpdateJsonIn;

public interface GraphDBClient {
	/* Device information */
	public BaseResponse nodeCreate(DeviceInfoCreateJsonIn deviceInfo) throws GraphDBClientException;
	public BaseResponse nodeUpdate(DeviceInfoUpdateJsonIn updateDeviceInfo) throws GraphDBClientException;
	public BaseResponse nodeDelete(String deviceName) throws GraphDBClientException;

	/* Port information */
	public BaseResponse portCreate(PortInfoCreateJsonIn portInfo) throws GraphDBClientException;
	public BaseResponse portUpdate(PortInfoUpdateJsonIn updatePortInfo) throws GraphDBClientException;
	public BaseResponse portDelete(String portName, String deviceName) throws GraphDBClientException;

	/* Physical topology */
	public BaseResponse connectPhysicalLink(PhysicalLinkJsonIn physicalLink) throws GraphDBClientException;
	public BaseResponse disconnectPhysicalLink(PhysicalLinkJsonIn physicalLink) throws GraphDBClientException;

	/* Logical topology */
	public LogicalTopologyGetJsonOut getLogicalTopology(List<Node> nodes) throws GraphDBClientException;
	public GraphDBPatchLinkJsonRes addLogicalLink(LogicalLink link) throws GraphDBClientException;
	public GraphDBPatchLinkJsonRes delLogicalLink(LogicalLink link) throws GraphDBClientException;
}
