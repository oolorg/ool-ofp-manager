package ool.com.ofpm.business;

import ool.com.ofpm.client.AgentClientImpl;
import ool.com.ofpm.json.BaseResultIn;
import ool.com.ofpm.json.ResultOut;
import ool.com.ofpm.json.StatusIn;

public class PhysicalServiceBypusBusiness {
	private String uri;
	public PhysicalServiceBypusBusiness(String uri) {
		this.uri = uri;
	}
	@SuppressWarnings("finally")
	public ResultOut get(){
		ResultOut result = null;
		try {
			AgentClientImpl agent_client = AgentClientImpl.getInstance(this.uri);
			BaseResultIn ofpa_result = agent_client.getTopology();
			result = convert(ofpa_result);
		} catch(Exception e) {
			result = new ResultOut();
			result.setMessage(e.getMessage());
			result.setStatus("500");
		} finally {
			return result;
		}
	}

	private ResultOut convert(BaseResultIn ofpaResult) {
		StatusIn ofpa_result = ofpaResult.getResult();

		ResultOut result = new ResultOut();
		ResultOut.Data result_data = result.new Data();
		result_data.setLinks(ofpaResult.getLinks());
		result_data.setNodes(ofpaResult.getNodes());

		result.setMessage(ofpa_result.getMessage());
		result.setStatus(ofpa_result.getStatus());
		result.setResult(result_data);
		return result;
	}
}
