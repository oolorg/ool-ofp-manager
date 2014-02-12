/**
 * @author OOL 1131080355959
 * @date 2014/02/06
 * @TODO
 */
package ool.com.ofpm.client;

import ool.com.ofpm.json.GraphDBResult;
import ool.com.ofpm.json.PhysicalRequestIn.NodeStatus;
import ool.com.ofpm.json.PhysicalRequestIn.NodeType;

/**
 * @author 1131080355959
 *
 */
public interface GraphDBClient {
	void exec();

	// GraphDBから指定された装置間の接続情報（物理・論理含む）を取得
//	public GraphDBResult getLinks(String[] deviceName);

	// GraphDBに登録される装置に関する操作
	public GraphDBResult appendDevice(String deviceName, NodeType type);
	public GraphDBResult deleteDevice(String deviceName, NodeType type);
	public GraphDBResult updateDevice(String deviceName, NodeType type, NodeStatus status);

	public GraphDBResult appendPort(String deviceName, NodeType type, String portName);
	public GraphDBResult deletePort(String deviceName, NodeType type, String portName);
	public GraphDBResult updatePort(String deviceName, NodeType type, String portName, NodeStatus status);

	// GraphDBに登録されている装置間の物理配線情報に関する操作
//	public GraphDBResult appendPhysicalLink();
//	public GraphDBResult deletePhysicalLink();
//	public GraphDBResult updatePhysicalLink();

//	public GraphDBResult updateLogicalLink(String srcDeviceName, String dstDeviceName);
}
