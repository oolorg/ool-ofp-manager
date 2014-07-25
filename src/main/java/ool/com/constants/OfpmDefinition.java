/**
 * @author OOL 1131080355959
 * @date 2014/02/05
 * @TODO TODO
 */
package ool.com.constants;

public class OfpmDefinition {
	/* Define static http code */
	public static final int STATUS_SUCCESS        = 200;
	public static final int STATUS_CREATED        = 201;
	public static final int STATUS_BAD_REQUEST    = 400;
	public static final int STATUS_FORBIDDEN      = 403;
	public static final int STATUS_NOTFOUND       = 404;
	public static final int STATUS_CONFLICT       = 409;
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

	/* Define Open AM url */
	public static final String OPEN_AM_URL                         = "openam.url";

	/* Define Device Manager url */
	public static final String DEVICE_MANAGER_URL                  = "devicemanager.url";

	/* Define Network Config Setupper url */
	public static final String NETWORK_CONFIG_SETUPPER_URL         = "networkconfigsetupper.url";
	public static final String NCS_PLANE_SW_CONFIG                 = "/plane_sw_config";

	/* Define Database url */
	public static final String CONFIG_KEY_DB_URL = "db.url";
	public static final String CONFIG_KEY_DB_USER= "db.user";
	public static final String CONFIG_KEY_DB_PASSWORD = "db.password";
	public static final String DB_DEFAULT = "admin";

	/* Define Open AM admin user */
	public static final String OPEN_AM_ADMIN_USER_ID               = "amadmin";
	public static final String OPEN_AM_ADMIN_USER_PW               = "okinawa1940";

	/* Define host name */
	public static final String D_PLANE_SW_HOST_NAME                = "D-Plane_SW01";
	public static final String OFP_SW_HOST_NAME                    = "OFP_SW01";

	/* Define validation parameters */
	public static final int COLLECT_NUMBER_OF_DEVICE_NAMES_IN_LINK = 2;
	public static final String[] ENABLE_OFP_FLAGS    = {"true", "false"};
	public static final String[] ENABLE_DEVICE_TYPES = {"Server", "Switch", "Leaf", "Spine"};
	public static final String CSV_SPLIT_REGEX = ",";
	public static final String REGEX_NUMBER = "[0-9]+";
	public static final String REGEX_DATAPATH_ID = "[0-9a-fA-F]{1,16}";
	
	/* Define max macaddress value */
	public static final int MIN_MACADDRESS_VALUE = 1;
	public static final long MAX_MACADDRESS_VALUE = 281474976710655L;
}
