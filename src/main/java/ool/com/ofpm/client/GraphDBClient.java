/**
 * @author OOL 1131080355959
 * @date 2014/02/06
 * @TODO
 */
package ool.com.ofpm.client;

import java.util.List;

import ool.com.ofpm.json.BaseNode;
import ool.com.ofpm.json.LogicalTopology.LogicalLink;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;
import ool.com.ofpm.json.PatchLinkJsonIn;

/**
 * @author 1131080355959
 *
 */
public interface GraphDBClient {
	// GraphDBから指定された装置間の接続情報（物理・論理含む）を取得
//	public GraphDBResult getLinks(String[] deviceName);

	// GraphDBに登録される装置に関する操作
//	public GraphDBResult appendDevice(String deviceName, NodeType type);
//	public GraphDBResult deleteDevice(String deviceName, NodeType type);
//	public GraphDBResult updateDevice(String deviceName, NodeType type, NodeStatus status);
//
//	public GraphDBResult appendPort(String deviceName, NodeType type, String portName);
//	public GraphDBResult deletePort(String deviceName, NodeType type, String portName);
//	public GraphDBResult updatePort(String deviceName, NodeType type, String portName, NodeStatus status);

	// 論理配線やPatchの設定
	public LogicalTopologyJsonInOut getLogicalTopology(List<BaseNode> nodes) throws GraphDBClientException;
	public PatchLinkJsonIn     addLogicalLink(LogicalLink link) throws GraphDBClientException;
	public PatchLinkJsonIn     delLogicalLink(LogicalLink link) throws GraphDBClientException;

	// GraphDBに登録されている装置間の物理配線情報に関する操作
//	public GraphDBResult appendPhysicalLink();
//	public GraphDBResult deletePhysicalLink();
//	public GraphDBResult updatePhysicalLink();

//	public GraphDBResult updateLogicalLink(String srcDeviceName, String dstDeviceName);
}
