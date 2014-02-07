package ool.com.ofpm.json;

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
