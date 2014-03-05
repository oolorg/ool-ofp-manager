/**
 * @author OOL 1131080355959
 * @date 2014/02/06
 * @TODO
 */
package ool.com.ofpm.client;

import java.util.List;

import ool.com.ofpm.json.BaseNode;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.DeviceInfoJsonInOut;
import ool.com.ofpm.json.LogicalTopology.LogicalLink;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;
import ool.com.ofpm.json.PatchLinkJsonIn;
import ool.com.ofpm.json.PhysicalLinkJsonInOut;
import ool.com.ofpm.json.PortInfoJsonInOut;

/**
 * @author 1131080355959
 *
 */
public interface GraphDBClient {
	// GraphDBから指定された装置間の接続情報（物理・論理含む）を取得
//	public GraphDBResult getLinks(String[] deviceName);

	// About device information
	public BaseResponse nodeCreate(DeviceInfoJsonInOut newDevice) throws GraphDBClientException;

	// About port information
	public BaseResponse portCreate(PortInfoJsonInOut portInfo) throws GraphDBClientException;

	// About Physical-link
	public BaseResponse connectPhysicalLink(PhysicalLinkJsonInOut physicalLink) throws GraphDBClientException;
	public BaseResponse disconnectPhysicalLink(PhysicalLinkJsonInOut physicalLink) throws GraphDBClientException;

	// LogicalLink
	public LogicalTopologyJsonInOut getLogicalTopology(List<BaseNode> nodes) throws GraphDBClientException;
	public PatchLinkJsonIn     addLogicalLink(LogicalLink link) throws GraphDBClientException;
	public PatchLinkJsonIn     delLogicalLink(LogicalLink link) throws GraphDBClientException;
}
