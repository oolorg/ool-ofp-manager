package ool.com.ofpm.json;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;



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
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
