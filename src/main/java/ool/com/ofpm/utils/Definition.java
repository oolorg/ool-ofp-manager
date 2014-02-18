/**
 * @author OOL 1131080355959
 * @date 2014/02/05
 * @TODO TODO
 */
package ool.com.ofpm.utils;

/**
 * @author 1131080355959
 *
 */
public class Definition {
	public static final String DEFAULT_PROPERTIY_FILE = "ofm.properties";
	public static final String OLD_TEST_APP_ADDR = "http://192.168.1.219:8080";

    public static final int STATUS_SUCCESS = 200;
    public static final int STATUS_CREATED = 201;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_NOTFOUND = 404;
    public static final int STATUS_INTERNAL_ERROR = 500;

	// Graph DBに関する設定
	public static String GRAPH_DB_ADDRESS           = "http://192.168.1.120:8080";
	public static String GRAPH_DB_LINK_GET          = "/orientdbservice/topology/logicalTopology";
	public static String GRAPH_DB_LINK_CREATE_PATH  = "/orientdbservice/ofpatch/connect";
	public static String GRAPH_DB_LINK_DELETE_PATH  = "/orientdbservice/ofpatch/disconnect";

	public static final String AGENT_PATH = "/ofpa/ctrl";

}
