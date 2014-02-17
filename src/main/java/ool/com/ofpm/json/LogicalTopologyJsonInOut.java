package ool.com.ofpm.json;



public class LogicalTopologyJsonInOut extends BaseResponse {
	private LogicalTopology result = new LogicalTopology();

	public LogicalTopology getResult() {
		return result;
	}

	public void setResult(LogicalTopology result) {
		this.result = result;
	}

	public boolean equals(LogicalTopologyJsonInOut other) {
		if(!result.equals(other.result)) {
			return false;
		}
		return super.equals(other);
	}
}
