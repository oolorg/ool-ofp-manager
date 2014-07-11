package ool.com.ofpm.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PhysicalLinkJsonIn {
	private List<PortInfo> link = new ArrayList<PortInfo>();

	public List<PortInfo> getLink() {
		return link;
	}
	public void setLink(List<PortInfo> link) {
		this.link = link;
	}

	public static PhysicalLinkJsonIn fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<PhysicalLinkJsonIn>(){}.getType();
		return gson.fromJson(json, type);
	}
	public String toJson() {
		Gson gson = new Gson();
		Type type = new TypeToken<PhysicalLinkJsonIn>(){}.getType();
		return gson.toJson(this, type);
	}
	@Override
	public String toString() {
		return this.toJson();
	}
}
