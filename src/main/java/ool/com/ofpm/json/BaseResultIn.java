package ool.com.ofpm.json;

import java.util.List;


public class BaseResultIn {
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
