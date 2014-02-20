package ool.com.ofpm.json;




public class LogicalTopologyJsonInOut extends BaseResponse {
	private LogicalTopology result = new LogicalTopology();

	public LogicalTopology getResult() {
		return result;
	}

	public void setResult(LogicalTopology result) {
		this.result = result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(this.getClass() != obj.getClass()) return false;
		LogicalTopologyJsonInOut other = (LogicalTopologyJsonInOut)obj;
		if(this.getStatus() != other.getStatus()) return false;
		if(! this.getMessage().equals(other.getMessage())) return false;
//		if(! super.equals(obj)) return false;
//		LogicalTopologyJsonInOut other = (LogicalTopologyJsonInOut)obj;
		return this.result.equals(other.result);
	}
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		if(this.result != null) hash += this.result.hashCode();
		return hash;
	}
}
