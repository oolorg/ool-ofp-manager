package ool.com.ofpm.json.ofc;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class AgentClientUpdateFlowReq {
	private List<AgentUpdateFlowData> list;

	public List<AgentUpdateFlowData> getList() {
		return list;
	}

	public void setList(List<AgentUpdateFlowData> list) {
		this.list = list;
	}

	public static AgentClientUpdateFlowReq fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<AgentClientUpdateFlowReq>() {}.getType();
		return gson.fromJson(json, type);

	}
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<AgentClientUpdateFlowReq>() {}.getType();
		return gson.toJson(this, type);
	}
	@Override
	public String toString() {
		return toJson();
	}

	public class AgentUpdateFlowData {
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

		@Override
		public String toString() {
			Gson gson = new Gson();
			Type type = new TypeToken<AgentUpdateFlowData>() {}.getType();
			return gson.toJson(this, type);
		}

	}
}
