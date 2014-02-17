package ool.com.ofpm.json;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;


public class ResultOut extends BaseResponse {
	public static Type TYPE = new TypeToken<ResultOut>(){}.getType();

	private Data result;

	public Data getResult() {
		return result;
	}

	public void setResult(final Data result) {
		this.result = result;
	}

	public class Data {
		private List<BaseNode> nodes;
		private List<LogicalTopology> links;
		public List<BaseNode> getNodes() {
			return nodes;
		}
		public void setNodes(List<BaseNode> nodes) {
			this.nodes = nodes;
		}
		public List<LogicalTopology> getLinks() {
			return links;
		}
		public void setLinks(List<LogicalTopology> links) {
			this.links = links;
		}
	}
}
