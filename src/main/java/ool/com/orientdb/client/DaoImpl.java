/**
 * @author OOL 1131080355959
 * @date 2014/02/17
 * @TODO
 */
package ool.com.orientdb.client;

import static ool.com.constants.ErrorMessage.*;
import static ool.com.constants.OrientDBDefinition.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
//import com.orientechnologies.orient.jdbc.OrientJdbcConnection;
import com.orientechnologies.orient.jdbc.OrientJdbcConnection;

/**
 * @author 1131080355959
 *
 */
public class DaoImpl implements Dao {

	private static final Logger logger = Logger.getLogger(DaoImpl.class);

	protected ConnectionUtils utils = null;
	protected ODatabaseDocumentTx database = null;
	protected List<ODocument> documents = null;

	public DaoImpl(ConnectionUtils utils) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("DaoImpl(utils=%s) - start", utils));
		}
		this.utils = utils;
		init();
		if (logger.isDebugEnabled()){
			logger.debug("DaoImpl() - end");
		}
	}

	synchronized private void init() throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug("init() - start");
		}
		database = utils.getDatabase();
		if (logger.isDebugEnabled()){
			logger.debug("init() - end");
		}
	}

	synchronized public void close() throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug("close() - start");
		}
		if (database != null && !database.isClosed()) {
			utils.close(database);
		}
		if (logger.isDebugEnabled()){
			logger.debug("close() - end");
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getDeviceRid(java.lang.String)
	 */
	@Override
	public String getDeviceRid(String deviceName) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("getDeviceRid(deviceName=%s) - start", deviceName));
		}
		try {
			String query = String.format(SQL_GET_DEVICE, deviceName);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			documents = utils.query(database, query);
			if (logger.isDebugEnabled()){
				logger.debug(String.format("getDeviceRid(ret=%s) - end", documents.get(0).getIdentity().toString()));
			}
			if (documents.size() > 0) {
				return documents.get(0).getIdentity().toString();
			} else {
				return null;
			}
		} catch (IndexOutOfBoundsException ioobe) {
			throw new SQLException(String.format(NOT_FOUND, deviceName));
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getDeviceInfo(java.lang.String)
	 */
	@Override
	public ODocument getDeviceInfo(String deviceName) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("getDeviceInfo(deviceName=%s) - start", deviceName));
		}
		try {
			String query = String.format(SQL_GET_DEVICE, deviceName);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			documents = utils.query(database, query);
			if (logger.isDebugEnabled()){
				logger.debug(String.format("getDeviceInfo(ret=%s) - end", documents.get(0)));
			}
			return documents.get(0);
		} catch (IndexOutOfBoundsException e) {
			throw new SQLException(String.format(NOT_FOUND, deviceName), e);
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getConnectedDevice(java.lang.String)
	 */
	@Override
	public List<ODocument> getConnectedDevice(String deviceRid) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("getConnectedDevice(deviceRid=%s) - start", deviceRid));
		}
		try {
			String query = String.format(SQL_GET_CONNECTED_NODE, deviceRid);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			documents = utils.query(database, query);
			if (logger.isDebugEnabled()){
				logger.debug(String.format("getConnectedDevice(ret=%s) - end", documents));
			}
			return documents;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getPatchPortRidList(java.lang.String, java.lang.String)
	 */
	@Override
	public List<String> getPatchPortRidList(String deviceRid) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("getPatchPortRidList(deviceRid=%s) - start", deviceRid));
		}
		try {
			String query = String.format(SQL_GET_PATCHPORT_RID, deviceRid);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			documents = utils.query(database, query);
			List<String> patchPortList = new ArrayList<String>();
			for (ODocument document : documents) {
				patchPortList.add(document.getIdentity().toString());
			}
			if (logger.isDebugEnabled()){
				logger.debug(String.format("getPatchPortRidList(ret=%s) - end", patchPortList));
			}
			return patchPortList;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#isContainsPatchWiring(java.util.List)
	 */
	@Override
	public boolean isContainsPatchWiring(List<String> patchPortPair) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("isContainsPatchWiring(patchPortPair=%s) - start", patchPortPair));
		}
		try {
			String query = String.format(SQL_GET_PATCH_WIRING, patchPortPair.get(0), patchPortPair.get(1));
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			documents = utils.query(database, query);
			boolean ret = (documents.size() > 0) ? true : false;
			if (logger.isDebugEnabled()){
				logger.debug(String.format("isContainsPatchWiring(ret=%s) - end", ret));
			}
			return ret;
		} catch (IndexOutOfBoundsException e) {
			return false;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getShortestPath(java.util.List)
	 */
	@Override
	public ODocument getShortestPath(List<String> deviceRidList) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("getShortestPath(deviceRidList=%s) - start", deviceRidList));
		}
		try {
			String query = String.format(SQL_GET_DIJKSTRA_PATH, deviceRidList.get(0), deviceRidList.get(1));
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			documents = utils.query(database, query);
			if (logger.isDebugEnabled()){
				logger.debug(String.format("getShortestPath(ret=%s) - end", documents.get(0)));
			}
			return documents.get(0);
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#insertPatchWiring(java.util.List)
	 */
	@Override
	synchronized public void insertPatchWiring(List<String> portRidList,
			String parentRid, List<String> deviceNameList) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("insertPatchWiring(portRidList=%s, parentRid=%s, deviceNameList=%s) - start", portRidList, parentRid, deviceNameList));
		}
		try {
			if (getPortRidPatchWiring(deviceNameList).size() > 0) {
				return; //duplicate error
			}
			String out = portRidList.get(0);
			String in = portRidList.get(1);
			String outDevName = deviceNameList.get(0);
			String inDevName = deviceNameList.get(1);
			String query = String.format(SQL_INSERT_PATCH_WIRING, out, in, parentRid, outDevName, inDevName);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();
			out = portRidList.get(1);
			in = portRidList.get(0);
			outDevName = deviceNameList.get(1);
			inDevName = deviceNameList.get(0);
			query = String.format(SQL_INSERT_PATCH_WIRING, out, in, parentRid, outDevName, inDevName);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();
			if (logger.isDebugEnabled()){
				logger.debug("insertPatchWiring() - end");
			}
			return;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getPortRidPatchWiring(java.util.List)
	 */
	@Override
	public List<Map<String, String>> getPortRidPatchWiring(List<String> deviceNameList) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("getPortRidPatchWiring(deviceNameList=%s) - start", deviceNameList));
		}
		try {
			List<Map<String, String>> portRidPairList = new ArrayList<Map<String, String>>();
			Map<String, String> portRidPair = new HashMap<String, String>();
			String query = String.format(SQL_GET_PATCH_WIRING2, deviceNameList.get(0), deviceNameList.get(1));
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			documents = utils.query(database, query);
			if (documents.size() > 0) {
				for (ODocument document : documents) {
					String out = document.field("out").toString();
					String in = document.field("in").toString();
					String parent = document.field("parent").toString();
					portRidPair.put("out", out.split("\\{")[0].substring("port".length()));
					portRidPair.put("in", in.split("\\{")[0].substring("port".length()));
					portRidPair.put("parent", parent.split("\\{")[0].substring("node".length()));
					portRidPairList.add(portRidPair);
				}
			}
			if (logger.isDebugEnabled()){
				logger.debug(String.format("getPortRidPatchWiring(ret=%s) - end", portRidPairList));
			}
			return portRidPairList;
		} catch (IndexOutOfBoundsException e) {
			return new ArrayList<Map<String, String>>();
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#deleteRecordPathcWirng(java.util.List)
	 */
	@Override
	synchronized public void deleteRecordPatchWiring(List<String> deviceNameList) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("deleteRecordPatchWiring(deviceNameList=%s) - start", deviceNameList));
		}
		try {
			String query = String.format(SQL_DELETE_PATCH_WIRING, deviceNameList.get(0), deviceNameList.get(1));
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();
			query = String.format(SQL_DELETE_PATCH_WIRING, deviceNameList.get(1), deviceNameList.get(0));
			database.command(new OCommandSQL(query)).execute();
			if (logger.isDebugEnabled()){
				logger.debug("deleteRecordPatchWiring() - end");
			}
			return;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getPortInfo(java.lang.String)
	 */
	@Override
	public ODocument getPortInfo(String rid) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("getPortInfo(rid=%s) - start", rid));
		}
		try {
			String query = String.format(SQL_GET_PORT, rid);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			documents = utils.query(database, query);
			if (logger.isDebugEnabled()){
				logger.debug(String.format("getPortInfo(ret=%s) - end", documents.get(0)));
			}
			return documents.get(0);
		} catch (IndexOutOfBoundsException ioobe) {
			throw new SQLException(String.format(NOT_FOUND, rid), ioobe);
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getPortInfo(java.lang.String, java.lang.String)
	 */
	@Override
	public ODocument getPortInfo(String name, String deviceName) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("getPortInfo(name=%s, deviceName=%s) - start", name, deviceName));
		}
		try {
			String query = String.format(SQL_GET_PORT_INFO, name, deviceName);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			documents = utils.query(database, query);
			if (logger.isDebugEnabled()){
				logger.debug(String.format("getPortInfo(ret=%s) - end", documents.get(0)));
			}
			return documents.get(0);
		} catch (IndexOutOfBoundsException ioobe) {
			throw new SQLException(String.format(NOT_FOUND, name + "," + deviceName), ioobe);
		}  catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getPortInfo(java.lang.String, java.lang.String)
	 */
	@Override
	public ODocument getPortInfo(int number, String deviceName) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("getPortInfo(number=%s, deviceName=%s) - start", number, deviceName));
		}
		try {
			String query = String.format(SQL_GET_PORT_INFO2, number, deviceName);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			documents = utils.query(database, query);
			if (logger.isDebugEnabled()){
				logger.debug(String.format("getPortInfo(ret=%s) - end", documents.get(0)));
			}
			return documents.get(0);
		} catch (IndexOutOfBoundsException ioobe) {
			throw new SQLException(String.format(NOT_FOUND, deviceName + "[" + number + "]"), ioobe);
		}  catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#updateLinkWeight(int, java.lang.String, java.lang.String)
	 */
	@Override
	synchronized public void updateLinkWeight(int weight, String portRid, String patchRid) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("updateLinkWeight(weight=%s, portRid=%s, patchRid=%s) - start", weight, portRid, patchRid));
		}
		try {
			String query = String.format(SQL_UPDATE_WEIGHT_TO_LINK, weight, portRid, patchRid);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();
			query = String.format(SQL_UPDATE_WEIGHT_TO_LINK, weight, patchRid, portRid);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();
			if (logger.isDebugEnabled()){
				logger.debug("updateLinkWeight() - end");
			}
			return;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getPatchConnectedDevice(java.lang.String)
	 */
	@Override
	public List<List<String>> getPatchConnectedDevice() throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug("getPatchConnectedDevice() - start");
		}
		try {
			List<List<String>> deviceNameList = new ArrayList<List<String>>();
			String query = SQL_GET_PATCH_CONNECTED_NODE;
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			documents = utils.query(database, query);
			for (ODocument document: documents) {
				List<String> connectNode = new ArrayList<String>();
				connectNode.add(document.field("outDeviceName").toString());
				connectNode.add(document.field("inDeviceName").toString());
				deviceNameList.add(connectNode);
			}
			if (logger.isDebugEnabled()){
				logger.debug(String.format("getPatchConnectedDevice(ret=%s) - end", deviceNameList));
			}
			return deviceNameList;
		} catch (IndexOutOfBoundsException e) {
			return new ArrayList<List<String>>();
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#createNodeInfo(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public int createNodeInfo(String name, String type, String datapathId, String ofcIp) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("createNodeInfo(name=%s, type=%s, datapathId=%s, ofcIp=%s) - start", name, type, datapathId, ofcIp));
		}
		try {
			try {
				ODocument document = getDeviceInfo(name);
				return DB_RESPONSE_STATUS_EXIST; //duplicate error
			} catch (SQLException se) {
				if (se.getCause() == null) {
					throw se;
				}
			}
			String query = String.format(SQL_INSERT_NODE, name, type, datapathId, ofcIp);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();
			if (logger.isDebugEnabled()){
				logger.debug("createNodeInfo() - end");
			}
			return DB_RESPONSE_STATUS_OK;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#createPortInfo(java.lang.String, int, java.lang.String, java.lang.String)
	 */
	@Override
	public int createPortInfo(String portName, int portNumber, String deviceName) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("createPortInfo(portName=%s, portNumber=%s, deviceName=%s) - start",
					portName, portNumber, deviceName));
		}
		try {
			String nodeRid = "";
			try {
				ODocument document = getDeviceInfo(deviceName);
				nodeRid = document.getIdentity().toString();
			} catch(SQLException se) {
				if (se.getCause() == null) {
					throw se;
				} else {
					return DB_RESPONSE_STATUS_NOT_FOUND;
				}
			}
			try {
				getPortInfo(portName, deviceName);
				return DB_RESPONSE_STATUS_EXIST; //duplicate error
			} catch(SQLException se) {
				if (se.getCause() == null) {
					throw se;
				}
			}
			try {
				getPortInfo(portNumber, deviceName);
				return DB_RESPONSE_STATUS_EXIST; //duplicate error
			} catch(SQLException se2) {
				if (se2.getCause() == null) {
					throw se2;
				}
			}
			String query = String.format(SQL_INSERT_PORT, portName, portNumber, deviceName);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();

			// get rid
			ODocument document = getPortInfo(portName, deviceName);
			String portRid = document.getIdentity().toString();
			createLinkInfo(nodeRid, portRid);
			createLinkInfo(portRid, nodeRid);
			if (logger.isDebugEnabled()){
				logger.debug("createPortInfo() - end");
			}
			return DB_RESPONSE_STATUS_OK;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getLinkInfo(java.lang.String, java.lang.String)
	 */
	@Override
	public ODocument getLinkInfo(String outRid, String inRid) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("getLinkInfo(inRid=%s, outRid=%s) - start", inRid, outRid));
		}
		try {
			String query = String.format(SQL_GET_LINK, outRid, inRid);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			documents = utils.query(database, query);
			if (logger.isDebugEnabled()){
				logger.debug(String.format("getLinkInfo(ret=%s) - end", documents.get(0)));
			}
			return documents.get(0);
		} catch (IndexOutOfBoundsException ioobe) {
			throw new SQLException(String.format(NOT_FOUND, "Link"), ioobe);
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#createLinkInfo(java.lang.String, java.lang.String)
	 */
	@Override
	public int createLinkInfo(String outRid, String inRid) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("createLinkInfo(inRid=%s, outRid=%s) - start", inRid, outRid));
		}
		try {
			try {
				getLinkInfo(outRid, inRid);
				return DB_RESPONSE_STATUS_EXIST; //duplicate error
			}
			catch(SQLException se){
				if (se.getCause() == null) {
					throw se;
				}
			}
			String query = String.format(SQL_INSERT_LINK, outRid, inRid);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();
			if (logger.isDebugEnabled()){
				logger.debug("createLinkInfo() - end");
			}
			return DB_RESPONSE_STATUS_OK;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}
	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#createLinkInfo(java.lang.String, java.lang.String, int band, int used)
	 */
	@Override
	public int createLinkInfo(String outRid, String inRid, int band, int used) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("createLinkInfo(inRid=%s, outRid=%s, band=%s, used=%s) - start", inRid, outRid, band, used));
		}
		try {
			try {
				getLinkInfo(outRid, inRid);
				return DB_RESPONSE_STATUS_EXIST; //duplicate error
			}
			catch(SQLException se){
				if (se.getCause() == null) {
					throw se;
				}
			}
			String query = String.format(SQL_INSERT_LINK, outRid, inRid, band, used);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();
			if (logger.isDebugEnabled()){
				logger.debug("createLinkInfo() - end");
			}
			return DB_RESPONSE_STATUS_OK;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#deleteLinkInfo(java.lang.String, java.lang.String)
	 */
	@Override
	public int deleteLinkInfo(String outRid, String inRid) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("deleteLinkInfo(inRid=%s, outRid=%s) - start", inRid, outRid));
		}
		try {
			try {
				ODocument document = getLinkInfo(outRid, inRid);
			}
			catch(SQLException se){
				if (se.getCause() == null) {
					throw se;
				} else {
					return DB_RESPONSE_STATUS_NOT_FOUND; //duplicate error
				}
			}
			String query = String.format(SQL_DELETE_LINK, outRid, inRid);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();
			if (logger.isDebugEnabled()){
				logger.debug("deleteLinkInfo() - end");
			}
			return DB_RESPONSE_STATUS_OK;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#updateNodeInfo(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public int updateNodeInfo(String key, String name, String datapathId, String ofcIp) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("updateNodeInfo(key=%s, name=%s, ofpFlag=%s, ofcIp=%s) - start", key, name, datapathId, ofcIp));
		}
		try {
			String nodeRid = "";
			ODocument document = null;
			try {
				document = getDeviceInfo(key);
				nodeRid = document.getIdentity().toString();
			} catch (SQLException se) {
				if (se.getCause() == null) {
					throw se;
				} else {
					return DB_RESPONSE_STATUS_NOT_FOUND; //not found error
				}
			}
			try  {
				if(StringUtils.isBlank(name)) {
					name = document.field("name").toString();
				} else {
					getDeviceInfo(name);
					return DB_RESPONSE_STATUS_EXIST;
				}
			} catch (SQLException se) {
				if (se.getCause() == null) {
					throw se;
				}
			}
			if(StringUtils.isBlank(datapathId)) {
				datapathId = document.field("datapathId");
			}
			if(StringUtils.isBlank(ofcIp)) {
				ofcIp = document.field("ofcIp").toString();
			}
			String query = String.format(SQL_UPDATE_NODE, name, datapathId, ofcIp, nodeRid);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();

			query = String.format(SQL_UPDATE_PORT_DEVICE_NAME, name, key);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();

			query = String.format(SQL_UPDATE_PATCH_WIRING_IN_DEVICE, name, key);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();

			query = String.format(SQL_UPDATE_PATCH_WIRING_OUT_DEVICE, name, key);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();
			if (logger.isDebugEnabled()){
				logger.debug("updateNodeInfo() - end");
			}
			return DB_RESPONSE_STATUS_OK;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#updateNodeInfo(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public int updatePortInfo(String keyPortName, String keyDeviceName, String portName, int portNumber) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("updatePortInfo(keyPortName=%s, keyDeviceName=%s, portName=%s, portNumber=%s) - start", keyPortName, keyDeviceName, portName, portNumber));
		}
		try {
			String portRid = "";
			ODocument document = null;
			try {
				document = getPortInfo(keyPortName, keyDeviceName);
				portRid = document.getIdentity().toString();
			} catch (SQLException se) {
				if (se.getCause() == null) {
					throw se;
				} else {
					return DB_RESPONSE_STATUS_NOT_FOUND; //not found error
				}
			}
			try {
				if (StringUtils.isBlank(portName)) {
					portName = document.field("name");
				} else {
					getPortInfo(portName, keyDeviceName);
					return DB_RESPONSE_STATUS_EXIST;
				}
			} catch (SQLException se) {
				if (se.getCause() == null) {
					throw se;
				}
			}

			try {
				if (0 == portNumber) {
					portNumber = document.field("number");
				} else {
					document = getDeviceInfo(keyDeviceName);
					String type = document.field("type").toString();
					if (type.equals(NODE_TYPE_SWITCH) || type.equals(NODE_TYPE_LEAF) || type.equals(NODE_TYPE_SPINE)) {
						getPortInfo(portNumber, keyDeviceName);
						return DB_RESPONSE_STATUS_EXIST;
					}
				}
			} catch (SQLException se) {
				if (se.getCause() == null) {
					throw se;
				}
			}

			String query = String.format(SQL_UPDATE_PORT, portName, portNumber, portRid);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();
			if (logger.isDebugEnabled()){
				logger.debug("updatePortInfo() - end");
			}
			return DB_RESPONSE_STATUS_OK;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getConnectedDevice(java.lang.String)
	 */
	@Override
	public List<ODocument> getConnectedLinks(String deviceRid) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("getNeighborPort(deviceRid=%s) - start", deviceRid));
		}
		try {
			String query = String.format(SQL_GET_CONNECTED_LINK, deviceRid);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			documents = utils.query(database, query);
			if (logger.isDebugEnabled()){
				logger.debug(String.format("getNeighborPort(ret=%s) - end", documents));
			}
			return documents;
		} catch (IndexOutOfBoundsException ioobe) {
			return new ArrayList<ODocument>();
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#deletePortInfo(java.lang.String, java.lang.String)
	 */
	@Override
	public int deletePortInfo(String portName, String deviceName) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("deletePortInfo(portName=%s, deviceName=%s) - start", portName, deviceName));
		}
		try {
			String portRid = "";
			String type = "";
			try {
				ODocument document = getDeviceInfo(deviceName);
				type = document.field("type").toString();
			} catch(SQLException se) {
				if (se.getCause() == null) {
					throw se;
				} else {
					return DB_RESPONSE_STATUS_NOT_FOUND;
				}
			}
			try {
				ODocument document = getPortInfo(portName, deviceName);
				portRid = document.getIdentity().toString();
			} catch(SQLException se) {
				if (se.getCause() == null) {
					throw se;
				} else {
					return DB_RESPONSE_STATUS_NOT_FOUND;
				}
			}
			if (type.equals(NODE_TYPE_LEAF) || type.equals(NODE_TYPE_SPINE)) {
				if (isConnectedPatchWiring(portRid)) {
					return DB_RESPONSE_STATUS_FORBIDDEN;
				}
			} else {
				List<ODocument> connectedLinks = getConnectedLinks(portRid);
				for (ODocument connectedLink: connectedLinks) {
					ODocument neighborPort = connectedLink.field("out");
					String neighborPortRid = neighborPort.getIdentity().toString();
					if (isConnectedPatchWiring(neighborPortRid)) {
						return DB_RESPONSE_STATUS_FORBIDDEN;
					}
				}
			}

			String query = String.format(SQL_DELETE_LINK_CONNECTED_PORT, portRid, portRid);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();

			query = String.format(SQL_DELETE_PORT, portName, deviceName);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();

			if (logger.isDebugEnabled()){
				logger.debug("deletePortInfo() - end");
			}
			return DB_RESPONSE_STATUS_OK;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#isConnectedPatchWiring(java.lang.String)
	 */
	@Override
	public boolean isConnectedPatchWiring(String portRid) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("isConnectedPatchWiring(portRid=%s) - start", portRid));
		}
		try {
			String query = String.format(SQL_IS_CONNECTED_PATCH_WIRING, portRid, portRid);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}

			List<ODocument> documents = utils.query(database, query);
			boolean ret = (documents.size() > 0) ? true : false;
			if (logger.isDebugEnabled()){
				logger.debug(String.format("isConnectedPatchWiring(ret=%s) - end", documents));
			}
			return ret;
		} catch (IndexOutOfBoundsException e) {
			return false;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#deleteDeviceInfo(java.lang.String)
	 */
	@Override
	public int deleteDeviceInfo(String deviceName) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("deleteDeviceInfo(deviceName=%s) - start", deviceName));
		}
		try {
			String nodeRid = "";
			String type = "";
			try {
				ODocument document = getDeviceInfo(deviceName);
				type = document.field("type").toString();
				nodeRid = document.getIdentity().toString();
			} catch (SQLException sqle) {
				if (sqle.getCause() == null) {
					throw sqle;
				} else {
					return DB_RESPONSE_STATUS_NOT_FOUND;
				}
			}
			if (type.equals(NODE_TYPE_LEAF) || type.equals(NODE_TYPE_SPINE)) {
				if (isPatched(nodeRid)) {
					return DB_RESPONSE_STATUS_FORBIDDEN;
				}
			} else {
				if (isContainsPatchWiring(deviceName)) {
					return DB_RESPONSE_STATUS_FORBIDDEN;
				}
			}
			List<ODocument> connectedLinks;
			connectedLinks = getConnectedLinks(nodeRid);

			String query = "";
			for (ODocument connectedLink : connectedLinks) {
				ODocument neighborPort = connectedLink.field("out");
				String neighborPortRid = neighborPort.getIdentity().toString();
				query = String.format(SQL_DELETE_LINK_CONNECTED_PORT, neighborPortRid, neighborPortRid);
				if (logger.isInfoEnabled()){
					logger.info(String.format("query=%s", query));
				}
				try {
					database.command(new OCommandSQL(query)).execute();
				} catch (Exception sqlException) {
					throw new SQLException(sqlException.getMessage());
				}
			}

			query = String.format(SQL_DELETE_PORT_DEViCE_NAME, deviceName);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			try {
				database.command(new OCommandSQL(query)).execute();
			} catch (Exception sqlException) {
				throw new SQLException(sqlException.getMessage());
			}

			query = String.format(SQL_DELETE_NODE, deviceName);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			database.command(new OCommandSQL(query)).execute();

			if (logger.isDebugEnabled()){
				logger.debug("deleteDeviceInfo() - end");
			}
			return DB_RESPONSE_STATUS_OK;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#isHadPatchWiring(java.lang.String)
	 */
	@Override
	public boolean isPatched(String rid) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("isHadPatchWiring(rid=%s) - start", rid));
		}
		try {
			String query = String.format(SQL_IS_HAD_PATCH_WIRING, rid);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}

			List<ODocument> documents = utils.query(database, query);
			boolean ret = (documents.size() > 0) ? true : false;
			if (logger.isDebugEnabled()){
				logger.debug(String.format("isHadPatchWiring(ret=%s) - end", documents));
			}
			return ret;
		} catch (IndexOutOfBoundsException e) {
			return false;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#isContaintsPatchWiring(java.lang.String)
	 */
	@Override
	public boolean isContainsPatchWiring(String deviceName) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("isContaintsPatchWiring(rid=%s) - start", deviceName));
		}
		try {
			String query = String.format(SQL_IS_CONTAINS_PATCH_WIRING, deviceName, deviceName
					);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}

			List<ODocument> documents = utils.query(database, query);
			boolean ret = (documents.size() > 0) ? true : false;
			if (logger.isDebugEnabled()){
				logger.debug(String.format("isContaintsPatchWiring(ret=%s) - end", documents));
			}
			return ret;
		} catch (IndexOutOfBoundsException e) {
			return false;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getPatchConnectedDevice(java.lang.String)
	 */
	@Override
	public List<List<String>> getPatchConnectedDevice(String deviceName) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug("getPatchConnectedDevice() - start");
		}
		try {
			List<List<String>> deviceNameList = new ArrayList<List<String>>();
			String query = String.format(SQL_GET_PATCH_CONNECTED_DEVICE_NAME, deviceName);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			documents = utils.query(database, query);
			for (ODocument document: documents) {
				List<String> connectNode = new ArrayList<String>();
				connectNode.add(document.field("outDeviceName").toString());
				connectNode.add(document.field("inDeviceName").toString());
				deviceNameList.add(connectNode);
			}
			if (logger.isDebugEnabled()){
				logger.debug(String.format("getPatchConnectedDevice(ret=%s) - end", deviceNameList));
			}
			return deviceNameList;
		} catch (IndexOutOfBoundsException e) {
			return new ArrayList<List<String>>();
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getPortList(java.lang.String)
	 */
	@Override
	public List<ODocument> getPortList(String deviceName) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("getPortList(deviceName=%s) - start", deviceName));
		}
		try {
			String query = String.format(SQL_GET_PORT_LIST, deviceName);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			documents = utils.query(database, query);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("getPortList(ret=%s) - end", documents));
			}
			return documents;
		} catch (IndexOutOfBoundsException ioobe) {
			throw new SQLException(String.format(NOT_FOUND, deviceName), ioobe);
		}  catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getDeviceList(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<ODocument> getDeviceList(String deviceName, String deviceType, String ofpFlag) throws SQLException {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("getDeviceList(deviceName=%s, deviceType=%s, ofpFlag=%s) - start", deviceName, deviceType, ofpFlag));
		}
		try {
			StringBuilder condition = new StringBuilder();
			condition.append("");
			boolean conditionFlag = false;
			if (!StringUtils.isBlank(deviceName)) {
				condition.append(SQL_NODE_KEY_NAME + "=" + "'" + deviceName + "'");
				conditionFlag = true;
			}
			if (!StringUtils.isBlank(deviceType)) {
				if (conditionFlag) {
					condition.append(" and ");
				} else {
					condition.append(" ");
				}
				condition.append(SQL_NODE_KEY_TYPE + "=" + "'" + deviceType + "'");
				conditionFlag = true;
			}
			if (!StringUtils.isBlank(ofpFlag)) {
				if (conditionFlag) {
					condition.append(" and ");
				} else {
					condition.append(" ");
				}
				condition.append(SQL_NODE_KEY_FLAG + "=" + ofpFlag);
				conditionFlag = true;
			}
			if (conditionFlag) {
				condition.insert(0, "where ");
			}
			String query = String.format(SQL_GET_DEVICE_LIST, condition);
			if (logger.isInfoEnabled()){
				logger.info(String.format("query=%s", query));
			}
			documents = utils.query(database, query);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("getDeviceList(ret=%s) - end", documents));
			}
			return documents;
		} catch (IndexOutOfBoundsException ioobe) {
			throw new SQLException(String.format(NOT_FOUND, deviceName), ioobe);
		}  catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getDeviceNameFromDatapathId(java.lang.String)
	 */
	@Override
	public String getDeviceNameFromDatapathId(String datapathId) throws SQLException {
		final String fname = "getDeviceNameFromDatapathId";
		if (logger.isDebugEnabled()){
			logger.debug(String.format("%s(datapathId=%s) - start", fname, datapathId));
		}
		try {
			Properties userInfo = new Properties();
			userInfo.put("user", "admin");
			userInfo.put("password", "admin");
			Connection conn = (OrientJdbcConnection) DriverManager.getConnection("jdbc:orient:remote:172.16.1.177:2480/of-patch", userInfo);
			PreparedStatement pstmt = conn.prepareStatement(SQL_GET_DEVICENAME_FROM_DATAPATHID);
			pstmt.setString(1, datapathId);

			ResultSet rs = pstmt.executeQuery();
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", rs.toString()));
			}
			return rs.getString("@rid");
		} catch (IndexOutOfBoundsException ioobe) {
			throw new SQLException(String.format(NOT_FOUND, datapathId), ioobe);
		}  catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getCableLinks(java.lang.String)
	 */
	@Override
	public List<ODocument> getCableLinks(String devName) {
		/* not implemented yet */
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getPatchWiring(java.lang.String)
	 */
	@Override
	public List<ODocument> getPatchWirings(String devName) {
		/* not implemented yet */
		// TODO Auto-generated method stub
		return null;
	}
}
