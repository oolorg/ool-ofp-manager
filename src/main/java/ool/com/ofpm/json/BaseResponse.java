package ool.com.ofpm.json;


public class BaseResponse {
	private int status;
	private String message;

	public String getMessage() {
		return this.message;
	}
	public void setMessage(final String message) {
		this.message = message;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	public boolean equals(BaseResponse other) {
		if(status != other.status) {
			return false;
		}
		if(message != other.message) {
			return false;
		}
		return true;
	}
}
