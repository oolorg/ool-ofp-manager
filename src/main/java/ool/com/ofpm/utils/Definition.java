/**
 * @author OOL 1131080355959
 * @date 2014/02/05
 * @TODO TODO
 */
package ool.com.ofpm.utils;

public class Definition {
	/* Define static http code */
    public static final int STATUS_SUCCESS        = 200;
    public static final int STATUS_CREATED        = 201;
    public static final int STATUS_BAD_REQUEST    = 400;
    public static final int STATUS_NOTFOUND       = 404;
    public static final int STATUS_INTERNAL_ERROR = 500;

	public static final String HTTP_METHOD_GET    = "GET";
	public static final String HTTP_METHOD_PUT    = "PUT";
	public static final String HTTP_METHOD_POST   = "POST";
	public static final String HTTP_METHOD_DELETE = "DELETE";

	/* Define property file */
	public static final String DEFAULT_PROPERTIY_FILE = "ofpm.properties";
	public static final String AGENT_CONFIG_FILE      = "agents.xml";

	/* Define agent manager config */
	public static final String AGENT_PATH   = "/ofpa/ctrl";
	public static final String AGENT_RECODE = "agent.recode";

	/* Define graph db url */
	public static final String GRAPH_DB_URL                        = "gdb.url";
	public static final String GRAPH_DB_LOGI_LINK_CONNECT_PATH     = "/orientdbservice/ofpatch/connect";
	public static final String GRAPH_DB_LOGI_LINK_DISCONNECT_PATH  = "/orientdbservice/ofpatch/disconnect";
	public static final String GRAPH_DB_NODE_CREATE_PATH           = "/orientdbservice/deviceManager/nodeCreate";
	public static final String GRAPH_DB_NODE_UPDATE_PATH           = "/orientdbservice/deviceManager/nodeUpdate";
	public static final String GRAPH_DB_NODE_DELETE_PATH           = "/orientdbservice/deviceManager/nodeDelete";
	public static final String GRAPH_DB_PORT_CREATE_PATH           = "/orientdbservice/deviceManager/portCreate";
	public static final String GRAPH_DB_PORT_UPDATE_PATH           = "/orientdbservice/deviceManager/portUpdate";
	public static final String GRAPH_DB_PORT_DELETE_PATH           = "/orientdbservice/deviceManager/portDelete";
	public static final String GRAPH_DB_LOGI_TOPO_GET_PATH         = "/orientdbservice/topology/logicalTopology";
	public static final String GRAPH_DB_PHYS_LINK_CONNECT_PATH     = "/orientdbservice/topology/physicalTopology/connect";
	public static final String GRAPH_DB_PHYS_LINK_DISCONNECT_PATH  = "/orientdbservice/topology/physicalTopology/disconnect";

	/* Define Open AM url */
	public static final String OPEN_AM_URL                         = "openam.url";

	/* Define validation parameters */
	public static final int COLLECT_NUMBER_OF_DEVICE_NAMES_IN_LINK = 2;
	public static final String[] ENABLE_OFP_FLAGS    = {"true", "false"};
	public static final String[] ENABLE_DEVICE_TYPES = {"Server", "Switch"};
	public static final String CSV_SPLIT_REGEX = ",";

}
