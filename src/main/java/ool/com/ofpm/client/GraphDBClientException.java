package ool.com.ofpm.client;

public class GraphDBClientException extends Exception {
	private int status;

	public GraphDBClientException (String msg, int status) {
		super(msg);
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
