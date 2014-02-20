package ool.com.ofpm.json;

import java.util.ArrayList;
import java.util.List;


public class AgentFlowJsonOut {
	private List<AgentFlow> list;

	public List<AgentFlow> getList() {
		return list;
	}

	public void setList(List<AgentFlow> list) {
		this.list = list;
	}

	public class AgentFlow {
		private String ofcUrl;
		private String type;
		private String ip;
		private List<Integer> port = new ArrayList<Integer>();

		public String getOfcUrl() {
			return ofcUrl;
		}
		public void setOfcUrl(String ofcUrl) {
			this.ofcUrl = ofcUrl;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public List<Integer> getPort() {
			return port;
		}
		public void setPort(
				List<Integer> port) {
			this.port = port;
		}

	}
}
