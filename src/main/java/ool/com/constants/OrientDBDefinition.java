/**
 * @author OOL 1131080355959
 * @date 2014/02/05
 * @TODO TODO
 */
package ool.com.constants;

public class OrientDBDefinition {
	/* Routing */
	public static final int DIJKSTRA_WEIGHT_NO_ROUTE = 100;
	public static final int DIJKSTRA_WEIGHT_AVAILABLE_ROUTE = 1;

	/* Node Type */
	public static final String NODE_TYPE_SERVER = "Server";
	public static final String NODE_TYPE_SWITCH = "Switch";
	public static final String NODE_TYPE_LEAF = "Leaf";
	public static final String NODE_TYPE_SPINE = "Spine";

	/* DB Response */
	public static final int DB_RESPONSE_STATUS_OK = 200;
	public static final int DB_RESPONSE_STATUS_EXIST = 210;
	public static final int DB_RESPONSE_STATUS_NOT_FOUND = 404;
	public static final int DB_RESPONSE_STATUS_USED = 220;
	public static final int DB_RESPONSE_STATUS_FORBIDDEN = 403;

	/* SQL Node Key */
	public static final String SQL_NODE_KEY_NAME = "name";
	public static final String SQL_NODE_KEY_TYPE = "type";
	public static final String SQL_NODE_KEY_FLAG = "ofpFlag";

	/* select */
	public static final String SQL_GET_DEVICE = "select from node where name='%s'";
	public static final String SQL_GET_NODE_INFO_FROM_DEVICE_NAME = "select @rid.asString(), name, type, datapathId, ofcIp from node where name=?";
	public static final String SQL_GET_NODE_INFO_FROM_DEVICE_RID  = "select @rid.asString(), name, type, datapathId, ofcIp from node where @rid=?";
	public static final String SQL_GET_DEVICE_LIST = "select from node %s";
	public static final String SQL_GET_CONNECTED_NODE = "select from (traverse * from %s) where @class='node' and $depth=6";
	public static final String SQL_GET_PATCHPORT_RID = "select from (traverse * from %s) where @class='port' and $depth=4";
	public static final String SQL_GET_PATCH_WIRING = "select from patchWiring where out = %s and in = %s";
	public static final String SQL_GET_DIJKSTRA_PATH = "select dijkstra(%s,%s,'band').asString() from V limit 1";
	public static final String SQL_GET_DIJKSTRA_PATH_FLATTEN  = "select @rid.asString() as rid, name, number, deviceName, type, datapathId, ofcIp, @class from "
			+ "(select flatten(dijkstra) from (select dijkstra(%s,%s,'used')))";
	public static final String SQL_GET_PATCH_WIRING2 = "select from patchWiring where outDeviceName = '%s' and inDeviceName = '%s'";
	public static final String SQL_GET_PATCH_CONNECTED_NODE = "select from patchWiring";
	public static final String SQL_GET_PORT = "select from port where @RID = %s";
	public static final String SQL_GET_PORT_INFO = "select from port where name = '%s' and deviceName = '%s'";
	public static final String SQL_GET_PORT_INFO2 = "select from port where number = %s and deviceName = '%s'";
	public static final String SQL_GET_PORT_INFO_FROM_PORT_NAME = "select @rid.asString(), name, number, deviceName from port where name = ? and deviceName = ?";
	public static final String SQL_GET_LINK = "select from link where out = %s and in = %s";

	public static final String SQL_GET_CABLE_LINK_FROM_PORT_RID = "select in.deviceName as inDeviceName, in.name as inPortName, in.number as inPortNumber, "
			+ "out.deviceName as outDeviceName, out.name as outPortName, out.number as outPortNumber, @RID.asString(), band, used "
			+ "from link where in.@RID = ? and in.@class='port' and out.@class='port'";
	public static final String SQL_GET_CABLE_LINKS    = "select in.deviceName as inDeviceName, in.name as inPortName, in.number as inPortNumber, "
			+ "out.deviceName as outDeviceName, out.name as outPortName, out.number as outPortNumber, @RID, band, used "
			+ "from link where in.deviceName = ? and out.@class = 'port'";
	public static final String SQL_GET_PATCH_WIRINGS_FROM_DEVICE_NAME           = "select from patchWiring where inDeviceName=?";
	public static final String SQL_GET_PATCH_WIRINGS_FROM_DEVICE_NAME_PORT_NAME = "select from patchWiring where inDeviceName=? and inPortName=?";
	public static final String SQL_GET_CONNECTED_LINK = "select from link where in = %s";
	public static final String SQL_IS_HAD_PATCH_WIRING = "select from patchWiring where parent = %s";
	public static final String SQL_IS_CONNECTED_PATCH_WIRING = "select from patchWiring where out = %s or in = %s";
	public static final String SQL_IS_CONTAINS_PATCH_WIRING = "select from patchWiring where outDeviceName = '%s' or inDeviceName = '%s'";
	public static final String SQL_GET_PATCH_CONNECTED_DEVICE_NAME = "select from patchWiring where inDeviceName = '%s'";

