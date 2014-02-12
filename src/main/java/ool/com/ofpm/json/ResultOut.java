package ool.com.ofpm.json;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;


public class ResultOut extends BaseResultOut {
	public static Type TYPE = new TypeToken<ResultOut>(){}.getType();

	private Data result;

	public Data getResult() {
		return result;
	}

	public void setResult(final Data result) {
		this.result = result;
	}

	public class Data {
		private List<Node> nodes;
		private List<Link> links;
		public List<Node> getNodes() {
			return nodes;
		}
		public void setNodes(List<Node> nodes) {
			this.nodes = nodes;
		}
		public List<Link> getLinks() {
			return links;
		}
		public void setLinks(List<Link> links) {
			this.links = links;
		}
	}
}
