package ool.com.ofpm.json.topology.physical;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ool.com.ofpm.json.device.PortData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PhysicalLinkJsonIn {
	private List<PortData> link = new ArrayList<PortData>();

	public List<PortData> getLink() {
		return link;
	}
	public void setLink(List<PortData> link) {
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
