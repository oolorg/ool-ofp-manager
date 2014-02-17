package ool.com.ofpm.utils;

import static org.junit.Assert.*;

import java.lang.reflect.Type;

import mockit.NonStrictExpectations;
import ool.com.ofpm.client.GraphDBClient;
import ool.com.ofpm.client.GraphDBClientException;
import ool.com.ofpm.client.OrientDBClientImpl;
import ool.com.ofpm.json.LogicalTopology;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.ClientResponse;

public class OrientDBClientImplTest {
	private Gson gson = new Gson();
	private String testLogicalTopologyJsonIn   = "{nodes:[{deviceName:'novaNode01'},{deviceName:'novaNode02'}]}";
	private String testLogicalTopologyJson     = "{nodes:[{deviceName:'novaNode01'},{deviceName:'novaNode02'}], links:[{deviceName:['novaNode01', 'novaNode02']}]}";
	private String testLogicalTopologyJsonOver = "{nodes:[{deviceName:'novaNode01'},{deviceName:'novaNode02'},{deviceName:'novaNode03'}], links:[{deviceName:['novaNode01', 'novaNode02']},{deviceName:['novaNode01','novaNode03']}]}";


	private LogicalTopology testLogicalTopologyIn;
	private LogicalTopology testLogicalTopology;
	private LogicalTopology testLogicalTopologyOver;

	public OrientDBClientImplTest () {
		Type type = new TypeToken<LogicalTopology>(){}.getType();
		testLogicalTopologyIn = gson.fromJson(testLogicalTopologyJsonIn, type);
		type = new TypeToken<LogicalTopology>(){}.getType();
		testLogicalTopology = gson.fromJson(testLogicalTopologyJson, type);
		type = new TypeToken<LogicalTopology>(){}.getType();
		testLogicalTopologyOver = gson.fromJson(testLogicalTopologyJsonOver, type);
	}
	/*
	 * 正常版
	 */
	public void testGetLogicalTopology() {
		new NonStrictExpectations() {
			ClientResponse gdbResponse;
			{
				gdbResponse.getStatus();
				result = 201;

				gdbResponse.getEntity(String.class);
				result = testLogicalTopologyJson;
			}
		};

		GraphDBClient gdbClient = OrientDBClientImpl.getInstance();
		try {
			LogicalTopologyJsonInOut resGdb = gdbClient.getLogicalTopology(testLogicalTopologyIn.getNodes());
			if(!testLogicalTopology.equals(resGdb.getResult())) {
				fail();
			}
		} catch (GraphDBClientException gdbe) {
			fail();
		}
	}

	/*
	 * Exceptionを正常に返すか試します。
	 */
	public void testGetLogicalTopologyException() {
		new NonStrictExpectations() {
			ClientResponse gdbResponse;
			{
				gdbResponse.getStatus();
				result = 400;

				gdbResponse.getEntity(String.class);
				result = testLogicalTopologyJson;
			}
		};

		GraphDBClient gdbClient = OrientDBClientImpl.getInstance();
		try {
			gdbClient.getLogicalTopology(testLogicalTopologyIn.getNodes());
			fail();
		} catch (GraphDBClientException gdbe) {
			if(gdbe.getStatus() == 201) {
				fail();
			}
		}
		try {
			gdbClient.getLogicalTopology(null);
		} catch (NullPointerException npe) {

		} catch (GraphDBClientException e) {
			fail();
		}
	}
}
