package ool.com.ofpm.json;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public 	class LogicalTopology {
	private List<BaseNode> nodes;
	private List<LogicalLink> links;

	public LogicalTopology() {
		nodes = new ArrayList<BaseNode>();
		links = new ArrayList<LogicalLink>();
	}
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
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
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
			return EqualsBuilder.reflectionEquals(this, obj);
		}
		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this);
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
