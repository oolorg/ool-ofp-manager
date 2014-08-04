/**
 * @author OOL 1131080355959
 * @date 2014/02/17
 * @TODO
 */
package ool.com.orientdb.client;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * @author 1131080355959
 *
 */
public interface Dao {
	
	/**
	 * set connectionUtilsJdbc
	 * @param utils
	 */
	void setConnectionUtilsJdbc(ConnectionUtilsJdbc utils);

	void close() throws SQLException;

	/**
	 * get rid of device from deviceName
	 * @param deviceName
	 * @return rid(device)
	 */
	String getDeviceRid(String deviceName) throws SQLException;

	/**
	 * get device info from deviceName
	 * @param deviceName
	 * @return ODocument
	 */
	ODocument getDeviceInfo(String deviceName) throws SQLException;

	/**
	 * get Connected device to device of rid
	 * @param deviceRid
	 * @return List<ODocument>
	 */
	List<ODocument> getConnectedDevice(String deviceRid) throws SQLException;

	/**
	 * get rid of port list
	 * @param deviceRid
	 * @param switchName
	 * @return rid of port list
	 */
	List<String> getPatchPortRidList(String deviceRid) throws SQLException;

	/**
	 *
	 * @param patchPortPair
	 * @return true:contain false:not contain
	 */
	boolean isContainsPatchWiring(List<String> patchPortPair) throws SQLException;

	/**
	 *
	 * @param deviceRidList
	 * @return
	 */
	ODocument getShortestPath(List<String> deviceRidList) throws SQLException;

	/**
	 *
	 * @param portRidList
	 */
	void insertPatchWiring(List<String> portRidList, String parentRid, List<String> deviceNameList) throws SQLException;

	/**
	 *
	 * @param deviceNameList two
	 * @return portRidList two
	 */
	List<Map<String, String>> getPortRidPatchWiring(List<String> deviceNameList) throws SQLException;

	/**
	 *
	 * @param deviceNameList
	 */
	void deleteRecordPatchWiring(List<String> deviceNameList) throws SQLException;

	/**
	 *
	 * @param rid
	 * @return portInfo
	 */
	ODocument getPortInfo(String rid) throws SQLException;

	/**
	 *
	 * @param rid
	 * @return portInfo
	 */
	ODocument getPortInfo(String name, String deviceName) throws SQLException;

	/**
	 *
	 * @param rid
	 * @return portInfo
	 */
	ODocument getPortInfo(int number, String deviceName) throws SQLException;

	/**
	 * @param inRid
	 * @param outRid
	 * @return
	 * @throws SQLException
	 */
	ODocument getLinkInfo(String outRid, String inRid) throws SQLException;

	/**
	 *
	 * @param weight
	 * @param portRid
	 * @param patchRid
	 */
	void updateLinkWeight(int weight, String portRid, String patchRid) throws SQLException;

	/**
	 * get patch Connected device to deviceName
	 * @param deviceName
	 * @return List<String>
	 */
	List<List<String>> getPatchConnectedDevice() throws SQLException;

	/**
	 *
	 * @param name
	 * @param type
	 * @param ofpFlag
	 * @return
	 * @throws SQLException
	 */
	int createNodeInfo(String name, String type, String datapathId, String ofcIp) throws SQLException;

	/**
	 * @param key
	 * @param name
	 * @param ofpFlag
	 * @return
	 * @throws SQLException
	 */
	int updateNodeInfo(String key, String name, String datapathId, String ofcIp) throws SQLException;

	/**
	 * @param portName
	 * @param portNumber
	 * @param deviceName
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	int createPortInfo(String portName, int portNumber, String deviceName) throws SQLException;

	/**
	 * @param key
	 * @param portName
	 * @param portNumber
	 * @param deviceName
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	int updatePortInfo(String keyPortName, String keyDeviceName, String portName, int portNumber) throws SQLException;

	/**
	 * @param inRid
	 * @param outRid
	 * @return
	 * @throws SQLException
	 */
	int createLinkInfo(String outRid, String inRid) throws SQLException;

	/**
	 * @param inRid
	 * @param outRid
	 * @return
	 * @throws SQLException
	 */
	int deleteLinkInfo(String outRid, String inRid) throws SQLException;

	/**
	 * @param deviceRid
	 * @return
	 * @throws SQLException
	 */
	List<ODocument> getConnectedLinks(String deviceRid) throws SQLException;

	/**
	 * @param portName
	 * @param deviceName
	 * @return
	 * @throws SQLException
	 */
	int deletePortInfo(String portName, String deviceName) throws SQLException;

	/**
	 * @param portRid
	 * @return
	 * @throws SQLException
	 */
	boolean isConnectedPatchWiring(String portRid) throws SQLException;

	/**
	 * @param deviceName
	 * @return
	 * @throws SQLException
	 */
	int deleteDeviceInfo(String deviceName) throws SQLException;

	/**
	 * @param deviceName
	 * @return
	 * @throws SQLException
	 */
	boolean isContainsPatchWiring(String deviceName) throws SQLException;

	/**
	 * @param rid
	 * @return
	 * @throws SQLException
	 */
	boolean isPatched(String rid) throws SQLException;

	/**
	 * @param deviceName
	 * @return
	 * @throws SQLException
	 */
	List<List<String>> getPatchConnectedDevice(String deviceName) throws SQLException;

	/**
	 * @param deviceName
	 * @return portRID List
	 * @throws SQLException
	 */
	List<ODocument> getPortList(String deviceName) throws SQLException;

