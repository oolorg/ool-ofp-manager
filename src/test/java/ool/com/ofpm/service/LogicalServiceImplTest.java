package ool.com.ofpm.service;

import static org.junit.Assert.*;

import java.lang.reflect.Type;

import mockit.Expectations;
import mockit.NonStrictExpectations;
import ool.com.ofpm.business.LogicalBusinessImpl;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.LogicalTopologyGetJsonOut;
import ool.com.ofpm.json.LogicalTopology;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LogicalServiceImplTest {

	private Gson gson = new Gson();

	private String testLogicalTopologyJson = "{nodes:[{deviceName:'novaNode01'},{deviceName:'novaNode02'}], links:[{deviceName:['novaNode01', 'novaNode02']}]}";
	private String testLogicalTopologyOutJson = "{status:200, message:'null', result:{nodes:[{deviceName:'novaNode01'},{deviceName:'novaNode02'}], links:[{deviceName:['novaNode01', 'novaNode02']}]}}";
	private LogicalTopologyGetJsonOut testLogicalTopologyOut;
	private String validBaseResponseJson = "{status:201, message:''}";
	private BaseResponse validBaseResponse;

	public LogicalServiceImplTest() {
		Type type = new TypeToken<LogicalTopology>(){}.getType();
		type = new TypeToken<LogicalTopologyGetJsonOut>(){}.getType();
		testLogicalTopologyOut = gson.fromJson(testLogicalTopologyOutJson, type);
		type = new TypeToken<BaseResponse>() {}.getType();
		validBaseResponse = gson.fromJson(validBaseResponseJson, type);
	}

	@Test
	public void getLogicalTopologyTest() {
		new NonStrictExpectations() {
			LogicalBusinessImpl logiBiz;
			{
				new LogicalBusinessImpl();
				logiBiz.getLogicalTopology((String) withNotNull());
				result = testLogicalTopologyOutJson;
			}
		};

		Type type = new TypeToken<LogicalTopologyGetJsonOut>(){}.getType();
		LogicalService ls = new LogicalServiceImpl();

		String res = ls.getLogicalTopology("test");
		LogicalTopologyGetJsonOut topoOut = gson.fromJson((String)res, type);
		assertEquals(topoOut, testLogicalTopologyOut);
	}

	@Test
	public void updateLogicalTopologyTest() {
		new Expectations() {
			LogicalBusinessImpl logiBiz;
			{
				new LogicalBusinessImpl();
				logiBiz.updateLogicalTopology((String) withNotNull());
				result = validBaseResponseJson;
			}
		};

		LogicalService ls = new LogicalServiceImpl();
		String res = ls.updateLogicalTopology(testLogicalTopologyJson);
		BaseResponse resOut = BaseResponse.fromJson(res);
		assertEquals(resOut, validBaseResponse);
	}
}

