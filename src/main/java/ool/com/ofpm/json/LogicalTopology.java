package ool.com.ofpm.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public 	class LogicalTopology implements Cloneable {
	private List<Node> nodes = new ArrayList<Node>();
	private List<LogicalLink> links = new ArrayList<LogicalLink>();

	@Override
	public LogicalTopology clone() {
		LogicalTopology newTopo = new LogicalTopology();
		for(Node node: nodes) {
			newTopo.nodes.add(node.clone());
		}
		for(LogicalLink link: links) {
			newTopo.links.add(link.clone());
		}
		return newTopo;
	}
	public LogicalTopology sub(LogicalTopology otherTopo) {
		LogicalTopology cloneTopo = clone();
		cloneTopo.nodes.removeAll(otherTopo.nodes);
		cloneTopo.links.removeAll(otherTopo.links);
		return cloneTopo;
	}
	public static LogicalTopology fromJson(String json) {
		Gson gson = new Gson();
		Type type = new TypeToken<LogicalTopology>(){}.getType();
		return gson.fromJson(json, type);
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(this.getClass() != obj.getClass()) return false;
		LogicalTopology other = (LogicalTopology)obj;
		if(this.nodes.size() != other.nodes.size()) return false;
		if(this.links.size() != other.links.size()) return false;
		for(Node node : other.nodes) {
			if(! this.nodes.contains(node)) return false;
		}
		for(LogicalLink link : other.links) {
			if(! this.links.contains(link)) return false;
		}
		return true;
	}
	@Override
	public int hashCode() {
		int hash = 0;
		if(this.nodes != null) {
			for(Node node : this.nodes) {
				if(node != null) hash += node.hashCode();
			}
		}
		if(this.links != null) {
			for(LogicalLink link : this.links) {
				if(link != null) hash += link.hashCode();
			}
		}
		return hash;
	}
	@Override
	public String toString() {
		Gson gson = new Gson();
		Type type = new TypeToken<LogicalTopology>() {}.getType();
		return gson.toJson(this, type);
	}

	public List<Node> getNodes() {
		return nodes;
	}
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<LogicalLink> getLinks() {
		return links;
	}
	public void setLinks(List<LogicalLink> links) {
		this.links = links;
	}

	public class LogicalLink implements Cloneable {
		private List<String> deviceName = new ArrayList<String>();

		public List<String> getDeviceName() {
			return deviceName;
		}
		public void setDeviceName(List<String> deviceName) {
			this.deviceName = deviceName;
		}

		@Override
		public boolean equals(Object obj) {
			if(this == obj) return true;
			if(obj == null) return false;
			if(this.getClass() != obj.getClass()) return false;
			LogicalLink other = (LogicalLink)obj;
			if(this.deviceName.size() != other.deviceName.size()) return false;
			return this.deviceName.containsAll(other.deviceName);
		}
		@Override
		public int hashCode() {
			int hash = 0;
			if(this.deviceName == null) return hash;
			for(String device : this.deviceName) {
				if(device != null) hash += device.hashCode();
			}
			return hash;
		}
		@Override
		public LogicalLink clone() {
			LogicalLink newLogicalLink = new LogicalLink();
			for(String dName : deviceName) {
				newLogicalLink.deviceName.add(dName);
			}
			return newLogicalLink;
		}
		public String toJson() {
			Gson gson = new Gson();
			Type type = new TypeToken<LogicalLink>(){}.getType();
			return gson.toJson(this, type);
		}
		@Override
		public String toString() {
			return this.toJson();
		}
	}
}
