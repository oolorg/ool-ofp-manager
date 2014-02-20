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

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(this.getClass() != obj.getClass()) return false;
		BaseResponse other = (BaseResponse)obj;
		if(this.status != other.status) return false;
		return (this.message == other.message);
	}
	@Override
	public int hashCode() {
		if(this.message == null) return 0;
		return this.message.hashCode();
	}
}
