/**
 * @author OOL 1134380013430
 * @date 2014/04/23
 * @TODO TODO
 */
package ool.com.ofpm.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

/**
 * @author 1134380013430
 *
 */
public class NetworkConfigSetupperIn {
	@SerializedName("auth")
	private String tokenId;
	private List<NetworkConfigSetupperInData> params = new ArrayList<NetworkConfigSetupperInData>();

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public List<NetworkConfigSetupperInData> getParams() {
		return params;
	}

	public void setParams(
			List<NetworkConfigSetupperInData> params) {
		this.params = params;
	}

	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<NetworkConfigSetupperIn>() {}.getType();
		return gson.toJson(this, type);
	}
}
