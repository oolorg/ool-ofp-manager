/**
 * @author OOL 1131080355959
 * @date 2014/02/06
 * @TODO
 */
package ool.com.ofpm.client;

import java.util.List;

import ool.com.ofpm.json.BaseNode;
import ool.com.ofpm.json.LogicalTopology.LogicalLink;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;
import ool.com.ofpm.json.PatchLinkJsonIn;

/**
 * @author 1131080355959
 *
 */
public class Neo4jDBClientImpl implements GraphDBClient {

	private static Neo4jDBClientImpl instance = null;

	private Neo4jDBClientImpl() {
	}

	public static Neo4jDBClientImpl getInstance() {
		if (instance == null) {
			instance = new Neo4jDBClientImpl();
		}
		return instance;
	}

	@Override
	public LogicalTopologyJsonInOut getLogicalTopology(
			List<BaseNode> nodes)
			throws GraphDBClientException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PatchLinkJsonIn addLogicalLink(
			LogicalLink link)
			throws GraphDBClientException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PatchLinkJsonIn delLogicalLink(
			LogicalLink link)
			throws GraphDBClientException {
		// TODO Auto-generated method stub
		return null;
	}

}
