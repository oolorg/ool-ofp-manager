package ool.com.ofpm.json;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;


public class BaseResultIn {
	public static Type TYPE_TOKEN = new TypeToken<BaseResultIn>(){}.getType();

	private List<BaseNode> nodes;
	private List<LogicalTopology> links;
	private StatusIn Result;

	public List<BaseNode> getNodes() {
		return nodes;
	}
	public void setNodes(final List<BaseNode> nodes) {
		this.nodes = nodes;
	}
	public List<LogicalTopology> getLinks() {
		return links;
	}
	public void setLinks(final List<LogicalTopology> links) {
		this.links = links;
	}
	public StatusIn getResult() {
		return Result;
	}
	public void setResult(final StatusIn result) {
		Result = result;
	}

}
