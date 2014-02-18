package ool.com.ofpm.utils;

import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.util.Set;

import mockit.Expectations;
import mockit.NonStrictExpectations;
import ool.com.ofpm.business.LogicalBusiness;
import ool.com.ofpm.business.LogicalBusinessImpl;
import ool.com.ofpm.client.GraphDBClientException;
import ool.com.ofpm.client.OrientDBClientImpl;
import ool.com.ofpm.json.BaseNode;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.LogicalTopology;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;
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
	@Test
	public void testDoGET() {
		final OrientDBClientImpl gdbClient = OrientDBClientImpl.getInstance();
		new NonStrictExpectations(gdbClient) {
			{
				try {
					gdbClient.getLogicalTopology((Set<BaseNode>) withNotNull());
					result = testLogicalTopologyOver;
				} catch (GraphDBClientException gdbe) {
					fail("GraphDBより予期しないエラーです");
				}
			}
		};

		LogicalBusiness logiBiz = new LogicalBusinessImpl();
		LogicalTopologyJsonInOut bizOut = logiBiz.doGET(testLogicalTopologyQueryIn);
		if(!bizOut.getResult().equals(testLogicalTopology)) {
			fail("Filterされていない要素があります。");
		}
		if(bizOut.getStatus() != 200) {
			fail("応答Statusが正常値でありません");
		}
	}

	/*
	 * GraphDBよりExceptionが返答された場合、つめなおして応答するか確認します。
	 */
	@Test
	public void testDoGETcheckGDBException() {
		final OrientDBClientImpl gdbClient = OrientDBClientImpl.getInstance();
		new Expectations(gdbClient) {
			{
				try {
					gdbClient.getLogicalTopology((Set<BaseNode>) withNotNull());
					result = new GraphDBClientException("ノードが見つかりません", 404);
				} catch (GraphDBClientException gdbe) {
					fail("GraphDBより予期しないエラーです");
				}
			}
		};

		LogicalBusiness logiBiz = new LogicalBusinessImpl();
		LogicalTopologyJsonInOut bizOut = logiBiz.doGET(testLogicalTopologyQueryIn);
		if(bizOut.getStatus() != 404) {
			fail("404を応答しなければなりません。");
		}
	}

	/*
	 * validatorからExceptionが返答された場合、400エラーを返送するか確認です。
	 */
	//@Test
	public void testDoGETcheckValidateException() {
		new Expectations() {
			LogicalTopologyValidate validator;
			{
				try {
					new LogicalTopologyValidate();
					validator.checkValidationGET((LogicalTopology) withNotNull());
					result = new ValidateException("不正なパラメータです");
				} catch (Exception e) {
					fail("Validatorより予期しないエラーです");
				}
			}
		};

		LogicalBusiness logiBiz = new LogicalBusinessImpl();
		LogicalTopologyJsonInOut bizOut = logiBiz.doGET(testLogicalTopologyQueryIn);
		if(bizOut.getStatus() != 400) {
			fail("400を応答しなければなりません");
		}
	}

	/*
	 *
	 */
	@Test
	public void testDoPUT() {
		final OrientDBClientImpl gdbClient = OrientDBClientImpl.getInstance();
		new NonStrictExpectations(gdbClient) {
			LogicalTopologyValidate validator;
			{
				try {
					new LogicalTopologyValidate();
					validator.checkValidation((LogicalTopology) withNotNull());

					gdbClient.getLogicalTopology((Set<BaseNode>) withNotNull());
					result = testLogicalTopologyOver;

					validator.checkValidation((LogicalTopology) withNotNull());
					result = new ValidateException("");

				} catch (Exception e) {
					fail("予期しないエラーです");
				}
			}
		};

		LogicalBusiness logiBiz = new LogicalBusinessImpl();
		BaseResponse bizOut = logiBiz.doPUT(testLogicalTopology);
		if(bizOut.getStatus() != Definition.STATUS_CREATED) {
			//fail("応答Statusが正常値でありません");
		}
	}
}
