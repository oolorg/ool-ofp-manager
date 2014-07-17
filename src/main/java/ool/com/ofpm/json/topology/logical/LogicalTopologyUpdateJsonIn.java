package ool.com.ofpm.json.topology.logical;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public 	class LogicalTopologyUpdateJsonIn extends LogicalTopology {
	private String tokenId = new String();

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(
			String tokenId) {
		this.tokenId = tokenId;
	}

	public static LogicalTopologyUpdateJsonIn fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<LogicalTopologyUpdateJsonIn>(){}.getType();
		return gson.fromJson(json, type);
	}
}
