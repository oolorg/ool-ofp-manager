package ool.com.ofpm.json;

import static org.junit.Assert.*;

import java.util.List;

import ool.com.odbcl.json.BaseResponse;
import ool.com.odbcl.json.LogicalTopology;
import ool.com.odbcl.json.Node;

import org.junit.Test;

public class LogicalTopologyTest {
	@Test
	public void testBaseResponse() {
		BaseResponse res1 = new BaseResponse();
		BaseResponse res2 = new BaseResponse();
		if(! res1.equals(res2)) fail();
		res1.setMessage("foo");
		if(res1.equals(res2)) fail();
		res2.setMessage("bar");
		if(res1.equals(res2)) fail();
		res2.setMessage("foo");
		if(! res1.equals(res2)) fail();

		res2.setStatus(3);
		if(res1.equals(res2)) fail();
		res1.setStatus(2);
		if(res1.equals(res2)) fail();
		res1.setStatus(3);
		if(! res1.equals(res2)) fail();

	}

	@Test
	public void testLogicalTopology() {
		LogicalTopology topo1 = new LogicalTopology();
		LogicalTopology topo2 = new LogicalTopology();
		if(! topo1.equals(topo2)) fail();
		List<Node> nodes1 = topo1.getNodes();
		Node node1_1 = new Node();
		nodes1.add(node1_1);
		if(topo1.equals(topo2)) fail();
	}
}
