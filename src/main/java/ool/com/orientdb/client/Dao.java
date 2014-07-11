/**
 * @author OOL 1131080355959
 * @date 2014/02/17
 * @TODO
 */
package ool.com.orientdb.client;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * @author 1131080355959
 *
 */
public interface Dao {

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
	int createNodeInfo(String name, String type, String ofpFlag) throws SQLException;

	/**
	 * @param key
	 * @param name
	 * @param ofpFlag
	 * @return
	 * @throws SQLException
	 */
	int updateNodeInfo(String key, String name, String ofpFlag) throws SQLException;

	/**
	 * @param portName
	 * @param portNumber
	 * @param deviceName
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	int createPortInfo(String portName, int portNumber, String deviceName, String type) throws SQLException;

	/**
	 * @param key
	 * @param portName
	 * @param portNumber
	 * @param deviceName
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	int updatePortInfo(String keyPortName, String keyDeviceName, String portName, int portNumber, String type) throws SQLException;

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

}
