package ool.com.ofpm.utils;

import static org.junit.Assert.*;

import java.lang.reflect.Type;

import javax.ws.rs.core.Response;

import mockit.Delegate;
import mockit.Expectations;
import mockit.NonStrictExpectations;
import ool.com.ofpm.business.LogicalBusinessImpl;
import ool.com.ofpm.json.BaseResponse;
import ool.com.ofpm.json.LogicalTopology;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;
import ool.com.ofpm.service.LogicalService;
import ool.com.ofpm.service.LogicalServiceImpl;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LogicalServiceImplTest {

	private Gson gson = new Gson();
	private String testLogicalTopologyJsonIn = "{'nodes':[{deviceName:'novaNode01'},{deviceName:'novaNode02'}]}";
	private String testLogicalTopologyJson = "{nodes:[{deviceName:'novaNode01'},{deviceName:'novaNode02'}], links:[{deviceName:['novaNode01', 'novaNode02']}]}";
	private String testLogicalTopologyOutJson = "{status:200, message:'null', result:{nodes:[{deviceName:'novaNode01'},{deviceName:'novaNode02'}], links:[{deviceName:['novaNode01', 'novaNode02']}]}}";
	private String testBaseResponseJson = "{status:201, message:''}";
	private LogicalTopology testLogicalTopologyIn;
	private LogicalTopology testLogicalTopology;
	private LogicalTopologyJsonInOut testLogicalTopologyOut;
	private BaseResponse testBaseResponse;
	private String[] testLogicalTopologyQueryIn = {"novaNode01", "novaNode02"};

	public LogicalServiceImplTest() {
		Type type = new TypeToken<LogicalTopology>(){}.getType();
		testLogicalTopologyIn = gson.fromJson(testLogicalTopologyJsonIn, type);
		testLogicalTopology = gson.fromJson(testLogicalTopologyJson, type);
		type = new TypeToken<LogicalTopologyJsonInOut>(){}.getType();
		testLogicalTopologyOut = gson.fromJson(testLogicalTopologyOutJson, type);
		type = new TypeToken<BaseResponse>() {}.getType();
		testBaseResponse = gson.fromJson(testBaseResponseJson, type);
	}



	@Test
	public void testGetLogicalTopology() {
		new NonStrictExpectations() {
			LogicalBusinessImpl logiBiz;
			{
				new LogicalBusinessImpl();
				logiBiz.getLogicalTopology((String[]) withNotNull());
				result = new Delegate() {
					@SuppressWarnings("unused")
					LogicalTopologyJsonInOut getLogicalTopology(String[] params) {
						for(String param : params) {
							if(StringUtils.isBlank(param)) fail();
						}
						if(!params[0].equals("novaNode01")) fail();
						if(!params[1].equals(" novaNode02")) fail();
						return testLogicalTopologyOut;
					}
				};
			}
		};

		Type type = new TypeToken<LogicalTopologyJsonInOut>(){}.getType();
		LogicalService ls = new LogicalServiceImpl();

		Response res = ls.getLogicalTopology("novaNode01, novaNode02");
		LogicalTopologyJsonInOut topoOut = gson.fromJson((String)res.getEntity(), type);
		if(! this.testLogicalTopologyOut.equals(topoOut)) fail();

		// unexpected null input, must not be throwing Exception.
//		res = ls.getLogicalTopology(null);
//		topoOut = gson.fromJson((String)res.getEntity(), type);
//		if(topoOut.getStatus() != Definition.STATUS_BAD_REQUEST) fail();
	}

	/*
	 *
	 */
	@Test
	public void testUpdateLogicalTopology() {
		new Expectations() {
			LogicalBusinessImpl logiBiz;
			{
				new LogicalBusinessImpl();
				logiBiz.updateLogicalTopology((LogicalTopology) withNotNull());
				result = new Delegate() {
					@SuppressWarnings("unused")
					BaseResponse updateLogicalTopology(LogicalTopology topology) {
						if(!topology.equals(testLogicalTopology)) fail();
						return testBaseResponse;
					}
				};
			}
		};

		Type type = new TypeToken<BaseResponse>(){}.getType();

		LogicalService ls = new LogicalServiceImpl();
		Response res = ls.updateLogicalTopology(testLogicalTopologyJson);
		BaseResponse brOut = gson.fromJson((String)res.getEntity(), type);
		//if(! this.testBaseResponse.equals(brOut)) fail();

		// unexpected json input, must not be throwing exception
//		res = ls.updateLogicalTopology("[]");
//		brOut = gson.fromJson((String)res.getEntity(), type);
//		if(! this.testBaseResponse.equals(brOut)) fail();

	}
}
