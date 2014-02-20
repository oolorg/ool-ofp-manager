package ool.com.ofpm.json;

import java.util.ArrayList;
import java.util.List;

public 	class LogicalTopology {
	private List<BaseNode> nodes = new ArrayList<BaseNode>();
	private List<LogicalLink> links = new ArrayList<LogicalLink>();

	public LogicalTopology clone() {
		LogicalTopology newTopo = new LogicalTopology();
		for(BaseNode node: nodes) {
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

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj != null) return false;
		if(this.getClass() != obj.getClass()) return false;
		LogicalTopology other = (LogicalTopology)obj;
		if(this.nodes.size() != other.nodes.size()) return false;
		if(this.links.size() != other.links.size()) return false;
		for(BaseNode node : other.nodes) {
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
		for(BaseNode node : this.nodes) {
			hash += node.hashCode();
		}
		for(LogicalLink link : this.links) {
			hash += link.hashCode();
		}
		return hash;
	}

	public List<BaseNode> getNodes() {
		return nodes;
	}
	public void setNodes(
			List<BaseNode> nodes) {
		this.nodes = nodes;
	}

	public List<LogicalLink> getLinks() {
		return links;
	}
	public void setLinks(List<LogicalLink> links) {
		this.links = links;
	}

	public class LogicalLink {
		private List<String> deviceName;

		public LogicalLink() {
			deviceName = new ArrayList<String>();
		}

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

		public LogicalLink clone() {
			LogicalLink newLogicalLink = new LogicalLink();
			for(String dName : deviceName) {
				newLogicalLink.deviceName.add(dName);
			}
			return newLogicalLink;
		}
	}
}
