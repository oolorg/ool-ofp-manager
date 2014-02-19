package ool.com.ofpm.utils;

import static org.junit.Assert.*;

import java.lang.reflect.Type;

import mockit.Delegate;
import mockit.Expectations;
import ool.com.ofpm.business.LogicalBusinessImpl;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.LogicalTopology;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;
import ool.com.ofpm.service.LogicalService;
import ool.com.ofpm.service.LogicalServiceImpl;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LogicalServiceImplTest {

	private Gson gson = new Gson();
	private String testLogicalTopologyJsonIn = "{'nodes':[{deviceName:'novaNode01'},{deviceName:'novaNode02'}]}";
	private String testLogicalTopologyJson = "{nodes:[{deviceName:'novaNode01'},{deviceName:'novaNode02'}], links:[{deviceName:['novaNode01', 'novaNode02']}]}";
	private String testBaseResponseJson = "{status:201, message:''}";
	private LogicalTopology testLogicalTopologyIn;
	private LogicalTopology testLogicalTopology;
	private LogicalTopologyJsonInOut testLogicalJsonOut;
	private BaseResponse testBaseResponse;
	private String[] testLogicalTopologyQueryIn = {"novaNode01", "novaNode02"};

	public LogicalServiceImplTest() {
		Type type = new TypeToken<LogicalTopology>(){}.getType();
		testLogicalTopologyIn = gson.fromJson(testLogicalTopologyJsonIn, type);
		testLogicalTopology = gson.fromJson(testLogicalTopologyJson, type);
		testLogicalJsonOut = new LogicalTopologyJsonInOut();
		testLogicalJsonOut.setResult(testLogicalTopology);
		type = new TypeToken<BaseResponse>() {}.getType();
		testBaseResponse = gson.fromJson(testBaseResponseJson, type);
	}



	@Test
	public void testDoGET() {
		new Expectations() {
			LogicalBusinessImpl logiBiz;
			{
				new LogicalBusinessImpl();
				logiBiz.getLogicalTopology((String[]) withNotNull());
				result = new Delegate() {
					@SuppressWarnings("unused")
					LogicalTopologyJsonInOut doGET(String[] params) {
						for(String param : params) {
							if(param == null) fail();
							if(param == "") fail();
						}
						for(int i = 0; i < params.length; i++ ) {
							System.out.println("params[" + i + "] : " +params[i]);
						}
						return testLogicalJsonOut;
					}
				};

			}
		};

		LogicalService ls = new LogicalServiceImpl();
		ls.getLogicalTopology("novaNode01,novaNode02");
	}

	/*
	 *
	 */
	@Test
	public void testDoPUT() {
		new Expectations() {
			LogicalBusinessImpl logiBiz;
			{
				new LogicalBusinessImpl();
				logiBiz.updateLogicalTopology((LogicalTopology) withNotNull());
				result = new Delegate() {
					@SuppressWarnings("unused")
					BaseResponse doPUT(LogicalTopology topology) {
						if(!topology.equals(testLogicalTopology)) fail();
						return testBaseResponse;
					}
				};
			}
		};

		LogicalService ls = new LogicalServiceImpl();
		ls.updateLogicalTopology(testLogicalTopologyJson);
	}
}
