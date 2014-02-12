package ool.com.ofpm.client;

import ool.com.ofpm.json.BaseResultIn;

public interface AgentClient {
	public BaseResultIn getTopology() throws Exception;
	public BaseResultIn addFlows() throws Exception;
	public BaseResultIn delFlows() throws Exception;
	public boolean addLinks(Link[] links);
	public boolean delLinks(Link[] links);
	public String[] getDeviceList();
}
