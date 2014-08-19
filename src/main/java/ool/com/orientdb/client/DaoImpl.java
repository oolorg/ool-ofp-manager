/**
 * @author OOL 1131080355959
 * @date 2014/02/17
 * @TODO
 */
package ool.com.orientdb.client;

import static ool.com.constants.ErrorMessage.*;
import static ool.com.constants.OrientDBDefinition.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ool.com.ofpm.utils.OFPMUtils;
import ool.com.orientdb.utils.handlers.MapListHandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
//import com.orientechnologies.orient.jdbc.OrientJdbcConnection;

/**
 * @author 1131080355959
 *
 */
public class DaoImpl implements Dao {

	private static final Logger logger = Logger.getLogger(DaoImpl.class);

	protected ConnectionUtils utils = null;
	protected ODatabaseDocumentTx database = null;
	protected List<ODocument> documents = null;

	// jdbc
	protected ConnectionUtilsJdbc utilsJdbc = null;

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

	// jdbc
	public DaoImpl(ConnectionUtilsJdbc utils) {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("DaoImpl(utils=%s) - start", utils));
		}
		this.utilsJdbc = utils;
		if (logger.isDebugEnabled()){
			logger.debug("DaoImpl() - end");
		}
	}

	// default constructor
	public DaoImpl() {
		if (logger.isDebugEnabled()){
			logger.debug(String.format("DaoImpl() - start"));
		}
		if (logger.isDebugEnabled()){
			logger.debug("DaoImpl() - end");
		}
	}

	// connectionUtil setter
	public void setConnectionUtilsJdbc(ConnectionUtilsJdbc utils) {
		this.utilsJdbc = utils;
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
				getDeviceInfo(name);
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
			createLinkInfo(nodeRid, portRid, Integer.MAX_VALUE, Integer.MAX_VALUE);
			createLinkInfo(portRid, nodeRid, Integer.MAX_VALUE, Integer.MAX_VALUE);
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
	 * @see ool.com.orientdb.client.Dao#createLinkInfo(java.lang.String, java.lang.String, int, int)
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
				getLinkInfo(outRid, inRid);
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
			if (OFPMUtils.isNodeTypeOfpSwitch(type)) {
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
			if (OFPMUtils.isNodeTypeOfpSwitch(type)) {
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
	public String getDeviceNameFromDatapathId(Connection conn, String datapathId) throws SQLException {
		final String fname = "getDeviceNameFromDatapathId";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, datapathId=%s) - start", fname, conn, datapathId));
		}
		try {
			List<Map<String, Object>> records = utilsJdbc.query(conn, SQL_GET_DEVICENAME_FROM_DATAPATHID,
                    new MapListHandler(), datapathId);
			if (records.size() <= 0) {
                //throw new Exception(String.format(NOT_FOUND, datapathId));
				// error
			}
			String ret = records.get(0).get("name").toString();
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", ret));
			}
			return ret;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getPortRidFromDeviceNamePortNumber(java.lang.String, int)
	 */
	@Override
	public String getPortRidFromDeviceNamePortNumber(Connection conn, String deviceName, int portNumber) throws SQLException {
		final String fname = "getPortRidFromDeviceNamePortNumber";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(deviceName=%s, portNumber=%s) - start", fname, deviceName, portNumber));
		}
		try {
			List<Map<String, Object>> records = utilsJdbc.query(conn, SQL_GET_PORTRID_FROM_DEVICENAME_PORTNUMBER,
                    new MapListHandler(), deviceName, portNumber);
			if (records.size() <= 0) {
                // error
			}
			String ret = records.get(0).get("rid").toString();
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", ret));
			}
			return ret;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getDevicePortInfoSetFlowFromPortRid(java.lang.String)
	 */
	@Override
	public List<Map<String, Map<String, Object>>> getDevicePortInfoSetFlowFromPortRid(Connection conn, String portRid) throws SQLException {
		final String fname = "getDevicePortInfoSetFlowFromPortRid";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(portRid=%s) - start", fname, portRid));
		}
		try {
			List<Map<String, Object>> records = utilsJdbc.query(conn, SQL_GET_PATCH_INFO_FROM_PATCHWIRING_PORTRID,
                    new MapListHandler(), portRid);
			if (records.size() <= 0) {
                // error
			}
			String inDeviceName = records.get(0).get("inDeviceName").toString();
			String inPortName = records.get(0).get("inPortName").toString();
			String outDeviceName = records.get(0).get("outDeviceName").toString();
			String outPortName = records.get(0).get("outPortName").toString();
			records = utilsJdbc.query(conn, SQL_GET_PATCH_INFO_FROM_PATCHWIRING_DEVICENAME_PORTNAME,
                    new MapListHandler("out", "in", "parent"), inDeviceName, inPortName, outDeviceName, outPortName);
			if (records.size() != 3 && records.size() != 1) {
                // error
			}

			Iterator<Map<String, Object>> it = records.iterator();
			List<Map<String, Map<String, Object>>> ret = new ArrayList<Map<String, Map<String, Object>>>();
			while (it.hasNext()) {
				Map<String, Object> record = it.next();
				Map<String, Object> inPortInfo = getPortInfoFromPortRid(conn, record.get("in").toString());
				Map<String, Object> outPortInfo = getPortInfoFromPortRid(conn, record.get("out").toString());
				Map<String, Object> parentInfo = getDeviceInfoFromDeviceRid(conn, record.get("parent").toString());

				Map<String, Map<String, Object>> tmp = new HashMap<String, Map<String, Object>>();
				tmp.put("in", inPortInfo);
				tmp.put("out", outPortInfo);
				tmp.put("parent", parentInfo);
				ret.add(tmp);
			}

			if (logger.isTraceEnabled()) {
				//logger.trace(String.format("%s(ret=%s) - end", ret));
			}
			return ret;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getCableLinksFromDeviceName(java.sql.Connection, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getCableLinksFromDeviceName(Connection conn, String deviceName) throws SQLException {
		final String fname = "getCableLinks";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, deviceName=%s) - start", fname, conn, deviceName));
		}
		MapListHandler rhs = new MapListHandler(
				"inDeviceName",  "inPortName",  "inPortNumber",
				"outDeviceName", "outPortName", "outPortNumber",
				"@rid.asString()", "band", "used");
		try {
			List<Map<String, Object>> maps = utilsJdbc.query(
					conn,
					SQL_GET_CABLE_LINKS,
					rhs,
					deviceName);
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, maps));;
			}
			return maps;
		} catch (IndexOutOfBoundsException e) {
			return null;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getPatchWiringsFromDeviceName(java.sql.Connection, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getPatchWiringsFromDeviceName(Connection conn, String deviceName) throws SQLException {
		final String fname = "getPatchWirings";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, deviceName=%s) - start", fname, conn, deviceName));
		}
		MapListHandler rhs = new MapListHandler(
				"inDeviceName",  "inPortName",  "inPortNumber",
				"outDeviceName", "outPortName", "outPortNumber",
				"@rid.asString()", "band", "used");
		try {
			List<Map<String, Object>> maps = utilsJdbc.query(
					conn,
					SQL_GET_PATCH_WIRINGS_FROM_DEVICENAME,
					rhs,
					deviceName);
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, maps));
			}
			return maps;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getPatchWiringsFromDeviceNamePortName(java.sql.Connection, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getPatchWiringsFromDeviceNamePortName(Connection conn, String deviceName, String portName) throws SQLException {
		final String fname = "getPatchWiringsFromDeviceNamePortName";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, devicename=%s, portName=%s) - start", fname, conn, deviceName, portName));
		}
		try {
			MapListHandler rsh = new MapListHandler("in", "out", "parent", "inDeviceName", "inPortName", "outDeviceName", "outPortName", "sequence");
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_PATCH_WIRINGS_FROM_DEVICENAME_PORTNAME, rsh, deviceName, portName);
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, maps));
			}
			return maps;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}


	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getPatchWiringsFromParentRid(java.sql.Connection, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getPatchWiringsFromParentRid(Connection conn, String parentRid) throws SQLException {
		final String fname = "getPatchWiringsFromParentRid";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, parentRid=%s) - start", fname, conn, parentRid));
		}
		List<Map<String, Object>> ret = null;
		try {
			ret = utilsJdbc.query(
					conn,
					SQL_GET_PATCH_WIRINGS_FROM_PARENTRID,
					new MapListHandler("in", "out", "parent", "inDeviceName", "inPortName", "outDeviceName", "outPortName", "sequence"),
					parentRid);
			return ret;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", ret));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#isContainsPatchWiringFromDeviceNamePortName(java.sql.Connection, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isContainsPatchWiringFromDeviceNamePortName(Connection conn, String deviceName, String portName) throws SQLException {
		final String fname = "isContainsPatchWiringFromDeviceNamePortName";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, devicename=%s, portName=%s) - start", fname, conn, deviceName, portName));
		}
		try {
			boolean ret = true;
			MapListHandler rsh = new MapListHandler("in", "out", "parent", "inDeviceName", "inPortName", "outDeviceName", "outPortName");
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_PATCH_WIRINGS_FROM_DEVICENAME_PORTNAME, rsh, deviceName, portName);
			if (maps == null || maps.isEmpty()) {
				ret = false;
			}
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#isDeviceNameContainedIntoPatchWiring(java.sql.Connection, java.lang.String)
	 */
	@Override
	public boolean isDeviceNameContainedIntoPatchWiring(Connection conn, String deviceName) throws SQLException {
		final String fname = "isDeviceNameContainedIntoPatchWiring";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, deviceName=%s) - start", fname, conn, deviceName));
		}
		boolean ret = true;
		try {
			MapListHandler rsh = new MapListHandler("parent");
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_PATCH_WIRINGS_FROM_DEVICENAME, rsh, deviceName);
			if (maps == null || maps.isEmpty()) {
				ret = false;
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public boolean isNodeRidContainedIntoPatchWiring(Connection conn, String nodeRid) throws SQLException {
		final String fname = "isNodeRidContainedIntoPatchWiring";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, nodeRid=%s) - start", fname, conn, nodeRid));
		}
		boolean ret = true;
		try {
			MapListHandler rsh = new MapListHandler("parent");
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_PATCH_WIRINGS_FROM_NODERID, rsh, nodeRid);
			if (maps == null || maps.isEmpty()) {
				ret = false;
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#isPortRidContainedIntoPatchWiring(java.sql.Connection, java.lang.String)
	 */
	@Override
	public boolean isPortRidContainedIntoPatchWiring(Connection conn, String portRid) throws SQLException {
		final String fname = "isPortRidContainedIntoPatchWiring";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, portRid=%s) - start", fname, conn, portRid));
		}
		boolean ret = true;
		try {
			MapListHandler rsh = new MapListHandler("parent");
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_PATCH_WIRINGS_FROM_PORTRID, rsh, portRid, portRid);
			if (maps == null || maps.isEmpty()) {
				ret = false;
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#deletePatchWiring(java.sql.Connection, java.lang.String, java.lang.String)
	 */
	@Override
	public int deletePatchWiring(Connection conn, String deviceName, String portName) throws SQLException {
		final String fname = "deletePatchWiring";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, devicename=%s, portName=%s) - start", fname, conn, deviceName, portName));
		}
		int ret = 0;
		try {
			Object[] params = {deviceName, portName, deviceName, portName};
			ret = utilsJdbc.update(conn, SQL_DELETE_PATCH_WIRING_FROM_DEVICE_NAME_PORT_NAME, params);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getCableLinkFromInPortRid(java.sql.Connection, java.lang.String)
	 */
	@Override
	public Map<String, Object> getCableLinkFromInPortRid(Connection conn, String inPortRid) throws SQLException {
		final String fname = "getCableLinkFromInPortRid";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, inPortRid=%s) - start", fname, conn, inPortRid));
		}
		Map<String, Object> ret = null;
		try {
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_CABLE_FROM_IN_PORTRID, new MapListHandler(), inPortRid);
			if (!maps.isEmpty()) {
				ret = maps.get(0);
			}
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getCableLinkFromOutPortRid(java.sql.Connection, java.lang.String)
	 */
	@Override
	public Map<String, Object> getCableLinkFromOutPortRid(Connection conn, String outPortRid) throws SQLException {
		final String fname = "getCableLinkFromOutPortRid";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, outPortRid=%s) - start", fname, conn, outPortRid));
		}
		Map<String, Object> ret = null;
		try {
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_CABLE_FROM_OUT_PORTRID, new MapListHandler(), outPortRid);
			if (!maps.isEmpty()) {
				ret = maps.get(0);
			}
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#createCableLink(java.sql.Connection, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public int createCableLink(Connection conn, String deviceName0, String portName0, String deviceName1, String portName1) throws SQLException {
		final String fname = "createCableLink";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, deviceName0=%s, portName0=%s, deviceName1=%s, portName1=%s) - start",
					fname, conn, deviceName0, portName0, deviceName1, portName1));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			String port0Rid = this.getPortRidFromDeviceNamePortName(conn, deviceName0, portName0);
			if (StringUtils.isBlank(port0Rid)) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}
			String port1Rid = this.getPortRidFromDeviceNamePortName(conn, deviceName1, portName1);
			if (StringUtils.isBlank(port1Rid)) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}

			String sql = SQL_INSERT_CABLE;
			sql = sql.replaceFirst("\\?", port0Rid);
			sql = sql.replaceFirst("\\?", port1Rid);
			int result = utilsJdbc.update(conn, sql);
			if (result != 1) {
				ret = DB_RESPONSE_STATUS_EXIST;
				return ret;
			}

			sql = SQL_INSERT_CABLE;
			sql = sql.replaceFirst("\\?", port1Rid);
			sql = sql.replaceFirst("\\?", port0Rid);
			result = utilsJdbc.update(conn, sql);
			if (result != 1) {
				ret = DB_RESPONSE_STATUS_EXIST;
				return ret;
			}

			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#deleteCableLink(java.sql.Connection, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public int deleteCableLink(Connection conn, String deviceName0, String portName0, String deviceName1, String portName1) throws SQLException {
		final String fname = "deleteCableLink";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, deviceName0=%s, portName0=%s, deviceName1=%s, portName1=%s) - start",
					fname, conn, deviceName0, portName0, deviceName1, portName1));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			boolean contain = false;
			contain = this.isContainsPatchWiringFromDeviceNamePortName(conn, deviceName0, portName0);
			if (contain) {
				ret = DB_RESPONSE_STATUS_USED;
				return ret;
			}

			contain = this.isContainsPatchWiringFromDeviceNamePortName(conn, deviceName1, portName1);
			if (contain) {
				ret = DB_RESPONSE_STATUS_USED;
				return ret;
			}

			String port0Rid = this.getPortRidFromDeviceNamePortName(conn, deviceName0, portName0);
			if (StringUtils.isBlank(port0Rid)) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}
			String port1Rid = this.getPortRidFromDeviceNamePortName(conn, deviceName1, portName1);
			if (StringUtils.isBlank(port1Rid)) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}

			contain = this.isPortRidContainedIntoPatchWiring(conn, port0Rid);
			if (contain) {
				ret = DB_RESPONSE_STATUS_USED;
				return ret;
			}

			contain = this.isPortRidContainedIntoPatchWiring(conn, port1Rid);
			if (contain) {
				ret = DB_RESPONSE_STATUS_USED;
				return ret;
			}

			String sql = SQL_DELETE_CABLE_FROM_ONE_PORTRID;
			sql = sql.replaceFirst("\\?", port0Rid);
			sql = sql.replaceFirst("\\?", port1Rid);
			int result = utilsJdbc.update(conn, sql);
			if (result == 0) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}
			if (result != 1) {
				ret = DB_RESPONSE_STATUS_FAIL;
				return ret;
			}

			sql = SQL_DELETE_CABLE_FROM_ONE_PORTRID;
			sql = sql.replaceFirst("\\?", port1Rid);
			sql = sql.replaceFirst("\\?", port0Rid);
			result = utilsJdbc.update(conn, sql);
			if (result == 0) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}
			if (result != 1) {
				ret = DB_RESPONSE_STATUS_FAIL;
				return ret;
			}

			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#updateCableLinkUsedFromPortRid(java.sql.Connection, java.lang.String, int)
	 */
	@Override
	public void updateCableLinkUsedFromPortRid(Connection conn, String portRid, long newUsed) throws SQLException {
		final String fname = "updateCableLinkUsedFromPortRid";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, portRid=%s, newUsed=%s) - start", fname, conn, portRid, newUsed));
		}
		try {
			Object[] params = {newUsed, portRid, portRid};
			int result = utilsJdbc.update(conn, SQL_UPDATE_CALBE_LINK_USED_VALUE_FROM_PORT_RID, params);
			if (result != 2) {
				throw new SQLException(String.format(INVALID_NUMBER_OF, "link(represent cable)"));
			}
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s() - end", fname));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getShortestPath(java.sql.Connection, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getShortestPath(Connection conn, String ridA, String ridZ) throws SQLException {
		final String fname = "getShortestPath";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, ridA=%s, ridZ=%s) - start", fname, conn, ridA, ridZ));
		}
		List<Map<String, Object>> ret = null;
		try {
			String query = String.format(SQL_GET_DIJKSTRA_PATH_FLATTEN, ridA, ridZ);
			MapListHandler rsh = new MapListHandler("rid", "class", "name", "number", "deviceName", "type", "datapathId", "ofcIp");
			ret = utilsJdbc.query(conn, query, rsh);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#insertPatchWiring(java.sql.Connection, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public int insertPatchWiring(Connection conn, String ofpRid, String in, String out, String inDeviceName, String inPortName, String outDeviceName, String outPortName, int sequence) throws SQLException {
		final String fname = "insertPatchWiring";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, ofpRid=%s, in=%s, out=%s, inDeviceName=%s, inPortName=%s, outDeviceName=%s, outPortName=%s, sequence=%s) - start",
					fname, conn, ofpRid, in, out, inDeviceName, inPortName, outDeviceName, outPortName, sequence));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			Object[] forwardParams = {in, out, ofpRid, inDeviceName, inPortName, outDeviceName, outPortName, sequence};
			int result = utilsJdbc.update(conn, SQL_INSERT_PATCH_WIRING_2, forwardParams);
			if (result != 1) {
				throw new SQLException(String.format(PATCH_INSERT_FAILD, inDeviceName, inPortName, outDeviceName, outPortName, ofpRid, in, out));
			}
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getDeviceInfoFromDeviceName(java.sql.Connection, java.lang.String)
	 */
	@Override
	public Map<String, Object> getNodeInfoFromDeviceName(Connection conn, String deviceName) throws SQLException {
		final String fname = "getDeviceInfo";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, deviceName=%s) - start", fname, conn, deviceName));
		}
		Map<String, Object> map = null;
		try {
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_NODE_INFO_FROM_DEVICE_NAME, new MapListHandler(), deviceName);
			if (!maps.isEmpty()) {
				map = maps.get(0);
			}
			if (logger.isTraceEnabled()){
				logger.trace(String.format("%s(ret=%s) - end", fname, map));
			}
			return map;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getDeviceInfoFromDeviceRid(java.sql.Connection, java.lang.String)
	 */
	@Override
	public Map<String, Object> getNodeInfoFromDeviceRid(Connection conn, String nodeRid) throws SQLException {
		final String fname = "getDeviceInfoFromDeviceRid";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, nodeRid=%s) - start", fname, conn, nodeRid));
		}
		Map<String, Object> map = null;
		try {
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_NODE_INFO_FROM_DEVICE_RID, new MapListHandler(), nodeRid);
			if (!maps.isEmpty()) {
				map = maps.get(0);
			}
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, map));
			}
			return map;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public String getNodeRidFromDeviceName(Connection conn, String deviceName) throws SQLException {
		final String fname = "getNodeRidFromDeviceName";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, deviceName=%s) - start", fname, conn, deviceName));
		}
		String ret = null;
		try {
			List<Map<String, Object>> records = utilsJdbc.query(
					conn,
					SQL_GET_NODE_RID_FROM_DEVICENAME,
					new MapListHandler("rid"),
					deviceName);
			if (records != null && !records.isEmpty() && records.get(0) != null) {
				ret = (String) records.get(0).get("rid");
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#createNodeInfo(java.sql.Connection, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public int createNodeInfo(Connection conn, String deviceName, String deviceType, String datapathId, String ofcIp) throws SQLException {
		final String fname = "createNodeInfo";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, deviceName=%s, deviceType=%s, datapathId=%s, ofcIp=%s) - start", fname, conn, deviceName, deviceType, datapathId, ofcIp));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			Object[] params = {deviceName, deviceType, datapathId, ofcIp};
			int nRecords = utilsJdbc.update(conn, SQL_INSERT_NODE_INFO, params);
			if (nRecords == 0) {
				return DB_RESPONSE_STATUS_EXIST;
			}
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#updateNodeInfo(java.sql.Connection, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public int updateNodeInfo(Connection conn, String keyDeviceName, String deviceName, String datapathId, String ofcIp) throws SQLException {
		final String fname = "updateNodeInfo";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, keyDeviceName=%s, newDeviceName=%s, datapathId=%s, ofcIp=%s) - start", fname, conn, keyDeviceName, deviceName, datapathId, ofcIp));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			Map<String, Object> current = this.getNodeInfoFromDeviceName(conn, keyDeviceName);
			if (current == null) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}

			String nodeRid = (String)current.get("rid");
			if (StringUtils.isBlank(deviceName)) {
				deviceName = (String)current.get("name");
			}
			if (StringUtils.isBlank(datapathId)) {
				datapathId = (String)current.get("datapathId");
			}
			if (StringUtils.isBlank(ofcIp)) {
				ofcIp = (String)current.get("ofcIp");
			}

			Object[] params = {deviceName, datapathId, ofcIp, nodeRid};
			int result = utilsJdbc.update(conn, SQL_UPDATE_NODE_INFO_FROM_RID, params);
			if (result == 0) {
				ret = DB_RESPONSE_STATUS_EXIST;
				return ret;
			}

			Object[] updDevNamePara = {deviceName, keyDeviceName};
			utilsJdbc.update(conn, SQL_UPDATE_PORT_DEVICENAME, updDevNamePara);
			utilsJdbc.update(conn, SQL_UPDATE_PATCH_WIRING_INDEVICENAME,  updDevNamePara);
			utilsJdbc.update(conn, SQL_UPDATE_PATCH_WIRING_OUTDEVICENAME, updDevNamePara);

			return ret;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()){
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#deleteNodeInfo(java.sql.Connection, java.lang.String)
	 */
	@Override
	public int deleteNodeInfo(Connection conn, String deviceName) throws SQLException {
		final String fname = "deleteNodeInfo";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, deviceName=%s) - start", fname, conn, deviceName));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			String nodeRid = this.getNodeRidFromDeviceName(conn, deviceName);
			if (StringUtils.isBlank(nodeRid)) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}

			boolean contain = this.isDeviceNameContainedIntoPatchWiring(conn, deviceName);
			if (contain) {
				ret = DB_RESPONSE_STATUS_FORBIDDEN;
				return ret;
			}

			contain = this.isNodeRidContainedIntoPatchWiring(conn, nodeRid);
			if (contain) {
				ret = DB_RESPONSE_STATUS_FORBIDDEN;
				return ret;
			}

			Object[] params = {deviceName};
			utilsJdbc.update(
					conn,
					SQL_DELETE_PORT_FROM_DEVICENAME,
					params);

			String sql = SQL_DELETE_NODE_FROM_NODERID;
			sql = sql.replaceFirst("\\?", nodeRid);
			int nRecord = utilsJdbc.update(conn, sql);
			if (nRecord != 1) {
				ret = DB_RESPONSE_STATUS_FAIL;
				return ret;
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#createPortInfo(java.sql.Connection, java.lang.String, int, java.lang.String)
	 */
	@Override
	public int createPortInfo(Connection conn, String portName, int portNumber, String deviceName) throws SQLException {
		final String fname = "createPortInfo";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, portName=%s, portNumber=%s, deviceName=%s) - start", fname, conn, portName, portNumber, deviceName));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			/* PHASE 1: Insert port info */
			Map<String, Object> devMap = this.getNodeInfoFromDeviceName(conn, deviceName);
			if (devMap == null) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}

			Object[] params = {portName, portNumber, deviceName};
			int nRecords = utilsJdbc.update(conn, SQL_INSERT_PORT_INFO, params);
			if (nRecords == 0) {
				ret = DB_RESPONSE_STATUS_EXIST;
				return ret;
			}

			/* PHASE 2: Insert Bus-link */
			String devRid  = (String) devMap.get("rid");
			String devType = (String) devMap.get("type");
			String portRid = this.getPortRidFromDeviceNamePortName(conn, deviceName, portName);
			if (StringUtils.isBlank(portRid)) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}

			/* Here, decide Used default value of bus of Node. */
			/* Might you think 'why is it implemented in here', might you think 'this process is have to implement with top-side Method'. */
			/* If we use gremlin or cypher, not need set high value to Used-value. */
			Long used = USED_BLOCKING_VALUE;
			if (OFPMUtils.isNodeTypeOfpSwitch(devType)) {
				used = 0L;
			}

			String sql = SQL_INSERT_UBUS;
			sql = sql.replaceFirst("\\?", portRid);
			sql = sql.replaceFirst("\\?", devRid);
			sql = sql.replaceFirst("\\?", used.toString());
			nRecords = utilsJdbc.update(conn, sql);
			if (nRecords == 0) {
				ret = DB_RESPONSE_STATUS_EXIST;
				return ret;
			}

			sql = SQL_INSERT_DBUS;
			sql = sql.replaceFirst("\\?", devRid);
			sql = sql.replaceFirst("\\?", portRid);
			sql = sql.replaceFirst("\\?", used.toString());
			nRecords = utilsJdbc.update(conn, sql);
			if (nRecords == 0) {
				ret = DB_RESPONSE_STATUS_EXIST;
				return ret;
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#updatePortInfo(java.sql.Connection, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public int updatePortInfo(Connection conn, String keyPortName, String keyDeviceName, String portName, int portNumber) throws SQLException {
		final String fname = "updatePortInfo";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, keyPortName=%s, keyDeviceName=%s, portName=%s, portNumber=%s) - start", fname, conn, keyPortName, keyDeviceName, portName, portNumber));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			Map<String, Object> current = this.getPortInfoFromPortName(conn, keyDeviceName, keyPortName);
			if (current == null) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				if (logger.isTraceEnabled()){
					logger.trace(String.format("%s(ret=%s) - end", fname, ret));
				}
				return ret;
			}

			String portRid = (String)current.get("rid");
			if (StringUtils.isBlank(portName)) {
				portName = (String)current.get("name");
			}
			if (portNumber == 0) {
				portNumber = (Integer)current.get("portNumber");
			}

			Object[] params = {portName, portNumber, portRid};
			int result = utilsJdbc.update(conn, SQL_UPDATE_PORT_INFO_FROM_RID, params);
			if (result == 0) {
				ret = DB_RESPONSE_STATUS_EXIST;
				if (logger.isTraceEnabled()) {
					logger.trace(String.format("%s(ret=%s) - end", fname, ret));
				}
				return ret;
			}

			Object[] updPortNamePara = {portName, keyPortName, keyDeviceName};
			utilsJdbc.update(conn, SQL_UPDATE_PATCH_WIRING_INPORTNAME, updPortNamePara);
			utilsJdbc.update(conn, SQL_UPDATE_PATCH_WIRING_OUTPORTNAME, updPortNamePara);
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}

		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(ret=%s) - end", fname, ret));
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#deletePortInfo(java.sql.Connection, java.lang.String, java.lang.String)
	 */
	@Override
	public int deletePortInfo(Connection conn, String portName, String deviceName) throws SQLException {
		final String fname = "deletePortInfo";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("%s(conn=%s, portName=%s, deviceName=%s) - start", fname, conn, portName, deviceName));
		}
		int ret = DB_RESPONSE_STATUS_OK;
		try {
			String portRid = this.getPortRidFromDeviceNamePortName(conn, deviceName, portName);
			if (StringUtils.isBlank(portRid)) {
				ret = DB_RESPONSE_STATUS_NOT_FOUND;
				return ret;
			}

			boolean contain = this.isContainsPatchWiringFromDeviceNamePortName(conn, deviceName, portName);
			if (contain) {
				ret = DB_RESPONSE_STATUS_FORBIDDEN;
				return ret;
			}

			contain = this.isPortRidContainedIntoPatchWiring(conn, portRid);
			if (contain) {
				ret = DB_RESPONSE_STATUS_FORBIDDEN;
				return ret;
			}

			String sql = SQL_DELETE_PORT_FROM_PORTRID;
			sql = sql.replaceFirst("\\?", portRid);
			int nRecord = utilsJdbc.update(conn, sql);
			if (nRecord != 1) {
				ret = DB_RESPONSE_STATUS_FAIL;
				return ret;
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getPortInfoFromPortName(java.sql.Connection, java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> getPortInfoFromPortName(Connection conn, String deviceName, String portName) throws SQLException {
		final String fname = "getPortInfoFromPortName";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, deviceName=%s, portName=%s) - start", fname, conn, deviceName, portName));
		}
		Map<String, Object> map = null;
		try {
			List<Map<String, Object>> maps = utilsJdbc.query(conn, SQL_GET_PORT_INFO_FROM_PORT_NAME, new MapListHandler(), portName, deviceName);
			if (!maps.isEmpty()) {
				map = maps.get(0);
			}
			if (logger.isTraceEnabled()){
				logger.trace(String.format("%s(ret=%s) - end", fname, map));
			}
			return map;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getPortInfoFromPortRid(java.sql.Connection, java.lang.String)
	 */
	@Override
	public Map<String, Object> getPortInfoFromPortRid(Connection conn, String rid) throws SQLException {
		final String fname = "getPortInfoFromPortRid";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(rid=%s) - start", fname, rid));
		}
		Map<String, Object> ret = null;
		try {
			List<Map<String, Object>> records = utilsJdbc.query(
					conn,
					SQL_GET_PORT_INFO_FROM_PORTRID,
                    new MapListHandler(),
                    rid);
			if (records != null && !records.isEmpty()) {
				ret = records.get(0);
			}

			if (logger.isTraceEnabled()){
				logger.trace(String.format("getPortInfo(ret=%s) - end", ret));
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getPortRidFromDeviceNamePortName(java.sql.Connection, java.lang.String, java.lang.String)
	 */
	@Override
	public String getPortRidFromDeviceNamePortName(Connection conn, String deviceName, String portName) throws SQLException {
		final String fname = "getPortRidFromDeviceNamePortName";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, deviceName=%s, portName=%s) - start", fname, deviceName, portName));
		}
		String ret = null;
		try {
			List<Map<String, Object>> records = utilsJdbc.query(
					conn,
					SQL_GET_PORT_RID_FROM_DEVICENAME_PORTNAME,
					new MapListHandler("rid"),
					portName, deviceName);
			if (records != null && !records.isEmpty() && records.get(0) != null) {
				ret = (String) records.get(0).get("rid");
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()){
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getPortInfoListFromDeviceName(java.sql.Connection, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getPortInfoListFromDeviceName(Connection conn, String deviceName) throws SQLException {
		final String fname = "getPortInfoListFromDeviceName";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, deviceName=%s) - start", fname, deviceName));
		}
		List<Map<String, Object>> ret = null;
		try {
			ret = utilsJdbc.query(
					conn,
					SQL_GET_PORT_INFO_FROM_DEVICENAME,
					new MapListHandler(),
					deviceName);
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()){
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	@Override
	public Map<String, Object> getNeighborPortFromPortRid(Connection conn, String portRid) throws SQLException {
		final String fname = "getPortInfoListFromDeviceName";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, portRid=%s) - start", fname, portRid));
		}
		Map<String, Object> ret = null;
		try {
			List<Map<String, Object>> records = utilsJdbc.query(
					conn,
					SQL_GET_NEIGHBOR_PORT_INFO_FROM_PORT_RID,
					new MapListHandler(),
					portRid);
			if (records != null && !records.isEmpty()) {
				ret = records.get(0);
			}
			return ret;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()){
				logger.trace(String.format("%s(ret=%s) - end", fname, ret));
			}
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getNodeInfoFromPortRid(java.sql.Connection, java.lang.String)
	 */
	@Override
	public Map<String, Object> getDeviceInfoFromDeviceRid(Connection conn, String rid) throws SQLException {
		final String fname = "getNodeInfoFromPortRid";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(rid=%s) - start", fname, rid));
		}
		try {
			List<Map<String, Object>> records = utilsJdbc.query(conn, SQL_GET_DEVICE_INFO_FROM_DEVICERID,
                    new MapListHandler(), rid);
			if (records.size() <= 0) {
				String msg = String.format(NOT_FOUND, "node(" + rid + ")");
                throw new SQLException(msg);
			}
			Map<String, Object> record = records.get(0);

			if (logger.isTraceEnabled()){
				logger.trace(String.format("%s(ret=%s) - end", fname, documents.get(0)));
			}
			return record;
		} catch (Exception e) {
			throw new SQLException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getInternalMacFromDeviceNameInPortSrcMacDstMac(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String getInternalMacFromDeviceNameInPortSrcMacDstMac(Connection conn, String deviceName, String inPort, String srcMac, String dstMac) throws SQLException {
		final String fname = "getInternalMacFromDeviceNameInPortSrcMacDstMac";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, deviceName=%s, inPort=%s, srcMac=%s, dstMac=%s) - start", fname, conn, deviceName, inPort, srcMac, dstMac));
		}
		try {
			String ret = null;
			List<Map<String, Object>> records = utilsJdbc.query(conn, SQL_GET_INTERNALMAC_FROM_SRCMAC_DSTMAC_INPORT_DEVICENAME,
                    new MapListHandler(), deviceName, inPort, srcMac, dstMac);
			if (records.size() > 0) {
                ret = OFPMUtils.longToMacAddress(Long.parseLong((records.get(0).get("internalMac").toString())));
			} else {
				records = utilsJdbc.query(conn, SQL_GET_MAX_INTERNALMAC, new MapListHandler());
				Long newInternalMac = 1L;
				if (records.size() > 0) {
					newInternalMac = Long.parseLong((records.get(0).get("maxInternalMac").toString())) + 1L;
				}
				Object[] params = {deviceName, inPort, srcMac, dstMac, newInternalMac};
				int rows = utilsJdbc.update(conn, SQL_INSERT_INTERNALMAC, params);
				if (rows == 0) {
					// TODO exist error
				}
				ret = OFPMUtils.longToMacAddress(newInternalMac);
			}

			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", ret));
			}
			return ret;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getInternalMacListFromDeviceNameInPort(java.sql.Connection, java.lang.String, java.lang.String)
	 */
	@Override
	public List<String> getInternalMacListFromDeviceNameInPort(Connection conn, String deviceName, String inPort) throws SQLException {
		final String fname = "getInternalMacListFromDeviceNameInPort";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, deviceName=%s, inPort=%s) - start", fname, conn, deviceName, inPort));
		}
		try {
			List<String> ret = new ArrayList<String>();
			List<Map<String, Object>> records = utilsJdbc.query(conn, SQL_GET_INTERNALMAC_LIST_FROM_DEVICENAME_INPORT, new MapListHandler("internalMac"), deviceName, inPort);
			for (int i = 0; i < records.size(); i++) {
				Map<String, Object> record = records.get(i);
				Long internalMac = Long.parseLong(record.get("internalMac").toString());
				ret.add(OFPMUtils.longToMacAddress(internalMac));
			}
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", ret));
			}
			return ret;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ool.com.orientdb.client.Dao#getInternalMacInfoListFromDeviceNameInPort(java.sql.Connection, java.lang.String, java.lang.Integer)
	 */
	@Override
	public List<Map<String, Object>> getInternalMacInfoListFromDeviceNameInPort(Connection conn, String deviceName, Integer portNumber) throws SQLException {
		final String fname = "getInternalMacInfoListFromDeviceNameInPort";
		if (logger.isTraceEnabled()){
			logger.trace(String.format("%s(conn=%s, deviceName=%s, portNumber=%s) - start", fname, conn, deviceName, portNumber));
		}
		List<Map<String, Object>> ret = null;
		try {
			ret = utilsJdbc.query(
					conn,
					SQL_GET_INTERNALMAC_INFO_LIST_FROM_DEVICENAME_PORTNUMBER,
					new MapListHandler("srcMac", "dstMac", "internalMac"),
					deviceName,
					portNumber);
			if (ret == null) {
				return new ArrayList<Map<String, Object>>();
			}
			return ret;
		} catch (Exception e){
			throw new SQLException(e.getMessage());
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("%s(ret=%s) - end", ret));
			}
		}
	}

}
