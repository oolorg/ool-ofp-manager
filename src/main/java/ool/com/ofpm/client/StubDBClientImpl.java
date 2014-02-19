package ool.com.ofpm.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ool.com.ofpm.json.BaseNode;
import ool.com.ofpm.json.LogicalTopology;
import ool.com.ofpm.json.LogicalTopology.LogicalLink;
import ool.com.ofpm.json.LogicalTopologyJsonInOut;
import ool.com.ofpm.json.PatchLinkJsonIn;
import ool.com.ofpm.json.PatchLinkJsonIn.PatchLink;

import com.sun.jersey.api.client.WebResource;

public class StubDBClientImpl implements GraphDBClient {

	private static Map<String, Integer> nameToPort = new HashMap<String, Integer>();
	private static Map<String, String> logicalLink = new HashMap<String, String>();
	private static StubDBClientImpl instance = null;
	WebResource resource;
	public StubDBClientImpl() {
		nameToPort.put("nova1", 1);
		nameToPort.put("nova2", 2);
		nameToPort.put("server1", 3);
		nameToPort.put("server2", 4);
	}
	public static StubDBClientImpl getInstance() {
		if(instance == null) {
			instance = new StubDBClientImpl();
		}
		return instance;
	}
	public void exec() {
		// TODO Auto-generated method stub

	}
	public LogicalTopologyJsonInOut getLogicalTopology(List<BaseNode> nodes) throws GraphDBClientException {
		LogicalTopologyJsonInOut res = new LogicalTopologyJsonInOut();
		LogicalTopology topology = res.getResult();
		List<BaseNode> resNode = topology.getNodes();
		List<LogicalLink> resLink = topology.getLinks();

		for(BaseNode node : nodes) {
			String deviceName = node.getDeviceName();
			if(!nameToPort.containsKey(deviceName)) continue;
			resNode.add(node);

			for(Map.Entry<String, String> entry : logicalLink.entrySet())  {
				if(entry.getKey() == deviceName) {
					LogicalLink link = topology.new LogicalLink();
					List<String> deviceNames = link.getDeviceName();
					deviceNames.add(entry.getKey());
					deviceNames.add(entry.getValue());
					resLink.add(link);
				} else if(entry.getValue() == deviceName) {
					LogicalLink link = topology.new LogicalLink();
					List<String> deviceNames = link.getDeviceName();
					deviceNames.add(entry.getKey());
					deviceNames.add(entry.getValue());
					resLink.add(link);
				}
			}
		}

		return res;
	}
	public PatchLinkJsonIn addLogicalLink(LogicalLink link) throws GraphDBClientException {
		PatchLinkJsonIn res = new PatchLinkJsonIn();
		for(String deviceName: link.getDeviceName()) {
			if(!nameToPort.containsKey(deviceName)) {
				res.setStatus(404);
				res.setMessage("device not found");
				return res;
			}
			if(logicalLink.containsKey(deviceName)) {
				res.setStatus(404);
				res.setMessage("device not found");
				return res;
			}
			if(logicalLink.containsValue(deviceName)) {
				res.setStatus(404);
				res.setMessage("device not found");
				return res;
			}
		}

		Iterator<String> iDN = link.getDeviceName().iterator();
		String src = iDN.next();
		String dst = iDN.next();

		logicalLink.put(src, dst);

		List<Integer> ports = new ArrayList<Integer>();
		ports.add(nameToPort.get(src));
		ports.add(nameToPort.get(dst));
		PatchLink patchLink = res.new PatchLink();
		//patchLink.setPortName(ports);
		patchLink.setDeviceName("sentec");
		res.getResult().add(patchLink);
		res.setStatus(201);
		return res;
	}
	public PatchLinkJsonIn delLogicalLink(LogicalLink link) throws GraphDBClientException {
		PatchLinkJsonIn res = new PatchLinkJsonIn();
		for(String deviceName : link.getDeviceName()) {
			if(!nameToPort.containsKey(deviceName)) {
				res.setStatus(404);
				res.setMessage("device not found");
			}
		}

		Iterator<String> iDN = link.getDeviceName().iterator();
		String src = iDN.next();
		String dst = iDN.next();
		if(logicalLink.containsKey(src)) {
			if(logicalLink.get(src) != dst) {
				res.setStatus(404);
				res.setMessage("link not found");
				return res;
			}
			logicalLink.remove(src);
		} else if(logicalLink.containsKey(src)) {
			if(logicalLink.get(src) != dst) {
				res.setStatus(404);
				res.setMessage("link not found");
				return res;
			}
			logicalLink.remove(dst);
		}

		List<Integer> ports = new ArrayList<Integer>();
		ports.add(nameToPort.get(src));
		ports.add(nameToPort.get(dst));
		PatchLink patchLink = res.new PatchLink();
		//patchLink.setPortName(ports);
		patchLink.setDeviceName("sentec");
		res.getResult().add(patchLink);
		res.setStatus(201);
		return res;
	}



}
