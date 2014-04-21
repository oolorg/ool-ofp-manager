package ool.com.ofpm.client;

import static org.junit.Assert.*;
import ool.com.odbcl.client.GraphDBClient;
import ool.com.odbcl.client.OrientDBClientImpl;
import ool.com.odbcl.exception.GraphDBClientException;
import ool.com.odbcl.json.LogicalTopology;
import ool.com.odbcl.json.LogicalTopologyGetJsonOut;

import org.junit.Test;

public class OrientDBClientImplTest {
	private static final String ORIENT_DB_SERVICE_URL = "172.16.1.84:12424";

	private String testLogicalTopologyJsonIn   = "{nodes:[{deviceName:'server-200'},{deviceName:'server-201'}]}";
	private String testLogicalTopologyJson     = "{nodes:[{deviceName:'server-200'},{deviceName:'server-201'}], links:[{deviceName:['server-200', 'server-201']}]}";

	private LogicalTopology testLogicalTopologyIn = LogicalTopology.fromJson(testLogicalTopologyJsonIn);
	private LogicalTopology testLogicalTopology   = LogicalTopology.fromJson(testLogicalTopologyJson);

	@Test
	public void testGetLogicalTopology() {
		//GraphDBClient gdbClient = OrientDBClientImpl.getInstance();

		GraphDBClient gdbClient = new OrientDBClientImpl(ORIENT_DB_SERVICE_URL);

		try {
			LogicalTopologyGetJsonOut resGdb = gdbClient.getLogicalTopology(testLogicalTopologyIn.getNodes());
			//assertEquals(resGdb.getResult(), testLogicalTopology);
			System.out.println(String.format("status=%d",resGdb.getStatus()));
		} catch (GraphDBClientException gdbe) {
			System.out.println(gdbe.getMessage());
			fail();
		}
	}
}
