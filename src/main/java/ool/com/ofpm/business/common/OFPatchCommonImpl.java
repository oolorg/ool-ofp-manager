/**
 * @author OOL 1131080355959
 * @date 2014/02/17
 * @TODO
 */
package ool.com.ofpm.business.common;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ool.com.ofpm.json.ofc.PatchLink;
import ool.com.ofpm.json.ofpatch.GraphDBPatchLinkJsonRes;
import ool.com.orientdb.client.ConnectionUtils;
import ool.com.orientdb.client.ConnectionUtilsImpl;
import ool.com.orientdb.client.Dao;
import ool.com.orientdb.client.DaoImpl;
import ool.com.util.Definition;
import ool.com.util.ErrorMessage;

import org.apache.log4j.Logger;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * @author 1131080355959
 *
 */
public class OFPatchCommonImpl implements OFPatchCommon {

	private static final Logger logger = Logger.getLogger(OFPatchCommonImpl.class);

	/* (non-Javadoc)
	 * @see ool.com.orientdb.business.OFPatchBusiness#connectPatch(ool.com.orientdb.json.ConnectPatchJsonPostIn)
	 */
	@Override
	public GraphDBPatchLinkJsonRes connectPatch(List<String> deviceNameList) {
		if (logger.isDebugEnabled()) {
    		logger.debug(String.format("connectPatch(inPara=%s) - start ", deviceNameList));
    	}
		GraphDBPatchLinkJsonRes ret = new GraphDBPatchLinkJsonRes();
		Dao dao = null;
		List<String> deviceRidList = new ArrayList<String>();
		ODocument document;
		List<PatchLink> linkPatchPortList = new ArrayList<PatchLink>();

		try {
			ConnectionUtils utils = new ConnectionUtilsImpl();
			dao = new DaoImpl(utils);
			deviceRidList.add(dao.getDeviceRid(deviceNameList.get(0)));
			deviceRidList.add(dao.getDeviceRid(deviceNameList.get(1)));
			document = dao.getShortestPath(deviceRidList);
			List<Map<String,String>> map = createHashMapShortestPath(document.field("dijkstra").toString());

			for (int i = 0; i < map.size(); i++) {
				if(map.get(i).containsKey("ofpFlag")) {
					if (i == 0 || i == map.size() - 1) continue;

					int weight1 = dao.getLinkInfo(map.get(i-1).get("RID"), map.get(i).get("RID")).field("weight");
					int weight2 = dao.getLinkInfo(map.get(i+1).get("RID"), map.get(i).get("RID")).field("weight");
					if (weight1 == Definition.DIJKSTRA_WEIGHT_NO_ROUTE || weight2 == Definition.DIJKSTRA_WEIGHT_NO_ROUTE) {
						linkPatchPortList.clear();
						break;
					}

					List<Integer> portNameList = new ArrayList<Integer>();
					PatchLink linkPatchPort = new PatchLink();
					List<String> portRidList = new ArrayList<String>();

					portRidList.add(map.get(i-1).get("RID"));
					portRidList.add(map.get(i+1).get("RID"));
					dao.insertPatchWiring(portRidList, map.get(i).get("RID"), deviceNameList);

					dao.updateLinkWeight(Definition.DIJKSTRA_WEIGHT_NO_ROUTE, map.get(i-1).get("RID"), map.get(i).get("RID"));
					dao.updateLinkWeight(Definition.DIJKSTRA_WEIGHT_NO_ROUTE, map.get(i+1).get("RID"), map.get(i).get("RID"));

					portNameList.add(Integer.valueOf(map.get(i-1).get("number")));
					portNameList.add(Integer.valueOf(map.get(i+1).get("number")));
					linkPatchPort.setDeviceName(map.get(i-1).get("deviceName"));
					linkPatchPort.setPortName(portNameList);
					linkPatchPortList.add(linkPatchPort);
				}
			}

			if (linkPatchPortList.isEmpty()) {
				ret.setMessage(String.format(ErrorMessage.IS_NO_ROUTE, deviceNameList.get(0), deviceNameList.get(1)));
				ret.setStatus(Definition.STATUS_NOTFOUND);
			} else {
				ret.setResult(linkPatchPortList);
				ret.setStatus(Definition.STATUS_CREATED);
			}
    	} catch (SQLException e) {
    		logger.error(e.getMessage());
    		ret.setStatus(Definition.STATUS_INTERNAL_ERROR);
    		ret.setMessage(e.getMessage());
		}  catch (RuntimeException re) {
			logger.error(re.getMessage());
			ret.setStatus(Definition.STATUS_INTERNAL_ERROR);
    		ret.setMessage(re.getMessage());
		} finally {
			try {
				if(dao != null) {
					dao.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
				ret.setStatus(Definition.STATUS_INTERNAL_ERROR);
	    		ret.setMessage(e.getMessage());
			}
		}
		if (logger.isDebugEnabled()) {
    		logger.debug(String.format("connectPatch(ret=%s) - end ", ret));
    	}
		return ret;
	}

	/* (non-Javadoc)
	 * @see ool.com.orientdb.business.OFPatchBusiness#disConnectPatch(ool.com.orientdb.json.ConnectPatchJsonIn)
	 */
	@Override
	public GraphDBPatchLinkJsonRes disConnectPatch(List<String> deviceNameList) {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("disConnectPatch(inPara=%s) - start ", deviceNameList));
		}

