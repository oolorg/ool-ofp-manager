/**
 * @author OOL 1131080355959
 * @date 2014/02/06
 * @TODO
 */
package ool.com.ofpm.client;

import java.util.Set;

import ool.com.ofpm.json.BaseNode;
import ool.com.ofpm.json.GraphDBResult;
import ool.com.ofpm.json.LogicalTopology.LogicalLink;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;
import ool.com.ofpm.json.PatchLinkJsonIn;
import ool.com.ofpm.json.PhysicalRequestIn.NodeStatus;
import ool.com.ofpm.json.PhysicalRequestIn.NodeType;

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

	public void exec() {
		// TODO Auto-generated method stub

	}

	public GraphDBResult appendDevice(
			String deviceName,
			NodeType type) {
		// TODO Auto-generated method stub
		return null;
	}

	public GraphDBResult deleteDevice(
			String deviceName,
			NodeType type) {
		// TODO Auto-generated method stub
		return null;
	}

	public GraphDBResult updateDevice(
			String deviceName,
			NodeType type,
			NodeStatus status) {
		// TODO Auto-generated method stub
		return null;
	}

	public GraphDBResult appendPort(
			String deviceName,
			NodeType type,
			String portName) {
		// TODO Auto-generated method stub
		return null;
	}

	public GraphDBResult deletePort(
			String deviceName,
			NodeType type,
			String portName) {
		// TODO Auto-generated method stub
		return null;
	}

	public GraphDBResult updatePort(
			String deviceName,
			NodeType type,
			String portName,
			NodeStatus status) {
		// TODO Auto-generated method stub
		return null;
	}

	public LogicalTopologyJsonInOut getLogicalTopology(
			Set<BaseNode> nodes)
			throws GraphDBClientException {
		// TODO Auto-generated method stub
		return null;
	}

	public PatchLinkJsonIn addLogicalLink(
			LogicalLink link)
			throws GraphDBClientException {
		// TODO Auto-generated method stub
		return null;
	}

	public PatchLinkJsonIn delLogicalLink(
			LogicalLink link)
			throws GraphDBClientException {
		// TODO Auto-generated method stub
		return null;
	}



}
