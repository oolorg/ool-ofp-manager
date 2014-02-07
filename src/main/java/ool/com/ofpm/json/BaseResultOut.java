package ool.com.ofpm.json;

public class BaseResultOut {
	private String status;
	private String message;

	public String getMessage() {
		return this.message;
	}
	public void setMessage(final String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(final String status) {
		this.status = status;
	}
}
