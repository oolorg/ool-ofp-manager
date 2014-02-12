package ool.com.ofpm.json;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;


public class BaseResultIn {
	public static Type TYPE_TOKEN = new TypeToken<BaseResultIn>(){}.getType();

	private List<Node> nodes;
	private List<Link> links;
	private StatusIn Result;

	public List<Node> getNodes() {
		return nodes;
	}
	public void setNodes(final List<Node> nodes) {
		this.nodes = nodes;
	}
	public List<Link> getLinks() {
		return links;
	}
	public void setLinks(final List<Link> links) {
		this.links = links;
	}
	public StatusIn getResult() {
		return Result;
	}
	public void setResult(final StatusIn result) {
		Result = result;
	}

}
