package ool.com.ofpm.json;

import java.util.HashSet;
import java.util.Set;

public class AgentFlowJsonOut {
	private Set<AgentFlow> list;

	public Set<AgentFlow> getList() {
		return list;
	}

	public void setList(Set<AgentFlow> list) {
		this.list = list;
	}

	public class AgentFlow {
		private String ofcUrl;
		private String type;
		private String ip;
		private Set<Integer> port = new HashSet<Integer>();

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
		public Set<Integer> getPort() {
			return port;
		}
		public void setPort(
				Set<Integer> port) {
			this.port = port;
		}

	}
}
