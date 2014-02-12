package ool.com.ofpm.json;

public class Link {
	private int target;
	private int source;
	private int target_port;
	private int source_port;
	private int id;
	private String linkKind;

	public int getTarget() {
		return this.target;
	}
	public void setTarget(final int target) {
		this.target = target;
	}
	public int getSource() {
		return this.source;
	}
	public void setSource(final int source) {
		this.source = source;
	}
	public int getTargetPort() {
		return this.target_port;
	}
	public void setTargetPort(final int targetPort) {
		this.target_port = targetPort;
	}
	public int getSourcePort() {
		return this.source_port;
	}
	public void setSourcePort(final int sourcePort) {
		this.source_port = sourcePort;
	}
	public int getId() {
		return this.id;
	}
	public void setId(final int id) {
		this.id =id;
	}
	public String getLinkKind() {
		return this.linkKind;
	}
	public void setLinkKind(final String linkKind) {
		this.linkKind = linkKind;
	}

	public enum LinkKind {
		PHYSICAL("physical"),
		LOGICAL("logical");

		private String name;
		public String getName() {
			return this.name;
		}
		private LinkKind(String name) {
			this.name = name;
		}
	}
}