		Dao dao = null;
		List<Map<String,String>> portRidPairList = new ArrayList<Map<String,String>>();
		ODocument document;
		GraphDBPatchLinkJsonRes ret = new GraphDBPatchLinkJsonRes();
		List<PatchLink> linkPatchPortList = new ArrayList<PatchLink>();

		try {
			ConnectionUtils utils = new ConnectionUtilsImpl();
			dao = new DaoImpl(utils);

			portRidPairList = dao.getPortRidPatchWiring(deviceNameList);
			dao.deleteRecordPatchWiring(deviceNameList);

			for (Map<String,String> portRidPair : portRidPairList) {
				String rid;
				String parentRid;
				List<Integer> portNameList = new ArrayList<Integer>();
				String deviceName;
				PatchLink linkPatchPort = new PatchLink();

				parentRid = portRidPair.get("parent");
				rid = portRidPair.get("out");
				document = dao.getPortInfo(rid);
				portNameList.add(Integer.valueOf(document.field("number").toString()));
				deviceName = document.field("deviceName").toString();

				dao.updateLinkWeight(Definition.DIJKSTRA_WEIGHT_AVAILABLE_ROUTE, rid, parentRid);

				rid = portRidPair.get("in");
				document = dao.getPortInfo(rid);
				portNameList.add(Integer.valueOf(document.field("number").toString()));
				linkPatchPort.setDeviceName(deviceName);
				linkPatchPort.setPortName(portNameList);
				linkPatchPortList.add(linkPatchPort);

				dao.updateLinkWeight(Definition.DIJKSTRA_WEIGHT_AVAILABLE_ROUTE, rid, parentRid);
			}
			ret.setResult(linkPatchPortList);
			ret.setStatus(Definition.STATUS_SUCCESS);

    	} catch (SQLException e) {
    		logger.error(e.getMessage());
    		ret.setStatus(Definition.STATUS_INTERNAL_ERROR);
    		ret.setMessage(e.getMessage());
		}  catch (RuntimeException re) {
			logger.error(re.getMessage());
			ret.setStatus(Definition.STATUS_INTERNAL_ERROR);
    		ret.setMessage(re.getMessage());
		} finally {
			try {
				if(dao != null) {
					dao.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
				ret.setStatus(Definition.STATUS_INTERNAL_ERROR);
	    		ret.setMessage(e.getMessage());
			}
		}

		if (logger.isDebugEnabled()) {
    		logger.debug(String.format("disConnectPatch(ret=%s) - end ", ret));
    	}
		return ret;
	}

	private List<Map<String,String>> createHashMapShortestPath(String shortestPath) throws RuntimeException {
		if (logger.isDebugEnabled()) {
    		logger.debug(String.format("createHashMapShortestPath(shortestPath=%s) - start ", shortestPath));
    	}

		List<String> tmpList = Arrays.asList(shortestPath.split(", (port|node)"));
		List<Map<String,String>> map = new ArrayList<Map<String,String>>();

		for (int i = 1; i < tmpList.size(); i++) {
			List<String> tmp2List = Arrays.asList(tmpList.get(i).split(","));
			Map<String,String> tmpMap = new HashMap<String,String>();
			for (int j = 0; j < tmp2List.size(); j++) {
				List<String> keyVal = new ArrayList<String>();
				if (j == 0) {
					List<String> tmp3List = Arrays.asList(tmp2List.get(j).split("\\{"));
					tmpMap.put("RID", tmp3List.get(0).toString());
					keyVal = Arrays.asList(tmp3List.get(1).split(":"));
					tmpMap.put(keyVal.get(0), keyVal.get(1));
					continue;
				} else if (j == tmp2List.size() - 1) {
					List<String> tmp3List = Arrays.asList(tmp2List.get(j).split("\\} "));
					keyVal = Arrays.asList(tmp3List.get(0).split(":"));
					tmpMap.put(keyVal.get(0), keyVal.get(1));
					break;
				}
				keyVal = Arrays.asList(tmp2List.get(j).split(":"));
				tmpMap.put(keyVal.get(0), keyVal.get(1));

			}
			map.add(tmpMap);
		}

		if (logger.isDebugEnabled()) {
    		logger.debug(String.format("createHashMapShortestPath(ret=%s) - end ", map));
    	}

		return map;
	}

}