	/**
	 * @param deviceName
	 * @param deviceType
	 * @param ofpFlag
	 * @return
	 * @throws SQLException
	 */
	List<ODocument> getDeviceList(String deviceName, String deviceType, String ofpFlag) throws SQLException;

	/**
	 * get device name from datapathid
	 * @param datapathId
	 * @return device name
	 * @throws SQLException failed sql
	 */
	String getDeviceNameFromDatapathId(Connection conn, String datapathId) throws SQLException;

	/**
	 * get port RID from deviceName and portNumber
	 * @param deviceName
	 * @param portNumber
	 * @return port Rid
	 * @throws SQLException
	 */
	String getPortRidFromDeviceNamePortNumber(String deviceName, int portNumber) throws SQLException;

	/**
	 *
	 * @param outRid
	 * @param inRid
	 * @param band
	 * @param used
	 * @return
	 * @throws SQLException
	 */
	int createLinkInfo(String outRid, String inRid, int band, int used) throws SQLException;




	/*********************************************************************************
	 * ----------------------------------------------------------------------------- *
	 *                               JDBC
	 * ----------------------------------------------------------------------------- *
	 *********************************************************************************/

	/**
	 * Get link-list that  is connected to other devices port.
	 * The link is correspond to LAN-cable or SPF-cable.
	 * @param devName
	 * @return
	 * @throws SQLException
	 */
	List<Map<String, Object>> getCableLinksFromDeviceName(Connection conn, String deviceName) throws SQLException;

	/**
	 * Get patchWiring-list that is connected to other devices.
	 * @param devName
	 * @return
	 * @throws SQLException
	 */
	List<Map<String, Object>> getPatchWiringsFromDeviceName(Connection conn, String deviceName) throws SQLException;

	/**
	 * Get patchWiring-list from devices port.
	 * @param deviceName
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	List<Map<String, Object>> getPatchWiringsFromDeviceNamePortName(Connection conn, String deviceName, String portName) throws SQLException;

	/**
	 * Delete patchWiring-list from devices port.
	 * @param conn
	 * @param deviceName
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	int deletePatchWiring(Connection conn, String deviceName, String portName) throws SQLException;


	/**
	 * Get link-list that is connected to other devices port from ports rid.
	 * The link is correspond to LAN-cable or SPF-cable.
	 * @param conn
	 * @param portRid
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> getCableLinkFromPortRid(Connection conn, String portRid) throws SQLException;

	/**
	 * Modify used-value of cable-link that include ports-rid.
	 * Calbe-link represent LAN-Cable, SFP-Cable.
	 * @param conn
	 * @param portRid
	 * @param newUsed
	 * @throws SQLException
	 */
	void updateCableLinkUsedFromPortRid(Connection conn, String portRid, long newUsed) throws SQLException;

	/**
	 * Get port-to-port path that computed by dijkstra.
	 * @param conn
	 * @param ridA
	 * @param ridZ
	 * @return
	 * @throws SQLException
	 */
	List<Map<String, Object>> getShortestPath(Connection conn, String ridA, String ridZ) throws SQLException;

	/**
	 * Insert patch-wiring infromation into db.
	 * @param Connection conn
	 * @param ofpRid RID of of-patch switch.
	 * @param in RID of of-patchs in port.
	 * @param out RID of of-patchs out port.
	 * @param inDeviceName
	 * @param inPortName
	 * @param outDeviceName
	 * @param outPortName
	 * @throws SQLException
	 */
	int insertPatchWiring(Connection conn, String ofpRid, String in, String out, String inDeviceName, String inPortName, String outDeviceName, String outPortName) throws SQLException;


	/*================ |1_2 3_4 5_6 7_8   RS-485 |
	 *  NodeInfo I/F   |[_] [_] [_] [_]  _______ |
	 *================ |[_]_[_]_[_]_[_]__\_::::_/| */
	/**
	 * Get DeviceInfo from device name.
	 * @param conn
	 * @param deviceName
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> getNodeInfoFromDeviceName(Connection conn, String deviceName) throws SQLException;

	/**
	 * Get DeviceInfo from devices rid.
	 * @param conn
	 * @param ofpRid
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> getNodeInfoFromDeviceRid(Connection conn, String ofpRid) throws SQLException;

	/**
	 * Create DeviceInfo.
	 * @param conn
	 * @param deviceName
	 * @param deviceType
	 * @param datapathId
	 * @param ofcIp
	 * @return
	 */
	int createNodeInfo(Connection conn, String deviceName, String deviceType, String datapathId, String ofcIp) throws SQLException;

	/**
	 * Update DeviceInfo.
	 * @param conn
	 * @param keyDeviceName current device name.
	 * @param deviceName new device name
	 * @param datapathId new datapath id
	 * @param ofcIp new openflow controller ip
	 * @return
	 * @throws SQLException
	 */
	int updateNodeInfo(Connection conn, String keyDeviceName, String deviceName, String datapathId, String ofcIp) throws SQLException;


	/*================ | _|    |_ |
	 *  PortInfo I/F   ||        ||
	 *================ ||__====__|| */
	/**
	 * Create PortInfo.
	 * @param conn
	 * @param portName
	 * @param portNumber
	 * @param deviceName
	 * @return
	 */
	int createPortInfo(Connection conn, String portName, int portNumber, String deviceName) throws SQLException;

	/**
	 * Get port info from device name and port name.
	 * @param conn
	 * @param deviceName
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> getPortInfoFromPortName(Connection conn, String deviceName, String portName) throws SQLException;

}
