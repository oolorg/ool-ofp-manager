package ool.com.ofpm.json;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public 	class LogicalTopology {
	private Set<BaseNode> nodes;
	private Set<LogicalLink> links;

	public LogicalTopology() {
		nodes = new HashSet<BaseNode>();
		links = new HashSet<LogicalLink>();
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

	public Set<BaseNode> getNodes() {
		return nodes;
	}
	public void setNodes(
			Set<BaseNode> nodes) {
		this.nodes = nodes;
	}

	public Set<LogicalLink> getLinks() {
		return links;
	}
	public void setLinks(Set<LogicalLink> links) {
		this.links = links;
	}

	public class LogicalLink {
		private Set<String> deviceName;

		public LogicalLink() {
			deviceName = new HashSet<String>();
		}

		public Set<String> getDeviceName() {
			return deviceName;
		}

		public void setDeviceName(Set<String> deviceName) {
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
