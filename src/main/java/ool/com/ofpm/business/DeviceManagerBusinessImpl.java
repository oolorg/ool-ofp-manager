/**
 * @author OOL 1131080355959
 * @date 2014/03/04
 * @TODO
 */
package ool.com.ofpm.business;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;

import ool.com.ofpm.json.DeviceManagerGetConnectedPortInfoJsonOut;
import ool.com.orientdb.client.ConnectionUtils;
import ool.com.orientdb.client.ConnectionUtilsImpl;
import ool.com.orientdb.client.Dao;
import ool.com.orientdb.client.DaoImpl;
import ool.com.util.Definition;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * @author 1131080355959
 *
 */
public class DeviceManagerBusinessImpl implements DeviceManagerBusiness {

	private static final Logger logger = Logger.getLogger(DeviceManagerBusinessImpl.class);

	Gson gson = new Gson();

	/* (non-Javadoc)
	 * @see ool.com.orientdb.business.DeviceManagerBusiness#getConnectedPortInfo(java.lang.String)
	 */
	@Override
	public String getConnectedPortInfo(String deviceName) {
    	if (logger.isDebugEnabled()) {
    		logger.debug(String.format("getConnectedPortInfo(params=%s) - start ", deviceName));
    	}

		String ret = "";
		Dao dao = null;
		DeviceManagerGetConnectedPortInfoJsonOut outPara = new DeviceManagerGetConnectedPortInfoJsonOut();
		DeviceManagerGetConnectedPortInfoJsonOut.ResultData resultData;
		DeviceManagerGetConnectedPortInfoJsonOut.ResultData.LinkData linkData;

		try {
        	ConnectionUtils utils = new ConnectionUtilsImpl();
        	dao = new DaoImpl(utils);

        	List<ODocument> documents = dao.getPortList(deviceName);

        	ODocument targetNodeInfo = dao.getDeviceInfo(deviceName);

        	for (ODocument document : documents) {
        		// list connected port with target device
        		List<ODocument> connectedPorts = dao.getConnectedLinks(document.getIdentity().toString());
        		// check connected port num
        		// if 1, no connected
        		if (connectedPorts.size() <= 1) {
        			continue;
        		}

        		resultData = outPara.new ResultData();
        		linkData = resultData.new LinkData();

            	linkData.setDeviceName(targetNodeInfo.field("name").toString());
    			linkData.setDeviceType(targetNodeInfo.field("type").toString());
    			linkData.setOfpFlag(targetNodeInfo.field("ofpFlag").toString());
    			linkData.setPortName(document.field("name").toString());
    			linkData.setPortNumber(Integer.parseInt(document.field("number").toString()));
    			resultData.addLinkData(linkData);

        		for (ODocument connectedPort : connectedPorts) {
        			try {
        				ODocument port = connectedPort.field("out");
        				ODocument portInfo = dao.getPortInfo(port.getIdentity().toString());

        				linkData = resultData.new LinkData();
        				linkData.setPortName(portInfo.field("name").toString());
        				linkData.setPortNumber(Integer.parseInt(portInfo.field("number").toString()));
        				ODocument nodeInfo = dao.getDeviceInfo(portInfo.field("deviceName").toString());
        				linkData.setDeviceName(nodeInfo.field("name").toString());
        				linkData.setDeviceType(nodeInfo.field("type").toString());
        				linkData.setOfpFlag(nodeInfo.field("ofpFlag").toString());
        				resultData.addLinkData(linkData);
        			} catch (SQLException sqlex) {
        				if (sqlex.getCause() == null) {
        					throw sqlex;
        				}
        			}
        		}
        		outPara.addResultData(resultData);
        	}
        	outPara.setStatus(Definition.STATUS_SUCCESS);
    	} catch (SQLException e) {
    		logger.error(e.getMessage());
			if (e.getCause() == null) {
				outPara.setStatus(Definition.STATUS_INTERNAL_ERROR);
			} else {
				outPara.setStatus(Definition.STATUS_NOTFOUND);
			}
    		outPara.setMessage(e.getMessage());
		}  catch (RuntimeException re) {
			logger.error(re.getMessage());
			outPara.setStatus(Definition.STATUS_INTERNAL_ERROR);
			outPara.setMessage(re.getMessage());
		} finally {
			try {
				if(dao != null) {
					dao.close();
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
				outPara.setStatus(Definition.STATUS_INTERNAL_ERROR);
				outPara.setMessage(e.getMessage());
			}
			Type type = new TypeToken<DeviceManagerGetConnectedPortInfoJsonOut>(){}.getType();
	        ret = gson.toJson(outPara, type);
		}
		if (logger.isDebugEnabled()) {
    		logger.debug(String.format("getConnectedPortInfo(ret=%s) - end ", ret));
    	}
		return ret;
	}
}
