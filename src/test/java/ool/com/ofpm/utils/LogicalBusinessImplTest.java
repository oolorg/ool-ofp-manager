package ool.com.ofpm.utils;

import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.util.List;

import mockit.Delegate;
import mockit.NonStrictExpectations;
import ool.com.ofpm.business.AgentManager;
import ool.com.ofpm.business.LogicalBusiness;
import ool.com.ofpm.business.LogicalBusinessImpl;
import ool.com.ofpm.client.GraphDBClientException;
import ool.com.ofpm.client.OrientDBClientImpl;
import ool.com.ofpm.json.BaseNode;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.LogicalTopology;
import ool.com.ofpm.json.LogicalTopology.LogicalLink;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LogicalBusinessImplTest {
	private Gson gson = new Gson();
	private String testLogicalTopologyJsonIn   = "{nodes:[{deviceName:'novaNode01'},{deviceName:'novaNode02'}]}";
	private String testLogicalTopologyJson     = "{nodes:[{deviceName:'novaNode01'},{deviceName:'novaNode02'}], links:[{deviceName:['novaNode01', 'novaNode02']}]}";
	private String testLogicalTopologyJsonOver = "{status:200, result:{nodes:[{deviceName:'novaNode01'},{deviceName:'novaNode02'},{deviceName:'novaNode03'}], links:[{deviceName:['novaNode01', 'novaNode02']},{deviceName:['novaNode01','novaNode03']}]}}";
	private String testLogicalTopologyJsonNull = "{status:400, message:'bad request!'}";
	private String[] testLogicalTopologyQueryIn  = {"novaNode01","novaNode02"};

	private LogicalTopology testLogicalTopologyIn;
	private LogicalTopology testLogicalTopology;
	private LogicalTopologyJsonInOut testLogicalTopologyOver;
	private LogicalTopologyJsonInOut testLogicalTopologyNull;


	//private String currentLogicalTopologyJson = "{nodes:[{deviceName:'Sample1', deviceName:'Sample2', deviceName:'Sample3'}], links:[{deviceName:['Sample1', 'Sample2']}}]}";
	private String validLogicalTopologyJson = "{nodes:[{deviceName:'Sample1'}, {deviceName:'Sample2'}, {deviceName:'Sample3'}], links:[{deviceName:['Sample2', 'Sample3']}]}";
	private String requestedTopologyJson = validLogicalTopologyJson;
	private String currentTopologyJson = "{nodes:[{deviceName:'Sample1'}, {deviceName:'Sample2'}, {deviceName:'Sample3'}], links:[{deviceName:['Sample2', 'Sample3']}, {deviceName:['Sample4', 'Sample1']}]}";
	private LogicalTopology validTopology;
	private List<BaseNode> validNodes;
	private List<LogicalLink> validLinks;
	private LogicalTopology currentTopology;
	private LogicalTopologyJsonInOut currentTopologyInOut = new LogicalTopologyJsonInOut();

	public LogicalBusinessImplTest () {
		Type type = new TypeToken<LogicalTopology>(){}.getType();
		testLogicalTopologyIn = gson.fromJson(testLogicalTopologyJsonIn, type);
		type = new TypeToken<LogicalTopology>(){}.getType();
		testLogicalTopology = gson.fromJson(testLogicalTopologyJson, type);
		type = new TypeToken<LogicalTopologyJsonInOut>(){}.getType();
		testLogicalTopologyOver = gson.fromJson(testLogicalTopologyJsonOver, type);
		testLogicalTopologyNull = gson.fromJson(testLogicalTopologyJsonNull, type);

		type = new TypeToken<LogicalTopology>() {}.getType();
		validTopology = gson.fromJson(validLogicalTopologyJson, type);
		validNodes = validTopology.getNodes();
		validLinks = validTopology.getLinks();
		currentTopology = gson.fromJson(currentTopologyJson, type);
		currentTopologyInOut.setResult(currentTopology);
		currentTopologyInOut.setStatus(Definition.STATUS_SUCCESS);
	}

	/*
	 * Filterが機能し、要求外のノードを含むlinksやnodesの要素を削除するか
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void getLogicalTopologyTest() {
		final OrientDBClientImpl gdbClient = OrientDBClientImpl.getInstance();
		new NonStrictExpectations(gdbClient) {
			{
				try {
					gdbClient.getLogicalTopology((List<BaseNode>) withNotNull());
					result = new GraphDBClientException("Test Exception");
					result = new Delegate() {
						@SuppressWarnings("unused")
						public LogicalTopologyJsonInOut getLogicalTopology(List<BaseNode> nodes) {
							assertNotNull("There is nodes, must be not null.", nodes);
							assertEquals("Different number of input nodes bitween valid node.", validNodes.size(), nodes.size());
							assertTrue("There is node that is not contain to validNodes.", validNodes.containsAll(nodes));
							return currentTopologyInOut;
						}
					};

				} catch (GraphDBClientException gdbe) {
					fail("Unexpected GraphDBClientException.");
				}
			}
		};

		LogicalBusiness logiBiz = new LogicalBusinessImpl();
		String resLogiBizJson;
		LogicalTopologyJsonInOut resLogiBiz;
		Type type = new TypeToken<LogicalTopologyJsonInOut>(){}.getType();

		resLogiBizJson = logiBiz.getLogicalTopology(null);
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 400 when DeviceName is null.", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.getLogicalTopology("");
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 400 when DeviceName is empty(\"\").", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.getLogicalTopology("Sample1, Sample2");
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 400 when DeviceName begins with space.", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.getLogicalTopology("Sample1 ,Sample2");
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 400 when DeviceName ending with space.", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.getLogicalTopology("Sample1,,Sample2");
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 400 when DeviceNames contains ,,.", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.getLogicalTopology("Sample1, ,Sample2");
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 400 when DeviceNames contains , ,.", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.getLogicalTopology("Sample1,Sample2,Sample1");
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 400 when DeviceName is overlapping.", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.getLogicalTopology("Sample1,Sample2,Sample3");
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 500 when catch GraphDBClientException.", Definition.STATUS_INTERNAL_ERROR, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.getLogicalTopology("Sample1,Sample2,Sample3");
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 200 when process complete.", Definition.STATUS_SUCCESS, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.getLogicalTopology("Sample1,Sample2,Sample3");
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 200 when process complete.", Definition.STATUS_SUCCESS, resLogiBiz.getStatus());
		assertEquals("Check response data that is filtered.", validTopology, resLogiBiz.getResult());

	}

	/*
	 *
	 */
	@Test
	public void updateLogicalTopologyTest() {
		final OrientDBClientImpl gdbClient = OrientDBClientImpl.getInstance();
		final AgentManager acm = AgentManager.getInstance();
		new NonStrictExpectations(gdbClient, acm) {
			{
				try {
					gdbClient.getLogicalTopology((List<BaseNode>) withNotNull());
					result = testLogicalTopologyOver;
//
//					client.updateFlows((AgentUpdateFlowRequest) withNotNull());
//					result = new Delegate() {
//						@SuppressWarnings("unused")
//						BaseResponse updateFlows(AgentUpdateFlowRequest in) {
//							BaseResponse res = new BaseResponse();
//							res.setStatus(Definition.STATUS_SUCCESS);
//							return res;
//						}
//					};


				} catch (Exception e) {
					fail(e.getMessage());
				}
			}
		};

		LogicalBusiness logiBiz = new LogicalBusinessImpl();
		Type type = new TypeToken<BaseResponse>(){}.getType();
		BaseResponse resLogiBiz = null;
		String resLogiBizJson = null;

		resLogiBizJson = logiBiz.updateLogicalTopology(null);
		System.out.println(resLogiBizJson);
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 400 when Nodes is null.", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.updateLogicalTopology("");
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 400 when Nodes is empty(\"\").", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.updateLogicalTopology("{nodes:[{'deviceName':'Sample1'},{'deviceName':}], links:[]}");
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 400 when Node.deviceName is null.", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.updateLogicalTopology("{nodes:[{'deviceName':'Sample1'},{'deviceName':''}], links:[]}");
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 400 when Node.deviceName is empty('').", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.updateLogicalTopology("{nodes:[{'deviceName':'Sample1'},{'deviceName':'Sample2'},{'deviceName':'Sample1'}], links:[]}");
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 400 when Node is overlapping.", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.updateLogicalTopology("{nodes:[{'deviceName':'Sample1'},{'deviceName':'Sample2'}], links:[{'deviceName':['Sample1','Sample3']}]}");
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 400 when There is Links that is not contain Node.", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

//		resLogiBizJson = logiBiz.updateLogicalTopology("{nodes:[{'deviceName':'Sample1'},{'deviceName':'Sample2'}], links:[{'deviceName':['Sample1',]}]}");
//		resLogiBiz = gson.fromJson(resLogiBizJson, type);
//		assertEquals("Must be status is 400 when There is Links.deviceName contain null.", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());
//
//		resLogiBizJson = logiBiz.updateLogicalTopology("{nodes:[{'deviceName':'Sample1'},{'deviceName':'Sample2'}], links:[{'deviceName':['Sample1','']}]}");
//		resLogiBiz = gson.fromJson(resLogiBizJson, type);
//		assertEquals("Must be status is 400 when There is Links.deviceName contain empty('').", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());
//
//		resLogiBizJson = logiBiz.updateLogicalTopology("{nodes:[{'deviceName':'Sample1'},{'deviceName':'Sample2'}], links:[{'deviceName':['Sample1','Sample2']}]}");
//		resLogiBiz = gson.fromJson(resLogiBizJson, type);
//		assertEquals("Must be status is 500 when catch GraphDBClientException.", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());
	}
}