	public static final String SQL_GET_PORT_LIST = "select from port where deviceName = '%s'";

	public static final String SQL_GET_DEVICENAME_FROM_DATAPATHID = "select name from node where datapathId = ?";
	public static final String SQL_GET_PORTRID_FROM_DEVICENAME_PORTNUMBER = "select @RID.asString() as rid from port where deviceName = ? and number = ?";
	public static final String SQL_GET_PATCH_INFO_FROM_PATCHWIRING_PORTRID = "select from patchWiring where in = ?";
	public static final String SQL_GET_PATCH_INFO_FROM_PATCHWIRING_DEVICENAME_PORTNAME = "select out, in, parent, sequence from patchWiring where inDeviceName = ? and inPortName = ? and "
			+ "outDeviceName = ? and outPortName = ? order by sequence desc";
	public static final String SQL_GET_PORT_INFO_FROM_PORTRID = "select from port where @RID = ?";
	public static final String SQL_GET_DEVICE_INFO_FROM_DEVICERID = "select from node where @RID = ?";
	public static final String SQL_GET_INTERNALMAC_FROM_SRCMAC_DSTMAC_INPORT_DEVICENAME = "select internalMac from internalMacMap where deviceName = ? and inPort = ? and "
			+ "srcMac = ? and dstMac = ?";
	public static final String SQL_GET_MAX_INTERNALMAC = "select max(internalMac) as maxInternalMac from internalMacMap";

	/* insert */
	public static final String SQL_INSERT_PATCH_WIRING   = "insert into patchWiring(out, in, parent, outDeviceName, inDeviceName) values (%s, %s, %s, '%s', '%s')";
	public static final String SQL_INSERT_PATCH_WIRING_2 = "insert into patchWiring(out, in, parent, inDeviceName, inPortName, outDeviceName, outPortName, sequence) values (?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String SQL_INSERT_NODE      = "create vertex node set name = '%s', type = '%s', datapathId = '%s', ofcIp = '%s'";
	public static final String SQL_INSERT_NODE_INFO = "create vertex node set name = ?, type = ?, datapathId = ?, ofcIp = ?";
	public static final String SQL_INSERT_PORT      = "create vertex port set name = '%s', number = %s, deviceName = '%s'";
	public static final String SQL_INSERT_PORT_INFO = "create vertex port set name = ?, number = ?, devicename = ?";
	public static final String SQL_INSERT_LINK = "create edge link from %s to %s set band = %s, used = %s";

	public static final String SQL_INSERT_INTERNALMAC = "insert into internalMacMap(deviceName, inPort, srcMac, dstMac, internalMac) values (?, ?, ?, ?, ?)";

	/* delete */
	public static final String SQL_DELETE_PATCH_WIRING = "delete from patchWiring where outDeviceName = '%s' and inDeviceName = '%s'";
	public static final String SQL_DELETE_LINK = "delete edge link where out = %s and in = %s";
	public static final String SQL_DELETE_LINK_CONNECTED_PORT = "delete edge link where out = %s or in = %s";
	public static final String SQL_DELETE_PORT = "delete vertex port where name = '%s' and deviceName = '%s'";
	public static final String SQL_DELETE_PORT_DEViCE_NAME = "delete vertex port where deviceName = '%s'";
	public static final String SQL_DELETE_NODE = "delete vertex node where name = '%s'";
	public static final String SQL_DELETE_PATCH_WIRING_FROM_DEVICE_NAME_PORT_NAME = "delete from patchWiring where (inDeviceName=? and inPortName=?) or (outDeviceName=? and outPortName=?)";

	/* update */
	public static final String SQL_UPDATE_WEIGHT_TO_LINK = "update link set band = %s where out = %s and in = %s";
	public static final String SQL_UPDATE_NODE = "update node set name = '%s', datapathId = '%s', ofcIp = '%s' where @RID = %s";
	public static final String SQL_UPDATE_NODE_INFO = "update node set name = ?, datapathid = ? , ofcIp = ? where @RID = ?";
	public static final String SQL_UPDATE_PORT_DEVICE_NAME = "update port set deviceName = '%s' where deviceName = '%s'";
	public static final String SQL_UPDATE_PATCH_WIRING_IN_DEVICE  = "update patchWiring set  inDeviceName = '%s' where  inDeviceName = '%s'";
	public static final String SQL_UPDATE_PATCH_WIRING_OUT_DEVICE = "update patchWiring set outDeviceName = '%s' where outDeviceName = '%s'";
	public static final String SQL_UPDATE_PORT = "update port set name = '%s', number = %s where @RID = %s";
	public static final String SQL_UPDATE_CALBE_LINK_USED_VALUE_FROM_PORT_RID = "update link set used = ? where out.@class='port' and in.@class='port' and (in.@RID = ? or out.@RID = ?)";

	/* OFP Flag */
	public static final String OFP_FLAG_TRUE  = "true";
	public static final String OFP_FLAG_FALSE = "false";

}
