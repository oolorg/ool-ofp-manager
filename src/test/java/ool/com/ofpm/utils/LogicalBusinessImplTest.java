package ool.com.ofpm.utils;

import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.util.List;

import mockit.Delegate;
import mockit.NonStrictExpectations;
import ool.com.ofpm.business.LogicalBusiness;
import ool.com.ofpm.business.LogicalBusinessImpl;
import ool.com.ofpm.client.AgentClient;
import ool.com.ofpm.client.GraphDBClientException;
import ool.com.ofpm.client.OrientDBClientImpl;
import ool.com.ofpm.json.AgentFlowJsonOut;
import ool.com.ofpm.json.BaseNode;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.LogicalTopology;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;
import ool.com.ofpm.validate.CommonValidate;
import ool.com.ofpm.validate.LogicalTopologyValidate;
import ool.com.ofpm.validate.ValidateException;

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

	public LogicalBusinessImplTest () {
		Type type = new TypeToken<LogicalTopology>(){}.getType();
		testLogicalTopologyIn = gson.fromJson(testLogicalTopologyJsonIn, type);
		type = new TypeToken<LogicalTopology>(){}.getType();
		testLogicalTopology = gson.fromJson(testLogicalTopologyJson, type);
		type = new TypeToken<LogicalTopologyJsonInOut>(){}.getType();
		testLogicalTopologyOver = gson.fromJson(testLogicalTopologyJsonOver, type);
		testLogicalTopologyNull = gson.fromJson(testLogicalTopologyJsonNull, type);
	}

	/*
	 * Filterが機能し、要求外のノードを含むlinksやnodesの要素を削除するか
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void getLogicalTopologyTest() {
		final OrientDBClientImpl gdbClient = OrientDBClientImpl.getInstance();
		new NonStrictExpectations(gdbClient) {
			CommonValidate validator;
			{
				try {
					// ここではGraphDBやClientなどに渡すデータが正常に整形されているかを確認しなければなりません
					validator.checkDeviceNameArray((String[]) withNotNull());
					result = null;
					result = new ValidateException("Bad request");
					result = null;

					gdbClient.getLogicalTopology((List<BaseNode>) withNotNull());
					result = testLogicalTopologyOver;
					result = new GraphDBClientException("Node not Find", Definition.STATUS_BAD_REQUEST);

				} catch (GraphDBClientException gdbe) {
					fail("Unexpected GraphDBClientException.");
				} catch (ValidateException ve) {
					fail("Unexpected ValidatenException.");
				}
			}
		};

		LogicalBusiness logiBiz = new LogicalBusinessImpl();
		LogicalTopologyJsonInOut resLogiBiz;

		resLogiBiz = logiBiz.getLogicalTopology(testLogicalTopologyQueryIn);
		assertEquals(resLogiBiz.getStatus(), Definition.STATUS_SUCCESS);

		resLogiBiz = logiBiz.getLogicalTopology(testLogicalTopologyQueryIn);
		assertEquals(resLogiBiz.getStatus(), Definition.STATUS_BAD_REQUEST);

		resLogiBiz = logiBiz.getLogicalTopology(testLogicalTopologyQueryIn);
		assertEquals(resLogiBiz.getStatus(), Definition.STATUS_INTERNAL_ERROR);
	}

	/*
	 *
	 */
	//@Test
	public void updateLogicalTopologyTest() {
		final OrientDBClientImpl gdbClient = OrientDBClientImpl.getInstance();
		new NonStrictExpectations(gdbClient) {
			LogicalTopologyValidate validator;
			AgentClient client;
			{
				try {
					new LogicalTopologyValidate();
					validator.checkValidationRequestIn((LogicalTopology) withNotNull());
					result = null;
					result = new ValidateException();

					gdbClient.getLogicalTopology((List<BaseNode>) withNotNull());
					result = testLogicalTopologyOver;

					client.updateFlows((AgentFlowJsonOut) withNotNull());
					result = new Delegate() {
						BaseResponse updateFlows(AgentFlowJsonOut in) {
							BaseResponse res = new BaseResponse();
							res.setStatus(Definition.STATUS_SUCCESS);
							return res;
						}
					};


				} catch (Exception e) {
					fail(e.getMessage());
				}
			}
		};

		LogicalBusiness logiBiz = new LogicalBusinessImpl();
		BaseResponse resLogiBiz;

		resLogiBiz = logiBiz.updateLogicalTopology(testLogicalTopology);
		assertEquals(resLogiBiz.getStatus(), Definition.STATUS_CREATED);

		resLogiBiz = logiBiz.updateLogicalTopology(testLogicalTopology);
		assertEquals(resLogiBiz.getStatus(), Definition.STATUS_BAD_REQUEST);
	}
}
