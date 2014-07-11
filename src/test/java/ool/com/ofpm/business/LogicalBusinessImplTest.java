package ool.com.ofpm.business;

import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.util.List;

import mockit.Delegate;
import mockit.NonStrictExpectations;
import ool.com.odbcl.client.OrientDBClientImpl;
import ool.com.odbcl.exception.GraphDBClientException;
import ool.com.odbcl.json.BaseResponse;
import ool.com.odbcl.json.LogicalTopology;
import ool.com.odbcl.json.LogicalTopologyGetJsonOut;
import ool.com.odbcl.json.Node;
import ool.com.util.Definition;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LogicalBusinessImplTest {
	private Gson gson = new Gson();
	private String testLogicalTopologyJsonOver = "{status:200, result:{nodes:[{deviceName:'novaNode01'},{deviceName:'novaNode02'},{deviceName:'novaNode03'}], links:[{deviceName:['novaNode01', 'novaNode02']},{deviceName:['novaNode01','novaNode03']}]}}";

	private LogicalTopologyGetJsonOut testLogicalTopologyOver;


	private String validLogicalTopologyJson = "{nodes:[{deviceName:'Sample1'}, {deviceName:'Sample2'}, {deviceName:'Sample3'}], links:[{deviceName:['Sample2', 'Sample3']}]}";
	private String currentTopologyJson = "{nodes:[{deviceName:'Sample1'}, {deviceName:'Sample2'}, {deviceName:'Sample3'}], links:[{deviceName:['Sample2', 'Sample3']}, {deviceName:['Sample4', 'Sample1']}]}";
	private LogicalTopology validTopology;
	private List<Node> validNodes;
	private LogicalTopology currentTopology;
	private LogicalTopologyGetJsonOut currentTopologyInOut = new LogicalTopologyGetJsonOut();

	public LogicalBusinessImplTest () {
		Type type = new TypeToken<LogicalTopology>(){}.getType();
		type = new TypeToken<LogicalTopology>(){}.getType();
		type = new TypeToken<LogicalTopologyGetJsonOut>(){}.getType();
		testLogicalTopologyOver = gson.fromJson(testLogicalTopologyJsonOver, type);

		type = new TypeToken<LogicalTopology>() {}.getType();
		validTopology = gson.fromJson(validLogicalTopologyJson, type);
		validNodes = validTopology.getNodes();
		currentTopology = gson.fromJson(currentTopologyJson, type);
		currentTopologyInOut.setResult(currentTopology);
		currentTopologyInOut.setStatus(Definition.STATUS_SUCCESS);
	}


	@SuppressWarnings("unchecked")
	@Test
	public void getLogicalTopologyTest() {
		final OrientDBClientImpl gdbClient = new OrientDBClientImpl("172.16.1.81:8080");//OrientDBClientImpl.getInstance();
		new NonStrictExpectations(gdbClient) {
			{
				try {
					gdbClient.getLogicalTopology((List<Node>) withNotNull());
					result = new GraphDBClientException("Test Exception");
					result = new Delegate() {
						@SuppressWarnings("unused")
						public LogicalTopologyGetJsonOut getLogicalTopology(List<Node> nodes) {
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
		LogicalTopologyGetJsonOut resLogiBiz;
		Type type = new TypeToken<LogicalTopologyGetJsonOut>(){}.getType();

		resLogiBizJson = logiBiz.getLogicalTopology(null);
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 400 when DeviceName is null.", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.getLogicalTopology("");
		resLogiBiz = gson.fromJson(resLogiBizJson, type);
		assertEquals("Must be status is 400 when DeviceName is empty(\"\").", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

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

	}

	/*
	 *
	 */
	@SuppressWarnings("unchecked")
	//@Test
	public void updateLogicalTopologyTest() {
		final OrientDBClientImpl gdbClient =  new OrientDBClientImpl("172.16.1.81:8080");//OrientDBClientImpl.getInstance();
		final AgentManager acm = AgentManager.getInstance();
		new NonStrictExpectations(gdbClient, acm) {
			{
				try {
					gdbClient.getLogicalTopology((List<Node>) withNotNull());
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
		BaseResponse resLogiBiz = null;
		String resLogiBizJson = null;

		resLogiBizJson = logiBiz.updateLogicalTopology(null);
		System.out.println(resLogiBizJson);
		resLogiBiz = BaseResponse.fromJson(resLogiBizJson);
		assertEquals("Must be status is 400 when Nodes is null.", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.updateLogicalTopology("");
		resLogiBiz = BaseResponse.fromJson(resLogiBizJson);
		assertEquals("Must be status is 400 when Nodes is empty(\"\").", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.updateLogicalTopology("{nodes:[{'deviceName':'Sample1'},{'deviceName':}], links:[]}");
		resLogiBiz = BaseResponse.fromJson(resLogiBizJson);
		assertEquals("Must be status is 400 when Node.deviceName is null.", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.updateLogicalTopology("{nodes:[{'deviceName':'Sample1'},{'deviceName':''}], links:[]}");
		resLogiBiz = BaseResponse.fromJson(resLogiBizJson);
		assertEquals("Must be status is 400 when Node.deviceName is empty('').", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.updateLogicalTopology("{nodes:[{'deviceName':'Sample1'},{'deviceName':'Sample2'},{'deviceName':'Sample1'}], links:[]}");
		resLogiBiz = BaseResponse.fromJson(resLogiBizJson);
		assertEquals("Must be status is 400 when Node is overlapping.", Definition.STATUS_BAD_REQUEST, resLogiBiz.getStatus());

		resLogiBizJson = logiBiz.updateLogicalTopology("{nodes:[{'deviceName':'Sample1'},{'deviceName':'Sample2'}], links:[{'deviceName':['Sample1','Sample3']}]}");
		resLogiBiz = BaseResponse.fromJson(resLogiBizJson);
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
