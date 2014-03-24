package ool.com.ofpm.client;

import static org.junit.Assert.*;

import javax.ws.rs.client.Invocation.Builder;

import mockit.NonStrictExpectations;
import ool.com.ofpm.exception.GraphDBClientException;
import ool.com.ofpm.json.LogicalTopology;
import ool.com.ofpm.json.LogicalTopologyGetJsonOut;
import ool.com.ofpm.utils.Definition;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;

public class OrientDBClientImplTest {
	private String testLogicalTopologyJsonIn   = "{nodes:[{deviceName:'novaNode01'},{deviceName:'novaNode02'}]}";
	private String testLogicalTopologyJson     = "{nodes:[{deviceName:'novaNode01'},{deviceName:'novaNode02'}], links:[{deviceName:['novaNode01', 'novaNode02']}]}";

	private LogicalTopology testLogicalTopologyIn = LogicalTopology.fromJson(testLogicalTopologyJsonIn);
	private LogicalTopology testLogicalTopology   = LogicalTopology.fromJson(testLogicalTopologyJson);

	public void testGetLogicalTopology() {
		new NonStrictExpectations() {
			ClientResponse gdbResponse;
			Builder resBuilder;
			{
				resBuilder.get(ClientResponse.class);
				//result = new UniformInterfaceException(gdbResponse);
				result = new ClientHandlerException();
				result = new Exception();
				result = testLogicalTopologyJson;

				gdbResponse.getStatus();
				result = Definition.STATUS_BAD_REQUEST;
				result = Definition.STATUS_SUCCESS;

				gdbResponse.getEntity(String.class);
				result = testLogicalTopologyJson;
			}
		};

		GraphDBClient gdbClient = OrientDBClientImpl.getInstance();
//		try {
//			gdbClient.getLogicalTopology(testLogicalTopologyIn.getNodes());
//			fail();
//		} catch (GraphDBClientException gdbe) {
//			System.out.println(gdbe.getMessage());
//		}
		try {
			gdbClient.getLogicalTopology(testLogicalTopologyIn.getNodes());
			fail();
		} catch (GraphDBClientException gdbe) {
			System.out.println(gdbe.getMessage());
		}
		try {
			gdbClient.getLogicalTopology(testLogicalTopologyIn.getNodes());
			fail();
		} catch (GraphDBClientException gdbe) {
			System.out.println(gdbe.getMessage());
		}
		try {
			gdbClient.getLogicalTopology(testLogicalTopologyIn.getNodes());
		} catch (GraphDBClientException gdbe) {
			System.out.println(gdbe.getMessage());
		}
		try {
			LogicalTopologyGetJsonOut resGdb = gdbClient.getLogicalTopology(testLogicalTopologyIn.getNodes());
			assertEquals(resGdb.getResult(), testLogicalTopology);
		} catch (GraphDBClientException gdbe) {
			fail();
		}
	}
}
